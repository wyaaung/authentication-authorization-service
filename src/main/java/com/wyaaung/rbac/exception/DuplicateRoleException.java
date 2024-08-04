package com.wyaaung.rbac.exception;

public class DuplicateRoleException extends RuntimeException {
  public DuplicateRoleException(final String message) {
    super(message);
  }
}
