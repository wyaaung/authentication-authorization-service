package com.wyaaung.rbac.domain;

import java.util.Set;

public record PermissionDetails(Set<Role> roles, Set<User> users) {
}
