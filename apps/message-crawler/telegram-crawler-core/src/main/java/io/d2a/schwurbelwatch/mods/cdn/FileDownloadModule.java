package io.d2a.schwurbelwatch.mods.cdn;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.mods.Accounts;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.io.File;
import java.util.Optional;
import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.LocalFile;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.MessageContent;
import org.drinkless.tdlib.TdApi.MessagePhoto;
import org.drinkless.tdlib.TdApi.Object;
import org.drinkless.tdlib.TdApi.PhotoSize;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

@Module(
    name = "Messages 2 Database",
    description = "Stores all message to the database via rest-api",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class FileDownloadModule extends BotModule {

  private TelegramClient client;

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

  private static final class DownloadFileResult implements ResultHandler {

    public static final File FILES_DIRECTORY = new File("data", "files");

    @Override
    public void onResult(final Object object) {
      if (object.getConstructor() != TdApi.File.CONSTRUCTOR) {
        return;
      }

      // cast to file
      final TdApi.File tdFile = (TdApi.File) object;
      final LocalFile localFile = tdFile.local;

      if (!localFile.isDownloadingCompleted) {
        Logger.warn("Download not (yet) comleted!");
        return;
      }

      final File file = new File(localFile.path);
      if (!file.exists()) {
        Logger.warn("File exists but well no");
        return;
      }

      // move file
      Logger.success("Download compelte. Moving file ...");
      final boolean bool = file.renameTo(new File(FILES_DIRECTORY, file.getName()));
      if (bool) {
        Logger.success("Done!");
      } else {
        Logger.warn("Done! But not quite yet");
      }
    }
  }

  @Subscribe
  public void onMessage(final UpdateNewMessage event) {
    final Message message = event.message;
    final MessageContent content = message.content;

    if (content instanceof MessagePhoto) {
      Logger.info("Got a photo!");
      final MessagePhoto photoMessage = (MessagePhoto) content;

      // because we want max quality, filter photo size with biggest dimensions
      TdApi.File downloadFile = null;
      for (final PhotoSize size : photoMessage.photo.sizes) {
        final TdApi.File photo = size.photo;
        if (downloadFile == null) {
          downloadFile = photo;
        } else if (photo.expectedSize > downloadFile.expectedSize) {
          downloadFile = photo;
        }
      }

      if (downloadFile != null) {
        Logger.debug("Biggest size: " + downloadFile.expectedSize);

        this.client.getClient().send(
            new TdApi.DownloadFile(downloadFile.id, 32, 0, 0, true),
            new DownloadFileResult()
        );
      }

    }
  }

}