/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.d2a.schwurbelwatch.tgcrawler.core.client.ClientRouter;
import io.d2a.schwurbelwatch.tgcrawler.core.config.Configs;
import io.d2a.schwurbelwatch.tgcrawler.core.database.MySqlDatabase;
import io.d2a.schwurbelwatch.tgcrawler.core.module.ModuleRegistry;
import java.io.IOError;
import java.io.IOException;
import lombok.Getter;
import lombok.SneakyThrows;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.LogStreamFile;
import org.drinkless.tdlib.TdApi.Object;
import org.drinkless.tdlib.TdApi.SetLogStream;

public class BotMain {

  public static final String LOG_STREAM_FILE = "tdlib.log";

  /**
   * Gson used for config files, etc.
   */
  public static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .serializeNulls()
      .setDateFormat("dd.MM.yyyy HH:mm:ss")
      .create();

  @Getter
  private static BotMain instance;

  @Getter
  private static ClientRouter clientRouter;

  /**
   * MySQL Client for chatlogs, etc.
   */
  @Getter
  private MySqlDatabase database;

  // Load native for telegram
  static {
    try {
      System.loadLibrary("tdjni");
    } catch (UnsatisfiedLinkError e) {
      e.printStackTrace();
    }
  }

  @SneakyThrows
  public BotMain() {
    BotMain.instance = this;

    // Config Kram
    System.out.println("> Config Kram");
    {
      Configs.databaseConfig = Configs.loadAndSaveDefault(
          Configs.DB_FILE,
          Configs.databaseConfig
      );
    }

    // Database Kram
    // ignore for testing purposes
    System.out.println("> Database Kram");
    {
      /* MySQL */
      // Open database
      database = new MySqlDatabase(Configs.databaseConfig.getHikariConfig());
      
      // Check if connection is open
      if (!database.getDataSource().isRunning()) {
        System.out.println("MySQL Database not connected.");
        System.exit(1);
        return;
      }
    }

    // Telegram Client Kram
    System.out.println("> TG Client Kram");
    {
      // Set verbosity to 3
      Client.execute(new TdApi.SetLogVerbosityLevel(3));

      // Set log file to ./tdlib.log
      final Object setLogStreamResult = Client.execute(
          new SetLogStream(new LogStreamFile(LOG_STREAM_FILE, 1 << 27))
      );

      if (setLogStreamResult instanceof TdApi.Error) {
        throw new IOError(new IOException("Write access to the current directory is required"));
      }
    }

    // Load telegram clients
    System.out.println("> Router");
    clientRouter = new ClientRouter();
    clientRouter.createClients();

    System.out.println("> Loading modules");
    // load modules
    ModuleRegistry.loadModules();

    System.out.println("> Done");
    Runtime.getRuntime().addShutdownHook(new Thread(ModuleRegistry::unloadModulesUnsafe));
  }

  /**
   * Main
   *
   * @param args Args to pass
   */
  public static void main(String[] args) {
    new BotMain();
  }

}