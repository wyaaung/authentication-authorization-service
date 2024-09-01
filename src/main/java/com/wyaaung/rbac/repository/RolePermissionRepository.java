package com.wyaaung.rbac.repository;

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

  public List<String> getPermissionsOfRole(final String roleName) {
    final String sql = """
      SELECT
        p.name AS permission_name
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
      List<String> permissions = new ArrayList<>();
      while (resultSet.next()) {
        permissions.add(resultSet.getString("permission_name"));
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

  public void deletePermissionToRole(final String roleName, final String permissionName) {
    final String sql = """
      DELETE FROM role_permission
      WHERE role_name = :role_name AND permission_name = :permission_name
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("role_name", roleName)
      .addValue("permission_name", permissionName);

    namedParameterJdbcTemplate.update(sql, paramSource);
  }
}
