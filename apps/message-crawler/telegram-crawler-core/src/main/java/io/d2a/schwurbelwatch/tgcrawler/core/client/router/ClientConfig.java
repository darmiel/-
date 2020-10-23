/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.client.router;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.ApiCredentials;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.SystemInfo;
import java.util.Set;
import lombok.Getter;

@Getter
public class ClientConfig {

  @SerializedName("use_cases")
  private Set<String> useCases = Sets.newHashSet();

  @SerializedName("credentials")
  private ApiCredentials credentials;

  @SerializedName("info")
  private SystemInfo systemInfo = new SystemInfo(
      "en",
      "Unknown",
      "Desktop",
      "1.0"
  );

  @SerializedName("database_directory")
  private String databaseDirectory;

  public boolean accepts(String useCase) {
    if (useCase == null) {
      return true;
    }
    return useCases.stream()
        .anyMatch(useCase::equalsIgnoreCase);
  }

}