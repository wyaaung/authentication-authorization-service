package com.wyaaung.rbac.controller;

import com.wyaaung.rbac.dto.PermissionDetailsDto;
import com.wyaaung.rbac.dto.PermissionDto;
import com.wyaaung.rbac.service.PermissionService;
import com.wyaaung.rbac.transformer.PermissionTransformer;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionController {
  private final PermissionService permissionService;

  public PermissionController(PermissionService permissionService) {
    this.permissionService = permissionService;
  }

  @GetMapping
  @ResponseStatus(OK)
  public List<PermissionDto> getAllPermissions() {
    return permissionService.getPermissions().stream().map(PermissionTransformer::toDto).toList();
  }

  @GetMapping("/{permissionName}")
  @ResponseStatus(OK)
  public PermissionDetailsDto getPermission(@PathVariable("permissionName") final String permissionName) {
    return PermissionTransformer.toPermissionDetailsDto(permissionService.getRolesAndUsersWithPermission(permissionName));
  }

  @PostMapping
  @ResponseStatus(CREATED)
  public void createPermission(@RequestBody final PermissionDto permissionDto) {
    permissionService.createPermission(PermissionTransformer.toDomain(permissionDto));
  }

  @DeleteMapping("/{permissionName}")
  @ResponseStatus(OK)
  public void deletePermission(@PathVariable("permissionName") final String permissionName) {
    permissionService.deletePermission(permissionName);
  }
}
