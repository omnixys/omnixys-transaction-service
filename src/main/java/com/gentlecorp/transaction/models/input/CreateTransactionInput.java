package com.gentlecorp.transaction.models.input;

import com.gentlecorp.transaction.models.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionInput (
    BigDecimal amount,
    UUID receiver,
    UUID sender,
    TransactionType type
) {
}
