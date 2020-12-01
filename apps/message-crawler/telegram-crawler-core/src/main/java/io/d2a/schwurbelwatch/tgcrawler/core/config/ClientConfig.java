/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.config;

import com.google.common.collect.Sets;
import com.google.gson.annotations.SerializedName;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.ApiCredentials;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.SystemInfo;
import java.util.Set;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ClientConfig {

  @SerializedName("use_cases")
  private final Set<String> useCases = Sets.newHashSet();

  @SerializedName("credentials")
  private final ApiCredentials credentials;

  @SerializedName("info")
  private final SystemInfo systemInfo = new SystemInfo(
      "en",
      "Unknown",
      "Desktop",
      "1.0"
  );

  public boolean accepts(@Nullable String useCase) {
    if (useCase == null) {
      return true;
    }
    return useCases.stream()
        .anyMatch(useCase::equalsIgnoreCase);
  }

}