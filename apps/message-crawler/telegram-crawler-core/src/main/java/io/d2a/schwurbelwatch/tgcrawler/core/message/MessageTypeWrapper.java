package io.d2a.schwurbelwatch.tgcrawler.core.message;

import com.google.gson.JsonObject;
import org.drinkless.tdlib.TdApi.MessageContent;

public interface MessageTypeWrapper<CT> {

  int getConstructor();
  ContentType getContentType();

  void execute (
      final CT content,
      final SimpleChatMessage.SimpleChatMessageBuilder builder,
      final JsonObject extra
  );

  default void execute(final MessageContent content,
      final SimpleChatMessage.SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    this.execute((CT) content, builder, extra);
  }

}