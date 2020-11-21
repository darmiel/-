package io.d2a.schwurbelwatch.tgcrawler.core.client;

import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;

public class AuthStateResultHandler implements ResultHandler {

  private final TelegramClient client;

  public AuthStateResultHandler(final TelegramClient client) {
    this.client = client;
  }

  @Override
  public void onResult(final TdApi.Object object) {
    switch (object.getConstructor()) {
      case TdApi.Error.CONSTRUCTOR:
        System.err.println("Receive an error:\n" + object);

        // result is already received through UpdateAuthorizationState, nothing to do
      case TdApi.Ok.CONSTRUCTOR:
        break;

      default:
        System.err.println("Receive wrong response from TDLib for client " + this.client.toString() + ":\n" + object);
    }
  }

}