package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.dto.RoleDto;

public class RoleTransformer {
  public static RoleDto toDto(Role role) {
    return new RoleDto(role.name(), role.description(), role.displayName());
  }

  public static Role toDomain(RoleDto roleDto) {
    return new Role(roleDto.name(), roleDto.description(), roleDto.displayName());
  }
}
