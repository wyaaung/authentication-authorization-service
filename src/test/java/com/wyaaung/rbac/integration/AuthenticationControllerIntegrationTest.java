package com.wyaaung.rbac.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.AuthRequestDto;
import com.wyaaung.rbac.dto.AuthResponseDto;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.service.AuthenticationService;
import com.wyaaung.rbac.unit.RepositoryTestHelper;
import java.util.Objects;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthenticationControllerIntegrationTest {
  @Autowired
  protected TestRestTemplate testRestTemplate;

  @Autowired
  protected CacheManager cacheManager;

  @LocalServerPort
  private int port;

  @Autowired
  private DataSource dataSource;

  @Autowired
  private AuthenticationService authenticationService;

  private String baseUrl;

  private String refreshToken;

  @BeforeAll
  void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
    cacheManager.getCacheNames().forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
    refreshToken = obtainRefreshToken();
    baseUrl = "http://localhost:" + port + "/api/v1/auth";
  }

  @Test
  public void testRegisterUser() {
    String registerUrl = baseUrl + "/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_register",
          "test_register_name",
          "test_register_password",
          "test_register@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(registerUrl,
      new HttpEntity<>(requestBody, headers), AuthResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotEmpty();
    assertThat(response.getBody().refreshToken()).isNotEmpty();
  }

  @Test
  public void testRegisterAlreadyExistingUser() {
    authenticationService.registerUser(
      new User("test_register_1", "test_register_1_name", "test_register_1_password", "test_register_1@email.com")
    );

    String registerUrl = baseUrl + "/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_register_1",
          "test_register_1_name",
          "test_register_1_password",
          "test_register_1@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<Void> response = testRestTemplate.postForEntity(registerUrl,
      new HttpEntity<>(requestBody, headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
  }

  @Test
  public void testAuthenticateUser() {
    authenticationService.registerUser(
      new User("test_auth", "test_auth_name", "test_auth_password", "test_auth@email.com")
    );

    String authenticateUrl = baseUrl + "/authenticate";
    AuthRequestDto authRequestDto = new AuthRequestDto("test_auth", "test_auth_password");

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(authRequestDto);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(authenticateUrl,
      new HttpEntity<>(requestBody, headers), AuthResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotEmpty();
    assertThat(response.getBody().refreshToken()).isNotEmpty();
  }

  @Test
  public void testAuthenticateNonExistingUser() {
    String authenticateUrl = baseUrl + "/authenticate";
    AuthRequestDto authRequestDto = new AuthRequestDto("NON_EXISTING", "NON_EXISTING_PASSWORD");

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(authRequestDto);
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<Void> response = testRestTemplate.postForEntity(authenticateUrl,
      new HttpEntity<>(requestBody, headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }


  @Test
  public void testRefreshToken() {
    String authenticateUrl = baseUrl + "/refresh-token";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(refreshToken);

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(authenticateUrl,
      new HttpEntity<>(headers), AuthResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotEmpty();
    assertThat(response.getBody().refreshToken()).isNotEmpty();
  }

  @Test
  public void testRefreshTokenWithMalformedHeader() {
    authenticationService.authenticateUser(
      new User("test_refresh_user", "test_refresh_user_name", "test_refresh_user_password", "test_refresh_user@email.com")
    );
    String authenticateUrl = baseUrl + "/refresh-token";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set(AUTHORIZATION, "BEEEER ...");

    ResponseEntity<Void> response = testRestTemplate.postForEntity(authenticateUrl,
      new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  public void testRefreshTokenWithHeaderComputedOutside() {
    authenticationService.authenticateUser(
      new User("test_refresh_user", "test_refresh_user_name", "test_refresh_user_password", "test_refresh_user@email.com")
    );
    String authenticateUrl = baseUrl + "/refresh-token";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth("eyJhbGciOiJIUzI1NiJ9.eyJTQU1QTEUiOiJTQU1QTEUifQ.XUXoNPJ5poeytV5W5zDHaaVLz-0L5AvijLszT2wQ_0o");

    ResponseEntity<Void> response = testRestTemplate.postForEntity(authenticateUrl,
      new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
  }

  private String obtainRefreshToken() {
    final String loginUrl = "http://localhost:" + port + "/api/v1/auth/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_refresh_user",
          "test_refresh_user_name",
          "test_refresh_user_password",
          "test_refresh_user@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(loginUrl, new HttpEntity<>(requestBody, headers), AuthResponseDto.class);

    return response.getBody().refreshToken();
  }
}
