package com.codebook;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostCartStruct {
    public String username, code, title, id, userImage;
    public Timestamp timestamp;
    public int likes, view, download;
    public boolean liked;
    public String id_token;

    PostCartStruct(String username, String userImage, String code, String title, Timestamp timestamp, int likes,
            int view, int download, Boolean liked, String id_token) {
        this.username = username;
        this.code = code;
        this.title = title;
        this.timestamp = timestamp;
        this.likes = likes;
        this.view = view;
        this.download = download;
        this.userImage = userImage;
        this.liked = liked;
        this.id_token = id_token;
    }

    PostCartStruct() {
    }
}
