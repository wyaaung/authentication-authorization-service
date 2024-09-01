package com.wyaaung.rbac;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.domain.AuthResponse;
import com.wyaaung.rbac.domain.User;
import com.wyaaung.rbac.dto.RoleDto;
import com.wyaaung.rbac.dto.RolePermissionsDto;
import com.wyaaung.rbac.dto.RoleUsersDto;
import com.wyaaung.rbac.exception.RoleNotFoundException;
import com.wyaaung.rbac.service.AuthenticationService;
import com.wyaaung.rbac.service.RoleService;
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
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RoleControllerIntegrationTest {
  @Autowired
  protected TestRestTemplate testRestTemplate;
  @Autowired
  protected CacheManager cacheManager;
  @Autowired
  private RoleService roleService;
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
    baseUrl = "http://localhost:" + port + "/api/v1/roles";
  }

  @Test
  public void testGetAllRoles() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleDto[]> response =
      testRestTemplate.exchange(baseUrl, GET, new HttpEntity<>(headers), RoleDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertEquals(3, response.getBody().length);
  }

  @Test
  public void testGetRole() {
    String roleName = "manager";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleUsersDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName, GET, new HttpEntity<>(headers), RoleUsersDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.equals("usertwo")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.equals("userthree")));
  }

  @Test
  public void testGetNonExistingRole() {
    String roleName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleUsersDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName, GET, new HttpEntity<>(headers), RoleUsersDto.class);
    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testGetPermissionsOfRole() {
    String roleName = "administrator";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RolePermissionsDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName + "/" + "permissions",
        GET,
        new HttpEntity<>(headers),
        RolePermissionsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
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
  public void testGetPermissonsOfNonExistingRole() {
    String roleName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName + "/" + "permissions",
        GET,
        new HttpEntity<>(headers),
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testCreateRole() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody =
        objectMapper.writeValueAsString(new RoleDto("analyst", "analyst description", "Analyst"));
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
    assertTrue(roleService.getRole("analyst").getName().equals("analyst"));
  }

  @Test
  public void testCreateDublicateRole() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(new RoleDto("user", "User Role", "User"));
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
  public void testDeleteRole() {
    String roleName = "analyst";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName, DELETE, new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThrows(RoleNotFoundException.class, () -> roleService.getRole(roleName));
  }

  @Test
  public void testDeleteNonExistingRole() {
    String roleName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName, DELETE, new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testDeleteRoleWithPermissions() {
    String roleName = "administrator";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName, DELETE, new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  public void testAddPermissionToRole() {
    String roleName = "user";
    String permissionName = "write";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RolePermissionsDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName + "/" + "permission/" + permissionName,
        PUT,
        new HttpEntity<>(headers),
        RolePermissionsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(
      response.getBody().permissions().stream()
        .anyMatch(permission -> permission.equals("write")));
  }

  @Test
  public void testAddNonExistingPermissionToRole() {
    String roleName = "user";
    String permissionName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName + "/" + "permission/" + permissionName,
        PUT,
        new HttpEntity<>(headers),
        Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testDeletePermissionToRole() {
    String roleName = "manager";
    String permissionName = "write";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RolePermissionsDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName + "/" + "permission/" + permissionName,
        DELETE,
        new HttpEntity<>(headers),
        RolePermissionsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(
      response.getBody().permissions().stream()
        .anyMatch(permission -> permission.equals("read")));
  }

  @Test
  public void testDeleteNonExistingPermissionInRole() {
    String roleName = "manager";
    String permissionName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RolePermissionsDto> response =
      testRestTemplate.exchange(
        baseUrl + "/" + roleName + "/" + "permission/" + permissionName,
        DELETE,
        new HttpEntity<>(headers),
        RolePermissionsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  private String obtainAccessToken() {
    authenticationService.registerUser(
      new User(
        "test_role",
        "test_role_name",
        "test_role_password",
        "test_role@email.com",
        null,
        null));

    AuthResponse authResponse =
      authenticationService.authenticateUser(
        new User("test_role", null, "test_role_password", null, null, null));

    return authResponse.getToken().getRefreshToken();
  }
}
