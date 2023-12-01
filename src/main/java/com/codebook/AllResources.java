package com.codebook;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllResources {
    List<ResourceStruct> posts;

    AllResources(List<ResourceStruct> ret) {
        this.posts = ret;
    }
}
