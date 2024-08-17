package com.wyaaung.rbac.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.domain.RoleDetails;
import com.wyaaung.rbac.dto.AuthResponseDto;
import com.wyaaung.rbac.dto.PermissionDto;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.dto.RoleDto;
import com.wyaaung.rbac.dto.RoleUsersDto;
import com.wyaaung.rbac.exception.RoleNotFoundException;
import com.wyaaung.rbac.service.RoleService;
import com.wyaaung.rbac.unit.RepositoryTestHelper;
import java.util.Arrays;
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
  @LocalServerPort
  private int port;
  @Autowired
  private DataSource dataSource;
  private String baseUrl;
  private String accessToken;

  @BeforeAll
  void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
    cacheManager.getCacheNames().forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
    accessToken = obtainAccessToken();
    baseUrl = "http://localhost:" + port + "/api/v1/role";
  }

  @Test
  public void testGetAllRoles() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleDto[]> response = testRestTemplate.exchange(baseUrl, GET, new HttpEntity<>(headers), RoleDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertEquals(3, response.getBody().length);
  }

  @Test
  public void testGetRole() {
    String roleName = "manager";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleUsersDto> response = testRestTemplate.exchange(baseUrl + "/" + roleName, GET, new HttpEntity<>(headers), RoleUsersDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.username().equals("usertwo")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.username().equals("userthree")));
  }

  @Test
  public void testGetNonExistingRole() {
    String roleName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleUsersDto> response = testRestTemplate.exchange(baseUrl + "/" + roleName, GET,new HttpEntity<>(headers), RoleUsersDto.class);
    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testGetPermissionsOfRole() {
    String roleName = "manager";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<PermissionDto[]> response = testRestTemplate.exchange(
      baseUrl + "/" + roleName + "/" + "permissions", GET, new HttpEntity<>(headers), PermissionDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(Arrays.stream(response.getBody()).anyMatch(permission -> permission.name().equals("read")));
    assertTrue(Arrays.stream(response.getBody()).anyMatch(permission -> permission.name().equals("write")));
  }

  @Test
  public void testGetPermissonsOfNonExistingRole() {
    String roleName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(
      baseUrl + "/" + roleName + "/" + "permissions", GET, new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testCreateRole() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RoleDto("analyst", "analyst description", "Analyst")
      );
    } catch (Exception e) {
      e.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl, POST, 
        new HttpEntity<>(requestBody, headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(CREATED);
    assertTrue(roleService.getRole("analyst").name().equals("analyst"));
  }

  @Test
  public void testCreateDublicateRole() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RoleDto("user", "User Role", "User")
      );
    } catch (Exception e) {
      e.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl, POST, 
        new HttpEntity<>(requestBody, headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(CONFLICT);
  }

  @Test
  public void testDeleteRole() {
    String roleName = "analyst";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl + "/" + roleName, DELETE, 
        new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThrows(RoleNotFoundException.class, () -> roleService.getRole(roleName));
  }

  @Test
  public void testDeleteNonExistingRole() {
    String roleName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl + "/" + roleName, DELETE, 
        new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testDeleteRoleWithPermissions() {
    String roleName = "administrator";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl + "/" + roleName, DELETE, 
        new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  @Test
  public void testAddPermissionToRole() {
    String roleName = "user";
    String permissionName = "write";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleDetails> response = testRestTemplate.exchange(
      baseUrl + "/" + roleName + "/" + "permission/" + permissionName, PUT, 
        new HttpEntity<>(headers), RoleDetails.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().permissions().stream().anyMatch(permission -> permission.name().equals("write")));
  }

  @Test
  public void testAddNonExistingPermissionToRole() {
    String roleName = "user";
    String permissionName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(
      baseUrl + "/" + roleName + "/" + "permission/" + permissionName, PUT, new HttpEntity<>(headers), Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  @Test
  public void testDeletePermissionToRole() {
    String roleName = "manager";
    String permissionName = "write";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleDetails> response = testRestTemplate.exchange(
      baseUrl + "/" + roleName + "/" + "permission/" + permissionName, DELETE, new HttpEntity<>(headers), RoleDetails.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().permissions().stream().anyMatch(permission -> permission.name().equals("read")));
  }

  @Test
  public void testDeleteNonExistingPermissionInRole() {
    String roleName = "manager";
    String permissionName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<RoleDetails> response = testRestTemplate.exchange(
      baseUrl + "/" + roleName + "/" + "permission/" + permissionName, DELETE, new HttpEntity<>(headers), RoleDetails.class);

    assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
  }

  private String obtainAccessToken() {
    final String loginUrl = "http://localhost:" + port + "/api/v1/auth/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_role",
          "test_role_name",
          "test_role_password",
          "test_role@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(loginUrl, new HttpEntity<>(requestBody, headers), AuthResponseDto.class);

    return response.getBody().accessToken();
  }
}
