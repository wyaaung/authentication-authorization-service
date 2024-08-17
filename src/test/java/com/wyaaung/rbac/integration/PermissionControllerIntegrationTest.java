package com.wyaaung.rbac.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.dto.AuthResponseDto;
import com.wyaaung.rbac.dto.PermissionDetailsDto;
import com.wyaaung.rbac.dto.PermissionDto;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.exception.PermissionNotFoundException;
import com.wyaaung.rbac.service.PermissionService;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PermissionControllerIntegrationTest {
  @Autowired
  protected TestRestTemplate testRestTemplate;
  @Autowired
  protected CacheManager cacheManager;
  @Autowired
  private PermissionService permissionService;
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
    baseUrl = "http://localhost:" + port + "/api/v1/permission";
  }

  @Test
  public void testGetAllPermissions() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<PermissionDto[]> response = testRestTemplate.exchange(baseUrl, GET, new HttpEntity<>(headers), PermissionDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertEquals(3, response.getBody().length);
  }

  @Test
  public void testGetPermission() {
    String permissionName = "read";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<PermissionDetailsDto> response = testRestTemplate.exchange(baseUrl + "/" + permissionName, GET, 
        new HttpEntity<>(headers), PermissionDetailsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().roles().stream().anyMatch(role -> role.name().equals("manager")));
    assertTrue(response.getBody().roles().stream().anyMatch(role -> role.name().equals("administrator")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.username().equals("userone")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.username().equals("usertwo")));
    assertTrue(response.getBody().users().stream().anyMatch(user -> user.username().equals("userthree")));
  }

  @Test
  public void testCreatePermission() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new PermissionDto("analyst", "analyst description", "Analyst")
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
    assertTrue(permissionService.getPermission("analyst").name().equals("analyst"));
  }

  @Test
  public void testCreateDuplicatePermission() {
    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new PermissionDto("read", "Permission to read", "Read")
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
  public void testDeletePermission() {
    String permissionName = "analyst";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl + "/" + permissionName, DELETE, 
        new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThrows(PermissionNotFoundException.class, () -> permissionService.getPermission(permissionName));
  }

  @Test
  public void testDeleteNonExistingPermission() {
    String permissionName = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl + "/" + permissionName, DELETE, 
        new HttpEntity<>(headers), Void.class);
    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  private String obtainAccessToken() {
    final String loginUrl = "http://localhost:" + port + "/api/v1/auth/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_permission",
          "test_permission_name",
          "test_permission_password",
          "test_permission@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(loginUrl, 
        new HttpEntity<>(requestBody, headers), AuthResponseDto.class);

    return response.getBody().accessToken();
  }
}
