package com.wyaaung.rbac.controller;

import com.wyaaung.rbac.dto.RoleDto;
import com.wyaaung.rbac.service.RoleService;
import com.wyaaung.rbac.transformer.RoleTransformer;
import com.wyaaung.rbac.validator.RoleValidator;
import java.util.List;
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
@RequestMapping("/api/v1/roles")
public class RoleController {
  private final RoleService roleService;
  private final RoleValidator roleValidator;

  public RoleController(RoleService roleService, RoleValidator roleValidator) {
    this.roleService = roleService;
    this.roleValidator = roleValidator;
  }

  @GetMapping
  @ResponseStatus(OK)
  public List<RoleDto> getAllPermissions() {
    return roleService.getRoles().stream().map(RoleTransformer::toDto).toList();
  }

  @PostMapping("/{roleName}")
  @ResponseStatus(CREATED)
  public void createRole(@PathVariable final String roleName,
                         @RequestBody final RoleDto roleDto) {
    roleValidator.validateCreateRole(roleName, roleDto);
    roleService.createRole(RoleTransformer.toDomain(roleDto));
  }
}
