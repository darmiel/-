package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.Collections;
import org.drinkless.tdlib.TdApi.MessageSticker;
import org.drinkless.tdlib.TdApi.MessageVenue;
import org.drinkless.tdlib.TdApi.Sticker;
import org.drinkless.tdlib.TdApi.Venue;

public class StickerMessageTypeWrapper implements MessageTypeWrapper<MessageSticker> {

  @Override
  public int getConstructor() {
    return MessageSticker.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.STICKER;
  }

  @Override
  public void execute(final MessageSticker content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    final Sticker sticker = content.sticker;

    // a sticker message can only be represented by an emoji
    builder.textCaption("[Sticker " + sticker.emoji + "]");

    final JsonObject object = new JsonObject();
    object.addProperty("setId", sticker.setId);
    object.addProperty("width", sticker.width);
    object.addProperty("height", sticker.height);
    object.addProperty("animated", sticker.isAnimated);

    extra.add("sticker", object);

    // files
    builder.files(Collections.singleton(sticker.sticker));
  }
}
