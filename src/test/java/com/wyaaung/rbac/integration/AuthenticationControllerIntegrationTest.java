package com.wyaaung.rbac.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wyaaung.rbac.dto.AuthRequestDto;
import com.wyaaung.rbac.dto.AuthResponseDto;
import com.wyaaung.rbac.dto.RegisterDto;
import com.wyaaung.rbac.unit.RepositoryTestHelper;
import java.util.Objects;
import javax.sql.DataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class AuthenticationControllerIntegrationTest {
  @Autowired
  protected TestRestTemplate testRestTemplate;
  @Autowired
  protected CacheManager cacheManager;
  @LocalServerPort
  private int port;
  @Autowired
  private DataSource dataSource;
  private String baseUrl;

  @BeforeAll
  void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
    cacheManager.getCacheNames().forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
    baseUrl = "http://localhost:" + port + "/api/v1/auth";
  }

  @Test
  @Order(1)
  public void testRegisterUser() {
    String registerUrl = baseUrl + "/register";

    ObjectMapper objectMapper = new ObjectMapper();
    String requestBody = null;
    try {
      requestBody = objectMapper.writeValueAsString(
        new RegisterDto(
          "test_auth",
          "test_auth_name",
          "test_auth_password",
          "test_auth@email.com")
      );
    } catch (Exception exception) {
      exception.printStackTrace();
    }

    HttpHeaders headers = new HttpHeaders();
    headers.set("Content-Type", "application/json");
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(registerUrl, request, AuthResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotEmpty();
    assertThat(response.getBody().refreshToken()).isNotEmpty();
  }

  @Test
  @Order(2)
  public void testAuthenticateUser() {
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
    HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

    ResponseEntity<AuthResponseDto> response = testRestTemplate.postForEntity(authenticateUrl, request, AuthResponseDto.class);

    assertThat(response.getStatusCode()).isEqualTo(OK);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().accessToken()).isNotEmpty();
    assertThat(response.getBody().refreshToken()).isNotEmpty();
  }
}
