package io.d2a.schwurbelwatch.tgcrawler.api.routes.messages;

import com.google.gson.annotations.SerializedName;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.base.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.ToString;
import org.drinkless.tdlib.TdApi;

@ToString
public class ApiMessage {

  public long messageId;
  public long chatId;
  public long userId;

  @SerializedName("reply_to")
  public long replyTo;

  @SerializedName("content_type")
  public int contentType;

  public String content;

  public long date;
  @SerializedName("deleted_on")
  public long deletedOn;

  @SerializedName("is_channel_post")
  public int isChannelPost;

  @SerializedName("extra")
  public String extra;

  public boolean isDeleted() {
    return this.deletedOn > 0;
  }

  public boolean isChannelPost() {
    return this.isChannelPost == 1;
  }

  @Nullable
  public static ApiMessage wrap(
      @Nonnull final TdApi.Message tdMessage,
      @Nonnull Map<Integer, ContentType> contentTypeMap) {

    final SimpleChatMessage dcm = SimpleChatMessage.wrap(tdMessage);
    if (dcm == null) {
      return null;
    }

    final ApiMessage msg = new ApiMessage();

    // content type
    for (final ContentType value : contentTypeMap.values()) {
      if (value.type.equalsIgnoreCase(dcm.getType().identifier)) {
        msg.contentType = value.typeId;
        break;
      }
    }

    msg.messageId = tdMessage.id;
    msg.chatId = tdMessage.chatId;
    msg.userId = dcm.getSenderId();

    msg.replyTo = tdMessage.replyToMessageId;

    msg.content = dcm.getTextCaption();

    msg.date = tdMessage.date * 1000L;
    msg.isChannelPost = tdMessage.isChannelPost ? 1 : 0;

    if (dcm.getExtraG() != null) {
      msg.extra = dcm.getExtraG().toString();
    }

    return msg;
  }

}