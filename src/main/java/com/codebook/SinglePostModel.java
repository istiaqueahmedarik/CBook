package com.codebook;

import java.util.Base64;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SinglePostModel {
  private String id, title, imgSrc, code, input, output, time, memory, aiResponse, like, download, email, username,
      displayName;
  Boolean liked;

  public static String decode(String base64String) {
    byte[] decodedBytes = Base64.getUrlDecoder().decode(base64String);
    return new String(decodedBytes);
  }

  SinglePostModel(String id, String title, String imgSrc, String code, String input, String output, String time,
      String memory, String aiResponse, String like, String download, String email, String username,
      String displayName, Boolean liked) {
    this.id = id;
    this.title = title;
    this.imgSrc = imgSrc;
    this.code = code;
    this.input = input;
    this.output = output;
    this.time = time;
    this.memory = memory;
    this.aiResponse = aiResponse;
    this.like = like;
    this.download = download;
    this.email = email;
    this.username = username;
    this.displayName = displayName;
    this.liked = liked;
  }

  SinglePostModel() {
  }
}