package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.Collections;
import org.drinkless.tdlib.TdApi.Audio;
import org.drinkless.tdlib.TdApi.MessageAudio;

public class AudioMessageTypeWrapper implements MessageTypeWrapper<MessageAudio> {

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
    audioObject.addProperty("filename", audio.fileName);
    audioObject.addProperty("mime", audio.mimeType);

    extra.add("audio", audioObject);

    // file
    builder.files(Collections.singleton(audio.audio));
  }
}
