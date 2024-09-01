package com.wyaaung.rbac.configuration.security.jwt;

import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.exception.JwtAuthenticationException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
public class JwtService {
  @Value("${jwt.token.prefix}")
  public String TOKEN_PREFIX;
  @Value("${jwt.secret-key}")
  private String SECRET_KEY;
  @Value("${jwt.access-token.expiration}")
  private long ACCESS_TOKEN_EXPIRATION;
  @Value("${jwt.refresh-token.expiration}")
  private long REFRESH_TOKEN_EXPIRATION;

  public String parseJwt(HttpServletRequest request) {
    final String authorizationHeader = request.getHeader(AUTHORIZATION);

    if (authorizationHeader == null || !authorizationHeader.startsWith(TOKEN_PREFIX)) {
      throw new JwtAuthenticationException("Bearer AccessToken Not Found");
    }

    return authorizationHeader.substring(7);
  }

  public String generateToken(User user) {
    return generateToken(new HashMap<>(), user);
  }

  public String generateToken(Map<String, Object> extraClaims, User user) {
    return buildToken(extraClaims, user, ACCESS_TOKEN_EXPIRATION);
  }

  public String generateRefreshToken(User user) {
    return buildToken(new HashMap<>(), user, REFRESH_TOKEN_EXPIRATION);
  }

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
    return Jwts.builder().claims(extraClaims).subject(user.getUsername()).issuedAt(new Date(System.currentTimeMillis()))
      .expiration(new Date(System.currentTimeMillis() + expiration)).signWith(getSignInKey(), Jwts.SIG.HS256).compact();
  }

  private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  private Claims extractAllClaims(String token) {
    try {
      return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
    } catch (
      ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException exception
    ) {
      throw new JwtException(exception.getMessage());
    }
  }

  private SecretKey getSignInKey() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
  }
}
