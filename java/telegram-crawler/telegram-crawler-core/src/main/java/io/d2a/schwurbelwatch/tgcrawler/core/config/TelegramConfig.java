/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.core.config;

import io.d2a.schwurbelwatch.tgcrawler.core.auth.ApiCredentials;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.SystemInfo;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class TelegramConfig implements ConfigFile {

  private ApiCredentials credentials = new ApiCredentials(
      999999,
      "ffffffffffffffffffffffffffffffff",
      "+49151242333213"
  );

  private SystemInfo systemInfo = new SystemInfo(
      "en",
      "Unknown",
      "Desktop",
      "1.0"
  );

  private String databaseDirectory;

}