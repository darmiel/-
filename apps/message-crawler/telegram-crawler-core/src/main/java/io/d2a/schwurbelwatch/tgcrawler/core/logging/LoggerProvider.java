package io.d2a.schwurbelwatch.tgcrawler.core.logging;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface LoggerProvider {

  @Nonnull
  LogLevel getMinimumLevel();

  void log(@Nullable final String message, @Nullable final LogLevel level);

}