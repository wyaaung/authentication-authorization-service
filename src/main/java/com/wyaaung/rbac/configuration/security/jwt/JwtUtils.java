package com.wyaaung.rbac.configuration.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.http.MediaType;

public class JwtUtils {
  public static void handleJwtException(Logger logger, HttpServletResponse response, String errorMessage, Exception exception) {
    logger.warn(errorMessage, exception);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    responseOutputStream(logger, response, errorMessage);
  }

  public static void responseOutputStream(Logger logger, HttpServletResponse response, String errorMessage) {
    logger.warn(errorMessage);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    final Map<String, Object> body = new HashMap<>();
    body.put("timestamp", Instant.now().toEpochMilli());
    body.put("message", errorMessage);

    final ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(response.getOutputStream(), body);
    } catch (IOException e) {
      logger.error("Error writing response body", e);
    }
  }
}
