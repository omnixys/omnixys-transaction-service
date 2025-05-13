package com.omnixys.transaction.models.dto;

public record ServiceValue(
    String schema,
    String host,
    int port
) {
}
