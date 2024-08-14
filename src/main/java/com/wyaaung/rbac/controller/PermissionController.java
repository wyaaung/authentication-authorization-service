package com.wyaaung.rbac.controller;

import com.wyaaung.rbac.dto.PermissionDetailsDto;
import com.wyaaung.rbac.dto.PermissionDto;
import com.wyaaung.rbac.service.PermissionService;
import com.wyaaung.rbac.transformer.PermissionDetailsTransformer;
import com.wyaaung.rbac.transformer.PermissionTransformer;
import com.wyaaung.rbac.validator.PermissionValidator;
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
@RequestMapping("/api/v1/permission")
public class PermissionController {
  private final PermissionService permissionService;
  private final PermissionValidator permissionValidator;

  public PermissionController(PermissionService permissionService, PermissionValidator permissionValidator) {
    this.permissionService = permissionService;
    this.permissionValidator = permissionValidator;
  }

  @GetMapping
  @ResponseStatus(OK)
  public List<PermissionDto> getAllPermissions() {
    return permissionService.getPermissions().stream().map(PermissionTransformer::toDto).toList();
  }

  @GetMapping("/{permissionName}")
  @ResponseStatus(OK)
  public PermissionDetailsDto getPermission(@PathVariable("permissionName") final String permissionName) {
    return PermissionDetailsTransformer.toDto(permissionService.getRolesAndUsersWithPermission(permissionName));
  }

  @PostMapping("/{permissionName}")
  @ResponseStatus(CREATED)
  public void createPermission(@PathVariable("permissionName") final String permissionName,
                               @RequestBody final PermissionDto permissionDto) {
    permissionValidator.validateCreatePermission(permissionName, permissionDto);
    permissionService.createPermission(PermissionTransformer.toDomain(permissionDto));
  }

  @DeleteMapping("/{permissionName}")
  @ResponseStatus(OK)
  public void deletePermission(@PathVariable("permissionName") final String permissionName) {
    permissionService.deletePermission(permissionName);
  }
}
