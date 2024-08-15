package com.wyaaung.rbac.repository.extractor;

import com.wyaaung.rbac.domain.Permission;
import com.wyaaung.rbac.domain.Role;
import com.wyaaung.rbac.domain.UserDetails;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsExtractor implements ResultSetExtractor<UserDetails> {
  @Override
  public UserDetails extractData(ResultSet resultSet) throws SQLException, DataAccessException {
    Set<Role> roles = new HashSet<>();
    Set<Permission> permissions = new HashSet<>();

    while (resultSet.next()) {
      final String roleName = resultSet.getString("role_name");
      final String roleDescription = resultSet.getString("role_description");
      final String roleDisplayName = resultSet.getString("role_display_name");
      final String permissionName = resultSet.getString("permission_name");
      final String permissionDescription = resultSet.getString("permission_description");
      final String permissionDisplayName = resultSet.getString("permission_display_name");

      roles.add(new Role(
        roleName, roleDescription, roleDisplayName
      ));
      permissions.add(new Permission(
        permissionName, permissionDescription, permissionDisplayName
      ));
    }
    return new UserDetails(roles, permissions);
  }
}
