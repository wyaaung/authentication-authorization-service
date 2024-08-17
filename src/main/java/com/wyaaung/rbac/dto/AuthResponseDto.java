package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthResponseDto(
  @JsonProperty("access_token") String accessToken,
  @JsonProperty("refresh_token") String refreshToken) {
}
