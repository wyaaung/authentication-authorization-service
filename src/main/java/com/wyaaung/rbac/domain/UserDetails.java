package com.wyaaung.rbac.domain;

import java.util.Set;

public record UserDetails(Set<Role> roles, Set<Permission> permissions) {
}
