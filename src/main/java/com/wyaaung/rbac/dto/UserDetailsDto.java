package com.wyaaung.rbac.dto;

import java.util.Set;

public record UserDetailsDto(Set<RoleDto> roles, Set<PermissionDto> permissions) {
}
