package com.wyaaung.rbac.exception;

public class PermissionAssignedToRoles extends RuntimeException {
  public PermissionAssignedToRoles(final String message) {
    super(message);
  }
}