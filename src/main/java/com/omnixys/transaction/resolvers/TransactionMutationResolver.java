package com.omnixys.transaction.resolvers;

import com.omnixys.transaction.exceptions.AccessForbiddenException;
import com.omnixys.transaction.exceptions.NotFoundException;
import com.omnixys.transaction.models.input.CreateTransactionInput;
import com.omnixys.transaction.models.mapper.TransactionMapper;
import com.omnixys.transaction.security.CustomUserDetails;
import com.omnixys.transaction.service.TransactionWriteService;
import graphql.GraphQLError;
import graphql.schema.DataFetchingEnvironment;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.UUID;

import static org.springframework.graphql.execution.ErrorType.FORBIDDEN;
import static org.springframework.graphql.execution.ErrorType.NOT_FOUND;

@Controller
@RequiredArgsConstructor
@Slf4j
public class TransactionMutationResolver {

  private final TransactionWriteService transactionWriteService;
  private final Validator validator;
  private final TransactionMapper transactionMapper;

  @MutationMapping("createTransaction")
  public UUID createTransaction(
      @Argument("input") final CreateTransactionInput createTransactionInput,
      final Authentication authentication
  ) {
    log.debug("createTransaction: transactionDTO={}", createTransactionInput);
    final var user = (CustomUserDetails) authentication.getPrincipal();
    final var transactionInput = transactionMapper.toTransaction(createTransactionInput);
    final var transaction = transactionWriteService.create(transactionInput, user);
    log.debug("createTransaction: transaction={}", transaction);
    return transaction.getId();
  }

  /**
   * Behandelt eine `AccessForbiddenException` und gibt ein entsprechendes GraphQL-Fehlerobjekt zurück.
   *
   * @param ex Die ausgelöste Ausnahme.
   * @param env Das GraphQL-Umfeld für Fehlerinformationen.
   * @return Ein `GraphQLError` mit der Fehlerbeschreibung.
   */
  @GraphQlExceptionHandler
  GraphQLError onAccessForbidden(final AccessForbiddenException ex, DataFetchingEnvironment env) {
    log.error("onAccessForbidden: {}", ex.getMessage());
    return GraphQLError.newError()
        .errorType(FORBIDDEN)
        .message(ex.getMessage())
        .path(env.getExecutionStepInfo().getPath().toList()) // Dynamischer Query-Pfad
        .location(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation()) // GraphQL Location
        .build();
  }

  /**
   * Behandelt eine `NotFoundException` und gibt ein entsprechendes GraphQL-Fehlerobjekt zurück.
   *
   * @param ex Die ausgelöste Ausnahme.
   * @param env Das GraphQL-Umfeld für Fehlerinformationen.
   * @return Ein `GraphQLError` mit der Fehlerbeschreibung.
   */
  @GraphQlExceptionHandler
  GraphQLError onNotFound(final NotFoundException ex, DataFetchingEnvironment env) {
    log.error("onNotFound: {}", ex.getMessage());
    return GraphQLError.newError()
        .errorType(NOT_FOUND)
        .message(ex.getMessage())
        .path(env.getExecutionStepInfo().getPath().toList()) // Dynamischer Query-Pfad
        .location(env.getExecutionStepInfo().getField().getSingleField().getSourceLocation()) // GraphQL Location
        .build();
  }

//
//  @KafkaListener(topics = "newAccount",groupId = "omnixys")
//  public void handleNewAccount(AccountDTO accountDTO) {
//    log.info("Handling new account {}", accountDTO);
//    final var accountInput = accountMapper.toAccount(accountDTO);
//    writeService.create(accountInput);
//  }
}
