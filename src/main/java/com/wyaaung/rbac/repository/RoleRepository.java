package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.Role;
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
public class RoleRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public RoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public boolean roleExists(final String roleName) {
    final String sql = """
        SELECT 1
        WHERE EXISTS (
          SELECT 1
          FROM role
          WHERE name = :role_name
        )
      """;

    SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("role_name", roleName);

    return !namedParameterJdbcTemplate.queryForList(sql, paramSource, Integer.class).isEmpty();
  }

  public List<Role> getRoles() {
    final String sql = """
      SELECT name, description, display_name
      FROM role
      """;

    return namedParameterJdbcTemplate.query(sql, resultSet -> {
      final List<Role> result = new ArrayList<>();

      while (resultSet.next()) {
        Role role = new Role();
        role.setName(resultSet.getString("name"));
        role.setDescription(resultSet.getString("description"));
        role.setDisplayName(resultSet.getString("display_name"));

        result.add(role);
      }
      return result;
    });
  }

  public Optional<Role> getRole(final String roleName) {
    final String sql = """
        SELECT name, description, display_name
        FROM role
        WHERE name = :role_name
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("role_name", roleName);

    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNum) -> {
        Role role = new Role();
        role.setName(resultSet.getString("name"));
        role.setDescription(resultSet.getString("description"));
        role.setDisplayName(resultSet.getString("display_name"));

        return role;
      }));
    } catch (EmptyResultDataAccessException ignored) {
      return Optional.empty();
    }
  }

  public void createRole(final Role role) {
    final String sql = """
        INSERT INTO role (name, description, display_name)
        VALUES
        (:name, :description, :display_name)
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("name", role.getName())
      .addValue("description", role.getDescription())
      .addValue("display_name", role.getDisplayName());

    namedParameterJdbcTemplate.update(sql, paramSource);
  }

  public void deleteRole(final String roleName) {
    final String sql = """
        DELETE FROM role
        WHERE name = :role_name
      """;
    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("role_name", roleName);

    namedParameterJdbcTemplate.update(sql, paramSource);
  }

  public Role getUsersWithRole(final Role role) {
    final String sql = """
      SELECT
          u.username
      FROM
          role r
              JOIN
          user_role  ur  ON r.name = ur.role_name
              JOIN
          user_account u ON ur.username = u.username
      WHERE
          r.name = :role_name
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("role_name", role.getName());

    return namedParameterJdbcTemplate.query(sql, parameters, (resultSet) -> {
      Set<String> users = new HashSet<>();

      while (resultSet.next()) {
        users.add(resultSet.getString("username"));
      }

      role.setUsers(users.stream().toList());

      return role;
    });
  }
}
