package com.wyaaung.rbac.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.Properties;
import javax.sql.DataSource;

public class HikariDataSourceFactory {
  static DataSource createDataSource(Properties properties) {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName(properties.getProperty("driverClassName"));
    config.setJdbcUrl(properties.getProperty("url"));
    config.setUsername(properties.getProperty("username"));
    config.setPassword(properties.getProperty("password"));
    config.setMaximumPoolSize(Integer.parseInt(properties.getProperty("datasource.maximumPoolSize")));
    config.setConnectionTimeout(Integer.parseInt(properties.getProperty("datasource.connectionTimeout")));
    config.setMinimumIdle(Integer.parseInt(properties.getProperty("datasource.minimumIdle")));

    return new HikariDataSource(config);
  }
}