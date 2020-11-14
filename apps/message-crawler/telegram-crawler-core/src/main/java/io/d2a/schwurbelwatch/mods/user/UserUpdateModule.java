package io.d2a.schwurbelwatch.mods.user;

import com.google.common.eventbus.Subscribe;
import io.d2a.schwurbelwatch.tgcrawler.api.SwApi;
import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import io.d2a.schwurbelwatch.tgcrawler.api.user.User;
import io.d2a.schwurbelwatch.tgcrawler.api.user.UserService;
import io.d2a.schwurbelwatch.tgcrawler.core.client.TelegramClient;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import io.d2a.schwurbelwatch.tgcrawler.core.module.BotModule;
import io.d2a.schwurbelwatch.tgcrawler.core.module.Module;
import java.io.IOException;
import java.util.Optional;
import okhttp3.ResponseBody;
import org.drinkless.tdlib.TdApi.UpdateUser;

import retrofit2.Call;
import retrofit2.Response;

@Module(
    name = "User Updates",
    description = "Adds all users to the database",
    version = "1.0",
    author = "darmiel <hi@d2a.io>"
)
public class UserUpdateModule extends BotModule {

  public static final String CLIENT_NAME = "walterheldcorona";
  private TelegramClient client;

  @Override
  public void onEnable() {
    // Main account for listening for messages: walterheldcorona
    final Optional<TelegramClient> clientOptional = findTelegramClient(CLIENT_NAME);
    if (clientOptional.isPresent()) {
      this.client = clientOptional.get();
      this.client.registerListeners(this);
      Logger.success("Registered listener for client: " + this.client);
    } else {
      Logger.warn("Client '" + CLIENT_NAME + "' not found");
    }
  }

  @Subscribe
  public void onUserInfo(final UpdateUser event) {
    final User user = User.wrap(event.user);
    SwApi.callDatabaseResult(SwApi.USER_SERVICE.addOrUpdateUser(user));
  }

}