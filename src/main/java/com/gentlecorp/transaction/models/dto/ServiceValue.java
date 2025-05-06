package com.gentlecorp.transaction.models.dto;

public record ServiceValue(
    String schema,
    String host,
    int port
) {
}
