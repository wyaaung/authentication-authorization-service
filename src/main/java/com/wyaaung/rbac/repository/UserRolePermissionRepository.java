package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.domain.RoleDetails;
import com.wyaaung.rbac.repository.extractor.PermissionDetailsExtractor;
import com.wyaaung.rbac.repository.extractor.RoleDetailsExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserRolePermissionRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final PermissionDetailsExtractor permissionDetailsExtractor;
  private final RoleDetailsExtractor roleDetailsExtractor;

  public UserRolePermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                      PermissionDetailsExtractor permissionDetailsExtractor,
                                      RoleDetailsExtractor roleDetailsExtractor) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.permissionDetailsExtractor = permissionDetailsExtractor;
    this.roleDetailsExtractor = roleDetailsExtractor;
  }

  public PermissionDetails getRolesAndUsersWithPermission(Permission permission) {
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

  public RoleDetails getPermissionsAndUsersWithRole(Role role) {
    final String sql = """
      SELECT
          p.name AS permission_name,
          p.description AS permission_description,
          p.display_name AS permission_display_name,
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
          r.name = :role_name;
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("role_name", role.name());

    return namedParameterJdbcTemplate.query(sql, parameters, roleDetailsExtractor);
  }
}
