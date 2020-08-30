/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
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