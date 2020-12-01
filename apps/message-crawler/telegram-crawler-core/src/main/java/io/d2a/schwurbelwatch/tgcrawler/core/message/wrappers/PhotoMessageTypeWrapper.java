package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.FileMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.LinkedHashSet;
import java.util.Set;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.MessagePhoto;
import org.drinkless.tdlib.TdApi.Photo;
import org.drinkless.tdlib.TdApi.PhotoSize;

public class PhotoMessageTypeWrapper implements FileMessageTypeWrapper<MessagePhoto> {

  public static final int MAX_SIZE_IN_BYTES = 50_000_000;

  @Override
  public Class<MessagePhoto> getTypeClass() {
    return MessagePhoto.class;
  }

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

    PhotoSize biggest = null;
    for (final PhotoSize size : photo.sizes) {
      if (biggest == null
          || size.width > biggest.width
          || size.height > biggest.height) {

        // check size
        if (size.photo.expectedSize > maxDownloadSize()) {
          continue;
        }

        biggest = size;
      }
    }

    if (biggest != null) {
      builder.file(biggest.photo);

      final JsonObject object = new JsonObject();
      object.addProperty("type", biggest.type);
      object.addProperty("width", biggest.width);
      object.addProperty("height", biggest.height);

      extra.add("photo", object);
    }
  }

  @Override
  public boolean downloadFile() {
    return true;
  }

}
