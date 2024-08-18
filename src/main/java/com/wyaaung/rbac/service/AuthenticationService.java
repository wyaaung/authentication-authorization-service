package com.wyaaung.rbac.service;

import com.wyaaung.rbac.configuration.security.JwtService;
import com.wyaaung.rbac.domain.AccessToken;
import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.repository.TokenRepository;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

  private final JwtService jwtService;
  private final UserService userService;
  private final TokenRepository tokenRepository;
  private final AuthenticationManager authenticationManager;
  private final PasswordEncoder passwordEncoder;
  @Value("${jwt.token.prefix}")
  public String TOKEN_PREFIX;

  public AuthenticationService(JwtService jwtService, UserService userService, TokenRepository tokenRepository,
                               AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.userService = userService;
    this.tokenRepository = tokenRepository;
    this.authenticationManager = authenticationManager;
    this.passwordEncoder = passwordEncoder;
  }

  public AuthResponse registerUser(final User user) {
    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);

    userService.registerUser(user);
    tokenRepository.saveToken(
      new AccessToken(jwtToken, Instant.now(), jwtService.extractExpiration(refreshToken).toInstant(), user.getUsername())
    );

    return new AuthResponse(jwtToken, refreshToken);
  }

  public AuthResponse authenticateUser(final User user) {
    authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
    );

    String accessToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    tokenRepository.deleteTokensOfUser(user);

    tokenRepository.saveToken(
      new AccessToken(accessToken, Instant.now(), jwtService.extractExpiration(refreshToken).toInstant(), user.getUsername())
    );

    return new AuthResponse(accessToken, refreshToken);
  }

  public AuthResponse refreshToken(final String authorization) {
    if (authorization == null) {
      throw new IllegalArgumentException("Missing Authorization Token");
    }

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

    User user = userService.getUser(username);

    boolean isValidToken = jwtService.isTokenValid(refreshToken, user);
    if (!isValidToken) {
      throw new IllegalArgumentException("Invalid Bearer Token");
    }

    String accessToken = jwtService.generateToken(user);

    tokenRepository.deleteTokensOfUser(user);
    tokenRepository.saveToken(
      new AccessToken(accessToken, Instant.now(), jwtService.extractExpiration(refreshToken).toInstant(), user.getUsername())
    );

    return new AuthResponse(accessToken, refreshToken);
  }
}
