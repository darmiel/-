/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.repo;

import com.google.common.annotations.Beta;
import io.d2a.schwurbelwatch.tgcrawler.modules.chatlog.message.DefaultChatMessage;
import org.sql2o.Connection;
import org.sql2o.Sql2o;

public class MessageRepository {

  private final Sql2o sql2o;

  public MessageRepository(Sql2o sql2o) {
    this.sql2o = sql2o;
  }

  public void pushMessage(DefaultChatMessage message) {
    try (final Connection open = sql2o.open()) {

      final String sql = "insert into " + Tables.MESSAGES + " ("
          + "chat_id, message_id, sender_id, sent_date, edit_date, type, text_caption, extra, forward_info, message_raw, deleted_at"
          + ") values ("
          + ":chatId, :messageId, :senderId, :sentDate, :editDate, :type, :textCaption, :extra, :forwardInfo, :raw, :deletedAt"
          + ");";

      open.createQuery(sql)
          .bind(message)
          .executeUpdate();

      open.commit();
    }
  }

  // TODO: This won't work. Fix this
  @Beta
  public DefaultChatMessage findMessage(long chatId, long messageId) {
    try (final Connection open = sql2o.open()) {
      final String sql = "select * from " + Tables.MESSAGES +
          " where "
          + "chat_id = :chat_id and message_id = :message_id";

      return open.createQuery(sql)
          .addParameter("chat_id", chatId)
          .addParameter("message_id", messageId)
          .executeAndFetchFirst(DefaultChatMessage.class);
    }
  }

}