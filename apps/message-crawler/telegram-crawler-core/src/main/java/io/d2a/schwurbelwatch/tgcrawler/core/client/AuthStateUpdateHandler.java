package io.d2a.schwurbelwatch.tgcrawler.core.client;

import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.AuthorizationState;
import org.drinkless.tdlib.TdApi.UpdateAuthorizationState;

public class AuthStateUpdateHandler {

  private final TelegramClient client;

  public AuthStateUpdateHandler(final TelegramClient client) {
    this.client = client;
  }

  /**
   * This function waits for input
   *
   * @param prompt The text shown on the prompt
   * @return The typed text
   */
  private static String promptString(String prompt) {
    System.out.print(prompt);

    final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    String str = "";
    try {
      str = reader.readLine();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return str;
  }

  /**
   * Creates a new result handler with the current telegram client object
   * @return New instance of ResultHandler
   */
  private ResultHandler result() {
    return new AuthStateResultHandler(this.client);
  }

  public void onAuthorizationStateUpdated(final UpdateAuthorizationState state) {
    Logger.debug("");
    Logger.debug("Auth State Updated:");
    Logger.debug(state);
    Logger.debug("");

    final AuthorizationState authorizationState = state.authorizationState;

    // update cached auth state?
    if (client.authorizationState != null) {
      client.authorizationState = authorizationState;
    }

    // Alias for client
    final Client client = this.client.getClient();

    switch (authorizationState.getConstructor()) {

      // Send "credentials"
      case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
        TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
        parameters.databaseDirectory = this.client.databaseDirectory;

        /* Api Credentials */
        parameters.apiId = this.client.getCredentials().getApiId();
        parameters.apiHash = this.client.getCredentials().getApiHash();

        /* System Info */
        parameters.systemLanguageCode = this.client.getSystemInfo().getSystemLanguageCode();
        parameters.systemVersion = this.client.getSystemInfo().getSystemVersion();
        parameters.deviceModel = this.client.getSystemInfo().getDeviceModel();
        parameters.applicationVersion = this.client.getSystemInfo().getApplicationVersion();

        /* Other Settings */
        parameters.useMessageDatabase = true;
        parameters.useSecretChats = true;
        parameters.enableStorageOptimizer = true;

        Logger.debug("Sending auth request:");
        Logger.debug(this.client.getCredentials());
        Logger.debug(this.client.getSystemInfo());

        client.send(new TdApi.SetTdlibParameters(parameters), result());
        break;

      // Check Database Encryption Key (by default: changeme1234)
      case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
        client.send(new TdApi.CheckDatabaseEncryptionKey(), result());
        break;

      // Send phone number
      case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
        client.send(new TdApi.SetAuthenticationPhoneNumber(
                this.client.getCredentials().getPhoneNumber(),
                null
            ), result()
        );
        break;
      }

      // Login Link
      case TdApi.AuthorizationStateWaitOtherDeviceConfirmation.CONSTRUCTOR: {
        final String link = ((TdApi.AuthorizationStateWaitOtherDeviceConfirmation) authorizationState).link;
        Logger.info("Please confirm this login link on another device: " + link);
        break;
      }

      // Auth code
      case TdApi.AuthorizationStateWaitCode.CONSTRUCTOR: {
        final String code = promptString("Please enter authentication code: ");
        client.send(new TdApi.CheckAuthenticationCode(code), result());
        break;
      }

      case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
        final String password = promptString("Please enter password: ");
        client.send(new TdApi.CheckAuthenticationPassword(password), result());
        break;
      }

      // Auth ready
      case TdApi.AuthorizationStateReady.CONSTRUCTOR:
        this.client.setLoggedIn(true);
        this.client.authorizationLock.lock();
        try {
          this.client.gotAuthorization.signal();
        } finally {
          this.client.authorizationLock.unlock();
        }
        break;

      // Log out
      case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
        this.client.setLoggedIn(false);
        Logger.info("Logging out");
        break;

      // Closing
      case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
        this.client.setLoggedIn(false);
        Logger.info("Closing");
        break;

      // Auth closed
      case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
        Logger.info("Closed");
        if (this.client.isReconnectOnError()) {
          this.client.recreateClient(); // recreate client after previous has closed
        }
        break;
      default:
        System.err.println("Unsupported authorization state:\n" + authorizationState);
    }
  }

}
