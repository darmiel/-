/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.modules.chatlog;

import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.listener.MessageListener;
import io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.repo.MessageRepository;

@Module(
    name = "ChatLog",
    description = "Saves all messages written in any chat to a mysql database",
    version = "1.0",
    author = "realuniq"
)
public class ChatLogModule extends BotModule {

  @Override
  public void onEnable() {

    // get message repository for saving and reading messages
    final MessageRepository messageRepository = new MessageRepository(
        getParent().getDatabase().getSql2o()
    );

    // register listeners
    registerListeners(
        new MessageListener(messageRepository)
    );

  }

}