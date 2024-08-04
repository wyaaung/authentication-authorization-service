package com.wyaaung.rbac.validator;

import com.wyaaung.rbac.dto.PermissionDto;
import com.wyaaung.rbac.exception.ValidationException;
import org.springframework.stereotype.Component;

@Component
public class PermissionValidator {

  public void validateCreatePermission(final String name, final PermissionDto permissionDto) {
    if (!name.equals(permissionDto.name())) {
      throw new ValidationException("Permission name in the path does not match the name in the payload");
    }
  }
}