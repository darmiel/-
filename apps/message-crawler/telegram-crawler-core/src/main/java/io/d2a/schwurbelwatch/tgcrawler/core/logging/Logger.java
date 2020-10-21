package io.d2a.schwurbelwatch.tgcrawler.core.logging;

public class Logger {

  public static final LoggerProvider[] PROVIDERS = {
      new ConsoleLogProvider()
  };

  private static void broadcast(final LogLevel level, final String message) {
    for (final LoggerProvider provider : PROVIDERS) {
      provider.log(message, level);
    }
  }

  public static void log(final String message) {
    broadcast(LogLevel.INFO, message);
  }

  public static void log(final Object object) {
    if (object != null) {
      log(object.toString());
    } else {
      log("null");
    }
  }

  public static void debug(final String message) {
    broadcast(LogLevel.DEBUG, message);
  }

  public static void debug(final Object object) {
    if (object != null) {
      debug(object.toString());
    } else {
      debug("null");
    }
  }

  public static void info(final String message) {
    log(message);
  }

  public static void info(final Object object) {
    if (object != null) {
      info(object.toString());
    } else {
      info("null");
    }
  }

  public static void warn(final String message) {
    broadcast(LogLevel.WARN, message);
  }

  public static void warn(final Object object) {
    if (object != null) {
      warn(object.toString());
    } else {
      warn("null");
    }
  }

  public static void error(final String message) {
    broadcast(LogLevel.ERROR, message);
  }

  public static void error(final Object object) {
    if (object != null) {
      error(object.toString());
    } else {
      error("null");
    }
  }


}