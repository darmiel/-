package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.Collections;
import org.drinkless.tdlib.TdApi.Audio;
import org.drinkless.tdlib.TdApi.MessageAudio;
import org.drinkless.tdlib.TdApi.MessageVideo;
import org.drinkless.tdlib.TdApi.Video;

public class VideoMessageTypeWrapper implements MessageTypeWrapper<MessageVideo> {

  @Override
  public int getConstructor() {
    return MessageVideo.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.VIDEO;
  }

  @Override
  public void execute(final MessageVideo content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    builder.textCaption(content.caption.text);

    // file
    final Video video = content.video;
    builder.file(video.video);

    // extra info
    final JsonObject videoObject = new JsonObject();
    videoObject.addProperty("duration", video.duration);
    videoObject.addProperty("width", video.width);
    videoObject.addProperty("height", video.height);
    videoObject.addProperty("fileName", video.fileName);
    videoObject.addProperty("mimeType", video.mimeType);

    extra.add("video", videoObject);
  }
}
