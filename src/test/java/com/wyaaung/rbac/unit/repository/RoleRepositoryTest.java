package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.domain.RoleUsers;
import com.wyaaung.rbac.repository.mapper.RoleRowMapper;
import java.util.List;
import java.util.Optional;
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
@Import({RoleRepository.class, RoleRowMapper.class})
@SpringJUnitConfig
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleRepositoryTest {
  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private DataSource dataSource;

  @BeforeAll
  public void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
  }

  @Test
  public void testRolesExist() {
    assertTrue(roleRepository.roleExists("user"));
    assertTrue(roleRepository.roleExists("manager"));
    assertTrue(roleRepository.roleExists("administrator"));
    assertFalse(roleRepository.roleExists("NOT_EXIST_AT_ALL"));
  }

  @Test
  public void testGetRoles() {
    List<Role> roles = roleRepository.getRoles();
    assertEquals(3, roles.size());
  }

  @Test
  public void testGetRole() {
    Optional<Role> optionalRole = roleRepository.getRole("user");
    assertTrue(optionalRole.isPresent());
    assertTrue(optionalRole.get().name().equals("user"));
  }

  @Test
  public void testGetEmptyRole() {
    Optional<Role> optionalRole = roleRepository.getRole("NOT_EXIST_AT_ALL");
    assertTrue(optionalRole.isEmpty());
  }

  @Test
  public void testCreateRole() {
    roleRepository.createRole(new Role("analyst", "role for analyst", "ANALYST"));
    List<Role> roles = roleRepository.getRoles();
    Optional<Role> optionalRole = roleRepository.getRole("analyst");
    assertTrue(optionalRole.get().name().equals("analyst"));
    assertEquals(4, roles.size());
  }

  @Test
  public void testDeleteRole() {
    roleRepository.deleteRole("analyst");
    List<Role> roles = roleRepository.getRoles();
    Optional<Role> optionalRole = roleRepository.getRole("analyst");
    assertTrue(optionalRole.isEmpty());
    assertEquals(3, roles.size());
  }

  @Test
  public void testGetRoleWithNoUsers() {
    Role role = roleRepository.getRole("user").get();
    RoleUsers roleUsers = roleRepository.getUsersWithRole(role);

    assertTrue(roleUsers.users().size() == 0);
  }

  @Test
  public void testGetRoleWithUsers() {
    Role role = roleRepository.getRole("manager").get();
    RoleUsers roleUsers = roleRepository.getUsersWithRole(role);

    assertTrue(roleUsers.users().size() == 2);
  }
}
