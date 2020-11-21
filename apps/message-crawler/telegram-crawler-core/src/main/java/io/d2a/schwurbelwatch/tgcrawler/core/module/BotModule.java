/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.module;

import io.d2a.schwurbelwatch.tgcrawler.core.BotMain;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.config.ClientConfig;
import java.util.Optional;
import javax.annotation.Nonnull;

public abstract class BotModule {

  public BotMain getParent() {
    return BotMain.getInstance();
  }

  /**
   * Called when loading module
   */
  public void onLoad() {

  }

  /**
   * Called after onLoad and onClientLoad
   */
  public abstract void onEnable();

  /**
   * Called when shutting down tgwtf
   */
  public void onDisable() {

  }

  /**
   * Alias to {@link io.d2a.schwurbelwatch.tgcrawler.core.client.ClientRouter#findConfiguration(String)}
   *
   * @param clientName Account name
   * @return Client configuration if found
   */
  public Optional<ClientConfig> findConfiguration(@Nonnull final String clientName) {
    return BotMain.getClientRouter().findConfiguration(clientName);
  }

  /**
   * Alias to {@link io.d2a.schwurbelwatch.tgcrawler.core.client.ClientRouter#findTelegramClient(String)}
   *
   * @param clientName Account name
   * @return Telegram client if found
   */
  public Optional<TelegramClient> findTelegramClient(@Nonnull final String clientName) {
    return BotMain.getClientRouter().findTelegramClient(clientName);
  }

}