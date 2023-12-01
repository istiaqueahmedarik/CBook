package com.codebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.gson.Gson;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestHandler {

  public String GetRequest(String requestUrl) {
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      Request request = new Request.Builder().url(requestUrl).build();
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (Exception e) {
      return e.toString();
    }
  }

  public String hashMapToString(HashMap<String, String> map) {
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for (Map.Entry<String, String> entry : map.entrySet()) {
      sb
        .append("\"")
        .append(entry.getKey())
        .append("\"")
        .append(": ")
        .append("\"")
        .append(entry.getValue())
        .append("\"")
        .append(",");
    }
    sb.deleteCharAt(sb.length() - 1);
    sb.append("}");
    return sb.toString();
  }

  public String PostRequest(String reqUrl, HashMap<String, String> params) {
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      RequestBody body = RequestBody.create(hashMapToString(params), mediaType);
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .build();
      Response response = client.newCall(request).execute();
      return response.body().string();
    } catch (Exception e) {
      return e.toString();
    }
  }

  public List<PostCodeStruct> getPosts(int pageNumber) {
    return Methods.allPosts(MainUser.idToken, pageNumber).posts;
  }

  public List<PostCodeStruct> getSearchResult(String search)
    throws JsonMappingException, JsonProcessingException, UnirestException {
    return Methods.search(MainUser.idToken, search).posts;
  }

  public List<ResourceStruct> getResources(int pageNumber) {
    return Methods.allResources(pageNumber).posts;
  }

  // public List<PostCartStruct> getCart() throws IOException {

  // return Methods.allCarts(MainUser.userEmail).posts;
  // }

  public List<PostCodeStruct> getUserPosts(String idToken, int curPage) {
    return Methods.allPostOfUser(idToken).posts;
  }

  public List<SingleContest> getContests()
    throws JsonProcessingException, UnirestException {
    return Methods.getCurrentContest().contests;
  }

  public List<CommentStruct> getComments(String id) {
    return Methods.allComments(id).getComment();
  }
}
