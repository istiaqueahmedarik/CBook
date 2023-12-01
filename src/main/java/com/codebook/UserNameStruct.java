package com.codebook;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserNameStruct {
    public UserNameStruct(String username2, boolean b, Timestamp now, String id_token) {
        this.username = username2;
        this.is_valid = b;
        this.timestamp = now;
        this.id_token = id_token;
    }

    String username;
    Boolean is_valid;
    Timestamp timestamp;
    public String id_token;

}
