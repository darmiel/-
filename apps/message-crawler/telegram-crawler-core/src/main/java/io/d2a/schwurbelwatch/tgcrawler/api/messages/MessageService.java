package io.d2a.schwurbelwatch.tgcrawler.api.messages;

import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface MessageService {

  @GET("messages")
  Call<List<Message>> getMessages(@Query("offset") int offset);

  @GET("messages/{id}")
  Call<Message> getMessage(@Path("id") int id);

  @POST("messages")
  Call<DatabaseResult> addMessage(@Body Message message);

}
