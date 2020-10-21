/*
 * Copyright (c) 2020.
 *
 * E-Mail: hi@d2a.io
 */

package io.d2a.schwurbelwatch.tgcrawler.core.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ApiCredentials {

  private int apiId;
  private String apiHash;
  private String phoneNumber;

  @Override
  public String toString() {
    final StringBuilder substrHash = new StringBuilder(this.apiHash.substring(0, 3));
    for (int i = 0; i < this.apiHash.length() - 3; i++) {
      substrHash.append("*");
    }

    final StringBuilder phoneNum = new StringBuilder(this.phoneNumber.substring(0, 3));
    for (int i = 0; i < this.phoneNumber.length() - 6; i++) {
      phoneNum.append("*");
    }
    phoneNum.append(this.phoneNumber.substring(this.phoneNumber.length() - 3));

    return String.format("Api-Credentials: ID: %d, Hash: %s, Phone: %s",
        this.apiId,
        substrHash.toString(),
        phoneNum.toString()
    );
  }

}