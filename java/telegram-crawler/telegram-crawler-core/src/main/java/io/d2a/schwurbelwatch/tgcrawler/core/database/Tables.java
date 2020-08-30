/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.core.database;

import lombok.Getter;

public enum Tables {
    MESSAGES("tgwtj_messages");

    @Getter
    String name;

    Tables(String name) {
      this.name = name;
    }

  @Override
  public String toString() {
    return "`" + name + "`";
  }
}