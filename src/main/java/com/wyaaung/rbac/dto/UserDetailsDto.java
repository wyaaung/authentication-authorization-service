package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UserDetailsDto(
  @JsonProperty("username") String username,
  @JsonProperty("full_name") String fullName,
  @JsonProperty("email_address") String emailAddress,
  @JsonProperty("roles") List<String> roles,
  @JsonProperty("permissions") List<String> permissions
) {
}
