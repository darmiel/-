package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import java.util.Collections;
import org.drinkless.tdlib.TdApi.Animation;
import org.drinkless.tdlib.TdApi.MessageAnimation;

public class AnimationMessageTypeWrapper implements MessageTypeWrapper<MessageAnimation> {

  @Override
  public int getConstructor() {
    return MessageAnimation.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.ANIMATION;
  }

  @Override
  public void execute(final MessageAnimation content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    builder.textCaption(content.caption.text);

    // file
    final Animation animation = content.animation;
    builder.files(Collections.singleton(animation.animation));

    // extra info
    final JsonObject animationObject = new JsonObject();
    animationObject.addProperty("duration", animation.duration);
    animationObject.addProperty("width", animation.width);
    animationObject.addProperty("height", animation.height);
    animationObject.addProperty("filename", animation.fileName);
    animationObject.addProperty("mime", animation.mimeType);
    animationObject.addProperty("stickers", animation.hasStickers);

    extra.add("animation", animationObject);
  }
}