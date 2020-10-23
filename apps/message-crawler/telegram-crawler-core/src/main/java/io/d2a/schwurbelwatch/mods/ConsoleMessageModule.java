package io.d2a.schwurbelwatch.mods;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.message.DefaultChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.util.Optional;
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

  @Override
  public void onEnable() {
    // Main account for listening for messages: walterheldcorona
    final Optional<TelegramClient> clientOptional = findTelegramClient(CLIENT_NAME);
    if (clientOptional.isPresent()) {
      final TelegramClient client = clientOptional.get();
      client.registerListeners(this);
      Logger.success("Registered listener for client: " + client);
    } else {
      Logger.warn("Client '" + CLIENT_NAME + "' not found");
    }
  }

  @Subscribe
  public void onMessage(final UpdateNewMessage event) {
    final Message message = event.message;
    final DefaultChatMessage wrap = DefaultChatMessage.wrap(message);
    Logger.value("-> " + wrap.toString());
  }

}
