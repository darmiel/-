package io.d2a.schwurbelwatch.mods.chatlog;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.message.DefaultChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.GetUser;
import org.drinkless.tdlib.TdApi.GetUserFullInfo;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

@Module(
    name = "Messages 2 Console",
    description = "Sends all message to the console",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class ConsoleMessageModule extends BotModule {

  public static final String CLIENT_NAME = "walterheldcorona";
  private TelegramClient client;

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
  }

  /*
  @Subscribe
  public void onMessage(final UpdateNewMessage event) {
    final Message message = event.message;
    final DefaultChatMessage wrap = DefaultChatMessage.wrap(message);
    // Logger.value("-> " + wrap.toString());
  }

  private final Set<Integer> requestedUsers = new HashSet<>();

  @Subscribe
  public void onMessage2(final UpdateNewMessage updateNewMessage) {
    final Message message = updateNewMessage.message;
    final int senderUserId = message.senderUserId;

    if (requestedUsers.contains(senderUserId)) {
      Logger.value("Already requested!");
      return;
    }
    requestedUsers.add(senderUserId);

    Logger.debug("Requesting normal-info for user...");
    this.client.getClient().send(
        new GetUser(senderUserId),
        Logger::value
    );
    Logger.debug("Requesting full-info for user ...");
    this.client.getClient().send(
        new GetUserFullInfo(senderUserId),
        Logger::value
    );
  }
  */

  @Subscribe
  public void any(final TdApi.Object object) {
    Logger.debug(object);
  }
}
