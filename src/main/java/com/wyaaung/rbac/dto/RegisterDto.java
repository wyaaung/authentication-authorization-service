package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RegisterDto(
  @JsonProperty("username") String username,
  @JsonProperty("full_name") String fullName,
  @JsonProperty("password") String password,
  @JsonProperty("email_address") String emailAddress
) {
}
