package io.d2a.schwurbelwatch.tgcrawler.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.d2a.schwurbelwatch.tgcrawler.api.messages.MessageService;
import io.d2a.schwurbelwatch.tgcrawler.api.other.BaseService;
import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import io.d2a.schwurbelwatch.tgcrawler.api.user.UserService;
import io.d2a.schwurbelwatch.tgcrawler.core.logging.Logger;
import java.io.IOException;
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
      .serializeNulls()
      .create();

  private static final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(API_URL)
      .addConverterFactory(GsonConverterFactory.create(GSON))
      .build();

  public static final MessageService MESSAGE_SERVICE = retrofit.create(MessageService.class);
  public static final BaseService BASE_SERVICE = retrofit.create(BaseService.class);
  public static final UserService USER_SERVICE = retrofit.create(UserService.class);

  public static void callDatabaseResult (final Call<DatabaseResult> call) {
    Logger.debug("");

    try {
      final Response<DatabaseResult> execute = call.execute();
      Logger.debug(execute);

      if (execute.code() != 200) {
        Logger.warn("Nope:");
        final ResponseBody object = execute.errorBody();
        if (object != null) {
          Logger.warn(object.string());
        }
      } else {
        Logger.success("Done!");
      }
    } catch (IOException e) {
      Logger.error("Error:");
      Logger.error(e);
    }

    Logger.debug("");
  }

}