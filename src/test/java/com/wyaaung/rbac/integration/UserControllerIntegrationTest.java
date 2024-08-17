package com.wyaaung.rbac.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.dto.AuthResponseDto;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.dto.UserDetailsDto;
import com.wyaaung.rbac.dto.UserDto;
import com.wyaaung.rbac.service.UserService;
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
    baseUrl = "http://localhost:" + port + "/api/v1/user";
  }

  @Test
  public void testGetAllUsers() {
    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity request = new HttpEntity<>(headers);

    ResponseEntity<UserDto[]> response = testRestTemplate.exchange(baseUrl, GET, request, UserDto[].class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertEquals(4, response.getBody().length);
  }

  @Test
  public void testGetRole() {
    String username = "userone";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity request = new HttpEntity<>(headers);

    ResponseEntity<UserDetailsDto> response = testRestTemplate.exchange(baseUrl + "/" + username, GET, request, UserDetailsDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertTrue(response.getBody().permissions().stream().anyMatch(permission -> permission.name().equals("read")));
    assertTrue(response.getBody().permissions().stream().anyMatch(permission -> permission.name().equals("write")));
    assertTrue(response.getBody().permissions().stream().anyMatch(permission -> permission.name().equals("delete")));
  }

  @Test
  public void testGetNonExistingRole() {
    String username = "NON_EXISTING";

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(accessToken);
    HttpEntity request = new HttpEntity<>(headers);

    ResponseEntity<Void> response = testRestTemplate.exchange(baseUrl + "/" + username, GET, request, Void.class);

    assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
  }

  private String obtainAccessToken() {
    final String loginUrl = "http://localhost:" + port + "/api/v1/auth/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_user",
          "test_user_name",
          "test_user_password",
          "test_user@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(loginUrl, request, AuthResponseDto.class);

    return response.getBody().accessToken();
  }
}
