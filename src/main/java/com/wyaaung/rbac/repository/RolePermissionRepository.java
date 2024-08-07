package com.wyaaung.rbac.repository;

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
