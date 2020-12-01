/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.client;

import com.google.common.eventbus.EventBus;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.ApiCredentials;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.SystemInfo;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.UpdateAuthorizationState;

@ToString
public class TelegramClient implements Client.ResultHandler {

  //region ## Auth State Update ##
  /**
   * Current telegram authorization state
   */
  TdApi.AuthorizationState authorizationState = null;
  volatile boolean haveAuthorization = false;

  // Lock
  final Lock authorizationLock = new ReentrantLock();
  final Condition gotAuthorization = authorizationLock.newCondition();

  /**
   * is the client currently logged in?
   */
  @Getter
  @Setter
  private boolean loggedIn = false;
  //endregion

  //region ## Config ##
  @Getter
  private final String identifier;

  @Getter
  private final ApiCredentials credentials;

  @Getter
  private final SystemInfo systemInfo;

  /**
   * Should the client try to reconnect if the connection was lost or the authentication failed
   */
  @Getter
  @Setter
  private boolean reconnectOnError = true;

  /**
   * Directory to store tdlib database files
   */
  final String databaseDirectory;
  //endregion

  //region ## Handlers ##
  private final AuthStateUpdateHandler stateUpdateHandler = new AuthStateUpdateHandler(this);
  //endregion

  /**
   * EventBus for listeners and telegram events
   */
  @Getter
  private final EventBus eventBus;


  /**
   * Telegram client
   */
  @Getter
  private Client client;

  private TelegramClient(
      @Nonnull final String identifier,
      @Nonnull final ApiCredentials credentials,
      @Nonnull final SystemInfo systemInfo,
      @Nullable final String databaseDirectory
  ) {
    this.identifier = identifier;

    this.credentials = credentials;
    this.systemInfo = systemInfo;
    this.databaseDirectory = databaseDirectory;

    this.eventBus = new EventBus(String.format("EB/%s", this.identifier));
    this.eventBus.register(this);

    // create client
    recreateClient();
  }

  /////////////////////////////////////////////////////////////////////////////////////////////

  @Nonnull
  public static TelegramClient create(
      @Nonnull final String identifier,
      @Nonnull final ApiCredentials credentials,
      @Nonnull final SystemInfo systemInfo
  ) {

    final String databaseDirectory = "data/tglibdata@" + identifier;

    return new TelegramClient(
        identifier,
        credentials,
        systemInfo,
        databaseDirectory
    );
  }

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

  /////////////////////////////////////////////////////////////////////////////////////////////

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
  public void onResult(final TdApi.Object object) {
    if (object instanceof UpdateAuthorizationState) {
      stateUpdateHandler.onAuthorizationStateUpdated((UpdateAuthorizationState) object);
      return; // we do not want to send these types of requests to the eventbus
    }

    this.eventBus.post(object);
  }

}