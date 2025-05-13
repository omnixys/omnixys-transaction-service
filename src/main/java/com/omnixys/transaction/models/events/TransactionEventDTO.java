package com.omnixys.transaction.models.events;

import com.omnixys.transaction.models.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record TransactionEventDTO(
    TransactionType type,
    BigDecimal amount,
    UUID sender,
    UUID receiver,
    LocalDateTime created,
    UUID paymentId
) {
}
