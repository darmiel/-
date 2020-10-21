/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Module {

  String name();
  String description();
  String version();
  String author();

}