/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.d2a.schwurbelwatch.tgcrawler.core.client.ClientRouter;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.module.ModuleRegistry;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import lombok.Getter;
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

  // Load native for telegram
  static {
    try {
      System.loadLibrary("tdjni");
    } catch (UnsatisfiedLinkError e) {
      e.printStackTrace();
    }
  }

  public BotMain() {
    final long stopwatchStart = System.currentTimeMillis();

    BotMain.instance = this;

    try {
      start();
    } catch (Throwable throwable) {
      // for now, we only want to log errors.
      Logger.error(throwable);
      System.out.println("catched throwable");
    }

    Logger.info("Done! Took " + (System.currentTimeMillis() - stopwatchStart) + " ms.");
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

  private void start() throws
      NoSuchMethodException,
      InstantiationException,
      IllegalAccessException,
      InvocationTargetException {

    // Telegram Client Kram
    Logger.info("Initializing TdApi ...");
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
    Logger.info("Creating clients ...");
    clientRouter = new ClientRouter();
    clientRouter.createClients();

    // load modules
    Logger.info("Loading modules ...");
    ModuleRegistry.loadModules();

    if (ModuleRegistry.getEnablabledModules().size() == 0) {
      Logger.warn("No modules enabled.");
    }
  }

}