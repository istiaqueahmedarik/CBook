package com.codebook;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllCarts {
    List<PostCartStruct> posts;

    AllCarts(List<PostCartStruct> posts) {
        this.posts = posts;
    }
}