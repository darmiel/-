package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.FileMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.Audio;
import org.drinkless.tdlib.TdApi.MessageAudio;

public class AudioMessageTypeWrapper implements FileMessageTypeWrapper<MessageAudio> {

  @Override
  public Class<MessageAudio> getTypeClass() {
    return MessageAudio.class;
  }

  @Override
  public int getConstructor() {
    return MessageAudio.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.AUDIO;
  }

  @Override
  public void execute(final MessageAudio content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    builder.textCaption(content.caption.text);

    // extra
    final Audio audio = content.audio;
    final JsonObject audioObject = new JsonObject();
    audioObject.addProperty("duration", audio.duration);
    audioObject.addProperty("title", audio.title);
    audioObject.addProperty("fileName", audio.fileName);
    audioObject.addProperty("mimeType", audio.mimeType);

    extra.add("audio", audioObject);

    // file
    builder.file(audio.audio);
  }

  @Override
  public boolean downloadFile() {
    return true;
  }

}
