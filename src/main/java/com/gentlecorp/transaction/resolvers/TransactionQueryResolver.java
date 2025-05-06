package com.gentlecorp.transaction.resolvers;

import com.gentlecorp.transaction.models.entities.Transaction;
import com.gentlecorp.transaction.models.input.SearchCriteria;
import com.gentlecorp.transaction.security.CustomUserDetails;
import com.gentlecorp.transaction.service.TransactionReadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import static java.util.Collections.emptyMap;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionQueryResolver {
    private final TransactionReadService transactionReadService;

    @QueryMapping("transaction")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    Transaction getTransactionById(
        @Argument final UUID id,
        final Authentication authentication
    ) {
        log.debug("getTransactionById: id={}", id);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var transaction = transactionReadService.findById(id, user);
        log.debug("getTransactionById: Transaction={}", transaction);
        return transaction;
    }

    @QueryMapping("getTransactionsByPerson")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER', 'SUPREME', 'ELITE', 'BASIC')")
    Collection<Transaction> getTransactionByPerson(
        final Authentication authentication
    ) {
        log.debug("getTransactionByPerson:");
        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var transaction = transactionReadService.findTransactionsByUser(user);
        log.debug("getTransactionByPerson: Transaction={}", transaction);
        return transaction;
    }

    @QueryMapping("transactions")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    Collection<Transaction> getTransactions(
        @Argument final Optional<SearchCriteria> input,
        final Authentication authentication
    ) {
        log.debug("getTransactions: input={}", input);
        final var user = (CustomUserDetails) authentication.getPrincipal();
        final var searchCriteria = input.map(SearchCriteria::toMap).orElse(emptyMap());
        final var transaction = transactionReadService.find(searchCriteria);
        log.debug("getTransactions: Transactions={}", transaction);
        return transaction;
    }
}
