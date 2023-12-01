package com.codebook;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BasicAuthResponse {
  public String idToken, email, refreshToken, expiresIn, localId;
}
