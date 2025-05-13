package com.omnixys.transaction.exceptions;

import lombok.Getter;

import java.util.UUID;

@Getter
public class InvalidTransactionException extends RuntimeException {
  private final UUID id;

  public InvalidTransactionException(final UUID id) {

    super(String.format("Invalid transaction AccountId: %s cant be the sender and the receiver", id));
    this.id = id;
  }
}
