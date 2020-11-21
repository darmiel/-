/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.client;

import static io.d2a.schwurbelwatch.tgcrawler.core.BotMain.GSON;

import com.google.gson.JsonSyntaxException;
import io.d2a.schwurbelwatch.tgcrawler.core.config.ClientConfig;
import io.d2a.schwurbelwatch.tgcrawler.core.config.TelegramConfig;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import javax.annotation.Nonnull;

public class ClientRouter {

  // file to load clients from
  public static final File CONFIG_FILE = new File("config", "telegram.json");

  private final Map<String, ClientConfig> clientConfigMap = new HashMap<>();
  private final Map<String, TelegramClient> accountMap = new HashMap<>();

  /**
   * Returns the string in lower-case and stripped
   *
   * @param clientName Input string
   * @return Lower-Case + Stripped
   */
  private String formatClientName(@Nonnull final String clientName) {
    return clientName.toLowerCase().trim();
  }

  public Optional<ClientConfig> findConfiguration(@Nonnull final String clientName) {
    final String key = formatClientName(clientName);
    return Optional.ofNullable(this.clientConfigMap.get(key));
  }

  public Optional<TelegramClient> findTelegramClient(@Nonnull final String clientName) {
    final String key = formatClientName(clientName);

    // account already initialized
    if (this.accountMap.containsKey(key)) {
      return Optional.of(this.accountMap.get(key));
    }

    final Optional<ClientConfig> configuration = findConfiguration(clientName);
    if (!configuration.isPresent()) {
      Logger.warn("Tried to find client '" + clientName + "/" + key + "' but it was not found.");
      return Optional.empty();
    }

    // create client
    Logger.debug("Creating client ...");
    final ClientConfig clientConfig = configuration.get();
    final String databaseDirectory = clientConfig.getDatabaseDirectory();
    final TelegramClient client = TelegramClient.create(
        key,
        clientConfig.getCredentials(),
        clientConfig.getSystemInfo(),
        databaseDirectory
    );
    Logger.debug("Client created.");

    // cache account for later use
    this.accountMap.put(key, client);

    return Optional.of(client);
  }

  /////////////////////////////////////////////////////////////////////////////////////////////////

  public void updateConfiguration(final boolean hardExit) {
    try (final FileReader fileReader = new FileReader(CONFIG_FILE)) {
      final TelegramConfig config = GSON.fromJson(fileReader, TelegramConfig.class);

      if (config.getSystemInfo() == null) {
        throw new NullPointerException("SystemInfo (info) was null");
      }

      // Load accounts
      for (final Entry<String, ClientConfig> entry : config.getClientConfigs().entrySet()) {
        final String key = entry.getKey().toLowerCase().trim();
        final ClientConfig value = entry.getValue();

        if (this.clientConfigMap.containsKey(key)) {
          Logger.warn("Rewriting client config for: '" + key + "'");
        }

        this.clientConfigMap.put(key, value);
        Logger.info("Loaded configuration info '" + key + "': " + value);
      }
    } catch (JsonSyntaxException e) {
      Logger.error("[TelegramConfig] Invalid JSON syntax");
      Logger.error(e);

      if (hardExit) {
        System.exit(1);
      }
    } catch (Exception e) {
      Logger.error("[TelegramConfig] Exception:");
      Logger.error(e);

      e.printStackTrace();

      if (hardExit) {
        System.exit(1);
      }
    }
  }

}