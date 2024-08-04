package com.wyaaung.rbac.domain;

import java.util.Set;

public record RoleUsers(String roleName, Set<User> users) {
}
