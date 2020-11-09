package io.d2a.schwurbelwatch.tgcrawler.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.d2a.schwurbelwatch.tgcrawler.api.messages.MessageService;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SwApi {

  /**
   * Base API URL for Node-API
   */
  public static final String API_URL = "http://localhost:3420/";

  public static final Gson GSON = new GsonBuilder()
      .setPrettyPrinting()
      .serializeNulls()
      .create();

  private static final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(API_URL)
      .addConverterFactory(GsonConverterFactory.create(GSON))
      .build();

  public static final MessageService MESSAGE_SERVICE = retrofit.create(MessageService.class);

}