package com.wyaaung.rbac.controller;

import com.wyaaung.rbac.dto.RoleDto;
import com.wyaaung.rbac.dto.RolePermissionsDto;
import com.wyaaung.rbac.dto.RoleUsersDto;
import com.wyaaung.rbac.service.RoleService;
import com.wyaaung.rbac.transformer.RoleTransformer;
import com.wyaaung.rbac.transformer.RoleUsersTransformer;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/role")
public class RoleController {
  private final RoleService roleService;

  public RoleController(RoleService roleService) {
    this.roleService = roleService;
  }

  @GetMapping
  @ResponseStatus(OK)
  public List<RoleDto> getAllRoles() {
    return roleService.getRoles().stream().map(RoleTransformer::toDto).toList();
  }

  @GetMapping("/{roleName}")
  @ResponseStatus(OK)
  public RoleUsersDto getRoleUsers(@PathVariable("roleName") final String roleName) {
    return RoleUsersTransformer.toDto(roleService.getUsersWithRole(roleName));
  }

  @GetMapping("/{roleName}/permissions")
  @ResponseStatus(OK)
  public RolePermissionsDto getPermissionsOfRole(@PathVariable("roleName") final String roleName) {
    return new RolePermissionsDto(roleName, roleService.getPermissionsOfRole(roleName));
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public void createRole(@RequestBody final RoleDto roleDto) {
    roleService.createRole(RoleTransformer.toDomain(roleDto));
  }

  @DeleteMapping("/{roleName}")
  @ResponseStatus(OK)
  public void deleteRole(@PathVariable("roleName") final String roleName) {
    roleService.deleteRole(roleName);
  }

  @PutMapping("/{roleName}/permission/{permissionName}")
  @ResponseStatus(OK)
  public RolePermissionsDto addPermissionToRole(@PathVariable("roleName") final String roleName,
                                                @PathVariable("permissionName") final String permissionName) {
    return new RolePermissionsDto(roleName, roleService.addPermissionToRole(roleName, permissionName));
  }

  @DeleteMapping("/{roleName}/permission/{permissionName}")
  @ResponseStatus(OK)
  public RolePermissionsDto deletePermissionToRole(@PathVariable("roleName") final String roleName,
                                                   @PathVariable("permissionName") final String permissionName) {
    return new RolePermissionsDto(roleName, roleService.deletePermissionToRole(roleName, permissionName));
  }
}
