package com.wyaaung.rbac.configuration;

import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@Configuration
public class DatabaseConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

  @Bean
  DataSource configureDataSource() {
    final Properties properties = getProperties("db.properties");
    final String dbPassword = System.getenv("DB_PASSWORD");
    if (dbPassword != null && !dbPassword.isEmpty()) {
      properties.put("password", dbPassword);
    }
    return getDataSource(properties);
  }

  @Bean
  NamedParameterJdbcTemplate configureNamedParameterJdbcTemplate(DataSource dataSource) {
    return new NamedParameterJdbcTemplate(dataSource);
  }

  private Properties getProperties(String fileName) {
    Properties properties = new Properties();
    try {
      properties.load(getClass().getClassLoader().getResourceAsStream(fileName));
    } catch (IOException e) {
      String msg = String.format("Could not load properties from classpath: %s", fileName);
      LOGGER.error(msg, e);
      throw new RuntimeException(msg, e);
    }
    return properties;
  }

  private DataSource getDataSource(final Properties properties) {
    try {
      return HikariDataSourceFactory.createDataSource(properties);
    } catch (Exception e) {
      final String message = "Failed to create datasource";
      LOGGER.error(message, e);
      throw new RuntimeException(message, e);
    }
  }
}
