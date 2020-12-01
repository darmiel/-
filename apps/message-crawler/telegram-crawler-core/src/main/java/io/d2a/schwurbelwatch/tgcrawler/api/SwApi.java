package io.d2a.schwurbelwatch.tgcrawler.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.base.BaseService;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.chats.ChatService;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.files.FileService;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.messages.MessageService;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.user.UserService;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.AnsiColor;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.io.IOException;
import javax.annotation.Nullable;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SwApi {

  /**
   * Base API URL for Node-API
   */
  public static final String API_URL = "http://127.0.0.1:4200/";

  public static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      // .serializeNulls()
      .create();

  private static final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(API_URL)
      .addConverterFactory(GsonConverterFactory.create(GSON))
      .build();

  public static final MessageService MESSAGE_SERVICE = retrofit.create(MessageService.class);
  public static final BaseService BASE_SERVICE = retrofit.create(BaseService.class);
  public static final UserService USER_SERVICE = retrofit.create(UserService.class);
  public static final FileService FILE_SERVICE = retrofit.create(FileService.class);
  public static final ChatService CHAT_SERVICE = retrofit.create(ChatService.class);

  public static void callDatabaseResult(@Nullable final Call<DatabaseResult> call) {
    if (call == null) {
      Logger.warn("Tried to call a null Call<?>");
      return;
    }

    try {
      long start = System.currentTimeMillis();

      // Print request
      {
        final Request request = call.request();
        Logger.debug("<<< " + request.method() + " " + request.url());
      }

      final Response<DatabaseResult> response = call.execute();

      // Print response
      {
        final String message = ">>> [" + (System.currentTimeMillis() - start) + "ms] " + response;
        if (response.isSuccessful()) {
          Logger.success(AnsiColor.ANSI_GREEN + message + AnsiColor.ANSI_RESET);
        } else {
          Logger.warn(AnsiColor.ANSI_PURPLE + message + AnsiColor.ANSI_RESET);
        }
      }

      if (response.code() != 200) {
        final ResponseBody object = response.errorBody();
        if (object != null) {
          Logger.warn(object.string());
        }
      }
    } catch (IOException e) {
      Logger.error("Error:");
      Logger.error(e);
    }

    Logger.debug("");
  }

}