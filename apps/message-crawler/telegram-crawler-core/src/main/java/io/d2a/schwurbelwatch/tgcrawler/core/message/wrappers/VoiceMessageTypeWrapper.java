package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.FileMessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.MessageVoiceNote;
import org.drinkless.tdlib.TdApi.VoiceNote;

public class VoiceMessageTypeWrapper implements FileMessageTypeWrapper<MessageVoiceNote> {

  @Override
  public Class<MessageVoiceNote> getTypeClass() {
    return MessageVoiceNote.class;
  }

  @Override
  public int getConstructor() {
    return MessageVoiceNote.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.VOICE;
  }

  @Override
  public void execute(final MessageVoiceNote content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    builder.textCaption(content.caption.text);

    // extra
    final VoiceNote voiceNote = content.voiceNote;
    final JsonObject voiceObject = new JsonObject();
    voiceObject.addProperty("duration", voiceNote.duration);
    voiceObject.addProperty("mimeType", voiceNote.mimeType);

    extra.add("audio", voiceObject);

    // file
    builder.file(voiceNote.voice);
  }

  @Override
  public boolean downloadFile() {
    return true;
  }

}
