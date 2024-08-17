package com.wyaaung.rbac.service;

import com.wyaaung.rbac.configuration.security.JwtService;
import com.wyaaung.rbac.domain.AccessToken;
import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.repository.TokenRepository;
import com.wyaaung.rbac.repository.UserRepository;
import java.time.Instant;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final TokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;

  public AuthenticationService(JwtService jwtService, UserRepository userRepository, TokenRepository tokenRepository, PasswordEncoder passwordEncoder) {
    this.jwtService = jwtService;
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public AuthResponse registerUser(User user) {
    String jwtToken = jwtService.generateToken(user);
    String refreshToken = jwtService.generateRefreshToken(user);

    String hashedPassword = passwordEncoder.encode(user.getPassword());
    user.setPassword(hashedPassword);

    userRepository.registerUser(user);
    tokenRepository.saveToken(
      new AccessToken(jwtToken, Instant.now(), jwtService.extractExpiration(refreshToken).toInstant(), user.getUsername())
    );

    return new AuthResponse(jwtToken, refreshToken);
  }
}
