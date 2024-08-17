package com.wyaaung.rbac.exception;

public class PermissionNotInRoleException extends RuntimeException {
  public PermissionNotInRoleException(final String message) {
    super(message);
  }
}