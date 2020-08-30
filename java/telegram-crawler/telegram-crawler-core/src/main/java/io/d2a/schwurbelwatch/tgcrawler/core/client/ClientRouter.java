/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.client;

import static io.d2a.schwurbelwatch.tgcrawler.core.BotMain.GSON;

import com.google.common.collect.Maps;
import com.google.gson.JsonSyntaxException;
import io.d2a.schwurbelwatch.tgcrawler.core.client.router.ClientConfig;
import io.d2a.schwurbelwatch.tgcrawler.core.auth.ApiCredentials;
import io.d2a.schwurbelwatch.tgcrawler.core.config.Configs;
import java.io.File;
import java.io.FileReader;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

public class ClientRouter {

  // file to load clients from
  public static final File CONFIG_FILE = new File(Configs.CONFIG_DIR, "telegram.json");

  private Map<TelegramClient, ClientConfig> clients = Maps.newHashMap();
  private TelegramConfig config;

  public ClientRouter() {

    try (final FileReader fileReader = new FileReader(CONFIG_FILE)) {
      this.config = GSON.fromJson(fileReader, TelegramConfig.class);

      if (this.config.getSystemInfo() == null) {
        throw new NullPointerException("SystemInfo (info) was null");
      }
    } catch (JsonSyntaxException e) {
      System.out.println("[TelegramConfig] Invalid JSON syntax");
      System.exit(1);
    } catch (Exception e) {
      System.out.println("[TelegramConfig] Exception:");
      System.out.println(e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }

  }

  public void createClients() {
    for (final ClientConfig config : config.getClientConfigs()) {

      if (this.clients.values().stream()
          .map(ClientConfig::getCredentials)
          .map(ApiCredentials::getPhoneNumber)
          .anyMatch(ph -> ph.equalsIgnoreCase(config.getCredentials().getPhoneNumber()))) {

        System.out.println("[ClientRouter] Warning: Duplicate TelegramClient in Router");
        continue;
      }

      final String databaseDirectory = config.getDatabaseDirectory() != null
          ? config.getDatabaseDirectory()
          : this.config.getDatabaseDirectory();

      final TelegramClient client = TelegramClient.create(
          config.getCredentials(),
          config.getSystemInfo(),
          databaseDirectory
      );

      this.clients.put(client, config);

    }
  }

  public Set<TelegramClient> findClients(String useCase) {
    return this.clients.entrySet().stream()
        .filter(entry -> entry.getValue().accepts(useCase))
        .map(Entry::getKey)
        .collect(Collectors.toSet());
  }

}