package com.wyaaung.rbac.repository.extractor;

import com.wyaaung.rbac.domain.PermissionDetails;
import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.domain.User;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class PermissionDetailsExtractor implements ResultSetExtractor<PermissionDetails> {
  @Override
  public PermissionDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    Set<Role> roles = new HashSet<>();
    Set<User> users = new HashSet<>();

    while (resultSet.next()) {
      final String roleName = resultSet.getString("role_name");
      final String roleDescription = resultSet.getString("role_description");
      final String roleDisplayName = resultSet.getString("role_display_name");
      final String username = resultSet.getString("username");
      final String fullName = resultSet.getString("full_name");
      final String emailAddress = resultSet.getString("email_address");

      roles.add(new Role(
        roleName, roleDescription, roleDisplayName
      ));
      users.add(new User(
        username, fullName, null, emailAddress
      ));
    }
    return new PermissionDetails(roles, users);
  }
}
