package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public UserRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public List<User> getUsers() {
    final String sql = """
        SELECT username, full_name, email_address
        FROM user_account
      """;

    return namedParameterJdbcTemplate.query(sql, resultSet -> {
      final List<User> result = new ArrayList<>();

      while (resultSet.next()) {
        User user = new User();
        user.setUsername(resultSet.getString("username"));
        user.setFullName(resultSet.getString("full_name"));
        user.setEmailAddress(resultSet.getString("email_address"));

        result.add(user);
      }
      return result;
    });
  }

  public Optional<User> getUser(final String username) {
    final String sql = """
        SELECT username, full_name, password, email_address
        FROM user_account
        WHERE username = :username
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("username", username);

    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNum) -> {
        User user = new User();
        user.setUsername(resultSet.getString("username"));
        user.setFullName(resultSet.getString("full_name"));
        user.setPassword(resultSet.getString("password"));
        user.setEmailAddress(resultSet.getString("email_address"));

        return user;
      }));
    } catch (EmptyResultDataAccessException ignored) {
      return Optional.empty();
    }
  }

  public void registerUser(User user) {
    final String sql = """
      INSERT INTO user_account (username, full_name, password, email_address)
      VALUES
      (:username, :full_name, :password, :email_address);
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("username", user.getUsername())
      .addValue("full_name", user.getFullName())
      .addValue("password", user.getPassword())
      .addValue("email_address", user.getEmailAddress());

    namedParameterJdbcTemplate.update(sql, paramSource);
  }
}
