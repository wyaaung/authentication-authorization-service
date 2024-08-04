package com.wyaaung.rbac.controller;

import com.wyaaung.rbac.dto.ExceptionDto;
import com.wyaaung.rbac.exception.DuplicatePermissionException;
import com.wyaaung.rbac.exception.DuplicateRoleException;
import com.wyaaung.rbac.exception.PermissionAssignedToRoles;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.exception.RoleNotFoundException;
import com.wyaaung.rbac.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@RestController
public class ControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  @ExceptionHandler(ValidationException.class)
  @ResponseStatus(BAD_REQUEST)
  public final ExceptionDto handleValidationException(final ValidationException validationException) {
    LOGGER.error(validationException.getMessage(), validationException);
    return new ExceptionDto(BAD_REQUEST.value(), BAD_REQUEST.name(), validationException.getMessage());
  }

  @ExceptionHandler(PermissionNotFoundException.class)
  @ResponseStatus(NOT_FOUND)
  public final ExceptionDto handlePermissionNotFoundException(final PermissionNotFoundException permissionNotFoundException) {
    LOGGER.warn(permissionNotFoundException.getMessage(), permissionNotFoundException);
    return new ExceptionDto(NOT_FOUND.value(), NOT_FOUND.name(), permissionNotFoundException.getMessage());
  }

  @ExceptionHandler(DuplicatePermissionException.class)
  @ResponseStatus(CONFLICT)
  public final ExceptionDto handlePermissionAlreadyExistException(final DuplicatePermissionException duplicatePermissionException) {
    LOGGER.error(duplicatePermissionException.getMessage(), duplicatePermissionException);
    return new ExceptionDto(CONFLICT.value(), CONFLICT.name(), duplicatePermissionException.getMessage());
  }

  @ExceptionHandler(PermissionAssignedToRoles.class)
  @ResponseStatus(BAD_REQUEST)
  public final ExceptionDto handlePermissionAssignedRolesException(final PermissionAssignedToRoles permissionAssignedToRoles) {
    LOGGER.error(permissionAssignedToRoles.getMessage(), permissionAssignedToRoles);
    return new ExceptionDto(BAD_REQUEST.value(), BAD_REQUEST.name(), permissionAssignedToRoles.getMessage());
  }

  @ExceptionHandler(RoleNotFoundException.class)
  @ResponseStatus(NOT_FOUND)
  public final ExceptionDto handleRuleNotFoundException(final RoleNotFoundException roleNotFoundException) {
    LOGGER.warn(roleNotFoundException.getMessage(), roleNotFoundException);
    return new ExceptionDto(NOT_FOUND.value(), NOT_FOUND.name(), roleNotFoundException.getMessage());
  }

  @ExceptionHandler(DuplicateRoleException.class)
  @ResponseStatus(CONFLICT)
  public final ExceptionDto handleDuplicateRoleException(final DuplicateRoleException duplicateRoleException) {
    LOGGER.error(duplicateRoleException.getMessage(), duplicateRoleException);
    return new ExceptionDto(CONFLICT.value(), CONFLICT.name(), duplicateRoleException.getMessage());
  }
}
