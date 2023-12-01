package com.codebook;

import java.io.IOException;

import javax.annotation.Nonnull;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetjsonData {

  public String getData(@Nonnull String json, @Nonnull String key) throws IOException {
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readTree(json).get(key).asText();
  }
}
