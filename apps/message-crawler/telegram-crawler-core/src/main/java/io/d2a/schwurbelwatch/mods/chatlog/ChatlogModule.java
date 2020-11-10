package io.d2a.schwurbelwatch.mods.chatlog;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.tgcrawler.api.SwApi;
import io.d2a.schwurbelwatch.tgcrawler.api.messages.MessageService;
import io.d2a.schwurbelwatch.tgcrawler.api.other.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.message.DefaultChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import okhttp3.ResponseBody;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.UpdateDeleteMessages;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;
import retrofit2.Call;
import retrofit2.Response;

@Module(
    name = "Messages 2 Database",
    description = "Stores all message to the database via rest-api",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class ChatlogModule extends BotModule {

  public static final String CLIENT_NAME = "walterheldcorona";
  private TelegramClient client;

  private final MessageService service = SwApi.MESSAGE_SERVICE;

  private final Map<Integer, ContentType> contentTypeMap = new HashMap<>();

  @Override
  public void onEnable() {
    // Main account for listening for messages: walterheldcorona
    final Optional<TelegramClient> clientOptional = findTelegramClient(CLIENT_NAME);
    if (clientOptional.isPresent()) {
      this.client = clientOptional.get();
      this.client.registerListeners(this);
      Logger.success("Registered listener for client: " + this.client);
    } else {
      Logger.warn("Client '" + CLIENT_NAME + "' not found");
    }

    updateContentTypes();
  }

  @SneakyThrows
  private void updateContentTypes() {
    // Update content types
    final Response<List<ContentType>> execute = SwApi.BASE_SERVICE.getContentTypes().execute();
    final List<ContentType> body = execute.body();
    if (body == null) {
      Logger.error("Failed to update content type. Response:");
      Logger.error(execute);
      return;
    }

    contentTypeMap.clear();
    for (final ContentType contentType : body) {
      contentTypeMap.put(contentType.typeId, contentType);
    }

    Logger.info("Updated Content Types: " + contentTypeMap.size() + " types found.");
  }


  @Subscribe
  public void onMessage(final UpdateNewMessage event) {
    final Message message = event.message;
    final DefaultChatMessage wrap = DefaultChatMessage.wrap(message);
    // Logger.value("-> " + wrap.toString());

    final io.d2a.schwurbelwatch.tgcrawler.api.messages.Message msg = new io.d2a.schwurbelwatch.tgcrawler.api.messages.Message();
    msg.messageId = message.id;
    msg.chatId = message.chatId;
    msg.userId = message.senderUserId;

    msg.contentType = 0;

    // content type
    for (final ContentType value : contentTypeMap.values()) {
      if (value.type.equalsIgnoreCase(wrap.getType())) {
        msg.contentType = value.typeId;
        break;
      }
    }

    if (msg.contentType == 0) {
      Logger.warn("No content type found for message. Using default '0' (unknown)");
    }

    msg.content = wrap.getTextCaption();
    msg.date = System.currentTimeMillis() / 1000;
    msg.deletedOn = 0;
    msg.isChannelPost = message.isChannelPost ? 1 : 0;

    Logger.info("Adding message to database:");
    Logger.info(msg);
    final Call<DatabaseResult> call = service.addMessage(msg);
    try {
      final Response<DatabaseResult> execute = call.execute();
      Logger.success("Done:");
      Logger.success(execute);
      if (execute.code() != 200) {
        Logger.warn("Nope:");
        Logger.warn(execute.raw());
        Logger.warn(execute.raw().message());
        final ResponseBody object = execute.errorBody();
        Logger.warn(object);
        if (object != null) {
          Logger.warn(object.string());
        }
      } else {
        Logger.success("Done!");
      }
    } catch (IOException e) {
      Logger.error("Error:");
      Logger.error(e);
    }
  }

  @Subscribe
  public void onDelete(final UpdateDeleteMessages event) {
    if (event.fromCache || !event.isPermanent) {
      return;
    }

    final JsonObject obj = new JsonObject();
    obj.addProperty("deleted_on", (System.currentTimeMillis() / 1000));

    for (final long messageId : event.messageIds) {
      Logger.info("Deleting message " + messageId);
      final Call<DatabaseResult> call = service.updateMessage(messageId, obj);
      try {
        final Response<DatabaseResult> execute = call.execute();
        Logger.success("Done!:");
        Logger.success(execute);
        Logger.success(execute.body());
        Logger.success(execute.errorBody());
      } catch (IOException e) {
        Logger.error("Error!:");
        Logger.error(e);
      }
    }
  }

}
