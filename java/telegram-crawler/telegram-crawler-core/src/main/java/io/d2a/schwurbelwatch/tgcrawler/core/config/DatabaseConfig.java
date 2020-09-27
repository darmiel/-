/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.config;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class DatabaseConfig implements ConfigFile {

  private HikariConfig hikariConfig = new HikariConfig();

}