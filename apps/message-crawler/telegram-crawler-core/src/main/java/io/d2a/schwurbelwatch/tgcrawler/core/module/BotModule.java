/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.module;

import com.google.common.collect.Sets;
import io.d2a.schwurbelwatch.tgcrawler.core.BotMain;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import java.util.Optional;
import java.util.Set;
import lombok.Getter;

public abstract class BotModule {

  @Getter
  private final Set<TelegramClient> clients = Sets.newHashSet();

  /**
   * Returns the first client in the clients-set
   *
   * @return TelegramClient
   */
  private Optional<TelegramClient> getFirstClient() {
    TelegramClient client = null;
    for (final TelegramClient telegramClient : clients) {
      client = telegramClient;
      break;
    }
    return Optional.ofNullable(client);
  }

  /**
   * Loads all clients for the module
   *
   * @param clients Set of TelegramClients to override current set
   */
  public void loadClients(Set<TelegramClient> clients) {
    this.clients.removeIf(a -> true);
    this.clients.addAll(clients);
  }

  public BotMain getParent() {
    return BotMain.getInstance();
  }

  public void registerListeners(Object... objects) {
    clients.forEach(c -> c.registerListeners(objects));
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
   * Called when the clients are load for the module
   *
   * @param clients The clients accepted for the module
   */
  public void onClientLoad(final Set<TelegramClient> clients) {

  }

}