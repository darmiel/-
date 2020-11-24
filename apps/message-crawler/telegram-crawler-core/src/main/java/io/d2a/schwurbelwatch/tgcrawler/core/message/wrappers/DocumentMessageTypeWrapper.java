package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.Collections;
import org.drinkless.tdlib.TdApi.Document;
import org.drinkless.tdlib.TdApi.MessageDocument;
import org.drinkless.tdlib.TdApi.MessageSticker;
import org.drinkless.tdlib.TdApi.Sticker;

public class DocumentMessageTypeWrapper implements MessageTypeWrapper<MessageDocument> {

  @Override
  public int getConstructor() {
    return MessageDocument.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.DOCUMENT;
  }

  @Override
  public void execute(final MessageDocument content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    builder.textCaption(content.caption.text);

    // extra
    final Document document = content.document;
    final JsonObject object = new JsonObject();
    object.addProperty("filename", document.fileName);
    object.addProperty("mime", document.mimeType);

    extra.add("document", object);

    // files
    builder.files(Collections.singleton(document.document));
  }
}
