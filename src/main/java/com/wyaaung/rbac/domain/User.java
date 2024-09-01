package com.wyaaung.rbac.domain;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements UserDetails {
  private String username;
  private String fullName;
  private String password;
  private String emailAddress;
  private List<String> roles;
  private List<String> permissions;

  public User() {
  }

  public User(String username, String fullName, String password, String emailAddress, List<String> roles, List<String> permissions) {
    this.username = username;
    this.fullName = fullName;
    this.password = password;
    this.emailAddress = emailAddress;
    this.roles = roles;
    this.permissions = permissions;
  }

  @Override
  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return null;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;
    return Objects.equals(username, user.username) && Objects.equals(fullName, user.fullName)
      && Objects.equals(password, user.password) && Objects.equals(emailAddress, user.emailAddress)
      && Objects.equals(roles, user.roles) && Objects.equals(permissions, user.permissions);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, fullName, password, emailAddress, roles, permissions);
  }
}
