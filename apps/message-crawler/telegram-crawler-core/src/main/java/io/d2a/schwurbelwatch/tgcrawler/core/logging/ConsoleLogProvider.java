package io.d2a.schwurbelwatch.tgcrawler.core.logging;

import lombok.Getter;
import lombok.Setter;

public class ConsoleLogProvider implements LoggerProvider {

  private static final String seperator = " | ";
  private static final String textColor = AnsiColor.ANSI_RESET;

  @Setter
  @Getter
  private LogLevel minimumLevel = LogLevel.DEBUG;

  @Override
  public void log(final String message, final LogLevel level) {
    // check level before printing the message
    if (level.getLevel() < this.getMinimumLevel().getLevel()) {
      return;
    }

    final StringBuilder builder = new StringBuilder();

    if (level.getConsolePrefix() != null) {
      builder.append(level.getConsolePrefix());
    }

    // make the prefixes a little bit more even
    for (int i = level.name().length(); i < 8; i++) {
      builder.append(" ");
    }

    builder.append(level.name());
    builder.append(AnsiColor.ANSI_RESET);

    builder.append(seperator);
    builder.append(textColor);
    builder.append(message);
    builder.append(AnsiColor.ANSI_RESET);

    System.out.println(builder);
  }

}