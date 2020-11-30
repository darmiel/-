package io.d2a.schwurbelwatch.mods;

import io.d2a.schwurbelwatch.mods.cdn.FileDownloadModule;
import io.d2a.schwurbelwatch.mods.chatlog.ChatlogModule;
import io.d2a.schwurbelwatch.mods.chatlog.ConsoleMessageModule;
import io.d2a.schwurbelwatch.mods.updaters.ChatsUpdateModule;
import io.d2a.schwurbelwatch.mods.updaters.UserUpdateModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

public class ModMain {

  public static final Set<Class<? extends BotModule>> MODULES = new LinkedHashSet<>(
      Arrays.asList(
          ChatlogModule.class,
          ConsoleMessageModule.class,
          FileDownloadModule.class,

          // updates
          UserUpdateModule.class,
          ChatsUpdateModule.class
      ));

  public ModMain() {
    // called after all modules were loaded
  }

}