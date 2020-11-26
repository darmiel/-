package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.Location;
import org.drinkless.tdlib.TdApi.MessageLocation;
import org.drinkless.tdlib.TdApi.MessagePoll;
import org.drinkless.tdlib.TdApi.Poll;
import org.drinkless.tdlib.TdApi.PollOption;
import org.drinkless.tdlib.TdApi.PollTypeRegular;

public class LocationMessageTypeWrapper implements MessageTypeWrapper<MessageLocation> {

  @Override
  public int getConstructor() {
    return MessageLocation.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.LOCATION;
  }

  @Override
  public void execute(final MessageLocation content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // a location has no caption
    builder.textCaption(null);

    final Location location = content.location;
    final JsonObject value = GlobalJsonWrappers.wrapLocation(location, content.expiresIn);
    extra.add("location", value);
  }
}
