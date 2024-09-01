package com.wyaaung.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.PermissionDetailsDto;
import com.wyaaung.rbac.dto.PermissionDto;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.service.AuthenticationService;
import com.wyaaung.rbac.service.PermissionService;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PermissionControllerIntegrationTest {
  @Autowired
  protected TestRestTemplate testRestTemplate;
  @Autowired
  protected CacheManager cacheManager;
  @Autowired
  private PermissionService permissionService;
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
    baseUrl = "http://localhost:" + port + "/api/v1/permissions";
  }

  @Test
  public void testGetAllPermissions() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<PermissionDto[]> response =
      testRestTemplate.exchange(baseUrl, GET, new HttpEntity<>(headers), PermissionDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertEquals(3, response.getBody().length);
  }


  @Test
  public void testGetAllPermissionsWithoutToken() {
    HttpHeaders headers = new HttpHeaders();

    ResponseEntity<Void> response =
      testRestTemplate.exchange(baseUrl, GET, new HttpEntity<>(headers), void.class);

    assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
  }


  @Test
  public void testGetPermission() {
    String permissionName = "read";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<PermissionDetailsDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + permissionName,
        GET,
        new HttpEntity<>(headers),
        PermissionDetailsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().roles().stream().anyMatch(role -> role.equals("manager")));
    assertTrue(response.getBody().roles().stream().anyMatch(role -> role.equals("administrator")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.equals("userone")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.equals("usertwo")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.equals("userthree")));
  }

  @Test
  public void testCreatePermission() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody =
        objectMapper.writeValueAsString(
          new PermissionDto("analyst", "analyst description", "Analyst"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl, POST, new HttpEntity<>(requestBody, headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(CREATED);
    assertTrue(permissionService.getPermission("analyst").getName().equals("analyst"));
  }

  @Test
  public void testCreateDuplicatePermission() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody =
        objectMapper.writeValueAsString(new PermissionDto("read", "Permission to read", "Read"));
    } catch (Exception e) {
      e.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl, POST, new HttpEntity<>(requestBody, headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
  }

  @Test
  public void testDeletePermission() {
    String permissionName = "analyst";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + permissionName, DELETE, new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThrows(
      PermissionNotFoundException.class, () -> permissionService.getPermission(permissionName));
  }

  @Test
  public void testDeleteNonExistingPermission() {
    String permissionName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + permissionName, DELETE, new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  private String obtainAccessToken() {
    authenticationService.registerUser(
      new User(
        "test_permission",
        "test_permission_name",
        "test_permission_password",
        "test_permission@email.com",
        null,
        null));

    AuthResponse authResponse =
      authenticationService.authenticateUser(
        new User("test_permission", null, "test_permission_password", null, null, null));

    return authResponse.getToken().getRefreshToken();
  }
}
