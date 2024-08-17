package com.wyaaung.rbac.configuration.security;

import com.wyaaung.rbac.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${jwt.secret-key}")
  private String SECRET_KEY;
  @Value("${jwt.access-token.expiration}")
  private long ACCESS_TOKEN_EXPIRATION;
  @Value("${jwt.refresh-token.expiration}")
  private long REFRESH_TOKEN_EXPIRATION;

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
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

  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
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
    return Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
