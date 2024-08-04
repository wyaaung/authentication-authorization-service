package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.RoleUsers;
import com.wyaaung.rbac.dto.RoleUsersDto;
import java.util.stream.Collectors;

public class RoleUsersTransformer {
  public static RoleUsersDto toDto(RoleUsers roleUsers) {
    return new RoleUsersDto(
      roleUsers.roleName(),
      roleUsers.users().stream().map(UserTransformer::toDto).collect(Collectors.toSet())
    );
  }
}
