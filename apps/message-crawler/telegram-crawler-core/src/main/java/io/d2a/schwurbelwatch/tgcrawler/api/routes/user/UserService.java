package io.d2a.schwurbelwatch.tgcrawler.api.routes.user;

import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface UserService {

  @POST("users")
  Call<DatabaseResult> addOrUpdateUser(@Body User user);

}
