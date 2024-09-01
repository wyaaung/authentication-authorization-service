package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record PermissionDetailsDto(
  @JsonProperty("roles") List<String> roles,
  @JsonProperty("users") List<String> users
) {
}
