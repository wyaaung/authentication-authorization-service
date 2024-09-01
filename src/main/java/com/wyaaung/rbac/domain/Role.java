package com.wyaaung.rbac.domain;

import java.util.List;
import java.util.Objects;

public class Role {
  private String name;
  private String description;
  private String displayName;
  private List<String> permissions;
  private List<String> users;

  public Role() {
  }

  public Role(String name, String description, String displayName, List<String> permissions, List<String> users) {
    this.name = name;
    this.description = description;
    this.displayName = displayName;
    this.permissions = permissions;
    this.users = users;
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

  public List<String> getPermissions() {
    return permissions;
  }

  public void setPermissions(List<String> permissions) {
    this.permissions = permissions;
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
    Role role = (Role) o;
    return name.equals(role.name) && description.equals(role.description) && displayName.equals(role.displayName)
      && permissions.equals(role.permissions) && users.equals(role.users);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, displayName, permissions, users);
  }
}
