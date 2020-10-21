package io.d2a.schwurbelwatch.tgcrawler.core.logging;

public interface LoggerProvider {

  LogLevel getMinimumLevel();

  void log(final String message, final LogLevel level);

}