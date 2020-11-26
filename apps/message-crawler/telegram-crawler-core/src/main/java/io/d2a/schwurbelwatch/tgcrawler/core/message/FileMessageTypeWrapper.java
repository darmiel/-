package io.d2a.schwurbelwatch.tgcrawler.core.message;

public interface FileMessageTypeWrapper<CT> extends MessageTypeWrapper<CT> {

  int DEFAULT_MAX_SIZE = 15_000_000; // 50 MB

  default boolean downloadFile() {
    return false;
  }

  default int maxDownloadSize() {
    return DEFAULT_MAX_SIZE;
  }

}