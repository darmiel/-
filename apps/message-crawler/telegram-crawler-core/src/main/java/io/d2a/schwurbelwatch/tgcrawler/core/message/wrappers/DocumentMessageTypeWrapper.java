package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.FileMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.Document;
import org.drinkless.tdlib.TdApi.MessageDocument;

public class DocumentMessageTypeWrapper implements FileMessageTypeWrapper<MessageDocument> {

  @Override
  public Class<MessageDocument> getTypeClass() {
    return MessageDocument.class;
  }

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
    object.addProperty("fileName", document.fileName);
    object.addProperty("mimeType", document.mimeType);

    extra.add("document", object);

    // files
    builder.file(document.document);
  }

  @Override
  public boolean downloadFile() {
    return true;
  }

}
