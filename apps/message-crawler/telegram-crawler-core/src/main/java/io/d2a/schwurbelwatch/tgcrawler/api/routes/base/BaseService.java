package io.d2a.schwurbelwatch.tgcrawler.api.routes.base;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface BaseService {

  @GET("contenttypes")
  Call<List<ContentType>> getContentTypes();

}