package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.repository.extractor.RolesAndUsersByPermissionExtractor;
import java.util.HashSet;
import java.util.Set;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserRolePermissionRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final RolesAndUsersByPermissionExtractor rolesAndUsersByPermissionExtractor;

  public UserRolePermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                      RolesAndUsersByPermissionExtractor rolesAndUsersByPermissionExtractor) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.rolesAndUsersByPermissionExtractor = rolesAndUsersByPermissionExtractor;
  }

  public Permission getRolesAndUsersByPermission(final Permission permission) {
    final String sql = """
      SELECT
          r.name AS role_name,
          u.username
      FROM
          permission p
      JOIN
          role_permission rp ON p.name = rp.permission_name
      JOIN
          role r ON rp.role_name = r.name
      JOIN
          user_role ur ON r.name = ur.role_name
      JOIN
          user_account u ON ur.username = u.username
      WHERE
          p.name = :permission_name;
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("permission_name", permission.getName());

    return namedParameterJdbcTemplate.query(sql, parameters, rolesAndUsersByPermissionExtractor);
  }

  public User getRolesAndPermissionsByUser(final User user) {
    final String sql = """
      SELECT
          r.name AS role_name,
          p.name AS permission_name
      FROM
          permission p
      JOIN
          role_permission rp ON p.name = rp.permission_name
      JOIN
          role r ON rp.role_name = r.name
      JOIN
          user_role ur ON r.name = ur.role_name
      JOIN
          user_account u ON ur.username = u.username
      WHERE
          u.username = :username;
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("username", user.getUsername());

    return namedParameterJdbcTemplate.query(sql, parameters, resultSet -> {
      Set<String> roles = new HashSet<>();
      Set<String> permissions = new HashSet<>();

      while (resultSet.next()) {
        final String roleName = resultSet.getString("role_name");
        final String permissionName = resultSet.getString("permission_name");

        roles.add(roleName);
        permissions.add(permissionName);
      }

      user.setRoles(roles.stream().toList());
      user.setPermissions(permissions.stream().toList());


      return user;
    });

  }
}
