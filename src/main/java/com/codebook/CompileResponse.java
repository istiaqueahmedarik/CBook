package com.codebook;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CompileResponse {
  private String stdout, time, memory, stderr, compile_output, message, description;
}
