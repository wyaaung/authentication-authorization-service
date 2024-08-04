package com.wyaaung.rbac.repository.mapper;

import com.wyaaung.rbac.domain.Role;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RoleRowMapper implements RowMapper<Role> {
  @Override
  public Role mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return new Role(
      resultSet.getString("name"),
      resultSet.getString("description"),
      resultSet.getString("display_name")
    );
  }
}
