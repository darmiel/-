package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.Location;
import org.drinkless.tdlib.TdApi.MessageLocation;
import org.drinkless.tdlib.TdApi.MessageVenue;
import org.drinkless.tdlib.TdApi.Venue;

public class VenueMessageTypeWrapper implements MessageTypeWrapper<MessageVenue> {

  @Override
  public int getConstructor() {
    return MessageVenue.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.VENUE;
  }

  @Override
  public void execute(final MessageVenue content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // a venue has no direct caption, using title
    final Venue venue = content.venue;
    builder.textCaption(venue.title);

    final JsonObject object = new JsonObject();
    object.addProperty("address", venue.address);
    object.addProperty("provider", venue.provider);
    object.addProperty("id", venue.id);
    object.addProperty("type", venue.type);

    object.add("location", GlobalJsonWrappers.wrapLocation(venue.location, null));

    extra.add("venue", object);
  }
}
