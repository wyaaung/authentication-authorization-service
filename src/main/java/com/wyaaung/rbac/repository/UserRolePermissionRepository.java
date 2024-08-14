package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.repository.extractor.PermissionDetailsExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserRolePermissionRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final PermissionDetailsExtractor permissionDetailsExtractor;

  public UserRolePermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                      PermissionDetailsExtractor permissionDetailsExtractor) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.permissionDetailsExtractor = permissionDetailsExtractor;
  }

  public PermissionDetails getRolesAndUsersWithPermission(final Permission permission) {
    final String sql = """
      SELECT
          r.name AS role_name,
          r.description AS role_description,
          r.display_name AS role_display_name,
          u.username,
          u.full_name,
          u.email_address
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

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("permission_name", permission.name());

    return namedParameterJdbcTemplate.query(sql, parameters, permissionDetailsExtractor);
  }
}
