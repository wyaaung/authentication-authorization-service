package com.wyaaung.rbac;

import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.UserDetailsDto;
import com.wyaaung.rbac.dto.UserDto;
import com.wyaaung.rbac.service.AuthenticationService;
import com.wyaaung.rbac.service.UserService;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserControllerIntegrationTest {
  @Autowired
  protected TestRestTemplate testRestTemplate;
  @Autowired
  protected CacheManager cacheManager;
  @Autowired
  private UserService userService;
  @Autowired
  private AuthenticationService authenticationService;
  @LocalServerPort
  private int port;
  @Autowired
  private DataSource dataSource;
  private String baseUrl;
  private String accessToken;

  @BeforeAll
  void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
    cacheManager
      .getCacheNames()
      .forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
    accessToken = obtainAccessToken();
    baseUrl = "http://localhost:" + port + "/api/v1/user";
  }

  @Test
  public void testGetAllUsers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<UserDto[]> response =
      testRestTemplate.exchange(baseUrl, GET, new HttpEntity<>(headers), UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertEquals(4, response.getBody().length);
  }

  @Test
  public void testGetUser() {
    String username = "userone";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<UserDetailsDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + username, GET, new HttpEntity<>(headers), UserDetailsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().roles().stream().anyMatch(role -> role.equals("administrator")));
    assertTrue(
      response.getBody().permissions().stream()
        .anyMatch(permission -> permission.equals("read")));
    assertTrue(
      response.getBody().permissions().stream()
        .anyMatch(permission -> permission.equals("write")));
    assertTrue(
      response.getBody().permissions().stream()
        .anyMatch(permission -> permission.equals("delete")));
  }

  @Test
  public void testGetNonExistingRole() {
    String username = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + username, GET, new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  private String obtainAccessToken() {
    authenticationService.registerUser(
      new User(
        "test_user",
        "test_user_name",
        "test_user_password",
        "test_user@email.com",
        null,
        null));

    AuthResponse authResponse =
      authenticationService.authenticateUser(
        new User("test_user", null, "test_user_password", null, null, null));

    return authResponse.getToken().getRefreshToken();
  }
}
