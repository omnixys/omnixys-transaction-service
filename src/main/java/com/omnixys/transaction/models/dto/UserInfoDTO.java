package com.omnixys.transaction.models.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserInfoDTO(
  @JsonProperty("sub")
  String sub
) {
}

