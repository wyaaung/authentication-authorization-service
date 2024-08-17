package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.dto.AuthResponseDto;

public class AuthResponseTransformer {
  public static AuthResponseDto toDto(AuthResponse authResponse) {
    return new AuthResponseDto(authResponse.accessToken(), authResponse.refreshToken());
  }
}
