package com.wyaaung.rbac.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record RolePermissionsDto(
  @JsonProperty("role_name") String roleName,
  @JsonProperty("permissions") List<String> permissions
) {
}
