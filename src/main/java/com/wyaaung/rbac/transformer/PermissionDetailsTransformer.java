package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.dto.PermissionDetailsDto;
import java.util.stream.Collectors;

public class PermissionDetailsTransformer {
  public static PermissionDetailsDto toDto(PermissionDetails permissionDetails) {
    return new PermissionDetailsDto(
      permissionDetails.roles().stream().map(RoleTransformer::toDto).collect(Collectors.toSet()),
      permissionDetails.users().stream().map(UserTransformer::toDto).collect(Collectors.toSet())
    );
  }
}
