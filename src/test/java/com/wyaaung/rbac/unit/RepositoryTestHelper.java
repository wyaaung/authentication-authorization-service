package com.wyaaung.rbac.unit.repository;

import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;

import javax.sql.DataSource;

public class RepositoryTestHelper {
  private final static String DATABASE_SETUP_SQL_PATH = "database-setup.sql";

  public static void resetDatabase(DataSource dataSource) {
    ScriptUtils.executeSqlScript(DataSourceUtils.getConnection(dataSource), new ClassPathResource(DATABASE_SETUP_SQL_PATH));
  }
}
