package com.codebook;

import com.google.cloud.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ResourceStruct {

    public String email;
    public String id;
    public String title;
    public String mainPost;
    public String imagePost;
    public Timestamp now;
    public String tags;
    public String id_token;

    public ResourceStruct(String email, String title, String mainPost, String imagePost, Timestamp now,
            String tags, String id_token) {
        this.email = email;
        this.title = title;
        this.mainPost = mainPost;
        this.imagePost = imagePost;
        this.now = now;
        this.tags = tags;
        this.id_token = id_token;
    }

    public ResourceStruct() {
    }

    public void setId(String id) {
        this.id = id;
    }

}
