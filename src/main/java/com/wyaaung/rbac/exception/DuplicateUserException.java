package com.wyaaung.rbac.exception;

public class DuplicateUserException extends RuntimeException {
  public DuplicateUserException(final String message) {
    super(message);
  }
}
