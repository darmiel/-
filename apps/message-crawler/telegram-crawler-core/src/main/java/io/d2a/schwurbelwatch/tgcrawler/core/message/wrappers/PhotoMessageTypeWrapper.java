package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.Audio;
import org.drinkless.tdlib.TdApi.MessageAudio;
import org.drinkless.tdlib.TdApi.MessagePhoto;
import org.drinkless.tdlib.TdApi.Photo;
import org.drinkless.tdlib.TdApi.PhotoSize;

public class PhotoMessageTypeWrapper implements MessageTypeWrapper<MessagePhoto> {

  @Override
  public int getConstructor() {
    return MessagePhoto.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.PHOTO;
  }

  @Override
  public void execute(final MessagePhoto content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    builder.textCaption(content.caption.text);

    // file
    final Photo photo = content.photo;
    final Set<TdApi.File> files = new LinkedHashSet<>();
    final JsonArray array = new JsonArray();
    for (final PhotoSize size : photo.sizes) {
      files.add(size.photo);

      final JsonObject object = new JsonObject();
      object.addProperty("type", size.type);
      object.addProperty("width", size.width);
      object.addProperty("height", size.height);
      array.add(object);
    }
    builder.files(files);

    extra.add("photo", array);
  }
}
