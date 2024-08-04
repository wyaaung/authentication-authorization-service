package com.wyaaung.rbac.exception;

public class DuplicatePermissionException extends RuntimeException {
  public DuplicatePermissionException(final String message) {
    super(message);
  }
}
