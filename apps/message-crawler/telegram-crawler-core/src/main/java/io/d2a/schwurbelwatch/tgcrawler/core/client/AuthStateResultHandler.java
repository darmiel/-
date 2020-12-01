package io.d2a.schwurbelwatch.tgcrawler.core.client;

import io.d2a.schwurbelwatch.tgcrawler.core.logging.AnsiColor;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import javax.annotation.Nonnull;
import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;

public class AuthStateResultHandler implements ResultHandler {

  private final TelegramClient client;

  public AuthStateResultHandler(@Nonnull final TelegramClient client) {
    this.client = client;
  }

  @Override
  public void onResult(final TdApi.Object object) {
    switch (object.getConstructor()) {
      case TdApi.Error.CONSTRUCTOR:
        Logger.error(AnsiColor.ANSI_RED + "Received an error | AuthStateResultHandler:");
        Logger.error(object);

      case TdApi.Ok.CONSTRUCTOR:
        break;

      default:
        Logger.warn("Receive wrong response from TDLib for client " +
            this.client.toString() + ":\n" + object);
    }
  }

}