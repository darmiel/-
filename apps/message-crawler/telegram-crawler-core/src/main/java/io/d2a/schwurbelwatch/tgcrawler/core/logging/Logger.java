package io.d2a.schwurbelwatch.tgcrawler.core.logging;

import javax.annotation.Nullable;

public class Logger {

  public static final LoggerProvider[] PROVIDERS = {
      new ConsoleLogProvider()
  };

  private static void broadcast(final LogLevel level, final String message) {
    for (final LoggerProvider provider : PROVIDERS) {
      provider.log(message, level);
    }
  }

  public static void log(@Nullable final String message) {
    broadcast(LogLevel.INFO, message);
  }

  public static void log(@Nullable final Object object) {
    if (object != null) {
      log(object.toString());
    } else {
      log("null");
    }
  }

  public static void debug(@Nullable final String message) {
    broadcast(LogLevel.DEBUG, message);
  }

  public static void debug(@Nullable final Object object) {
    if (object != null) {
      debug(object.toString());
    } else {
      debug("null");
    }
  }

  public static void info(@Nullable final String message) {
    log(message);
  }

  public static void info(@Nullable final Object object) {
    if (object != null) {
      info(object.toString());
    } else {
      info("null");
    }
  }

  public static void warn(@Nullable final String message) {
    broadcast(LogLevel.WARN, message);
  }

  public static void warn(@Nullable final Object object) {
    if (object != null) {
      if (object instanceof Throwable) {
        final Throwable throwable = (Throwable) object;
        warn(throwable.getClass().getName() + ": " + throwable.getMessage());
        throwable.printStackTrace();
      } else {
        warn(object.toString());
      }
    } else {
      warn("null");
    }
  }

  public static void error(final String message) {
    broadcast(LogLevel.ERROR, message);
  }

  public static void error(final Object object) {
    if (object != null) {
      if (object instanceof Throwable) {
        final Throwable throwable = (Throwable) object;
        error(throwable.getClass().getName() + ": " + throwable.getMessage());
        throwable.printStackTrace();
      } else {
        error(object.toString());
      }
    } else {
      error("null");
    }
  }

  public static void value(final String message) {
    broadcast(LogLevel.VALUE, message);
  }

  public static void value(final Object object) {
    if (object != null) {
      value(object.toString());
    } else {
      value("null");
    }
  }

  public static void success(final String message) {
    broadcast(LogLevel.SUCCESS, message);
  }

  public static void success(final Object object) {
    if (object != null) {
      success(object.toString());
    } else {
      success("null");
    }
  }


}