package com.codebook;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AllComments {
    List<CommentStruct> comment;

    public AllComments(List<CommentStruct> ret) {
        this.comment = ret;
    }

}
