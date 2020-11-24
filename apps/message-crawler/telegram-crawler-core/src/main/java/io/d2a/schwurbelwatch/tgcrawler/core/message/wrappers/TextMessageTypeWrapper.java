package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.MessageText;

public class TextMessageTypeWrapper implements MessageTypeWrapper<TdApi.MessageText> {

  @Override
  public int getConstructor() {
    return MessageText.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.TEXT;
  }

  @Override
  public void execute(final MessageText content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    builder.textCaption(content.text.text);
  }

}