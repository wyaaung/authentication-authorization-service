package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PermissionDto(
  @JsonProperty("name") String name,
  @JsonProperty("description") String description,
  @JsonProperty("display_name") String displayName
) {
}
