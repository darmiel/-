package io.d2a.schwurbelwatch.mods.chatlog;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.mods.Accounts;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.util.Optional;
import org.drinkless.tdlib.TdApi;

@Module(
    name = "Messages 2 Console",
    description = "Sends all message to the console",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class ConsoleMessageModule extends BotModule {

  private TelegramClient client;

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
  }

  @Subscribe
  public void any(final TdApi.Object object) {
    // Logger.debug(object);
  }
}
