package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record RoleDto(
  @JsonProperty("name") String name,
  @JsonProperty("description") String description,
  @JsonProperty("display_name") String displayName
) {
}
