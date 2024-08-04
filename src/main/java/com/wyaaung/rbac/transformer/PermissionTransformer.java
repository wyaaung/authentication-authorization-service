package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.dto.PermissionDto;

public class PermissionTransformer {
  public static PermissionDto toDto(Permission permission) {
    return new PermissionDto(permission.name(), permission.description(), permission.displayName());
  }

  public static Permission toDomain(PermissionDto permissionDto) {
    return new Permission(permissionDto.name(), permissionDto.description(), permissionDto.displayName());
  }
}
