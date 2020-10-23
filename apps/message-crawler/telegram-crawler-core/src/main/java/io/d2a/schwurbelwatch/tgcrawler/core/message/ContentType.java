/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.message;

import org.drinkless.tdlib.TdApi.MessageAudio;
import org.drinkless.tdlib.TdApi.MessageContact;
import org.drinkless.tdlib.TdApi.MessageContent;
import org.drinkless.tdlib.TdApi.MessageLocation;
import org.drinkless.tdlib.TdApi.MessagePhoto;
import org.drinkless.tdlib.TdApi.MessagePoll;
import org.drinkless.tdlib.TdApi.MessageText;
import org.drinkless.tdlib.TdApi.MessageVenue;
import org.drinkless.tdlib.TdApi.MessageVideo;

public enum ContentType {

  TEXT("text", MessageText.CONSTRUCTOR),
  AUDIO("audio", MessageAudio.CONSTRUCTOR),
  PHOTO("photo", MessagePhoto.CONSTRUCTOR),
  VIDEO("video", MessageVideo.CONSTRUCTOR),
  POLL("poll", MessagePoll.CONSTRUCTOR),
  LOCATION("location", MessageLocation.CONSTRUCTOR),
  VENUE("venue", MessageVenue.CONSTRUCTOR),
  CONTACT("contact", MessageContact.CONSTRUCTOR);

  String identifier;
  int[] constructors;

  ContentType(String identifier, int... constructors) {
    this.identifier = identifier.toLowerCase();
    this.constructors = constructors;

    if (this.identifier.startsWith("@")) {
      this.identifier = this.identifier.substring(1);
    }
  }

  public static ContentType getType(String identifier) {
    if (identifier.startsWith("@")) {
      identifier = identifier.substring(1).toLowerCase();
    }
    for (final ContentType value : values()) {
      if (value.identifier.equals(identifier)) {
        return value;
      }
    }
    return null;
  }

  public static ContentType getType(MessageContent content) {
    for (final ContentType value : values()) {
      for (final int constructor : value.constructors) {
        if (constructor == content.getConstructor()) {
          return value;
        }
      }
    }
    return null;
  }

}
