package com.wyaaung.rbac.repository.extractor;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.RoleDetails;
import com.wyaaung.rbac.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class RoleDetailsExtractor implements ResultSetExtractor<RoleDetails> {
  @Override
  public RoleDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    Set<Permission> permissions = new HashSet<>();
    Set<User> users = new HashSet<>();

    while (resultSet.next()) {
      final String permissionName = resultSet.getString("permission_name");
      final String permissionDescription = resultSet.getString("permission_description");
      final String permissionDisplayName = resultSet.getString("permission_display_name");
      final String username = resultSet.getString("username");
      final String fullName = resultSet.getString("full_name");
      final String emailAddress = resultSet.getString("email_address");

      permissions.add(new Permission(
        permissionName, permissionDescription, permissionDisplayName
      ));
      users.add(new User(
        username, fullName, null, emailAddress
      ));
    }
    return new RoleDetails(permissions, users);
  }
}
