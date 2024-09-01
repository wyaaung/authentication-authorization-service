package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.dto.RoleUsersDto;

public class RoleUsersTransformer {
  public static RoleUsersDto toDto(Role role) {
    return new RoleUsersDto(role.getName(), role.getUsers());
  }
}
