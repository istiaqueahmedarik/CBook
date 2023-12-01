package com.codebook;

import com.google.cloud.Timestamp;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class FriendRequestStruct {
    public String my_username;
    public Timestamp now;

    public FriendRequestStruct(String my_username, Timestamp now) {
        this.my_username = my_username;
        this.now = now;
    }

}
