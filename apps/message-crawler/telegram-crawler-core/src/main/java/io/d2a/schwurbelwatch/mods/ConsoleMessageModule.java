package io.d2a.schwurbelwatch.mods;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.tgcrawler.core.BotMain;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.message.DefaultChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

@Module(
    name = "Messages 2 Console",
    description = "Sends all message to the console",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class ConsoleMessageModule extends BotModule {

  @Override
  public void onEnable() {
    for (final TelegramClient client : BotMain.getClientRouter().findClients(null)) {
      Logger.debug("Registering listener for client: " + client);
      client.registerListeners(this);
    }
  }

  @Subscribe
  public void onMessage(final UpdateNewMessage event) {
    final Message message = event.message;
    final DefaultChatMessage wrap = DefaultChatMessage.wrap(message);
    Logger.value("-> " + wrap.toString());
  }

}
