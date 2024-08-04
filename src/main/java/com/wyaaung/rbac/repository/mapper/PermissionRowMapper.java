package com.wyaaung.rbac.repository.mapper;

import com.wyaaung.rbac.domain.Permission;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class PermissionRowMapper implements RowMapper<Permission> {
  @Override
  public Permission mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return new Permission(
      resultSet.getString("name"),
      resultSet.getString("description"),
      resultSet.getString("display_name")
    );
  }
}
