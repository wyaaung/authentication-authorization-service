package com.wyaaung.rbac.service;

import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.exception.DuplicateRoleException;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.exception.PermissionNotInRoleException;
import com.wyaaung.rbac.exception.RoleNotFoundException;
import com.wyaaung.rbac.exception.ValidationException;
import com.wyaaung.rbac.repository.PermissionRepository;
import com.wyaaung.rbac.repository.RolePermissionRepository;
import com.wyaaung.rbac.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final RolePermissionRepository rolePermissionRepository;

  public RoleService(RoleRepository roleRepository,
                     PermissionRepository permissionRepository,
                     RolePermissionRepository rolePermissionRepository) {
    this.roleRepository = roleRepository;
    this.permissionRepository = permissionRepository;
    this.rolePermissionRepository = rolePermissionRepository;
  }

  @Cacheable(value = "RoleCache")
  public List<Role> getRoles() {
    return roleRepository.getRoles();
  }

  @Cacheable(value = "RoleCache", key = "{#roleName}")
  public Role getUsersWithRole(final String roleName) {
    Role role = getRole(roleName);
    return roleRepository.getUsersWithRole(role);
  }

  @Cacheable(value = "RoleCache", key = "{#roleName}")
  public List<String> getPermissionsOfRole(final String roleName) {
    if (!roleExists(roleName)) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    return rolePermissionRepository.getPermissionsOfRole(roleName);
  }

  public void createRole(final Role role) {
    if (roleExists(role.getName())) {
      throw new DuplicateRoleException(String.format("Role '%s' already exists", role.getName()));
    }
    roleRepository.createRole(role);
  }

  public void deleteRole(final String roleName) {
    if (!roleExists(roleName)) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    final Set<String> usersWithRole = getUsersWithRole(roleName).getUsers().stream().collect(Collectors.toSet());
    if (!usersWithRole.isEmpty()) {
      throw new ValidationException("Role is assigned to users: %s".formatted(String.join(", ", usersWithRole)));
    }

    roleRepository.deleteRole(roleName);
  }

  public List<String> addPermissionToRole(final String roleName, final String permissionName) {
    boolean roleExists = roleExists(roleName);

    if (!roleExists) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    if (!permissionRepository.permissionExists(permissionName)) {
      throw new PermissionNotFoundException(String.format("Permission '%s' does not exist", permissionName));
    }

    rolePermissionRepository.addPermissionToRole(roleName, permissionName);

    return rolePermissionRepository.getPermissionsOfRole(roleName);
  }

  public List<String> deletePermissionToRole(final String roleName, final String permissionName) {
    boolean roleExists = roleExists(roleName);

    if (!roleExists) {
      throw new RoleNotFoundException(String.format("Role '%s' does not exist", roleName));
    }

    boolean permissionExists =
      rolePermissionRepository.getPermissionsOfRole(roleName).stream().anyMatch(permission -> permission.equals(permissionName));

    if (!permissionExists) {
      throw new PermissionNotInRoleException(String.format("Permission '%s' does not exist in Role %s", permissionName, roleName));
    }

    rolePermissionRepository.deletePermissionToRole(roleName, permissionName);

    return rolePermissionRepository.getPermissionsOfRole(roleName);
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
