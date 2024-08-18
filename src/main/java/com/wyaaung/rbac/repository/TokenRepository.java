package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.AccessToken;
import com.wyaaung.rbac.domain.User;
import java.sql.Timestamp;
import java.util.Optional;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class TokenRepository {
  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  public TokenRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
    this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
  }

  public Optional<AccessToken> getToken(final String accessToken) {
    final String sql = """
        SELECT token, created_at, expires_at, username
        FROM access_token
        WHERE token = :accessToken
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("accessToken", accessToken);

    try {
      return Optional.of(namedParameterJdbcTemplate.queryForObject(sql, parameters, (resultSet, rowNum) -> {
        return new AccessToken(
          resultSet.getString("token"),
          resultSet.getTimestamp("created_at").toInstant(),
          resultSet.getTimestamp("expires_at").toInstant(),
          resultSet.getString("username")
        );
      }));
    } catch (EmptyResultDataAccessException ignored) {
      return Optional.empty();
    }
  }

  public void saveToken(final AccessToken accessToken) {
    final String sql = """
        INSERT INTO  access_token (token, created_at, expires_at, username)
        VALUES
        (:token, :created_at, :created_at, :username)
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("token", accessToken.token())
      .addValue("created_at", Timestamp.from(accessToken.createdAt()))
      .addValue("expires_at", Timestamp.from(accessToken.expiresAt()))
      .addValue("username", accessToken.username());

    namedParameterJdbcTemplate.update(sql, paramSource);
  }

  public void deleteTokensOfUser(final User user) {
    final String sql = """
      DELETE FROM access_token
      WHERE username = :username
      """;

    final SqlParameterSource paramSource = new MapSqlParameterSource()
      .addValue("username", user.getUsername());

    namedParameterJdbcTemplate.update(sql, paramSource);
  }
}
