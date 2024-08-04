package com.wyaaung.rbac.dto;

import java.util.Set;

public record RoleUsersDto(String roleName, Set<UserDto> users) {
}
