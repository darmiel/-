package io.d2a.schwurbelwatch.tgcrawler.api.routes.user;

import com.google.gson.annotations.SerializedName;
import javax.annotation.Nonnull;
import lombok.ToString;
import org.drinkless.tdlib.TdApi;

@ToString
public class User {

  public long userId;
  public String username;

  @SerializedName("first_name")
  public String firstName;

  @SerializedName("last_name")
  public String lastName;

  @SerializedName("phone_nr")
  public String phoneNr;

  @SerializedName("is_verified")
  public int isVerified;

  @SerializedName("is_support")
  public int isSupport;

  @SerializedName("is_scam")
  public int isScam;

  @Nonnull
  public static User wrap(@Nonnull final TdApi.User tdUser) {
    final User res = new User();
    res.userId = tdUser.id;
    res.username = tdUser.username;
    // Set everything null with length of 0
    if (res.username != null && res.username.length() == 0) {
      res.username = null;
    }

    res.firstName = tdUser.firstName;
    if (res.firstName != null && res.firstName.length() == 0) {
      res.firstName = null;
    }

    res.lastName = tdUser.lastName;
    if (res.lastName != null && res.lastName.length() == 0) {
      res.lastName = null;
    }

    res.phoneNr = tdUser.phoneNumber;
    if (res.phoneNr != null && res.phoneNr.length() == 0) {
      res.phoneNr = null;
    }

    return res;
  }

}