package com.codebook;

import java.util.List;

public class PageVariable {
    public static int curPage = 0;
    public static String code;
    public static String title;
    public static String input;
    public static String output;
    public static String time;
    public static String memory;
    public static String aiResponse;
    public static String postPic;
    public static List<PostCodeStruct> posts, carts, personal;
    public static List<SingleContest> contests;
    public static List<ContributionStruct> contributions;
    public static List<ResourceStruct> resources, personalResource;
    public static ExtraDetails extraDetails;

    public static void setData(String title, String code, String input, String output, String time, String memory,
            String aiResponse) {
        PageVariable.title = title;
        PageVariable.code = code;
        PageVariable.input = input;
        PageVariable.output = output;
        PageVariable.time = time;
        PageVariable.memory = memory;
        PageVariable.aiResponse = aiResponse;
    }
}