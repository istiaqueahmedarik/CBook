package com.codebook;

import java.math.BigInteger;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SingleContest {
    public String start_time;
    public String duration;
    public String site;
    public String name;
    public String url;

    SingleContest(String start_time, String duration, String site, String name, String url) {
        this.start_time = start_time;
        this.duration = duration;
        this.site = site;
        this.name = name;
        this.url = url;
    }

}
