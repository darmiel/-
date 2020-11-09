package io.d2a.schwurbelwatch.tgcrawler.api;

import io.d2a.schwurbelwatch.tgcrawler.api.messages.Message;
import io.d2a.schwurbelwatch.tgcrawler.api.messages.MessageService;
import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class ApiTest {

  public static void main(String[] args) throws IOException {
    final MessageService service = SwApi.MESSAGE_SERVICE;

    final Message message = new Message();
    message.messageId = 8869;
    message.userId = 3;
    message.chatId = 4;
    message.contentType = 2;
    message.content = "Hallo!";
    
    final Call<DatabaseResult> call = service.addMessage(message);
    System.out.println(call);
    final Response<DatabaseResult> response = call.execute();
    System.out.println(response);
    final DatabaseResult body = response.body();
    if (body.isError()) {
      System.out.println(" -> Error! :: " + body.errorMessage);
    }
    System.out.println(body);
  }
}