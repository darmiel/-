package io.d2a.schwurbelwatch.tgcrawler.api;

import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.messages.ApiMessage;
import io.d2a.schwurbelwatch.tgcrawler.api.routes.messages.MessageService;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Response;

public class ApiTest {

  public static void main(String[] args) throws IOException {
    final MessageService service = SwApi.MESSAGE_SERVICE;

    final ApiMessage apiMessage = new ApiMessage();
    apiMessage.messageId = 8869;
    apiMessage.userId = 3;
    apiMessage.chatId = 4;
    apiMessage.contentType = 2;
    apiMessage.content = "Hallo!";

    final Call<DatabaseResult> call = service.addMessage(apiMessage);
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