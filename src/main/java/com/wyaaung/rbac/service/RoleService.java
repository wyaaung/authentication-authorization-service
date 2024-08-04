package com.wyaaung.rbac.service;

import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.domain.RoleUsers;
import com.wyaaung.rbac.exception.DuplicateRoleException;
import com.wyaaung.rbac.exception.RoleNotFoundException;
import com.wyaaung.rbac.repository.RoleRepository;
import com.wyaaung.rbac.repository.UserRolePermissionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

  private final RoleRepository roleRepository;
  private final UserRolePermissionRepository userRolePermissionRepository;

  public RoleService(RoleRepository roleRepository, UserRolePermissionRepository userRolePermissionRepository) {
    this.roleRepository = roleRepository;
    this.userRolePermissionRepository = userRolePermissionRepository;
  }

  public List<Role> getRoles() {
    return roleRepository.getRoles();
  }

  public RoleUsers getUsersWithRole(final String roleName) {
    Role role = getRole(roleName);
    return userRolePermissionRepository.getUsersWithRole(role);
  }

  public void createRole(final Role role) {
    if (roleExists(role.name())) {
      throw new DuplicateRoleException(String.format("Role '%s' already exists", role.name()));
    }
    roleRepository.createRole(role);
  }

  public void deleteRole(final String roleName) {
    roleRepository.deleteRole(roleName);
  }

  private Role getRole(final String roleName) {
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
