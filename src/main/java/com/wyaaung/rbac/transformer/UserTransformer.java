package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.UserDto;

public class UserTransformer {
  public static UserDto toDto(User user) {
    return new UserDto(user.username(), user.fullName(), user.emailAddress());
  }
}
