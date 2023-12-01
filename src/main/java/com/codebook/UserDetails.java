package com.codebook;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDetails {
  private String localId, email, emailVerified, displayName, photoUrl, lastLoginAt, createAt;
}
