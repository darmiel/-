/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.message;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.AnimationMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.AudioMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.ContactMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.DocumentMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.LocationMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.PhotoMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.PollMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.StickerMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.TextMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.VenueMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers.VideoMessageTypeWrapper;
import javax.annotation.Nonnull;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.MessageAnimation;
import org.drinkless.tdlib.TdApi.MessageAudio;
import org.drinkless.tdlib.TdApi.MessageContact;
import org.drinkless.tdlib.TdApi.MessageContent;
import org.drinkless.tdlib.TdApi.MessageDocument;
import org.drinkless.tdlib.TdApi.MessageLocation;
import org.drinkless.tdlib.TdApi.MessagePhoto;
import org.drinkless.tdlib.TdApi.MessagePoll;
import org.drinkless.tdlib.TdApi.MessageSticker;
import org.drinkless.tdlib.TdApi.MessageText;
import org.drinkless.tdlib.TdApi.MessageVenue;
import org.drinkless.tdlib.TdApi.MessageVideo;

public enum ContentType {

  TEXT("text", new TextMessageTypeWrapper(), MessageText.CONSTRUCTOR),
  AUDIO("audio", new AudioMessageTypeWrapper(), MessageAudio.CONSTRUCTOR),
  PHOTO("photo", new PhotoMessageTypeWrapper(), MessagePhoto.CONSTRUCTOR),
  VIDEO("video", new VideoMessageTypeWrapper(), MessageVideo.CONSTRUCTOR),
  POLL("poll", new PollMessageTypeWrapper(), MessagePoll.CONSTRUCTOR),
  LOCATION("location", new LocationMessageTypeWrapper(), MessageLocation.CONSTRUCTOR),
  VENUE("venue", new VenueMessageTypeWrapper(), MessageVenue.CONSTRUCTOR),
  STICKER("sticker", new StickerMessageTypeWrapper(), MessageSticker.CONSTRUCTOR),
  DOCUMENT("document", new DocumentMessageTypeWrapper(), MessageDocument.CONSTRUCTOR),
  CONTACT("contact", new ContactMessageTypeWrapper(), MessageContact.CONSTRUCTOR),
  ANIMATION("animation", new AnimationMessageTypeWrapper(), MessageAnimation.CONSTRUCTOR);

  public final String identifier;
  public final int constructor;
  public final MessageTypeWrapper<? extends TdApi.MessageContent> wrapper;

  ContentType(String identifier,
      MessageTypeWrapper<? extends TdApi.MessageContent> wrapper,
      int constructor) {

    // identifier
    String id = identifier.toLowerCase();
    if (id.startsWith("@")) {
      id = id.substring(1);
    }
    this.identifier = id;

    this.wrapper = wrapper;
    this.constructor = constructor;
  }

  public static ContentType getType(@Nonnull final MessageContent content) {
    for (final ContentType value : values()) {
      if (value.constructor == content.getConstructor()) {
        return value;
      }
    }
    return null;
  }

  public void wrapAction(final TdApi.MessageContent content,
      final SimpleChatMessage.SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    this.wrapper.execute(content, builder, extra);
  }

}