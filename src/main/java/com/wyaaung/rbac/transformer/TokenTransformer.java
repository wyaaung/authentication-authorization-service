package com.wyaaung.rbac.transformer;

import com.wyaaung.rbac.domain.Token;
import com.wyaaung.rbac.dto.TokenDto;

public class TokenTransformer {
  public static TokenDto toDto(Token token) {
    return new TokenDto(token.getAccessToken(), token.getRefreshToken());
  }
}
