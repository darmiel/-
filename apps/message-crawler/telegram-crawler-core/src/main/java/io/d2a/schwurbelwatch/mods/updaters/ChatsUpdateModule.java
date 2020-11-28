package io.d2a.schwurbelwatch.mods.updaters;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.mods.Accounts;
import io.d2a.schwurbelwatch.tgcrawler.api.SwApi;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.chats.ApiChat;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.chats.ChatType;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.drinkless.tdlib.Client;
import org.drinkless.tdlib.Client.ResultHandler;
import org.drinkless.tdlib.TdApi;
import org.drinkless.tdlib.TdApi.BasicGroup;
import org.drinkless.tdlib.TdApi.Chat;
import org.drinkless.tdlib.TdApi.ChatTypeBasicGroup;
import org.drinkless.tdlib.TdApi.ChatTypeSupergroup;
import org.drinkless.tdlib.TdApi.Error;
import org.drinkless.tdlib.TdApi.GetBasicGroup;
import org.drinkless.tdlib.TdApi.GetSupergroup;
import org.drinkless.tdlib.TdApi.Object;
import org.drinkless.tdlib.TdApi.Supergroup;
import org.drinkless.tdlib.TdApi.UpdateChatOnlineMemberCount;
import org.drinkless.tdlib.TdApi.UpdateChatTitle;
import org.drinkless.tdlib.TdApi.UpdateNewMessage;

@Module(
    name = "Chat Updates",
    description = "Adds a chat to the database",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class ChatsUpdateModule extends BotModule {

  private final Map<Long, Long> chatInfoCache = new HashMap<>();
  private TelegramClient client;

  @Override
  public void onEnable() {
    // Main account for listening for messages: walterheldcorona
    final Optional<TelegramClient> clientOptional = findTelegramClient(Accounts.WATCHER_1);
    if (clientOptional.isPresent()) {
      this.client = clientOptional.get();
      this.client.registerListeners(this);
      Logger.success("Registered listener for client: " + this.client);
    } else {
      Logger.warn("Client '" + Accounts.WATCHER_1 + "' not found");
    }
  }

  private void addOrUpdateChat(@Nullable final Chat chat, @Nullable final ApiChat apiChat) {
    if (chat != null) {
      // Logger.debug(chat);
    }
    //Logger.debug(apiChat);

    SwApi.callDatabaseResult(SwApi.CHAT_SERVICE.addChat(apiChat));
  }

  @Subscribe
  public void onUpdateNewChat(final TdApi.UpdateNewChat event) {
    final Chat chat = event.chat;
    final ApiChat build = ApiChat.builder()
        .chatId(chat.id)
        .title(chat.title)
        .type(ChatType.getType(chat.type))
        .lastUpdated(System.currentTimeMillis())
        .build();
    addOrUpdateChat(chat, build);
  }

  @Subscribe
  public void onChatTitleUpdate(final UpdateChatTitle event) {
    final ApiChat build = ApiChat.builder()
        .chatId(event.chatId)
        .title(event.title)
        .build();
    addOrUpdateChat(null, build);
  }

  @Subscribe
  public void onMemberOnlineCountChanged(final UpdateChatOnlineMemberCount event) {
    SwApi.callDatabaseResult(SwApi.CHAT_SERVICE.updateMemberCount(
        event.chatId,
        event.onlineMemberCount
    ));
  }

  @Subscribe
  public void onNewMessage(final UpdateNewMessage event) {
    final long chatId = event.message.chatId;
    final long lastUpdated = this.chatInfoCache.getOrDefault(chatId, 0L);

    // only update every 300s
    if (System.currentTimeMillis() - lastUpdated <= 300_000) {
      return;
    }
    this.chatInfoCache.put(chatId, System.currentTimeMillis());

    Logger.info("Updating chat " + chatId);
    this.client.getClient().send(
        new TdApi.GetChat(chatId),
        new ChatInfoResultHandler(this.client.getClient())
    );
  }

  @RequiredArgsConstructor
  public class ChatInfoResultHandler implements ResultHandler {

    private final Client client;

    @Override
    public void onResult(final Object object) {
      if (object.getConstructor() == Error.CONSTRUCTOR) {
        Logger.error("Error: " + ((Error) object).message);
        return;
      }

      // Chat -> Get info by type
      if (object.getConstructor() == Chat.CONSTRUCTOR) {
        final Chat chat = (Chat) object;
        final long chatId = chat.id;
        int groupId = 0;

        switch (ChatType.getType(chat.type)) {
          case BASIC:
            final ChatTypeBasicGroup chatTypeBasicGroup = (ChatTypeBasicGroup) chat.type;
            groupId = chatTypeBasicGroup.basicGroupId;

            this.client.send(new GetBasicGroup(groupId), this);
            break;
          case SUPER:
            final ChatTypeSupergroup chatTypeSupergroup = (ChatTypeSupergroup) chat.type;
            groupId = chatTypeSupergroup.supergroupId;

            this.client.send(new GetSupergroup(groupId), this);
            break;
        }

        final ApiChat build = ApiChat.builder()
            .chatId(chat.id)
            .groupId(groupId)
            .title(chat.title)
            .type(ChatType.getType(chat.type))
            .lastUpdated(System.currentTimeMillis())
            .build();

        addOrUpdateChat(null, build);
        return;
      }

      ApiChat apiChat = null;

      // BasicGroup -> Basic Group Info
      if (object.getConstructor() == BasicGroup.CONSTRUCTOR) {
        Logger.debug("Retrieved a basic group");

        final BasicGroup group = (BasicGroup) object;
        System.out.println(group);
        apiChat = ApiChat.builder()
            .groupId(group.id)
            .memberCount(group.memberCount)
            .build();
      } else if (object.getConstructor() == Supergroup.CONSTRUCTOR) {
        Logger.debug("Retrieved a super group");

        final Supergroup group = (Supergroup) object;
        System.out.println(group);

        apiChat = ApiChat.builder()
            .groupId(group.id)
            .username(group.username)
            .date(group.date)
            .memberCount(group.memberCount)
            .isVerified(group.isScam ? (byte) 1 : (byte) 0)
            .isScam(group.isScam ? (byte) 1 : (byte) 0)
            .build();
      }

      if (apiChat != null) {
        addOrUpdateChat(null, apiChat);
      }
    }
  }

}