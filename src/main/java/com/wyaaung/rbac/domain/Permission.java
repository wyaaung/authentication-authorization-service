package com.wyaaung.rbac.domain;

import java.util.List;
import java.util.Objects;

public class Permission {
  private String name;
  private String description;
  private String displayName;
  private List<String> roles;
  private List<String> users;

  public Permission() {
  }

  public Permission(String name, String description, String displayName) {
    this.name = name;
    this.description = description;
    this.displayName = displayName;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public List<String> getRoles() {
    return roles;
  }

  public void setRoles(List<String> roles) {
    this.roles = roles;
  }

  public List<String> getUsers() {
    return users;
  }

  public void setUsers(List<String> users) {
    this.users = users;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Permission that = (Permission) o;
    return name.equals(that.name) && description.equals(that.description) && displayName.equals(that.displayName)
      && Objects.equals(roles, that.roles) && Objects.equals(users, that.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, displayName, roles, users);
  }
}
