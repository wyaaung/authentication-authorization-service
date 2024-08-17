package com.wyaaung.rbac.service;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.exception.DuplicatePermissionException;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.exception.ValidationException;
import com.wyaaung.rbac.repository.PermissionRepository;
import com.wyaaung.rbac.repository.RepositoryTestHelper;
import com.wyaaung.rbac.repository.UserRolePermissionRepository;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PermissionServiceIntegrationTest {

  @Autowired
  private PermissionService permissionService;

  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private UserRolePermissionRepository userRolePermissionRepository;

  @Autowired
  private DataSource dataSource;

  @BeforeAll
  public void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
  }

  @Test
  void testCreatePermissionSuccess() {
    List<Permission> permissions = permissionService.getPermissions();
    assertEquals(3, permissions.size());

    Permission permission = new Permission("test_permission", "test_permission_description", "Test Permission");

    permissionService.createPermission(permission);

    List<Permission> newPermissions = permissionService.getPermissions();
    assertEquals(4, newPermissions.size());

    Optional<Permission> result = permissionRepository.getPermission("test_permission");
    assertTrue(result.isPresent());
    assertEquals("test_permission", result.get().name());
  }

  @Test
  void testCreatePermissionDuplicate() {
    Permission permission = new Permission("read", "Permission to read", "Read");

    assertThrows(DuplicatePermissionException.class, () -> permissionService.createPermission(permission));
  }

  @Test
  void testGetPermissions() {
    Permission permission1 = new Permission("PERMISSION_ONE", "Description One", "Display One");
    Permission permission2 = new Permission("PERMISSION_TWO", "Description Two", "Display Two");

    permissionService.createPermission(permission1);
    permissionService.createPermission(permission2);

    List<Permission> permissions = permissionService.getPermissions();

    assertNotNull(permissions);
    assertEquals(5, permissions.size());
    assertTrue(permissions.stream().anyMatch(permission -> permission.name().equals("PERMISSION_ONE")));
    assertTrue(permissions.stream().anyMatch(permission -> permission.name().equals("PERMISSION_TWO")));
  }


  @Test
  void testGetRolesAndUsersWithPermission() {
    PermissionDetails result = permissionService.getRolesAndUsersWithPermission("read");

    assertNotNull(result);
    assertTrue(result.roles().stream().anyMatch(role -> role.name().equals("manager")));
    assertTrue(result.roles().stream().anyMatch(role -> role.name().equals("administrator")));
  }

  @Test
  void testGetRolesAndUsersWithNonExistingPermission() {
    assertThrows(PermissionNotFoundException.class,
      () -> permissionService.getRolesAndUsersWithPermission("NOT_EXIST_AT_ALL"));
  }

  @Test
  void testDeletePermissionSuccess() {
    Permission permission = new Permission("test_permission", "test_permission_description", "Test Permission");
    permissionService.createPermission(permission);

    permissionService.deletePermission("test_permission");

    Optional<Permission> result = permissionRepository.getPermission("test_permission");
    assertTrue(result.isEmpty());
    assertThrows(PermissionNotFoundException.class,
      () -> permissionService.getRolesAndUsersWithPermission("DELETE_PERMISSION"));
  }

  @Test
  void testDeleteNonExistingPermission() {
    assertThrows(PermissionNotFoundException.class, () -> permissionService.deletePermission("NOT_EXIST_AT_ALL"));
  }

  @Test
  void testDeletePermissionAssignedToRoles() {
    assertThrows(ValidationException.class, () -> permissionService.deletePermission("read"));
  }
}
