package com.wyaaung.rbac.validator;

import com.wyaaung.rbac.dto.RoleDto;
import com.wyaaung.rbac.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class RoleValidator {
  public void validateCreateRole(final String name, final RoleDto roleDto) {
    if (!name.equals(roleDto.name())) {
      throw new ValidationException("Role name in the path does not match the name in the payload");
    }
  }
}