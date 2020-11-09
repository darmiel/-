package io.d2a.schwurbelwatch.tgcrawler.api.response;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

public class ErrorResult {

  @Getter
  public boolean error;

  @SerializedName("message")
  public String errorMessage;

}
