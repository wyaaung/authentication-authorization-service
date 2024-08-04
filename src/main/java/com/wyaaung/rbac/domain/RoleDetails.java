package com.wyaaung.rbac.domain;

import java.util.Set;

public record RoleDetails(Set<Permission> permissions, Set<User> users) {
}
