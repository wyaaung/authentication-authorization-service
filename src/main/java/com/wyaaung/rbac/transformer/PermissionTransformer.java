package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.dto.PermissionDetailsDto;
import com.wyaaung.rbac.dto.PermissionDto;

public class PermissionTransformer {
  public static PermissionDto toDto(Permission permission) {
    return new PermissionDto(
      permission.getName(), permission.getDescription(), permission.getDisplayName());
  }

  public static Permission toDomain(PermissionDto permissionDto) {
    return new Permission(
      permissionDto.name(), permissionDto.description(), permissionDto.displayName());
  }

  public static PermissionDetailsDto toPermissionDetailsDto(Permission permission) {
    return new PermissionDetailsDto(permission.getRoles(), permission.getUsers());
  }
}
