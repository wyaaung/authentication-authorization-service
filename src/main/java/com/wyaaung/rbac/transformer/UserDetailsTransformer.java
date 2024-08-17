package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.domain.UserDetails;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.dto.UserDetailsDto;
import java.util.stream.Collectors;

public class UserDetailsTransformer {
  public static UserDetailsDto toDto(UserDetails userDetails) {
    return new UserDetailsDto(
      userDetails.roles().stream().map(RoleTransformer::toDto).collect(Collectors.toSet()),
      userDetails.permissions().stream().map(PermissionTransformer::toDto).collect(Collectors.toSet())
    );
  }

  public static User toUserFromRegisterDto(RegisterDto registerDto) {
    return new User(registerDto.username(), registerDto.fullName(), registerDto.password(), registerDto.emailAddress());
  }
}
