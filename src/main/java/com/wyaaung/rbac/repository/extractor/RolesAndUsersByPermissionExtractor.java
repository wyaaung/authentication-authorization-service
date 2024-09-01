package com.wyaaung.rbac.repository.extractor;

import com.wyaaung.rbac.domain.Permission;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class RolesAndUsersByPermissionExtractor implements ResultSetExtractor<Permission> {
  @Override
  public Permission extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    List<String> roles = new ArrayList<>();
    List<String> users = new ArrayList<>();

    while (resultSet.next()) {
      final String roleName = resultSet.getString("role_name");
      final String username = resultSet.getString("username");

      roles.add(roleName);
      users.add(username);
    }

    Permission permission = new Permission();
    permission.setRoles(roles);
    permission.setUsers(users);

    return permission;
  }
}
