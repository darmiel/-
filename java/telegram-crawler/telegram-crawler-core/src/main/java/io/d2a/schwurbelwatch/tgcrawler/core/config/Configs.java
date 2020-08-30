/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.core.config;

import com.google.gson.stream.JsonReader;
import io.d2a.schwurbelwatch.tgcrawler.core.BotMain;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configs {

  /**
   * Parent directory of all configs
   */
  public static final File CONFIG_DIR = new File("config");

  /**
   * Config for the telegram client
   */
  public static final File TELEGRAM_CONFIG_FILE = new File(CONFIG_DIR, "telegram.json");

  /**
   * Config for databases
   * (MySQL, Redis, Mongo, ...)
   */
  public static final File DB_FILE = new File(CONFIG_DIR, "dbs.json");

  /**
   * Config for modules
   * (Forwarder, ...)
   */
  public static final File MOD_FILE = new File(CONFIG_DIR, "modules.json");


  public static TelegramConfig telegramConfig = new TelegramConfig() /* <- Default config */;
  public static DatabaseConfig databaseConfig = new DatabaseConfig() /* <- Default config */;

  // Create directory if not exists
  static {
    assert CONFIG_DIR.exists() && CONFIG_DIR.isDirectory() || CONFIG_DIR.mkdirs();
  }

  // TODO: Change this ASAP
  public static <T> T loadAndSaveDefault(final File file, final T config) throws IOException {

    // Check if the config file already exists
    if (!file.exists()) {
      file.getParentFile().mkdirs();
      file.createNewFile();

      // Write config if file not exists
      try (final FileWriter fileWriter = new FileWriter(file)) {
        BotMain.GSON.toJson(config, fileWriter);
      }
    } else {
      // Load config from file with gson
      try (final FileReader fileReader = new FileReader(file);
          final JsonReader jsonReader = new JsonReader(fileReader)) {
        return BotMain.GSON.fromJson(jsonReader, config.getClass());
      }
    }

    return config;
  }

}