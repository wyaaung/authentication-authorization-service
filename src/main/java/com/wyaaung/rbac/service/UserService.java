package com.wyaaung.rbac.service;

import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.domain.UserDetails;
import com.wyaaung.rbac.exception.DuplicateUserException;
import com.wyaaung.rbac.exception.UserNotFoundException;
import com.wyaaung.rbac.repository.UserRepository;
import com.wyaaung.rbac.repository.UserRolePermissionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UserService {
  private final UserRepository userRepository;
  private final UserRolePermissionRepository userRolePermissionRepository;

  public UserService(UserRepository userRepository, UserRolePermissionRepository userRolePermissionRepository) {
    this.userRepository = userRepository;
    this.userRolePermissionRepository = userRolePermissionRepository;
  }

  public List<User> getUsers() {
    return userRepository.getUsers();
  }

  public UserDetails getRolesAndPermissionsByUser(final String username) {
    User user = getUser(username);
    return userRolePermissionRepository.getRolesAndPermissionsByUser(user);
  }

  public void registerUser(final User user) {
    final Optional<User> optionalUser = userRepository.getUser(user.getUsername());

    if (optionalUser.isPresent()) {
      throw new DuplicateUserException(String.format("User '%s' already exists", user.getUsername()));
    }

    userRepository.registerUser(user);
  }

  public User getUser(final String username) {
    final Optional<User> optionalUser = userRepository.getUser(username);

    if (!optionalUser.isPresent()) {
      throw new UserNotFoundException(String.format("User '%s' does not exist", username));
    }

    return optionalUser.get();
  }
}
