package com.wyaaung.rbac.exception;

public class PermissionNotFoundException extends RuntimeException {
  public PermissionNotFoundException(final String message) {
    super(message);
  }
}
