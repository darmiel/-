package io.d2a.schwurbelwatch.mods.cdn;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.mods.Accounts;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.AnsiColor;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.message.SimpleChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.DownloadFile;
import org.drinkless.tdlib.TdApi.Error;
import org.drinkless.tdlib.TdApi.LocalFile;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.Object;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

@Module(
    name = "Messages 2 Database",
    description = "Stores all message to the database via rest-api",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class FileDownloadModule extends BotModule {

  private TelegramClient client;

  private static final Map<Long, Integer> messageFiles = new HashMap<>();
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
    messageFiles.put(scm.getMessageId(), file.id);

    Logger.debug(AnsiColor.ANSI_BLUE + "^^ " +
        AnsiColor.ANSI_YELLOW + "Downloading file: " +
        AnsiColor.ANSI_GREEN + file.id + AnsiColor.ANSI_RESET);

    this.client.getClient().send(new DownloadFile(
            file.id, ++priority, 0, 0, true
        ),
        new DownloadFileResult()
    );
  }

  private static final class DownloadFileResult implements ResultHandler {

    public static final File FILES_DIRECTORY = new File("data", "files");

    @Override
    public void onResult(final Object object) {
      if (object.getConstructor() == Error.CONSTRUCTOR) {
        Logger.error(AnsiColor.ANSI_RED + "Error downloading file: " +
            ((Error) object).message + AnsiColor.ANSI_BLACK +
            " [#" + ((Error) object).code + "]");
        return;
      }

      // retrieved file?
      if (object.getConstructor() != TdApi.File.CONSTRUCTOR) {
        return;
      }

      // cast to file
      final TdApi.File tdFile = (TdApi.File) object;
      final LocalFile localFile = tdFile.local;

      if (!localFile.isDownloadingCompleted) {
        Logger.warn("Oops! Download not (yet) comleted!");
        return;
      }

      final String fileUid = tdFile.remote.id;

      final File file = new File(localFile.path);
      if (!file.exists()) {
        Logger.warn("File exists but well no");
        return;
      }

      final int fileId = tdFile.id;
      long messageId = -1;
      for (final Entry<Long, Integer> next : messageFiles.entrySet()) {
        if (next.getValue() == fileId) {
          messageId = next.getKey();
        }
      }
      Logger.debug("Message " + AnsiColor.ANSI_RED + messageId + AnsiColor.ANSI_RESET +
          " belongs to file " + AnsiColor.ANSI_BLUE + fileId);

      // if no message was found, delete file
      if (messageId == -1) {
        Logger.warn("Deleting file, because no message was found: " +
            AnsiColor.ANSI_CYAN + file.delete());
        return;
      }

      final String oldFileName = file.getName();

      // get extension
      final String extension = oldFileName.contains(".")
          ? oldFileName.substring(oldFileName.lastIndexOf(".")).trim()
          : oldFileName;

      final String newFileName = fileUid + extension;

      // move file
      final File destFile = new File(FILES_DIRECTORY, newFileName);
      if (destFile.exists()) {
        Logger.warn("Destination file already exists!");
        return;
      }

      if (file.renameTo(destFile)) {
        Logger.success("Done!");
      } else {
        Logger.warn("Done! But not quite yet. Dunno why.");
      }
    }
  }


}