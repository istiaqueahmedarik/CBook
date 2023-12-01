package com.codebook;

import com.google.cloud.Timestamp;
import com.google.firebase.database.IgnoreExtraProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IgnoreExtraProperties
public class PostCodeStruct {
    public String username, displayName, userEmail, code, title, id, userImage, language, time, memory, output,
            aiResponse, stdin;
    public Timestamp timestamp;
    public int likes, view, download;
    public boolean liked;
    public String id_token;
    public ProfileStruct profile;
}
