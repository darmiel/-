/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.client;

import com.google.common.eventbus.EventBus;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.ApiCredentials;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.SystemInfo;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;

public class TelegramClient implements Client.ResultHandler {

  // Lock
  private final Lock authorizationLock = new ReentrantLock();
  private final Condition gotAuthorization = authorizationLock.newCondition();
  //

  @Getter
  private final ApiCredentials credentials;

  @Getter
  private final SystemInfo systemInfo;

  /**
   * EventBus for listeners and telegram events
   */
  @Getter
  private final EventBus eventBus;

  /**
   * Should the client try to reconnect if the connection was lost or the authentication failed
   */
  @Getter
  @Setter
  private boolean reconnectOnError = true;

  /**
   * is the client currently logged in?
   */
  @Getter
  private boolean loggedIn = false;

  /**
   * Telegram client
   */
  @Getter
  private Client client;

  /**
   * Directory to store tdlib database files
   */
  private String databaseDirectory;

  /**
   * Current telegram authorization state
   */
  private TdApi.AuthorizationState authorizationState = null;

  private TelegramClient(
      final ApiCredentials credentials,
      final SystemInfo systemInfo,
      final String databaseDirectory
  ) {
    this.credentials = credentials;
    this.systemInfo = systemInfo;
    this.databaseDirectory = databaseDirectory;

    this.eventBus = new EventBus(String.format("EB/%s", credentials.getPhoneNumber()));

    // create client
    recreateClient();
  }

  public static TelegramClient create(
      final ApiCredentials credentials,
      final SystemInfo systemInfo,
      @Nullable String databaseDirectory
  ) {

    // Database directory default
    if (databaseDirectory == null) {
      databaseDirectory = "tglibdata";
    }

    return new TelegramClient(
        credentials,
        systemInfo,
        databaseDirectory
    );
  }

  /////////////////////////////////////////////////////////////////////////////////////////////

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

  /////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * connects the client
   */
  public void recreateClient() {
    this.client = Client.create(
        this,
        null,
        null
    );
  }

  /**
   * Registers an listener
   *
   * @param objects Object to register listeners inside
   */
  public void registerListeners(Object... objects) {
    for (final Object object : objects) {
      this.eventBus.register(object);
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////

  /**
   * Callback called on result of query to TDLib or incoming update from TDLib.
   *
   * @param object Result of query or update of type TdApi.Update about new events.
   */
  @Override
  public void onResult(final org.drinkless.tdlib.TdApi.Object object) {
    this.eventBus.post(object);

    switch (object.getConstructor()) {
      case TdApi.Error.CONSTRUCTOR:
        System.err.println("Receive an error:\n" + object);

        // result is already received through UpdateAuthorizationState, nothing to do
      case TdApi.Ok.CONSTRUCTOR:
        break;

      default:
        System.err.println("Receive wrong response from TDLib:\n" + object);
    }
  }

  /**
   * Call this when the authorization state changes
   *
   * @param authorizationState Auth-State
   */
  public void onAuthorizationStateUpdated(@NonNull TdApi.AuthorizationState authorizationState) {

    // update cached auth state?
    if (this.authorizationState != null) {
      this.authorizationState = authorizationState;
    }

    // Alias for client
    final Client client = this.getClient();

    switch (authorizationState.getConstructor()) {

      // Send "credentials"
      case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
        TdApi.TdlibParameters parameters = new TdApi.TdlibParameters();
        parameters.databaseDirectory = this.databaseDirectory;

        /* Api Credentials */
        parameters.apiId = this.credentials.getApiId();
        parameters.apiHash = this.credentials.getApiHash();

        /* System Info */
        parameters.systemLanguageCode = this.systemInfo.getSystemLanguageCode();
        parameters.systemVersion = this.systemInfo.getSystemVersion();
        parameters.deviceModel = this.systemInfo.getDeviceModel();
        parameters.applicationVersion = this.systemInfo.getApplicationVersion();

        /* Other Settings */
        parameters.useMessageDatabase = true;
        parameters.useSecretChats = true;
        parameters.enableStorageOptimizer = true;

        Logger.debug("Sending auth request:");
        Logger.debug(this.credentials);
        Logger.debug(this.systemInfo);

        client.send(new TdApi.SetTdlibParameters(parameters), this);
        break;

      // Check Database Encryption Key (by default: changeme1234)
      case TdApi.AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
        client.send(new TdApi.CheckDatabaseEncryptionKey(), this);
        break;

      // Send phone number
      case TdApi.AuthorizationStateWaitPhoneNumber.CONSTRUCTOR: {
        client.send(new TdApi.SetAuthenticationPhoneNumber(
                this.credentials.getPhoneNumber(),
                null
            ), this
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
        client.send(new TdApi.CheckAuthenticationCode(code), this);
        break;
      }

      case TdApi.AuthorizationStateWaitPassword.CONSTRUCTOR: {
        final String password = promptString("Please enter password: ");
        client.send(new TdApi.CheckAuthenticationPassword(password),
            this);
        break;
      }

      // Auth ready
      case TdApi.AuthorizationStateReady.CONSTRUCTOR:
        loggedIn = true;
        authorizationLock.lock();
        try {
          gotAuthorization.signal();
        } finally {
          authorizationLock.unlock();
        }
        break;

      // Log out
      case TdApi.AuthorizationStateLoggingOut.CONSTRUCTOR:
        loggedIn = false;
        Logger.info("Logging out");
        break;

      // Closing
      case TdApi.AuthorizationStateClosing.CONSTRUCTOR:
        loggedIn = false;
        Logger.info("Closing");
        break;

      // Auth closed
      case TdApi.AuthorizationStateClosed.CONSTRUCTOR:
        Logger.info("Closed");
        if (this.isReconnectOnError()) {
          this.recreateClient(); // recreate client after previous has closed
        }
        break;
      default:
        System.err.println("Unsupported authorization state:\n" + authorizationState);
    }
  }

}