package io.d2a.schwurbelwatch.tgcrawler.api.routes.files;

import io.d2a.schwurbelwatch.tgcrawler.api.response.DatabaseResult;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FileService {

  @POST("files")
  Call<DatabaseResult> addFile(@Body ApiFile message);

}
