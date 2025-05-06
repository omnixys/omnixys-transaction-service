package com.gentlecorp.transaction.service;

import com.gentlecorp.transaction.exceptions.InsufficientFundsException;
import com.gentlecorp.transaction.exceptions.InvalidTransactionException;
import com.gentlecorp.transaction.exceptions.NotFoundException;
import com.gentlecorp.transaction.models.dto.BalanceDTO;
import com.gentlecorp.transaction.models.entities.Transaction;
import com.gentlecorp.transaction.repository.TransactionRepository;
import com.gentlecorp.transaction.security.CustomUserDetails;
import jakarta.validation.Validation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TransactionWriteService {

  private final TransactionRepository transactionRepository;
  private final TransactionReadService transactionReadService;
  private final KafkaTemplate<String, BalanceDTO> kafkaTemplate;

  private boolean validateTransaction(final Transaction transaction, final String token) {
    log.debug("Validating transaction {}", transaction);

    if (transaction.getSender() != null && transaction.getReceiver() != null) {
      if (transaction.getSender().equals(transaction.getReceiver())) {
        log.error("Sender and Receiver are the same");
        //TODO passenden Namen für die Exception
        throw new InvalidTransactionException(transaction.getSender());
      }
      return true;
    }

    if (transaction.getSender() != null) {
      final var senderAccount = transactionReadService.findAccountById(transaction.getSender(), token);

      if (senderAccount.balance().compareTo(transaction.getAmount()) < 0) {
        log.error("Insufficient funds for transaction. Sender balance: {}", senderAccount.balance());
        throw new InsufficientFundsException();
      }

      return false;
    }

    if (transaction.getReceiver() != null) {
      transactionReadService.findAccountById(transaction.getReceiver(), token);
      return true;
    }
    throw new NotFoundException();
  }

  public Transaction create(final Transaction transaction, final CustomUserDetails user) {
    log.debug("Creating transaction {}", transaction);

    final var token = String.format("Bearer %s", user.getToken());
    final var isTransaction = validateTransaction(transaction, token);

    calculateBalance(transaction, isTransaction, token);

    var savedTransaction = transactionRepository.save(transaction);
    log.debug("Transaction saved: {}", savedTransaction);
    return savedTransaction;
  }

  private void calculateBalance(final Transaction transaction, final boolean isTransaction, final String token) {
    log.debug("Calculating balance for transaction: transaction={}, isTransaction={}", transaction, isTransaction);

    final var transactionAmount = transaction.getAmount();
    final var senderId = transaction.getSender();
    final var receiverId = transaction.getReceiver();

    if (isTransaction) {
      handleSenderReceiverBalance(senderId, receiverId, transactionAmount, token);
    } else {
      handleSingleAccountBalance(senderId, receiverId, transactionAmount, token);
    }
  }


  private void handleSenderReceiverBalance(final UUID senderId, final UUID receiverId, final BigDecimal transactionAmount, final String token) {
    log.debug("handleSenderReceiverBalance: senderId={}, receiverId={}, amount={}", senderId, receiverId, transactionAmount);

    final var senderAccount = transactionReadService.findAccountById(senderId, token);

//    validation.validateCustomer(senderAccount.customerUsername(), jwt);
    final BigDecimal newSenderBalance = senderAccount.balance().subtract(transactionAmount);
    if (newSenderBalance.compareTo(BigDecimal.ZERO) < 0) {
      log.error("Insufficient funds for transaction. Sender balance: {}", newSenderBalance);
      throw new InsufficientFundsException();
    }

    sendBalanceAdjustment(senderId, transactionAmount.negate());
    sendBalanceAdjustment(receiverId, transactionAmount);
  }

  private void handleSingleAccountBalance(final UUID senderId, final UUID receiverId, final BigDecimal transactionAmount, final String token) {
    log.debug("handleSingleAccountBalance: senderId={}, receiverId={}, amount={}", senderId, receiverId, transactionAmount);

    final UUID accountId = Optional.ofNullable(senderId).orElse(receiverId);

//    var account = readService.findAccountById(accountId, token);
//    validation.validateCustomer(account.customerUsername(), jwt);
//    final BigDecimal adjustedAmount = accountId.equals(receiverId) ? transactionAmount.negate() : transactionAmount;

   // sendBalanceAdjustment(accountId, adjustedAmount);
    sendBalanceAdjustment(accountId, transactionAmount);
  }


  private void sendBalanceAdjustment(UUID accountId, BigDecimal amount) {
    final BalanceDTO balanceDTO = new BalanceDTO(accountId, amount);
    kafkaTemplate.send("adjustBalance", balanceDTO);
    log.debug("Balance adjustment sent: {}", balanceDTO);
  }
}
