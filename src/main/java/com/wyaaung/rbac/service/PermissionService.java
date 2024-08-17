package com.wyaaung.rbac.service;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.exception.DuplicatePermissionException;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.exception.ValidationException;
import com.wyaaung.rbac.repository.PermissionRepository;
import com.wyaaung.rbac.repository.UserRolePermissionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class PermissionService {
  private final PermissionRepository permissionRepository;
  private final UserRolePermissionRepository userRolePermissionRepository;

  public PermissionService(PermissionRepository permissionRepository, UserRolePermissionRepository userRolePermissionRepository) {
    this.permissionRepository = permissionRepository;
    this.userRolePermissionRepository = userRolePermissionRepository;
  }

  public List<Permission> getPermissions() {
    return permissionRepository.getPermissions();
  }

  public PermissionDetails getRolesAndUsersWithPermission(final String permissionName) {
    Permission permission = getPermission(permissionName);
    return userRolePermissionRepository.getRolesAndUsersByPermission(permission);
  }

  public void createPermission(final Permission permission) {
    if (permissionExists(permission.name())) {
      throw new DuplicatePermissionException(String.format("Permission '%s' already exists", permission.name()));
    }

    permissionRepository.createPermission(permission);
  }

  public void deletePermission(final String permissionName) {
    if (!permissionExists(permissionName)) {
      throw new PermissionNotFoundException(String.format("Permission '%s' does not exist", permissionName));
    }

    Set<String> permissionAssignedRoles = permissionAssigneRoles(permissionName);
    if (!permissionAssignedRoles.isEmpty()) {
      throw new ValidationException(String.format("Permission '%s' has been assigned to certain roles: %s", permissionName,
        permissionAssignedRoles));
    }

    permissionRepository.deletePermission(permissionName);
  }

  public Permission getPermission(final String permissionName) {
    final Optional<Permission> optionalPermission = permissionRepository.getPermission(permissionName);

    if (!optionalPermission.isPresent()) {
      throw new PermissionNotFoundException(String.format("Permission '%s' does not exist", permissionName));
    }

    return optionalPermission.get();
  }

  private boolean permissionExists(final String permissionName) {
    return permissionRepository.permissionExists(permissionName);
  }

  private Set<String> permissionAssigneRoles(final String permissionName) {
    return permissionRepository.permissionAssigneRoles(permissionName);
  }
}
