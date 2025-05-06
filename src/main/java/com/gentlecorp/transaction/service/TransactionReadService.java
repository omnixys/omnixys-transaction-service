package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.exceptions.AccessForbiddenException;
import com.gentlecorp.transaction.exceptions.NotFoundException;
import com.gentlecorp.transaction.models.entities.Transaction;
import com.gentlecorp.transaction.models.entities.Account;
import com.gentlecorp.transaction.repository.TransactionRepository;
import com.gentlecorp.transaction.repository.SpecificationBuilder;
import com.gentlecorp.transaction.security.CustomUserDetails;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.client.FieldAccessException;
import org.springframework.graphql.client.GraphQlTransportException;
import org.springframework.graphql.client.HttpGraphQlClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import java.util.UUID;

import static com.gentlecorp.transaction.models.enums.TransactionType.DEPOSIT;
import static com.gentlecorp.transaction.models.enums.TransactionType.INCOME;
import static com.gentlecorp.transaction.models.enums.TransactionType.PAYMENT;
import static com.gentlecorp.transaction.models.enums.TransactionType.REFUND;
import static com.gentlecorp.transaction.models.enums.TransactionType.TRANSFER;
import static com.gentlecorp.transaction.models.enums.TransactionType.WITHDRAWAL;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class TransactionReadService {
  private final TransactionRepository transactionRepository;
  private final HttpGraphQlClient graphQlClient;
  private final SpecificationBuilder specificationBuilder;

  private void setTransactionType(final Collection<Transaction> transactions, final UUID accountId) {
    log.debug("setTransactionType: accountId={}", accountId);
    UUID zeroUUID = UUID.fromString("30000000-0000-0000-0000-000000000000");

    transactions.forEach(transaction -> {
      final var sender = transaction.getSender();
      final var receiver = transaction.getReceiver();

      // Payment and Refund checks first to avoid overwriting
      if (zeroUUID.equals(receiver)) {
        transaction.setType(PAYMENT);
        return;
      }

      if (zeroUUID.equals(sender)) {
        transaction.setType(REFUND);
        return;
      }

      if (accountId.equals(sender)) {
        if (receiver == null) {
          transaction.setType(DEPOSIT);
        } else {
          transaction.setType(TRANSFER);
        }
        return;
      }

      if (accountId.equals(receiver)) {
        if (sender == null) {
          transaction.setType(WITHDRAWAL);
        } else {
          transaction.setType(INCOME);
        }
      }
    });
  }

  public @NonNull Transaction findById(final UUID id, final CustomUserDetails user) {
    log.debug("Find transaction by id: {}", id);
    final var transaction = transactionRepository.findById(id).orElseThrow(NotFoundException::new);
    log.debug("findById: transaction={}", transaction);
    return transaction;
  }

  public Collection<Transaction> findTransactionsByAccountId(final UUID id) {
    log.debug("Find transaction by AccountId: {}", id);

    final var transactions = transactionRepository.findByAccountId(id);
    setTransactionType(transactions, id);

    log.debug("findTransactionsByAccountId: transaction={}", transactions);
    return transactions;
  }

  public Collection<Transaction> findTransactionsByUser(final CustomUserDetails user) {
    log.debug("Find transaction by User: user={}", user.getUsername());

    final var accountIdList = findAccountsByUser(user);
    final var transactions = accountIdList.stream()
        .distinct()
        .flatMap(id -> findTransactionsByAccountId(id).stream())
        .toList();

    if (transactions.isEmpty()) {
      throw new NotFoundException();
    }

    log.debug("findTransactionsByUser: transactions={}", transactions);
    return transactions;
  }

  private Collection<UUID> findAccountsByUsername(final String username, final String token) {
    log.debug("findAccountsByUser: username={}", username);

    final var query = """
        query Accounts {
            accountsByUsername {
                id
            }
        }
        """;

    final List<UUID> accountIdList;
    try {
      final var accountList = graphQlClient
          .mutate()
          .header(AUTHORIZATION, token)
          .build()
          .document(query)
          .retrieveSync("accountsByUsername")
          .toEntityList(Account.class);

      if (accountList.isEmpty()) {
        throw new NotFoundException();
      }

      accountIdList = accountList.stream()
          .map(Account::id)
          .toList();

      log.debug("findAccountsByUser: accountIdList={}", accountIdList);
    } catch (final FieldAccessException | GraphQlTransportException ex) {
      log.debug("findAccountsByUser", ex);
      throw new NotFoundException();
    }
    log.debug("findAccountsByUser: accountIdList={}", accountIdList);
    return accountIdList;
  }

  public @NonNull Collection<Transaction> find(@NonNull final Map<String, List<Object>> searchCriteria) {
    log.debug("find: searchCriteria={}", searchCriteria);

    if (searchCriteria.isEmpty()) {
      return transactionRepository.findAll();
    }

    final var specification = specificationBuilder
      .build(searchCriteria)
      .orElseThrow(() -> new NotFoundException(searchCriteria));
    final var transactions = transactionRepository.findAll(specification);

    if (transactions.isEmpty())
      throw new NotFoundException(searchCriteria);
    log.debug("find: transactions={}", transactions);
    return transactions;
  }

  @SuppressWarnings("ReturnCount")
  public Collection<UUID> findAccountsByUser(final CustomUserDetails user) {
    log.debug("findAccountById");

    final Collection<UUID> accountList;
    try {
      final var token = String.format("Bearer %s",user.getToken());
      accountList = findAccountsByUsername(user.getUsername(), token);
    } catch (final HttpClientErrorException.Unauthorized ex) {
      log.error("Unauthorized access attempt with token: {}", user.getToken());
      throw new AccessForbiddenException("User does not have the required permissions.",1);
    } catch (final HttpClientErrorException.Forbidden ex) {
      log.error("Access forbidden with token: {}", user.getToken());
      throw new AccessForbiddenException("User role is not permitted.",1);
    } catch (final HttpClientErrorException.NotFound ex) {
      log.debug("No account found for ID: {}", user.getUsername());
      throw new NotFoundException();
    } catch (final HttpStatusCodeException ex) {
      log.error("HTTP error while finding account with ID={}: Status code={}, Message={}", user.getUsername(), ex.getStatusCode(), ex.getMessage());
      throw new RuntimeException("Unexpected error while processing the request.");
    }

    log.debug("Account found: {}", accountList);
    return accountList;
  }

  public Account findAccountById(final UUID id, final String token) {
    log.debug("findAccountById: id={}", id);

    final var query = """
        query Account($id: ID!) {
            account(id: $id) {
                id
                balance
            }
        }
        """;

    final Map<String, Object> variables = Map.of(
        "id", id
    );

    final Account account;
    try {
      account = graphQlClient
          .mutate()
          .header(AUTHORIZATION, token)
          .build()
          .document(query)
          .variables(variables)
          .retrieveSync("account")
          .toEntity(Account.class);

      log.debug("findAccountById: accountIdList={}", account);
    } catch (final FieldAccessException | GraphQlTransportException ex) {
      log.debug("findAccountById", ex);
      throw new NotFoundException();
    }
    log.debug("findAccountById: accountIdList={}", account);
    return  account;
  }
}
