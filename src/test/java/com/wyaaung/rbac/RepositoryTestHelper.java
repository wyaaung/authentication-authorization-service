package com.wyaaung.rbac;

import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.init.ScriptUtils;

public class RepositoryTestHelper {
  private static final String DATABASE_SETUP_SQL_PATH = "database-setup.sql";

  public static void resetDatabase(DataSource dataSource) {
    ScriptUtils.executeSqlScript(
      DataSourceUtils.getConnection(dataSource), new ClassPathResource(DATABASE_SETUP_SQL_PATH));
  }
}
