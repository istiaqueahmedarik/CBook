package com.codebook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Id_data {
  public String idToken;

  public Id_data(String idToken2) {
    this.idToken = idToken2;
  }

  public Id_data() {
  }

}
