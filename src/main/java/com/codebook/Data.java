package com.codebook;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Data {
  public Boolean is_valid;
  public Timestamp timestamp;
  public String username;
  public String id_token;
}
