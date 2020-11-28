package io.d2a.schwurbelwatch.tgcrawler.api.routes.chats;

import com.google.gson.annotations.SerializedName;
import lombok.Builder;
import lombok.ToString;

@Builder
@ToString
public class ApiChat {

  public final Long chatId;
  public final Integer groupId;

  public final String username;
  public final Integer date;
  public final String title;
  public final String description;

  @SerializedName("member_count")
  public final Integer memberCount;

  @SerializedName("type")
  public final ChatType type;

  @SerializedName("is_verified")
  public final Byte isVerified;

  @SerializedName("is_scam")
  public final Byte isScam;

  @SerializedName("last_updated")
  public final long lastUpdated;

  public final Byte monitor;

}