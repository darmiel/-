package io.d2a.schwurbelwatch.tgcrawler.api.files;

import com.google.gson.annotations.SerializedName;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class File {

  private final String fileUid;
  private final long messageId;

  @SerializedName("cdn_path")
  private final String cdnPath;
  private final long size;

  private final long downloaded;

}