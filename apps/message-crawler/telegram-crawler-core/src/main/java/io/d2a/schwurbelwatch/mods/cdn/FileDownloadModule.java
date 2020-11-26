package io.d2a.schwurbelwatch.mods.cdn;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.mods.Accounts;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.AnsiColor;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.DownloadFile;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

@Module(
    name = "Messages 2 Database",
    description = "Stores all message to the database via rest-api",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class FileDownloadModule extends BotModule {

  private TelegramClient client;

  public static final Map<Integer, Long> messageFiles = new HashMap<>();
  private int priority = 1;

  @Override
  public void onEnable() {
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
  public void onMessage(final UpdateNewMessage event) {
    final Message message = event.message;

    final SimpleChatMessage scm = SimpleChatMessage.wrap(message);
    if (scm == null) {
      return;
    }

    // check if type has any files
    if (!scm.isFileValid()) {
      return;
    }

    // reset priority
    // range [1;32]
    if (this.priority > 32) {
      this.priority = 1;
    }

    final TdApi.File file = scm.getFile();

    // cache file id
    messageFiles.put(file.id, scm.getMessageId());

    Logger.debug(AnsiColor.ANSI_BLUE + "^^ " +
        AnsiColor.ANSI_YELLOW + "Downloading file: " +
        AnsiColor.ANSI_GREEN + file.id + AnsiColor.ANSI_RESET);

    this.client.getClient().send(new DownloadFile(
            file.id, ++priority, 0, 0, true
        ),
        new DownloadFileResult()
    );
  }

}