package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.core.message.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.core.message.MessageTypeWrapper;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage.SimpleChatMessageBuilder;
import org.drinkless.tdlib.TdApi.MessagePoll;
import org.drinkless.tdlib.TdApi.Poll;
import org.drinkless.tdlib.TdApi.PollOption;
import org.drinkless.tdlib.TdApi.PollTypeRegular;

public class PollMessageTypeWrapper implements MessageTypeWrapper<MessagePoll> {

  @Override
  public Class<MessagePoll> getTypeClass() {
    return MessagePoll.class;
  }

  @Override
  public int getConstructor() {
    return MessagePoll.CONSTRUCTOR;
  }

  @Override
  public ContentType getContentType() {
    return ContentType.POLL;
  }

  @Override
  public void execute(final MessagePoll content,
      final SimpleChatMessageBuilder builder,
      final JsonObject extra) {

    // caption
    final Poll poll = content.poll;
    builder.textCaption(poll.question);

    final JsonArray array = new JsonArray();
    for (final PollOption option : poll.options) {
      final JsonObject object = new JsonObject();
      object.addProperty("text", option.text);
      object.addProperty("voterCount", option.voterCount);
      array.add(object);
    }

    final JsonObject object = new JsonObject();
    object.addProperty("id", poll.id);
    object.addProperty("totalVoterCount", poll.totalVoterCount);
    object.addProperty("isAnonymous", poll.isAnonymous);
    object.addProperty("isClosed", poll.isClosed);
    object.addProperty("closeDate", poll.closeDate);
    object.addProperty("openPeriod", poll.openPeriod);
    object.addProperty("type",
        poll.type.getConstructor() == PollTypeRegular.CONSTRUCTOR ? "REGULAR" : "QUIZ");

    object.add("options", array);

    extra.add("poll", object);
  }
}
