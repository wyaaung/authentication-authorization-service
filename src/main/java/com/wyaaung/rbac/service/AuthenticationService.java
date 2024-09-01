package com.wyaaung.rbac.service;

import com.wyaaung.rbac.configuration.security.jwt.JwtService;
import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.Token;
import com.wyaaung.rbac.domain.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final JwtService jwtService;
  private final UserService userService;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;

  @Value("${jwt.token.prefix}")
  public String TOKEN_PREFIX;

  public AuthenticationService(
    JwtService jwtService,
    UserService userService,
    AuthenticationManager authenticationManager,
    PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  public User registerUser(final User user) {
    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);
    userService.registerUser(user);

    return userService.getUser(user.getUsername());
  }

  public AuthResponse authenticateUser(final User user) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    return new AuthResponse(
      new Token(accessToken, refreshToken), userService.getUser(user.getUsername()));
  }

  public Token refreshToken(final String authorization) {
    if (!authorization.startsWith(TOKEN_PREFIX)) {
      throw new IllegalArgumentException("Malformed Authorization request");
    }

    final String username;
    final String refreshToken;

    refreshToken = authorization.substring(7);
    username = jwtService.extractUsername(refreshToken);

    if (username == null) {
      throw new IllegalArgumentException("Malformed Bearer Token");
    }

    String accessToken = jwtService.generateToken(userService.getUser(username));

    return new Token(accessToken, refreshToken);
  }
}
