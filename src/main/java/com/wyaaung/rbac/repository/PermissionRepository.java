package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.repository.mapper.PermissionRowMapper;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class PermissionRepository {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
  private final PermissionRowMapper permissionRowMapper;

  public PermissionRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, PermissionRowMapper permissionRowMapper) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    this.permissionRowMapper = permissionRowMapper;
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

    final SqlParameterSource paramSource = new MapSqlParameterSource().addValue("permission_name", permissionName);

    return !namedParameterJdbcTemplate.queryForList(sql, paramSource, Integer.class).isEmpty();
  }

  public Set<String> permissionAssigneRoles(final String permissionName) {
    final String sql = """
        SELECT role_name, permission_name
        FROM role_permission
        WHERE permission_name = :permission_name
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("permission_name", permissionName);

    return namedParameterJdbcTemplate.query(sql, paramSource, (resultSet) -> {
      Set<String> roles = new HashSet<>();

      while (resultSet.next()) {
        roles.add(resultSet.getString("role_name"));
      }

      return roles;
    });
  }

  public List<Permission> getPermissions() {
    final String sql = """
      SELECT name, description, display_name
      FROM permission
      """;


    return namedParameterJdbcTemplate.query(sql, resultSet -> {
      final List<Permission> result = new ArrayList<>();

      while (resultSet.next()) {
        result.add(
          new Permission(
            resultSet.getString("name"),
            resultSet.getString("description"),
            resultSet.getString("display_name")
          )
        );
      }
      return result;
    });
  }

  public Optional<Permission> getPermission(final String permissionName) {
    final String sql = """
        SELECT name, description, display_name
        FROM permission
        WHERE name = :permission_name
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("permission_name", permissionName);

    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, permissionRowMapper));
    } catch (EmptyResultDataAccessException ignored) {
      return Optional.empty();
    }
  }

  public void createPermission(final Permission permission) {
    final String sql = """
        INSERT INTO permission (name, description, display_name)
        VALUES
        (:name, :description, :display_name)
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("name", permission.name())
      .addValue("description", permission.description())
      .addValue("display_name", permission.displayName());

    namedParameterJdbcTemplate.update(sql, paramSource);
  }

  public void deletePermission(final String permissionName) {
    final String sql = """
        DELETE FROM permission
        WHERE name = :permission_name
      """;
    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("permission_name", permissionName);

    namedParameterJdbcTemplate.update(sql, paramSource);
  }
}
