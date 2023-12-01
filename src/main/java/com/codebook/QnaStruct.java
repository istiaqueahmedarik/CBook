package com.codebook;

import com.google.cloud.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class QnaStruct {
    public String title;
    public String question;
    public Timestamp date;
    public String imageUrl;
    public String email;
    public String id;
    public String id_token;
}
