package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.domain.UserDetails;
import com.wyaaung.rbac.repository.extractor.PermissionDetailsExtractor;
import com.wyaaung.rbac.repository.extractor.UserDetailsExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserRolePermissionRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final PermissionDetailsExtractor permissionDetailsExtractor;
  private final UserDetailsExtractor userDetailsExtractor;

  public UserRolePermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                      PermissionDetailsExtractor permissionDetailsExtractor, UserDetailsExtractor userDetailsExtractor) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.permissionDetailsExtractor = permissionDetailsExtractor;
    this.userDetailsExtractor = userDetailsExtractor;
  }

  public PermissionDetails getRolesAndUsersByPermission(final Permission permission) {
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

  public UserDetails getRolesAndPermissionsByUser(final User user) {
    final String sql = """
        SELECT
      r.name AS role_name,
      r.description AS role_description,
      r.display_name AS role_display_name,
      p.name AS permission_name,
      p.description AS permission_description,
      p.display_name AS permission_display_name
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

    return namedParameterJdbcTemplate.query(sql, parameters, userDetailsExtractor);
  }
}
