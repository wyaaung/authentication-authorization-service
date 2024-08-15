package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.UserDetails;
import com.wyaaung.rbac.dto.UserDetailsDto;
import java.util.stream.Collectors;

public class UserDetailsTransformer {
  public static UserDetailsDto toDto(UserDetails userDetails) {
    return new UserDetailsDto(
      userDetails.roles().stream().map(RoleTransformer::toDto).collect(Collectors.toSet()),
      userDetails.permissions().stream().map(PermissionTransformer::toDto).collect(Collectors.toSet())
    );
  }
}
