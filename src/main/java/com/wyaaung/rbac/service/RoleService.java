package com.wyaaung.rbac.service;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.domain.RoleDetails;
import com.wyaaung.rbac.domain.RoleUsers;
import com.wyaaung.rbac.exception.DuplicateRoleException;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.exception.PermissionNotInRoleException;
import com.wyaaung.rbac.exception.RoleNotFoundException;
import com.wyaaung.rbac.exception.ValidationException;
import com.wyaaung.rbac.repository.PermissionRepository;
import com.wyaaung.rbac.repository.RolePermissionRepository;
import com.wyaaung.rbac.repository.RoleRepository;
import com.wyaaung.rbac.repository.UserRolePermissionRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  private final RoleRepository roleRepository;
  private final UserRolePermissionRepository userRolePermissionRepository;
  private final PermissionRepository permissionRepository;
  private final RolePermissionRepository rolePermissionRepository;

  public RoleService(RoleRepository roleRepository, UserRolePermissionRepository userRolePermissionRepository, PermissionRepository permissionRepository, RolePermissionRepository rolePermissionRepository) {
    this.roleRepository = roleRepository;
    this.userRolePermissionRepository = userRolePermissionRepository;
    this.permissionRepository = permissionRepository;
    this.rolePermissionRepository = rolePermissionRepository;
  }

  public List<Role> getRoles() {
    return roleRepository.getRoles();
  }

  public RoleUsers getUsersWithRole(final String roleName) {
    Role role = getRole(roleName);
    return roleRepository.getUsersWithRole(role);
  }

  public List<Permission> getPermissionsOfRole(final String roleName) {
    if (!roleExists(roleName)) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    return rolePermissionRepository.getPermissionsOfRole(roleName);
  }

  public void createRole(final Role role) {
    if (roleExists(role.name())) {
      throw new DuplicateRoleException(String.format("Role '%s' already exists", role.name()));
    }
    roleRepository.createRole(role);
  }

  public void deleteRole(final String roleName) {
    if (!roleExists(roleName)) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    final Set<String> usersWithRole = getUsersWithRole(roleName).users().stream().map((user) -> user.getUsername()).collect(Collectors.toSet());
    if (!usersWithRole.isEmpty()) {
      throw new ValidationException("Role is assigned to users: %s".formatted(String.join(", ", usersWithRole)));
    }

    roleRepository.deleteRole(roleName);
  }

  public RoleDetails addPermissionToRole(final String roleName, final String permissionName) {
    Role role = getRole(roleName);

    if (!permissionRepository.permissionExists(permissionName)) {
      throw new PermissionNotFoundException(String.format("Permission '%s' does not exist", permissionName));
    }

    rolePermissionRepository.addPermissionToRole(roleName, permissionName);

    return new RoleDetails(rolePermissionRepository.getPermissionsOfRole(roleName).stream().collect(Collectors.toSet()), roleRepository.getUsersWithRole(role).users());
  }

  public RoleDetails deletePermissionToRole(final String roleName, final String permissionName) {
    Role role = getRole(roleName);

    boolean permissionExists = rolePermissionRepository.getPermissionsOfRole(roleName).stream().anyMatch(permission -> permission.name().equals(permissionName));

    if (!permissionExists) {
      throw new PermissionNotInRoleException(String.format("Permission '%s' does not exist in Role %s", permissionName, roleName));
    }

    rolePermissionRepository.deletePermissionToRole(roleName, permissionName);

    return new RoleDetails(rolePermissionRepository.getPermissionsOfRole(roleName).stream().collect(Collectors.toSet()), roleRepository.getUsersWithRole(role).users());
  }

  public Role getRole(final String roleName) {
    final Optional<Role> optionalRole = roleRepository.getRole(roleName);

    if (!optionalRole.isPresent()) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    return optionalRole.get();
  }

  private boolean roleExists(final String roleName) {
    return roleRepository.roleExists(roleName);
  }
}
