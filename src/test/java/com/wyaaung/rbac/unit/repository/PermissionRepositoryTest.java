package com.wyaaung.rbac.unit.repository;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.repository.PermissionRepository;
import com.wyaaung.rbac.repository.mapper.PermissionRowMapper;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@Import({PermissionRepository.class, PermissionRowMapper.class})
@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PermissionRepositoryTest {
  @Autowired
  private PermissionRepository permissionRepository;

  @Autowired
  private DataSource dataSource;

  @BeforeAll
  public void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
  }

  @Test
  public void testPermissionsExist() {
    assertTrue(permissionRepository.permissionExists("read"));
    assertTrue(permissionRepository.permissionExists("write"));
    assertTrue(permissionRepository.permissionExists("delete"));
    assertFalse(permissionRepository.permissionExists("analysis"));
  }

  @Test
  public void testGetPermissions() {
    List<Permission> permissions = permissionRepository.getPermissions();
    assertEquals(3, permissions.size());
  }

  @Test
  public void testGetPermission() {
    Optional<Permission> permission = permissionRepository.getPermission("read");
    assertTrue(permission.isPresent());
    assertTrue(permission.get().name().equals("read"));
  }

  @Test
  public void testGetEmptyPermission() {
    Optional<Permission> permission = permissionRepository.getPermission("NOT_EXIST_AT_ALL");
    assertTrue(permission.isEmpty());
  }

  @Test
  public void testCreatePermission() {
    permissionRepository.createPermission(
      new Permission("test_permission", "test_permission_description", "Test Permission")
    );
    List<Permission> permissions = permissionRepository.getPermissions();
    Optional<Permission> permission = permissionRepository.getPermission("test_permission");
    assertTrue(permission.get().name().equals("test_permission"));
    assertEquals(4, permissions.size());
  }

  @Test
  public void testDeletePermission() {
    permissionRepository.deletePermission("test_permission");
    List<Permission> permissions = permissionRepository.getPermissions();
    Optional<Permission> permission = permissionRepository.getPermission("test_permission");
    assertTrue(permission.isEmpty());
    assertEquals(3, permissions.size());
  }

  @Test
  public void testPermissionAssigneRoles() {
    Set<String> roles = permissionRepository.permissionAssigneRoles("read");
    assertEquals(3, roles.size());
    assertTrue(roles.stream().anyMatch(role -> role.equals("user")));
    assertTrue(roles.stream().anyMatch(role -> role.equals("manager")));
    assertTrue(roles.stream().anyMatch(role -> role.equals("administrator")));
  }
}
