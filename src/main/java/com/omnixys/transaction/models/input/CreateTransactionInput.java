package com.omnixys.transaction.models.input;

import com.omnixys.transaction.models.enums.TransactionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateTransactionInput (
    BigDecimal amount,
    UUID receiver,
    UUID sender,
    TransactionType type
) {
}
