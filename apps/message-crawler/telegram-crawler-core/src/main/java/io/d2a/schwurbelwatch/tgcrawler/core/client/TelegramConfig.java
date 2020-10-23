/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.client;

import com.google.gson.annotations.SerializedName;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.SystemInfo;
import io.d2a.schwurbelwatch.tgcrawler.core.client.router.ClientConfig;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

@Getter
public class TelegramConfig {

  @SerializedName("info")
  private SystemInfo systemInfo;

  @SerializedName("database_directory")
  private String databaseDirectory;

  @Setter
  @SerializedName("accounts")
  private Map<String, ClientConfig> clientConfigs;

}