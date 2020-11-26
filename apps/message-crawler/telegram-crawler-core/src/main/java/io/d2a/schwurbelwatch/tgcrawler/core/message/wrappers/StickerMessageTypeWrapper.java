package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.FileMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.MessageSticker;
import org.drinkless.tdlib.TdApi.Sticker;

public class StickerMessageTypeWrapper implements FileMessageTypeWrapper<MessageSticker> {

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
    object.addProperty("isAnimated", sticker.isAnimated);

    extra.add("sticker", object);

    // files
    builder.file(sticker.sticker);
  }

  @Override
  public boolean downloadFile() {
    return false;
  }
}
