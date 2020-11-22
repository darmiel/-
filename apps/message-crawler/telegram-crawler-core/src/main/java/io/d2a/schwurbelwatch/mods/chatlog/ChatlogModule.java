package io.d2a.schwurbelwatch.mods.chatlog;

import com.google.common.eventbus.Subscribe;
import com.google.gson.JsonObject;
import io.d2a.schwurbelwatch.mods.Accounts;
import io.d2a.schwurbelwatch.tgcrawler.api.SwApi;
import io.d2a.schwurbelwatch.tgcrawler.api.messages.Message;
import io.d2a.schwurbelwatch.tgcrawler.api.messages.MessageService;
import io.d2a.schwurbelwatch.tgcrawler.api.other.ContentType;
import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.SneakyThrows;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.GetMessage;
import org.drinkless.tdlib.TdApi.UpdateDeleteMessages;
import org.drinkless.tdlib.TdApi.UpdateMessageEdited;
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

  private TelegramClient client;

  private final MessageService service = SwApi.MESSAGE_SERVICE;

  private final Map<Integer, ContentType> contentTypeMap = new HashMap<>();

  @Override
  public void onEnable() {
    // Main account for listening for messages: walterheldcorona
    final Optional<TelegramClient> clientOptional = findTelegramClient(Accounts.WATCHER_1);
    if (clientOptional.isPresent()) {
      this.client = clientOptional.get();
      this.client.registerListeners(this);
      Logger.success("Registered listener for client: " + this.client);
    } else {
      Logger.warn("Client '" + Accounts.WATCHER_1 + "' not found");
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

  private void updateInsertMessage (final TdApi.Message tdMessage) {
    final Message msg = Message.wrap(tdMessage, contentTypeMap);
    SwApi.callDatabaseResult(service.addMessage(msg));
  }

  @Subscribe
  public void onMessage(final UpdateNewMessage event) {
   updateInsertMessage(event.message);
  }

  @Subscribe
  public void onEdit(final UpdateMessageEdited event) {
    client.getClient().send(new GetMessage(event.chatId, event.messageId), object -> {
      if (object.getConstructor() == TdApi.Message.CONSTRUCTOR) {
        updateInsertMessage((TdApi.Message) object);
      }
    });
  }

  @Subscribe
  public void onDelete(final UpdateDeleteMessages event) {
    if (event.fromCache || !event.isPermanent) {
      return;
    }

    final JsonObject obj = new JsonObject();
    obj.addProperty("deleted_on", (System.currentTimeMillis()));

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
