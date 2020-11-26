package io.d2a.schwurbelwatch.mods.cdn;

import io.d2a.schwurbelwatch.tgcrawler.api.SwApi;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.AnsiColor;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.io.File;
import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.Error;
import org.drinkless.tdlib.TdApi.LocalFile;
import org.drinkless.tdlib.TdApi.Object;

public class DownloadFileResult implements ResultHandler {

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

    // base64 id
    final String fileUid = tdFile.remote.id;

    // check if downloaded file exists
    final File file = new File(localFile.path);
    if (!file.exists()) {
      Logger.warn("File exists but well no");
      return;
    }

    final int fileId = tdFile.id;

    // get message id and delete from map
    final long messageId = FileDownloadModule.messageFiles.getOrDefault(fileId, -1L);
    FileDownloadModule.messageFiles.entrySet().removeIf(e -> e.getKey() == fileId);

    Logger.debug("Message " + AnsiColor.ANSI_RED + messageId + AnsiColor.ANSI_RESET +
        " belongs to file " + AnsiColor.ANSI_BLUE + fileId);

    // if no message was found, delete file
    if (messageId == -1) {
      Logger.warn("Deleting file, because no message was found: " +
          AnsiColor.ANSI_CYAN + file.delete());
      return;
    }

    // get extension
    final String oldFileName = file.getName();
    final String extension = oldFileName.contains(".")
        ? oldFileName.substring(oldFileName.lastIndexOf(".")).trim()
        : oldFileName;

    // generate a new file name:
    // uid of remote file + extension
    final String newFileName = fileUid + extension;

    // check if destination file already exists
    final File destFile = new File(FILES_DIRECTORY, newFileName);
    if (destFile.exists()) {
      Logger.warn("Destination file already exists!");
      return;
    }

    // move file to new directory with new name
    if (file.renameTo(destFile)) {
      Logger.success("Done!");

      SwApi.callDatabaseResult(SwApi.FILE_SERVICE.addFile(new io.d2a.schwurbelwatch.tgcrawler.api.files.File(
          fileUid,
          messageId,
          newFileName,
          tdFile.expectedSize,
          System.currentTimeMillis()
      )));

    } else {
      Logger.warn("Done! But not quite yet. Dunno why.");
    }
  }
}