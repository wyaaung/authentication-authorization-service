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

  @BeforeAll
  void setUp() {
    RepositoryTestHelper.resetDatabase(dataSource);
    cacheManager.getCacheNames().forEach(c -> Objects.requireNonNull(cacheManager.getCache(c)).clear());
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
}
