package io.d2a.schwurbelwatch.tgcrawler.core.message.wrappers;

import com.google.gson.JsonObject;
import org.drinkless.tdlib.TdApi;

public class GlobalJsonWrappers {

  public static JsonObject wrapLocation(final TdApi.Location location, final Integer expiresIn) {
    final JsonObject object = new JsonObject();
    object.addProperty("latitude", location.latitude);
    object.addProperty("longitude", location.longitude);
    if (expiresIn != null) {
      object.addProperty("expires", expiresIn);
    }
    return object;
  }

}