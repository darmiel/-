package io.d2a.schwurbelwatch.tgcrawler.api.routes.chats;

import javax.annotation.Nonnull;
import lombok.Getter;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.ChatTypeBasicGroup;
import org.drinkless.tdlib.TdApi.ChatTypePrivate;
import org.drinkless.tdlib.TdApi.ChatTypeSecret;
import org.drinkless.tdlib.TdApi.ChatTypeSupergroup;

public enum ChatType {
  UNKNOWN(-1),
  BASIC(ChatTypeBasicGroup.CONSTRUCTOR),
  PRIVATE(ChatTypePrivate.CONSTRUCTOR),
  SECRET(ChatTypeSecret.CONSTRUCTOR),
  SUPER(ChatTypeSupergroup.CONSTRUCTOR);

  @Getter
  private final int constructor;

  ChatType(int constructor) {
    this.constructor = constructor;
  }

  @Nonnull
  public static ChatType getType(final TdApi.ChatType type) {
    for (final ChatType value : values()) {
      if (value.constructor == type.getConstructor()) {
        return value;
      }
    }
    return UNKNOWN;
  }
}