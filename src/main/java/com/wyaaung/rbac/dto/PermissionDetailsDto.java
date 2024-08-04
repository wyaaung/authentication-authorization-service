package com.wyaaung.rbac.dto;

import java.util.Set;

public record PermissionDetailsDto(Set<RoleDto> roles, Set<UserDto> users) {
}
