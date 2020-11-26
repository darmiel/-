/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.message;

import static io.d2a.schwurbelwatch.tgcrawler.core.BotMain.GSON;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.util.Set;
import javax.annotation.Nonnull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.MessageSender;
import org.drinkless.tdlib.TdApi.MessageSenderChat;
import org.drinkless.tdlib.TdApi.MessageSenderUser;

@Getter
@Builder
@ToString
public class SimpleChatMessage {

  /* Meta */
  private final long chatId;
  private final long messageId;
  private final long senderId;

  // dates
  private final int sentDate;
  private final int editDate;

  /* Content */
  private final ContentType type;
  private final String textCaption;

  /* File */
  private final transient TdApi.File file;

  /* Extra Info */
  private final JsonObject extraG;

  public boolean hasFiles() {
    return this.file != null;
  }

  public static SimpleChatMessage wrap(@Nonnull Message message) {

    // get content type
    final ContentType type = ContentType.getType(message.content);
    if (type == null) {
      Logger.warn("Invalid message: " + message);
      Logger.warn(" -> ContentType not found.");
      return null;
    }

    final SimpleChatMessageBuilder res = SimpleChatMessage.builder()
        .chatId(message.chatId)
        .messageId(message.id)
        .sentDate(message.date)
        .editDate(message.editDate)
        .type(type);

    // wrap message
    final JsonObject extra = new JsonObject();
    type.wrapAction(message.content, res, extra);
    res.extraG(extra);

    // get id of sender of message
    final MessageSender sender = message.sender;
    if (sender instanceof MessageSenderUser) {
      final MessageSenderUser messageSenderUser = (MessageSenderUser) sender;
      res.senderId(messageSenderUser.userId);
    } else if (sender instanceof MessageSenderChat) {
      final MessageSenderChat messageSenderChat = (MessageSenderChat) sender;
      res.senderId(messageSenderChat.chatId);
    } else {
      Logger.warn("Invalid sender for message: " + message);
      return null;
    }

    return res.build();
  }

}