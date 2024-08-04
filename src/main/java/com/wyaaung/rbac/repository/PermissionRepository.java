package com.wyaaung.rbac.repository;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public PermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public boolean permissionExists(final String permissionName) {
    final String sql = """
      SELECT 1
      WHERE EXISTS (
        SELECT 1
        FROM permission
        WHERE name = :permission_name
      ) 
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("permission_name", permissionName);

    return !namedParameterJdbcTemplate.queryForList(sql, paramSource, Integer.class).isEmpty();
  }
}
