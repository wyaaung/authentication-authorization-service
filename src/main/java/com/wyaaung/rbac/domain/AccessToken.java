package com.wyaaung.rbac.domain;

import java.time.Instant;

public record AccessToken(String token, Instant createdAt, Instant expiresAt, String username) {
}
