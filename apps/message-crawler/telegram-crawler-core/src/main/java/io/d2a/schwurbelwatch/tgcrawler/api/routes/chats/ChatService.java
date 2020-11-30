package io.d2a.schwurbelwatch.tgcrawler.api.routes.chats;

import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ChatService {

  @GET("chats")
  Call<List<ApiChat>> getChats(@Query("offset") long offset);

  @POST("chats/chatId")
  Call<DatabaseResult> addChatByChatId(@Body ApiChat chat);

  @POST("chats/groupId")
  Call<DatabaseResult> addChatByGroupId(@Body ApiChat chat);

  @GET("chats/:id")
  Call<ApiChat> getChat(@Path("id") long chatId);

  @PUT("chats/:id")
  Call<ApiChat> updateChat(@Path("id") long chatId, @Body ApiChat chat);

  @POST("chats/:id/count/:count")
  Call<DatabaseResult> updateMemberCount(@Path("id") long chatId, @Path("count") int count);

  default Call<DatabaseResult> addChat(ApiChat chat) {
    if (chat.chatId != null) {
      return addChatByChatId(chat);
    } else if (chat.groupId != null) {
      return addChatByGroupId(chat);
    } else {
      return null;
    }
  }

}