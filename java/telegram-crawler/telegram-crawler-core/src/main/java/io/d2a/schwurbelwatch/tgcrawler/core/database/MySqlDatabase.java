/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.core.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class MySqlDatabase {

  @Getter
  private final HikariDataSource dataSource;

  @Getter
  private final HikariConfig config;

  @Getter
  final Sql2o sql2o;

  public MySqlDatabase(final HikariConfig config) {
    // load config
    this.config = config;
    this.dataSource = new HikariDataSource(this.config);

    // Sql2o
    this.sql2o = new Sql2o(this.dataSource);

    // Mappings
    Map<String, String> columnMappings = new HashMap<>();
    // TODO: Add these
    this.sql2o.setDefaultColumnMappings(columnMappings);
  }

  public Connection open() {
    return this.sql2o.open();
  }

}