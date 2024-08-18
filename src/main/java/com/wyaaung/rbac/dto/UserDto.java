package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDto(
  @JsonProperty("username") String username,
  @JsonProperty("full_name") String fullName,
  @JsonProperty("email_address") String emailAddress) {
}
