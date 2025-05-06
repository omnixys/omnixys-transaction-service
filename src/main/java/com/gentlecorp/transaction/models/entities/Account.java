package com.gentlecorp.transaction.models.entities;

import java.math.BigDecimal;
import java.util.UUID;

public record  Account(
    UUID id,
  String username,
  BigDecimal balance
) {
}
