/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.message;

import static io.d2a.schwurbelwatch.tgcrawler.core.BotMain.GSON;

import lombok.Getter;
import lombok.ToString;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.MessageAudio;
import org.drinkless.tdlib.TdApi.MessageContent;
import org.drinkless.tdlib.TdApi.MessagePhoto;
import org.drinkless.tdlib.TdApi.MessagePoll;
import org.drinkless.tdlib.TdApi.MessageSender;
import org.drinkless.tdlib.TdApi.MessageSenderChat;
import org.drinkless.tdlib.TdApi.MessageSenderUser;
import org.drinkless.tdlib.TdApi.MessageSticker;
import org.drinkless.tdlib.TdApi.MessageText;
import org.drinkless.tdlib.TdApi.MessageVideo;
import org.drinkless.tdlib.TdApi.Sticker;

@Getter
@ToString
public class SimpleChatMessage {

  /* Meta */
  private long chatId;
  private long messageId;
  private long senderId;
  private int sentDate;
  private int editDate;

  /* Content */
  private String type;
  private String textCaption;
  private String extra;
  private String raw;
  private String forwardInfo;

  /* Deleted? */
  private long deletedAt = 0L;

  public static SimpleChatMessage wrap(Message message) {
    final SimpleChatMessage res = new SimpleChatMessage();

    /* Meta */
    res.chatId = message.chatId;
    res.messageId = message.id;

    // get id of sender of message
    final MessageSender sender = message.sender;
    if (sender instanceof MessageSenderUser) {
      final MessageSenderUser messageSenderUser = (MessageSenderUser) sender;
      res.senderId = messageSenderUser.userId;
    } else if (sender instanceof MessageSenderChat) {
      final MessageSenderChat messageSenderChat = (MessageSenderChat) sender;
      res.senderId = messageSenderChat.chatId;
    }

    res.sentDate = message.date;
    res.editDate = message.editDate;

    /* Content */
    final MessageContent content = message.content;

    ContentType type = null;
    String text = null;
    String extra = null;

    // Normal Text
    // Photo
    // Audio
    switch (content.getConstructor()) {

      // Normal Text
      case MessageText.CONSTRUCTOR:
        final MessageText messageText = (MessageText) content;
        text = messageText.text.text;

        type = ContentType.TEXT;
        break;

      // Photo
      case MessagePhoto.CONSTRUCTOR:
        final MessagePhoto messagePhoto = (MessagePhoto) content;
        text = messagePhoto.caption.text;

        type = ContentType.PHOTO;
        break;

      // Audio
      case MessageAudio.CONSTRUCTOR:
        final MessageAudio messageAudio = (MessageAudio) content;
        text = messageAudio.caption.text;

        type = ContentType.AUDIO;

        extra = messageAudio.audio.fileName;
        break;
      case MessagePoll.CONSTRUCTOR:
        final MessagePoll messagePoll = (MessagePoll) content;
        text = messagePoll.poll.question;

        type = ContentType.POLL;

        extra = GSON.toJson(messagePoll.poll.options);
        break;
      case MessageVideo.CONSTRUCTOR:
        final MessageVideo messageVideo = (MessageVideo) content;
        text = messageVideo.caption.text;

        type = ContentType.VIDEO;

        extra = messageVideo.video.fileName;
        break;
      case MessageSticker.CONSTRUCTOR:
        final Sticker sticker = ((MessageSticker) content).sticker;
        text = "[Sticker: " + sticker.emoji + ", " + sticker.width + "x" + sticker.height +  "]";
        type = ContentType.STICKER;
        break;
    }

    res.type = type != null ? type.identifier : null;
    res.textCaption = text;
    res.extra = extra;

    res.forwardInfo = GSON.toJson(message.forwardInfo);

    // if this works,
    // i dunno
    res.raw = GSON.toJson(message);

    return res;
  }

  public boolean wasDeleted() {
    return deletedAt > 0;
  }

}