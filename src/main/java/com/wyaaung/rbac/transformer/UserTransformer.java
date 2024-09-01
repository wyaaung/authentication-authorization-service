package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.dto.UserDetailsDto;
import com.wyaaung.rbac.dto.UserDto;

public class UserTransformer {
  public static UserDto toDto(User user) {
    return new UserDto(user.getUsername(), user.getFullName(), user.getEmailAddress());
  }

  public static UserDetailsDto toUserDetailsDto(User user) {
    return new UserDetailsDto(
      user.getUsername(),
      user.getFullName(),
      user.getEmailAddress(),
      user.getRoles(),
      user.getPermissions());
  }

  public static User toUserFromRegisterDto(RegisterDto registerDto) {
    return new User(
      registerDto.username(),
      registerDto.fullName(),
      registerDto.password(),
      registerDto.emailAddress(),
      null,
      null);
  }
}
