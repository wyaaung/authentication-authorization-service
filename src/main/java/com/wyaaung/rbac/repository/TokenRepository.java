package com.wyaaung.rbac.repository;

import com.wyaaung.rbac.domain.AccessToken;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
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

  public List<AccessToken> getTokens(final String username) {
    final String sql = """
        SELECT
          t.token,
          t.created_at,
          t.expires_at
          t.username
        FROM access_token t
        INNER JOIN user_account u
        ON
          t.username = u.username
        WHERE
          u.username = :username
      """;

    final SqlParameterSource parameters = new MapSqlParameterSource().addValue("username", username);

    return namedParameterJdbcTemplate.query(sql, parameters, resultSet -> {
      final List<AccessToken> result = new ArrayList<>();

      while (resultSet.next()) {
        result.add(
          new AccessToken(
            resultSet.getString("token"),
            resultSet.getTimestamp("created_at").toInstant(),
            resultSet.getTimestamp("expires_at").toInstant(),
            resultSet.getString("username")
          )
        );
      }
      return result;
    });
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
}
