package io.d2a.schwurbelwatch.tgcrawler.api.routes.files;

import com.google.gson.annotations.SerializedName;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ApiFile {

  private final String fileUid;
  private final long messageId;

  @SerializedName("cdn_path")
  private final String cdnPath;
  private final long size;

  private final long downloaded;

}