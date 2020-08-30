/*
 * Copyright (c) 2020.
 *
 * E-Mail: d5a@pm.me
 */

package io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.listener;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.message.DefaultChatMessage;
import io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.repo.MessageRepository;
import org.drinkless.tdlib.TdApi.Message;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

public class MessageListener {

  private MessageRepository repository;

  public MessageListener(MessageRepository repository) {
    this.repository = repository;
  }

  @Subscribe
  public void onMessage(UpdateNewMessage event) {
    final Message message = event.message;
    final DefaultChatMessage defaultChatMessage = DefaultChatMessage.wrap(message);

    // Save to repository
    this.repository.pushMessage(defaultChatMessage);
  }

}