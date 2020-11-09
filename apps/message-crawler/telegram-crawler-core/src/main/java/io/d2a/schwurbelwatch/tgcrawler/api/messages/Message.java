package io.d2a.schwurbelwatch.tgcrawler.api.messages;

import com.google.gson.annotations.SerializedName;
import lombok.ToString;

@ToString
public class Message {

  public int messageId;
  public int chatId;
  public int userId;

  @SerializedName("content_type")
  public int contentType;

  public String content;

  public long date;
  @SerializedName("deleted_on")
  public long deletedOn;

  @SerializedName("is_channel_post")
  public int isChannelPost;

  public boolean isDeleted() {
    return this.deletedOn > 0;
  }
  public boolean isChannelPost() {
    return this.isChannelPost == 1;
  }

}