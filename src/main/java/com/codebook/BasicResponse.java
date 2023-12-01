package com.codebook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicResponse {
  private String message;
  private Integer status_code;
  private String refreshToken, idToken;
}
