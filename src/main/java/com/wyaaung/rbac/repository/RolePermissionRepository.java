package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Permission;
import java.util.ArrayList;
import java.util.List;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class RolePermissionRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public RolePermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public List<Permission> getPermissionsOfRole(final String roleName) {
    final String sql = """
      SELECT
        p.name AS permission_name,
        p.description AS permission_description,
        p.display_name AS permission_display_name
      FROM
        role_permission rp
      JOIN
        permission p ON rp.permission_name = p.name
      WHERE
        rp.role_name = :role_name;
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("role_name", roleName);

    return namedParameterJdbcTemplate.query(sql, paramSource, (resultSet -> {
      List<Permission> permissions = new ArrayList<>();
      while (resultSet.next()) {
        permissions.add(new Permission(
          resultSet.getString("permission_name"),
          resultSet.getString("permission_description"),
          resultSet.getString("permission_display_name")
        ));
      }
      return permissions;
    }));
  }

  public void addPermissionToRole(final String roleName, final String permissionName) {
    final String sql = """
      INSERT INTO role_permission (role_name, permission_name)
      VALUES
      (:role_name, :permission_name)
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("role_name", roleName)
      .addValue("permission_name", permissionName);

    namedParameterJdbcTemplate.update(sql, paramSource);
  }
}
