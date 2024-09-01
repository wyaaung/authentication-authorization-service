package com.wyaaung.rbac.domain;

import java.util.Objects;

public class AuthResponse {
  private Token token;
  private User user;

  public AuthResponse() {
  }

  public AuthResponse(Token token, User user) {
    this.token = token;
    this.user = user;
  }

  public Token getToken() {
    return token;
  }

  public void setToken(Token token) {
    this.token = token;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AuthResponse that = (AuthResponse) o;
    return Objects.equals(token, that.token) && Objects.equals(user, that.user);
  }

  @Override
  public int hashCode() {
    return Objects.hash(token, user);
  }
}
