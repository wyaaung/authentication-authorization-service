package com.wyaaung.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.AuthRequestDto;
import com.wyaaung.rbac.dto.AuthResponseDto;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.dto.TokenDto;
import com.wyaaung.rbac.dto.UserDetailsDto;
import com.wyaaung.rbac.service.AuthenticationService;
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
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

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
    cacheManager
      .getCacheNames()
      .forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
    refreshToken = obtainRefreshToken();
    baseUrl = "http://localhost:" + port + "/api/v1/auth";
  }

  @Test
  public void testRegisterUser() {
    String registerUrl = baseUrl + "/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody =
        objectMapper.writeValueAsString(
          new RegisterDto(
            "test_register",
            "test_register_name",
            "test_register_password",
            "test_register@email.com"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<UserDetailsDto> response =
      testRestTemplate.postForEntity(
        registerUrl, new HttpEntity<>(requestBody, headers), UserDetailsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().username()).isNotEmpty();
    assertThat(response.getBody().fullName()).isNotEmpty();
    assertThat(response.getBody().emailAddress()).isNotEmpty();
  }

  @Test
  public void testRegisterAlreadyExistingUser() {
    authenticationService.registerUser(
      new User(
        "test_register_1",
        "test_register_1_name",
        "test_register_1_password",
        "test_register_1@email.com",
        null,
        null));

    String registerUrl = baseUrl + "/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody =
        objectMapper.writeValueAsString(
          new RegisterDto(
            "test_register_1",
            "test_register_1_name",
            "test_register_1_password",
            "test_register_1@email.com"));
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<Void> response =
      testRestTemplate.postForEntity(
        registerUrl, new HttpEntity<>(requestBody, headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
  }

  @Test
  public void testAuthenticateUser() {
    authenticationService.registerUser(
      new User(
        "test_auth",
        "test_auth_name",
        "test_auth_password",
        "test_auth@email.com",
        null,
        null));

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

    ResponseEntity<AuthResponseDto> response =
      testRestTemplate.postForEntity(
        authenticateUrl, new HttpEntity<>(requestBody, headers), AuthResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().userDetailsDto().username()).isNotEmpty();
    assertThat(response.getBody().userDetailsDto().fullName()).isNotEmpty();
    assertThat(response.getBody().userDetailsDto().emailAddress()).isNotEmpty();
    assertThat(response.getBody().tokenDto().accessToken()).isNotEmpty();
    assertThat(response.getBody().tokenDto().refreshToken()).isNotEmpty();
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

    ResponseEntity<Void> response =
      testRestTemplate.postForEntity(
        authenticateUrl, new HttpEntity<>(requestBody, headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testRefreshToken() {
    String authenticateUrl = baseUrl + "/refresh-token";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(refreshToken);

    ResponseEntity<TokenDto> response =
      testRestTemplate.postForEntity(authenticateUrl, new HttpEntity<>(headers), TokenDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotEmpty();
    assertThat(response.getBody().refreshToken()).isNotEmpty();
  }

  @Test
  public void testRefreshTokenWithMalformedHeader() {
    authenticationService.authenticateUser(
      new User(
        "test_refresh_user",
        "test_refresh_user_name",
        "test_refresh_user_password",
        "test_refresh_user@email.com",
        null,
        null));
    String authenticateUrl = baseUrl + "/refresh-token";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.set(AUTHORIZATION, "BEEEER ...");

    ResponseEntity<Void> response =
      testRestTemplate.postForEntity(authenticateUrl, new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  public void testRefreshTokenWithHeaderComputedOutside() {
    authenticationService.authenticateUser(
      new User(
        "test_refresh_user",
        "test_refresh_user_name",
        "test_refresh_user_password",
        "test_refresh_user@email.com",
        null,
        null));
    String authenticateUrl = baseUrl + "/refresh-token";

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(
      "eyJhbGciOiJIUzI1NiJ9.eyJTQU1QTEUiOiJTQU1QTEUifQ.XUXoNPJ5poeytV5W5zDHaaVLz-0L5AvijLszT2wQ_0o");

    ResponseEntity<Void> response =
      testRestTemplate.postForEntity(authenticateUrl, new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }

  private String obtainRefreshToken() {
    authenticationService.registerUser(
      new User(
        "test_refresh_user",
        "test_refresh_user_name",
        "test_refresh_user_password",
        "test_refresh_user@email.com",
        null,
        null));

    AuthResponse authResponse =
      authenticationService.authenticateUser(
        new User("test_refresh_user", null, "test_refresh_user_password", null, null, null));

    return authResponse.getToken().getRefreshToken();
  }
}
