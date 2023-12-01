package com.codebook;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firebase.cloud.StorageClient;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import javax.annotation.Nonnull;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class Methods {
  public static final String API_Key = System.getProperty(
    "CODEBOOK_PALM_API_KEY"
  );
  public static final String CODEBOOK_SUPABASE_API_KEY = System.getProperty(
    "CODEBOOK_SUPABASE_API_KEY"
  );

  public static String decode(String base64String) {
    byte[] decodedBytes = Base64.decodeBase64(base64String);
    return new String(decodedBytes, StandardCharsets.UTF_8);
  }

  public static String encode(String string) {
    byte[] encodedBytes = Base64.encodeBase64(string.getBytes());
    return new String(encodedBytes, StandardCharsets.UTF_8);
  }

  public static AllPosts allPosts(String id, int pageNumber) {
    UserDetails user = getUserDetails(id);
    if (user == null) {
      return null;
    }
    String email = user.getEmail();
    int perPage = 10;
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("code")
      .orderBy("timestamp")
      .startAfter(pageNumber * perPage)
      .limit(perPage);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<PostCodeStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        PostCodeStruct post = document.toObject(PostCodeStruct.class);
        post.setLiked(isUserLikes(email, post.getId()));
        ret.add(post);
      }
      Collections.reverse(ret);
      AllPosts posts = new AllPosts(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static Data getData(String document_id)
    throws InterruptedException, ExecutionException {
    System.out.println("hello");
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<DocumentSnapshot> future = db
      .collection("data")
      .document(document_id)
      .get();
    System.out.println(future.get());
    Data ret = new Data();
    try {
      DocumentSnapshot document = future.get();
      if (document.exists()) {
        ret = document.toObject(Data.class);
        System.out.println(ret);
        return ret;
      }
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    return null;
  }

  public static Data getUser(String username)
    throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    ApiFuture<DocumentSnapshot> future = db
      .collection("UserNameList")
      .document(username)
      .get();
    Data ret = new Data();
    try {
      DocumentSnapshot document = future.get();
      if (document.exists()) {
        ret = document.toObject(Data.class);
        System.out.println(ret);
        return ret;
      }
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    return null;
  }

  public static PostCodeStruct SinglePost(String id) {
    System.out.println(id);
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("code").document(id);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      return null;
    }
    PostCodeStruct post = docSnap.toObject(PostCodeStruct.class);
    return post;
  }

  public static CompileResponse compile(
    String code,
    String language,
    String input
  ) {
    CompileResponse ret = new CompileResponse();
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      RequestBody body = RequestBody.create(
        String.format(
          "{\"source_code\":\"%s\",\"language_id\":%s,\"stdin\":\"%s\"}",
          encode(decode(code)),
          language,
          input
        ),
        mediaType
      );
      Request request = new Request.Builder()
        .url("https://ce.judge0.com/submissions?base64_encoded=true&wait=true")
        .method("POST", body)
        .addHeader("Host", "ce.judge0.com")
        .addHeader("Content-Length", "4137")
        .addHeader("Sec-Ch-Ua", "")
        .addHeader("Accept", "*/*")
        .addHeader("Content-Type", "application/json")
        .addHeader("Sec-Ch-Ua-Mobile", "?0")
        .addHeader(
          "User-Agent",
          "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.5845.111 Safari/537.36"
        )
        .addHeader("Sec-Ch-Ua-Platform", "\"\"")
        .addHeader("Origin", "https://ide.judge0.com")
        .addHeader("Sec-Fetch-Site", "same-site")
        .addHeader("Sec-Fetch-Mode", "cors")
        .addHeader("Sec-Fetch-Dest", "empty")
        .addHeader("Referer", "https://ide.judge0.com/")
        .addHeader("Accept-Language", "en-US,en;q=0.9")
        .build();
      Response response = client.newCall(request).execute();
      System.out.println(language);
      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        String out = mapper.readTree(jsonString).get("stdout").asText();
        out = decode(out);
        System.out.println(out);
        ret.setStdout(out);
        ret.setMemory(mapper.readTree(jsonString).get("memory").asText());
        ret.setTime(mapper.readTree(jsonString).get("time").asText());
        ret.setCompile_output(
          mapper.readTree(jsonString).get("compile_output").asText()
        );
        ret.setStderr(mapper.readTree(jsonString).get("stderr").asText());
        ret.setMessage(mapper.readTree(jsonString).get("message").asText());
        ret.setDescription(
          mapper.readTree(jsonString).get("status").get("description").asText()
        );
        System.out.println(mapper.readTree(jsonString));
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public static BasicResponse AddCode(
    String username,
    String email,
    String displayName,
    String userImage,
    String code,
    String input,
    String language,
    String title,
    String time,
    String memory,
    String output
  ) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    String decoded = "this is the title " + title + decode(code);
    String modify = "";
    for (int i = 0; i < decoded.length(); i++) {
      if (decoded.charAt(i) == '"') {
        modify += "\\\"";
      } else if (decoded.charAt(i) == '\n') {
        modify += "\\n";
      } else if (decoded.charAt(i) == '\t') {
        modify += "\\t";
      } else {
        modify += decoded.charAt(i);
      }
    }
    String query_to_ai = modify;

    String ai_res = chatGPT(query_to_ai);

    PostCodeStruct post = new PostCodeStruct(
      MainUser.username,
      MainUser.displayName,
      MainUser.userEmail,
      code,
      title,
      "",
      MainUser.photoUrl,
      language,
      time,
      memory,
      output,
      "",
      input,
      Timestamp.now(),
      0,
      0,
      0,
      false,
      MainUser.idToken,
      new ProfileStruct(
        MainUser.displayName,
        MainUser.username,
        MainUser.photoUrl,
        MainUser.idToken,
        isAdmin(MainUser.username)
      )
    );
    DocumentReference docRef = db.collection("code").document();
    post.setId(docRef.getId());
    post.setAiResponse(ai_res);
    post.setLiked(false);
    embeddings(docRef.getId(), modify);
    docRef.set(post);
    ret.setMessage(ai_res);
    ret.setIdToken(docRef.getId());
    ret.setStatus_code(200);
    db
      .collection("UsersPost")
      .document(username)
      .collection("Posts")
      .document(docRef.getId())
      .set(post);
    return ret;
  }

  public static void addUserName(
    @Nonnull String username,
    @Nonnull String name,
    @Nonnull String id_token
  ) {
    Firestore db = FirestoreClient.getFirestore();
    UserNameStruct user;
    user = new UserNameStruct(username, true, Timestamp.now(), id_token);
    db.collection("UserNameList").document(name).set(user);
  }

  public static BasicResponse deletePost(String id) throws UnirestException {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    DocumentReference docRef = db.collection("code").document(id);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      ret.setMessage("No such document!");
      ret.setStatus_code(404);
      return ret;
    }
    String name = docSnap.toObject(PostCodeStruct.class).getUsername();
    docRef.delete();
    docRef =
      db
        .collection("UsersPost")
        .document(name)
        .collection("Posts")
        .document(id);
    docRef.delete();
    Unirest.setTimeouts(0, 0);
    HttpResponse<String> response = Unirest
      .delete("https://qsipezvuqaentfjlwfpc.supabase.co/rest/v1/documents")
      .header("apikey", "Bearer " + CODEBOOK_SUPABASE_API_KEY)
      .header("Content-Type", "application/json")
      .body("{\"id\":\"" + id + "\"}")
      .asString();

    ret.setMessage("Success!");
    ret.setStatus_code(200);
    return ret;
  }

  public static BasicResponse updatePost(PostCodeStruct post)
    throws UnirestException {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    DocumentReference docRef = db.collection("code").document(post.id);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      ret.setMessage("No such document!");
      ret.setStatus_code(404);
      return ret;
    }
    PostCodeStruct oldPost = docSnap.toObject(PostCodeStruct.class);
    post.setLiked(oldPost.liked);
    post.setTimestamp(oldPost.getTimestamp());
    post.download = oldPost.download;

    String decoded = decode(post.code);
    String modify = "";
    for (int i = 0; i < decoded.length(); i++) {
      if (decoded.charAt(i) == '"') {
        modify += "\\\"";
      } else if (decoded.charAt(i) == '\n') {
        modify += "\\n";
      } else if (decoded.charAt(i) == '\t') {
        modify += "\\t";
      } else {
        modify += decoded.charAt(i);
      }
    }
    String query_to_ai = modify;

    String ai_res = chatGPT(query_to_ai);
    post.aiResponse = ai_res;
    Unirest.setTimeouts(0, 0);
    Double[] embed = embedList(ai_res);
    String embedString = "[";
    for (int i = 0; i < embed.length; i++) {
      embedString += embed[i].toString();
      if (i != embed.length - 1) {
        embedString += ",";
      }
    }
    embedString += "]";
    //// System.out.println(embedString);
    String req =
      "{\"id\":\"" +
      post.id +
      "\",\"content\":\"" +
      ai_res +
      "\",\"embedding\":" +
      embedString +
      "}";
    HttpResponse<String> response = Unirest
      .patch(
        "https://qsipezvuqaentfjlwfpc.supabase.co/rest/v1/documents?id=" +
        post.id
      )
      .header("apikey", "Bearer " + CODEBOOK_SUPABASE_API_KEY)
      .header("Content-Type", "application/json")
      .body(req)
      .asString();

    docRef.set(post);
    docRef =
      db
        .collection("UsersPost")
        .document(post.username)
        .collection("Posts")
        .document(post.id);
    docRef.set(post);
    ret.setMessage(ai_res);
    ret.setIdToken(post.id);
    ret.setStatus_code(200);
    return ret;
  }

  public static BasicAuthResponse login(User user) {
    String username = user.getUsername();
    String password = user.getPassword();
    System.out.println(username);
    BasicAuthResponse ret = new BasicAuthResponse();
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");
      System.out.println(username);
      RequestBody body = RequestBody.create(
        String.format(
          "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
          username,
          password
        ),
        mediaType
      );
      String reqUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" +
        API_Key;
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();

      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        String idToken = mapper.readTree(jsonString).get("idToken").asText();
        String email = mapper.readTree(jsonString).get("email").asText();
        usernameVsIdToken(username, idToken);
        Firestore db = FirestoreClient.getFirestore();
        ExtraDetails extraDetails = db
          .collection("Extra")
          .document(username)
          .get()
          .get()
          .toObject(ExtraDetails.class);
        PageVariable.extraDetails = extraDetails;
        String refreshToken = mapper
          .readTree(jsonString)
          .get("refreshToken")
          .asText();
        String expiresIn = mapper
          .readTree(jsonString)
          .get("expiresIn")
          .asText();
        String localId = mapper.readTree(jsonString).get("localId").asText();
        ret.setIdToken(idToken);
        ret.setEmail(email);
        ret.setRefreshToken(refreshToken);
        ret.setExpiresIn(expiresIn);
        ret.setLocalId(localId);
        System.out.println(email);
        System.out.println("Success");
        return ret;
      } else {
        return ret;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static void updateExtra(ExtraDetails e) {
    Firestore db = FirestoreClient.getFirestore();
    db.collection("Extra").document(MainUser.username).set(e);
  }

  public static void updateName(String idToken, String name) {
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(
      String.format(
        "{\"idToken\":\"%s\",\"displayName\":\"%s\",\"returnSecureToken\":true}",
        idToken,
        name
      ),
      mediaType
    );

    String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");
    String reqUrl =
      "https://identitytoolkit.googleapis.com/v1/accounts:update?key=" +
      API_Key;
    Request request = new Request.Builder()
      .url(reqUrl)
      .method("POST", body)
      .addHeader("Content-Type", "application/json")
      .build();
    Response response1 = null;
    try {
      response1 = client.newCall(request).execute();
      System.out.println(response1.body().string());
    } catch (IOException e) {
      e.printStackTrace();
    }
    Firestore db = FirestoreClient.getFirestore();
    db
      .collection("Profiles")
      .document(MainUser.username)
      .update("displayName", name);
    db
      .collection("Profiles")
      .document(MainUser.userEmail)
      .update("displayName", name);
  }

  public static BasicAuthResponse verify(String id) {
    BasicAuthResponse ret = new BasicAuthResponse();

    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");
      RequestBody body = RequestBody.create(
        String.format(
          "{\"grant_type\":\"refresh_token\",\"refresh_token\":\"%s\"}",
          id
        ),
        mediaType
      );

      String reqUrl =
        "https://securetoken.googleapis.com/v1/token?key=" + API_Key;
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        String idToken = mapper.readTree(jsonString).get("id_token").asText();
        ret.setIdToken(idToken);
        String user_id = mapper.readTree(jsonString).get("user_id").asText();
        ret.setLocalId(user_id);
        return ret;
      } else {
        return ret;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static String sendMail(Id_data vId) {
    String id = vId.getIdToken();
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");

      RequestBody body = RequestBody.create(
        String.format(
          "{\"requestType\":\"VERIFY_EMAIL\",\"idToken\":\"%s\"}",
          id
        ),
        mediaType
      );

      String reqUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" +
        API_Key;
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        System.out.println(jsonString);
        String mail = mapper.readTree(jsonString).get("email").asText();
        return mail;
      } else {
        return "problem";
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static String createImage(String name) {
    // String ret = "data:image/png;base64,";
    String ret = "https://robohash.org/" + name;

    return ret;
  }

  public static BasicResponse isUserExist(String username)
    throws InterruptedException, ExecutionException {
    Data data = getUser(username);
    System.out.println(data);
    if (data == null) {
      BasicResponse ret = new BasicResponse();
      ret.setMessage("User does not exist");
      ret.setStatus_code(404);
      return ret;
    } else {
      BasicResponse ret = new BasicResponse();
      ret.setMessage("User exist");
      ret.setStatus_code(401);
      return ret;
    }
  }

  public static String getContribution(String username) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Contributions")
      .document(username);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      return null;
    }
    ContributionStruct contributionStruct = docSnap.toObject(
      ContributionStruct.class
    );
    return String.valueOf(contributionStruct.getContribution());
  }

  public static boolean isAdmin(String username) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("Admins").document(username);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
    if (docSnap != null && !docSnap.exists()) {
      return false;
    }
    return true;
  }

  public static boolean isUserNameExists(String username) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("UserNameList").document(username);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return false;
    }
    if (docSnap != null && !docSnap.exists()) {
      return false;
    }
    return true;
  }

  public static void addAdmin(String username)
    throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    ProfileStruct profileStruct = db
      .collection("Profiles")
      .document(username)
      .get()
      .get()
      .toObject(ProfileStruct.class);
    db.collection("Admins").document(username).set(profileStruct);
    profileStruct.isAdmin = true;
    db.collection("Profiles").document(username).set(profileStruct);
    String email = profileStruct.username;
    profileStruct.username = username;
    db.collection("Profiles").document(email).set(profileStruct);
  }

  public static BasicResponse signUp(User user) {
    String username = user.getUsername();
    String name = user.getName();
    String password = user.getPassword();
    String email = user.getEmail();
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");

      RequestBody body = RequestBody.create(
        String.format(
          "{\"email\":\"%s\",\"password\":\"%s\",\"returnSecureToken\":true}",
          email,
          password
        ),
        mediaType
      );
      String reqUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" +
        API_Key;
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        System.out.println(jsonString);
        String idToken = mapper.readTree(jsonString).get("idToken").asText();
        String refreshToken = mapper
          .readTree(jsonString)
          .get("refreshToken")
          .asText();
        String defaultImage = createImage(name);
        body =
          RequestBody.create(
            String.format(
              "{\"idToken\":\"%s\",\"displayName\":\"%s\",\"photoUrl\":\"%s\",\"returnSecureToken\":true}",
              idToken,
              name,
              defaultImage
            ),
            mediaType
          );
        reqUrl =
          "https://identitytoolkit.googleapis.com/v1/accounts:update?key=" +
          API_Key;
        request =
          new Request.Builder()
            .url(reqUrl)
            .method("POST", body)
            .addHeader("Content-Type", "application/json")
            .build();
        Response response1 = client.newCall(request).execute();
        addUserName(username, name, idToken);
        System.out.println(idToken);
        UserDetails userDetail = getUserDetails(idToken);

        BasicResponse ret = new BasicResponse();
        ret.setIdToken(idToken);
        ret.setRefreshToken(refreshToken);
        if (userDetail.getEmailVerified() == "true") ret.setStatus_code(
          400
        ); else {
          ret.setStatus_code(201);
          sendMail(new Id_data(idToken));
        }

        Firestore db = FirestoreClient.getFirestore();
        ProfileStruct profileStruct = new ProfileStruct(
          name,
          username,
          defaultImage,
          idToken,
          isAdmin(username)
        );
        db.collection("Profiles").document(email).set(profileStruct);
        profileStruct.username = email;
        db.collection("Profiles").document(username).set(profileStruct);
        ExtraDetails extraDetails = new ExtraDetails("", "", "", "");
        ContributionStruct contributionStruct = new ContributionStruct(
          username,
          profileStruct.userImage,
          0,
          idToken
        );
        db
          .collection("Contributions")
          .document(username)
          .set(contributionStruct);

        db.collection("Extra").document(username).set(extraDetails);

        return ret;
      } else {
        BasicResponse ret = new BasicResponse();
        ret.setMessage("Invalid Credentials");
        ret.setStatus_code(404);
        return ret;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static UserDetails getUserDetails(String id) {
    if (id == null) return null;
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");

      RequestBody body = RequestBody.create(
        String.format("{\"idToken\":\"%s\"}", id),
        mediaType
      );
      String reqUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=" +
        API_Key;
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        JsonNode rootNode = mapper.readTree(jsonString);
        if (rootNode.has("users")) {
          JsonNode usersNode = rootNode.get("users");
          if (usersNode.isArray() && usersNode.size() > 0) {
            JsonNode userNode = usersNode.get(0);
            String localId = userNode.get("localId").asText();
            String email = userNode.get("email").asText();
            String emailVerified = userNode.get("emailVerified").asText();
            String displayName = userNode.get("displayName").asText();
            String lastLoginAt = userNode.get("lastLoginAt").asText();
            String createAt = userNode.get("createdAt").asText();
            String photoUrl = userNode.get("photoUrl").asText();
            UserDetails ret = new UserDetails();
            ret.setLocalId(localId);
            ret.setEmail(email);
            ret.setEmailVerified(emailVerified);
            ret.setDisplayName(displayName);
            ret.setLastLoginAt(lastLoginAt);
            ret.setCreateAt(createAt);
            ret.setPhotoUrl(photoUrl);
            return ret;
          }
        }
      } else {
        return null;
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static BasicResponse resetPassEmailSent(String email) {
    BasicResponse ret = new BasicResponse();
    ret.setMessage("The is not a valid email");
    ret.setStatus_code(401);
    try {
      OkHttpClient client = new OkHttpClient().newBuilder().build();
      MediaType mediaType = MediaType.parse("application/json");
      String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");

      RequestBody body = RequestBody.create(
        String.format(
          "{\"requestType\":\"PASSWORD_RESET\",\"email\":\"%s\"}",
          email
        ),
        mediaType
      );
      String reqUrl =
        "https://identitytoolkit.googleapis.com/v1/accounts:sendOobCode?key=" +
        API_Key;
      Request request = new Request.Builder()
        .url(reqUrl)
        .method("POST", body)
        .addHeader("Content-Type", "application/json")
        .build();
      Response response = client.newCall(request).execute();
      if (response.isSuccessful()) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = response.body().string();
        System.out.println(jsonString);
        String email1 = mapper.readTree(jsonString).get("email").asText();
        if (email1.equals(email)) {
          ret.setMessage("Success!");
          ret.setStatus_code(200);
        }
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static String chatGPT(String message) {
    message = "\"" + message + " ";
    System.out.println(message);

    try {
      System.out.println("hello");

      Unirest.setTimeouts(0, 0);
      HttpResponse<String> response = Unirest
        .post(
          "https://generativelanguage.googleapis.com/v1beta3/models/text-bison-001:generateText?key=" +
          API_Key
        )
        .header("Content-Type", "application/json")
        .body(
          "{\"prompt\":{\"text\": " +
          message +
          "\ntime complexity and explanation of this code is:\"},\"temperature\":0.7,\"top_k\":40,\"top_p\":0.95,\"candidate_count\":1,\"max_output_tokens\":1024,\"stop_sequences\":[],\"safety_settings\":[{\"category\":\"HARM_CATEGORY_DEROGATORY\",\"threshold\":1},{\"category\":\"HARM_CATEGORY_TOXICITY\",\"threshold\":1},{\"category\":\"HARM_CATEGORY_VIOLENCE\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_SEXUAL\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_MEDICAL\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_DANGEROUS\",\"threshold\":2}]}"
        )
        .asString();

      String body = response.getBody();
      System.out.println(body);
      JSONObject jsonObject = new JSONObject(body);
      String content = jsonObject
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getString("output");

      return content;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public static Double[] embedList(String message) {
    System.out.println(message);
    message = "\"" + message + "\"";
    String reqUrl =
      "https://generativelanguage.googleapis.com/v1beta2/models/embedding-gecko-001:embedText?key=" +
      API_Key;
    try {
      Unirest.setTimeouts(0, 0);
      HttpResponse<String> response = Unirest
        .post(reqUrl)
        .header("Content-Type", "application/json")
        .body("{\"text\": " + message + "}")
        .asString();
      String body = response.getBody();
      // System.out.println(body);
      JSONObject jsonObject = new JSONObject(body);
      // System.out.println(jsonObject);
      JSONArray jsonArray = jsonObject
        .getJSONObject("embedding")
        .getJSONArray("value");
      Double[] ret = new Double[jsonArray.length()];
      for (int i = 0; i < jsonArray.length(); i++) {
        ret[i] = jsonArray.getDouble(i);
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static String embeddings(String id, String message) {
    message = "\"" + message + " ";
    System.out.println(message);

    try {
      System.out.println("hello");

      Unirest.setTimeouts(0, 0);
      HttpResponse<String> response = Unirest
        .post(
          "https://generativelanguage.googleapis.com/v1beta3/models/text-bison-001:generateText?key=" +
          API_Key
        )
        .header("Content-Type", "application/json")
        .body(
          "{\"prompt\":{\"text\": " +
          message +
          "\nthe complete explanation line by line for this code is:\"},\"temperature\":0.7,\"top_k\":40,\"top_p\":0.95,\"candidate_count\":1,\"max_output_tokens\":1024,\"stop_sequences\":[],\"safety_settings\":[{\"category\":\"HARM_CATEGORY_DEROGATORY\",\"threshold\":1},{\"category\":\"HARM_CATEGORY_TOXICITY\",\"threshold\":1},{\"category\":\"HARM_CATEGORY_VIOLENCE\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_SEXUAL\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_MEDICAL\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_DANGEROUS\",\"threshold\":2}]}"
        )
        .asString();

      String body = response.getBody();
      System.out.println(body);
      JSONObject jsonObject = new JSONObject(body);
      String content = jsonObject
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getString("output");
      String totalContent = content;
      String modify = "";
      for (int i = 0; i < totalContent.length(); i++) {
        if (totalContent.charAt(i) == '"') {
          modify += " ";
        } else if (totalContent.charAt(i) == '\n') {
          modify += " ";
        } else if (totalContent.charAt(i) == '\t') {
          modify += " ";
        } else if (totalContent.charAt(i) == '\\') {
          modify += " ";
        } else {
          modify += totalContent.charAt(i);
        }
      }
      sendToVectorDB(modify, id);

      return content;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public static void sendToVectorDB(String message, String id)
    throws UnirestException {
    Double[] embed = embedList(message);
    String embedString = "[";
    for (int i = 0; i < embed.length; i++) {
      embedString += embed[i].toString();
      if (i != embed.length - 1) {
        embedString += ",";
      }
    }
    embedString += "]";
    // System.out.println(embedString);
    String req =
      "{\"id\":\"" +
      id +
      "\",\"content\":\"" +
      message +
      "\",\"embedding\":" +
      embedString +
      "}";
    // System.out.println(req);
    Unirest.setTimeouts(0, 0);
    HttpResponse<String> response = Unirest
      .post("https://qsipezvuqaentfjlwfpc.supabase.co/rest/v1/documents")
      .header(
        "Authorization",
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFzaXBlenZ1cWFlbnRmamx3ZnBjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDAzOTgxODksImV4cCI6MjAxNTk3NDE4OX0.1-a64iv-XydhhOyGI2mFZc3hNuB3Rl_v8jKP4GWjzFA"
      )
      .header("Content-Type", "application/json")
      .header(
        "apiKey",
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFzaXBlenZ1cWFlbnRmamx3ZnBjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDAzOTgxODksImV4cCI6MjAxNTk3NDE4OX0.1-a64iv-XydhhOyGI2mFZc3hNuB3Rl_v8jKP4GWjzFA"
      )
      .body(req)
      .asString();
    System.out.println(response.getBody().toString());
  }

  public static String getFromVectorDB(String message) throws UnirestException {
    Double[] embed = embedList(message);
    String embedString = "[";
    for (int i = 0; i < embed.length; i++) {
      embedString += embed[i].toString();
      if (i != embed.length - 1) {
        embedString += ",";
      }
    }
    embedString += "]";
    String req =
      "{\n    \"query_embedding\": \"<embedding>\",\n    \"match_threshold\": 0.6,\n    \"match_count\": 10\n  }";
    req = req.replace("<embedding>", embedString);
    // System.out.println(req);
    Unirest.setTimeouts(0, 0);
    HttpResponse<String> response = Unirest
      .post(
        "https://qsipezvuqaentfjlwfpc.supabase.co/rest/v1/rpc/match_documents"
      )
      .header(
        "Authorization",
        "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFzaXBlenZ1cWFlbnRmamx3ZnBjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDAzOTgxODksImV4cCI6MjAxNTk3NDE4OX0.1-a64iv-XydhhOyGI2mFZc3hNuB3Rl_v8jKP4GWjzFA"
      )
      .header("Content-Type", "application/json")
      .header(
        "apiKey",
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InFzaXBlenZ1cWFlbnRmamx3ZnBjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MDAzOTgxODksImV4cCI6MjAxNTk3NDE4OX0.1-a64iv-XydhhOyGI2mFZc3hNuB3Rl_v8jKP4GWjzFA"
      )
      .body(req)
      .asString();
    System.out.println(response.getBody().toString());
    return response.getBody().toString();
  }

  public static AllPosts search(String userId, String search)
    throws JsonMappingException, JsonProcessingException, UnirestException {
    UserDetails user = getUserDetails(userId);
    String email = user.getEmail();
    List<PostCodeStruct> ret = new ArrayList<>();
    String jsonString = getFromVectorDB(search);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(jsonString);
    if (rootNode.isArray()) {
      for (JsonNode node : rootNode) {
        String id = node.get("id").asText();
        System.out.println(id);
        PostCodeStruct post;
        Firestore db = FirestoreClient.getFirestore();
        DocumentReference docRef = db.collection("code").document(id);
        ApiFuture<DocumentSnapshot> document = docRef.get();
        DocumentSnapshot docSnap = null;
        try {
          docSnap = document.get();
        } catch (Exception e) {
          System.out.println(e);
          return null;
        }
        if (docSnap != null && !docSnap.exists()) {
          return null;
        }
        post = docSnap.toObject(PostCodeStruct.class);
        post.setLiked(isUserLikes(email, id));
        ret.add(post);
      }
    } else {
      AllPosts posts = new AllPosts(ret);
      return posts;
    }
    AllPosts posts = new AllPosts(ret);
    return posts;
  }

  public static final MediaType JSON = MediaType.parse(
    "application/json; charset=utf-8"
  );

  // public static String GenralChatGPT(String mes) throws IOException {
  // try {
  // OkHttpClient client = new OkHttpClient();

  // // Request body
  // String jsonBody = "{\n" +
  // " \"model\": \"@cf/meta/llama-2-7b-chat-int8\",\n" +
  // " \"messages\": [\n" +
  // " {\n" +
  // " \"role\": \"system\",\n" +
  // " \"content\": \"You are a helpful assistant.\"\n" +
  // " },\n" +
  // " {\n" +
  // " \"role\": \"user\",\n" +
  // " \"content\": \"Hello!\"\n" +
  // " }\n" +
  // " ]\n" +
  // "}";

  // // Create a request object
  // Request request = new Request.Builder()
  // .url("https://openai-cf.istiaqueahmedarik.workers.dev/chat/completions")
  // .post(RequestBody.create(jsonBody, JSON))
  // .addHeader("Content-Type", "application/json")
  // .build();

  // // Execute the request
  // Response response = client.newCall(request).execute();

  // // Print the response
  // System.out.println(response.body().string());

  // } catch (Exception e) {
  // e.printStackTrace();
  // }
  // return "";

  // }

  public static String gptTagging(String message) {
    message = "\"" + message + "\"";
    System.out.println(message);
    message =
      "{\n            \"role\": \"system\",\n            \"content\": \"You are an AI assistant that tags any message you will be given, given a message you will tag them with comma separated and don't return anything extra only the tags.        }," +
      "{\n            \"role\": \"user\",\n            \"content\":  " +
      message +
      " }";
    try {
      System.out.println("hello");

      Unirest.setTimeouts(0, 0);
      HttpResponse<String> response = Unirest
        .post(
          "https://openai-cf.istiaqueahmedarik.workers.dev/chat/completions"
        )
        .header("Content-Type", "application/json")
        .body(
          "{\n    \"model\": \"@cf/meta/llama-2-7b-chat-int8\",\n    \"messages\": [\n       " +
          message +
          "   ]\n}"
        )
        .asString();
      String body = response.getBody();

      JSONObject jsonObject = new JSONObject(body);
      String content = jsonObject
        .getJSONArray("choices")
        .getJSONObject(0)
        .getJSONObject("message")
        .getString("content");

      return content;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  public static AllPosts allCarts(String Email) {
    System.out.println("Accessing all carts of " + Email);
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("UsersCart")
      .document(Email)
      .collection("Carts")
      .orderBy("timestamp");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<PostCodeStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        PostCodeStruct post = document.toObject(PostCodeStruct.class);
        post.setLiked(isUserLikes(Email, post.getId()));
        ret.add(post);
      }
      AllPosts posts = new AllPosts(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static BasicResponse AddCart(PostCodeStruct post, String username) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    String decoded = decode(post.code);
    String modify = "";
    for (int i = 0; i < decoded.length(); i++) {
      if (decoded.charAt(i) == '"') {
        modify += "\\\"";
      } else if (decoded.charAt(i) == '\n') {
        modify += "\\n";
      } else if (decoded.charAt(i) == '\t') {
        modify += "\\t";
      } else {
        modify += decoded.charAt(i);
      }
    }
    post.setId(post.getId());
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    db
      .collection("UsersCart")
      .document(username)
      .collection("Carts")
      .document(post.getId())
      .set(post);
    return ret;
  }

  public static BasicResponse increaseDownload(String userName, String id)
    throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    // update donwlod on code, userpost, contribution
    DocumentReference docRef = db.collection("code").document(id);
    DocumentReference docRef1 = db
      .collection("UsersPost")
      .document(userName)
      .collection("Posts")
      .document(id);
    DocumentReference docRef2 = db
      .collection("Contributions")
      .document(userName);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    ApiFuture<DocumentSnapshot> document1 = docRef1.get();
    ApiFuture<DocumentSnapshot> document2 = docRef2.get();
    DocumentSnapshot docSnap = null;
    DocumentSnapshot docSnap1 = null;
    DocumentSnapshot docSnap2 = null;
    try {
      docSnap = document.get();
      docSnap1 = document1.get();
      docSnap2 = document2.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      ret.setMessage("No such document!");
      ret.setStatus_code(404);
      return ret;
    }
    PostCodeStruct post = docSnap.toObject(PostCodeStruct.class);
    post.setDownload(post.getDownload() + 1);
    docRef.set(post);
    PostCodeStruct post1 = docSnap1.toObject(PostCodeStruct.class);
    post1.setDownload(post1.getDownload() + 1);
    docRef1.set(post1);
    ContributionStruct contributionStruct = docSnap2.toObject(
      ContributionStruct.class
    );
    contributionStruct.setContribution(
      contributionStruct.getContribution() + 1
    );
    docRef2.set(contributionStruct);
    System.out.println(post.getDownload());
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    return ret;
  }

  public static BasicResponse increaseLike(String userName, String id) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    DocumentReference docRef = db.collection("code").document(id);
    DocumentReference docRef1 = db
      .collection("UsersPost")
      .document(userName)
      .collection("Posts")
      .document(id);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    ApiFuture<DocumentSnapshot> document1 = docRef1.get();
    DocumentSnapshot docSnap = null;
    DocumentSnapshot docSnap1 = null;
    try {
      docSnap = document.get();
      docSnap1 = document1.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      ret.setMessage("No such document!");
      ret.setStatus_code(404);
      return ret;
    }
    PostCodeStruct post = docSnap.toObject(PostCodeStruct.class);
    post.setLikes(post.getLikes() + 1);
    docRef.set(post);
    PostCodeStruct post1 = docSnap1.toObject(PostCodeStruct.class);
    post1.setLikes(post1.getLikes() + 1);
    docRef1.set(post1);
    System.out.println(post.getLikes());
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    System.out.println(userName + " " + id);
    DocumentReference likeRef = db
      .collection("likes")
      .document(MainUser.username)
      .collection(id)
      .document("like");
    ApiFuture<DocumentSnapshot> likeDoc = likeRef.get();
    DocumentSnapshot likeSnap = null;
    try {
      likeSnap = likeDoc.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (likeSnap != null && !likeSnap.exists()) {
      LikeStruct likeStruct = new LikeStruct(userName, id, true);
      likeRef.set(likeStruct);
    } else {
      LikeStruct likeStruct = likeSnap.toObject(LikeStruct.class);
      likeStruct.b = true;
      System.out.println(likeStruct.b);
      likeRef.set(likeStruct);
    }

    return ret;
  }

  public static BasicResponse decreaseLike(String userName, String id) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    DocumentReference docRef = db.collection("code").document(id);
    DocumentReference docRef1 = db
      .collection("UsersPost")
      .document(userName)
      .collection("Posts")
      .document(id);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    ApiFuture<DocumentSnapshot> document1 = docRef1.get();
    DocumentSnapshot docSnap = null;
    DocumentSnapshot docSnap1 = null;
    try {
      docSnap = document.get();
      docSnap1 = document1.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      ret.setMessage("No such document!");
      ret.setStatus_code(404);
      return ret;
    }
    PostCodeStruct post = docSnap.toObject(PostCodeStruct.class);
    post.setLikes(post.getLikes() - 1);
    docRef.set(post);
    PostCodeStruct post1 = docSnap1.toObject(PostCodeStruct.class);
    post1.setLikes(post1.getLikes() - 1);
    docRef1.set(post1);
    System.out.println(post.getLikes());
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    DocumentReference likeRef = db
      .collection("likes")
      .document(MainUser.username)
      .collection(id)
      .document("like");
    ApiFuture<DocumentSnapshot> likeDoc = likeRef.get();
    DocumentSnapshot likeSnap = null;
    try {
      likeSnap = likeDoc.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (likeSnap != null && !likeSnap.exists()) {
      LikeStruct likeStruct = new LikeStruct(userName, id, false);
      likeRef.set(likeStruct);
    } else {
      LikeStruct likeStruct = likeSnap.toObject(LikeStruct.class);
      likeStruct.b = false;
      likeRef.set(likeStruct);
    }
    return ret;
  }

  public static Boolean isUserLikes(String userName, String id) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("likes")
      .document(userName)
      .collection(id)
      .document("like");
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
    }
    if (docSnap != null && !docSnap.exists()) {
      LikeStruct likeStruct = new LikeStruct(userName, id, false);
      System.out.println(likeStruct.b + " " + likeStruct.id + " " + userName);
      docRef.set(likeStruct);
      return false;
    }
    LikeStruct likeStruct = docSnap.toObject(LikeStruct.class);
    System.out.println(likeStruct.b + " " + likeStruct.id + " " + userName);
    System.out.println(likeStruct.b + " " + likeStruct.id);
    return likeStruct.b;
  }

  public static BasicResponse clearCart(String Email) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    Query query = db
      .collection("UsersCart")
      .document(Email)
      .collection("Carts");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<PostCodeStruct> ret1 = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        PostCodeStruct post = document.toObject(PostCodeStruct.class);
        System.out.println(post.getCode());
        ret1.add(post);
        document.getReference().delete();
      }
      ret.setMessage("Success!");
      ret.setStatus_code(200);
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static String getRealName(String id) {
    return null;
  }

  public static AllPosts allPostOfUser(String username) {
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("UsersPost")
      .document(username)
      .collection("Posts")
      .orderBy("timestamp");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<PostCodeStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        PostCodeStruct post = document.toObject(PostCodeStruct.class);
        post.setLiked(isUserLikes(username, post.getId()));
        System.out.println(post.getCode());
        ret.add(post);
      }
      Collections.reverse(ret);
      AllPosts posts = new AllPosts(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }

    return null;
  }

  public static String convertSecondsToDHMS(double seconds) {
    int day = (int) TimeUnit.SECONDS.toDays((long) seconds);
    long hours = TimeUnit.SECONDS.toHours((long) seconds) - (day * 24);
    long minute =
      TimeUnit.SECONDS.toMinutes((long) seconds) -
      (TimeUnit.SECONDS.toHours((long) seconds) * 60);
    long second =
      TimeUnit.SECONDS.toSeconds((long) seconds) -
      (TimeUnit.SECONDS.toMinutes((long) seconds) * 60);
    StringBuilder result = new StringBuilder();
    if (day != 0) {
      result.append(day).append(" days ");
    }
    if (hours != 0) {
      result.append(hours).append(" hours ");
    }
    if (minute != 0) {
      result.append(minute).append(" minutes ");
    }
    if (second != 0) {
      result.append(second).append(" seconds ");
    }

    return result.toString().trim();
  }

  public static AllContests getCurrentContest()
    throws UnirestException, JsonProcessingException {
    List<SingleContest> ret = new ArrayList<>();
    Unirest.setTimeouts(0, 0);
    HttpResponse<String> response = Unirest
      .get(
        "https://clist.by/api/v4/contest/?upcoming=true&format_time=false&end_time__during=86400&filtered=true&category=list&order_by=start&&format=json&&username=istiaqueahmedarik&api_key=" +
        System.getProperty("CODEBOOK_CLIST_API_KEY")
      )
      .header("Cookie", "csrftoken=MIvfbOEfDwZ2nZr5VHavcrBaNrFFCNvC")
      .asString();
    String body = response.getBody();
    System.out.println(body);
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = mapper.readTree(body);
    if (rootNode.has("objects")) {
      JsonNode objectsNode = rootNode.get("objects");
      if (objectsNode.isArray()) {
        for (JsonNode node : objectsNode) {
          if (node.has("event")) {
            String start = node.get("start").asText();
            String host = node.get("host").asText();
            String name = node.get("event").asText();
            String duration = node.get("duration").asText();
            String href = node.get("href").asText();
            SingleContest singleContest = new SingleContest(
              start,
              duration,
              host,
              name,
              href
            );
            ret.add(singleContest);
          }
        }
      }
    }
    AllContests contests = new AllContests(ret);
    return contests;
  }

  public static BasicResponse AddResource(ResourceStruct post) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    DocumentReference docRef = db.collection("Resources").document();
    post.setId(docRef.getId());
    System.out.println("this is post:" + post.mainPost);
    post.setMainPost(post.mainPost);
    docRef.set(post);
    docRef =
      db
        .collection("UsersResource")
        .document(post.email)
        .collection("Resources")
        .document(post.id);
    docRef.set(post);
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    return ret;
  }

  public static AllResources allResources(int pageNumber) {
    int perPage = 10;
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("Resources")
      .orderBy("now")
      .startAfter(pageNumber * perPage)
      .limit(perPage);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<ResourceStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      System.out.println(documents.size());
      for (DocumentSnapshot document : documents) {
        ResourceStruct post = document.toObject(ResourceStruct.class);
        System.out.println(post.getMainPost());
        ret.add(post);
      }
      AllResources posts = new AllResources(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static AllResources allUserResources(int pageNumber) {
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("Resources")
      .document(MainUser.userEmail)
      .collection("Resources")
      .orderBy("now");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<ResourceStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      System.out.println(documents.size());
      for (DocumentSnapshot document : documents) {
        ResourceStruct post = document.toObject(ResourceStruct.class);
        System.out.println(post.getMainPost());
        ret.add(post);
      }
      AllResources posts = new AllResources(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static String commentOnId(
    String id,
    String comment,
    String my_id,
    String username
  ) {
    Firestore db = FirestoreClient.getFirestore();
    CommentStruct post = new CommentStruct(
      my_id,
      comment,
      username,
      Timestamp.now()
    );
    DocumentReference docRef = db
      .collection("AllComments")
      .document(id)
      .collection("Comments")
      .document();
    docRef.set(post);
    return username;
  }

  public static AllComments allComments(String id) {
    System.out.println("Accessing all comments of " + id);
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("AllComments")
      .document(id)
      .collection("Comments")
      .orderBy("now");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<CommentStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        CommentStruct post = document.toObject(CommentStruct.class);
        System.out.println("Added " + post.comment + " by " + post.username);
        ret.add(post);
      }
      AllComments posts = new AllComments(ret);
      return posts;
    } catch (Exception e) {
      System.out.println("Error");
      System.out.println(e);
    }
    return null;
  }

  public static AllContribution allContribution() {
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("Contributions")
      .orderBy("contribution", Query.Direction.DESCENDING);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<ContributionStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        ContributionStruct post = document.toObject(ContributionStruct.class);
        System.out.println(post.getUsername());
        ret.add(post);
      }
      AllContribution posts = new AllContribution(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static AllPosts allPostsForSearch() {
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("code")
      .orderBy("timestamp", Query.Direction.DESCENDING);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<PostCodeStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        PostCodeStruct post = document.toObject(PostCodeStruct.class);
        post.code = decode(post.code);
        ret.add(post);
      }
      AllPosts posts = new AllPosts(ret);
      return posts;
    } catch (Exception e) {
      System.out.println(e);
    }

    return null;
  }

  public static void usernameVsIdToken(String username, String idToken) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("UsernameVsIdToken")
      .document(username);
    docRef.set(new Id_data(idToken));
  }

  public static Id_data getIdToken(String username) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("UsernameVsIdToken")
      .document(username);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      return null;
    }
    Id_data id_data = docSnap.toObject(Id_data.class);
    return id_data;
  }

  public static ProfileStruct getProfile(String email) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("Profiles").document(email);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      return null;
    }
    ProfileStruct profileStruct = docSnap.toObject(ProfileStruct.class);
    return profileStruct;
  }

  public static BasicResponse updateResource(ResourceStruct post) {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    System.out.println(post.tags);
    DocumentReference docRef = db
      .collection("Resources")
      .document(post.getId());
    docRef.set(post);
    docRef =
      db
        .collection("UsersResource")
        .document(post.email)
        .collection("Resources")
        .document(post.getId());
    docRef.set(post);
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    return ret;
  }

  public static BasicResponse DeleteResource(String id)
    throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    BasicResponse ret = new BasicResponse();
    DocumentReference docRef = db.collection("Resources").document(id);
    ResourceStruct post = docRef.get().get().toObject(ResourceStruct.class);
    String email = post.email;
    docRef.delete();
    docRef =
      db
        .collection("UsersResource")
        .document(email)
        .collection("Resources")
        .document(id);
    docRef.delete();
    System.out.println("Deleted " + id);
    ret.setMessage("Success!");
    ret.setStatus_code(200);
    return ret;
  }

  public static BasicResponse updateImage(File img, String idToken) {
    BasicResponse ret = new BasicResponse();
    String url = uploadImageToFirebaseStorage(img);
    System.out.println(url);
    MainUser.photoUrl = url;
    UserDetails user = getUserDetails(idToken);
    String email = user.getEmail();
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("Profiles").document(email);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      return null;
    }
    ProfileStruct profileStruct = docSnap.toObject(ProfileStruct.class);
    profileStruct.setUserImage(url);
    docRef.set(profileStruct);
    OkHttpClient client = new OkHttpClient();
    MediaType mediaType = MediaType.parse("application/json");
    RequestBody body = RequestBody.create(
      String.format(
        "{\"idToken\":\"%s\",\"photoUrl\":\"%s\",\"returnSecureToken\":true}",
        idToken,
        url
      ),
      mediaType
    );

    String API_Key = System.getProperty("CODEBOOK_FIREBASE_API_KEY");

    String reqUrl =
      "https://identitytoolkit.googleapis.com/v1/accounts:update?key=" +
      API_Key;
    Request request = new Request.Builder()
      .url(reqUrl)
      .method("POST", body)
      .addHeader("Content-Type", "application/json")
      .build();
    Response response1 = null;
    try {
      response1 = client.newCall(request).execute();
      System.out.println(response1.body().string());
    } catch (IOException e) {
      e.printStackTrace();
    }
    ret.setMessage(url);
    ret.setStatus_code(200);

    db
      .collection("Profiles")
      .document(MainUser.username)
      .update("userImage", url);

    db
      .collection("Profiles")
      .document(MainUser.userEmail)
      .update("userImage", url);

    return ret;
  }

  public static String uploadImageToFirebaseStorage(File imagePost) {
    try {
      Bucket bucket = StorageClient.getInstance().bucket();
      String name = "image" + System.currentTimeMillis() + ".png";
      Blob blob = bucket.create(
        name,
        new FileInputStream(imagePost),
        "image/png"
      );
      blob.createAcl(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
      return blob.getMediaLink();
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static ExtraDetails getExtraDetails(String username) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db.collection("Extra").document(username);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
    if (docSnap != null && !docSnap.exists()) {
      return null;
    }
    ExtraDetails extraDetails = docSnap.toObject(ExtraDetails.class);
    return extraDetails;
  }

  public static void FriendRequest(String my_username, String friend_username)
    throws InterruptedException, ExecutionException {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(friend_username)
      .collection("Requests")
      .document(my_username);
    if (docRef.get().get().exists()) {
      return;
    }
    docRef.set(new FriendRequestStruct(my_username, Timestamp.now()));
  }

  public static void FriendRequestRemove(
    String my_username,
    String friend_username
  ) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(friend_username)
      .collection("Requests")
      .document(my_username);
    docRef.delete();
  }

  public static void AcceptFriend(String my_username, String frndUsername) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(my_username)
      .collection("Friends")
      .document(frndUsername);
    docRef.set(new FriendRequestStruct(frndUsername, Timestamp.now()));
    docRef =
      db
        .collection("Friendship")
        .document(frndUsername)
        .collection("Friends")
        .document(my_username);
    docRef.set(new FriendRequestStruct(my_username, Timestamp.now()));
    System.out.println(my_username + " " + frndUsername);
    docRef =
      db
        .collection("Friendship")
        .document(my_username)
        .collection("Requests")
        .document(frndUsername);
    docRef.delete();
  }

  public static void RejectFriendRequest(
    String my_username,
    String frndUsername
  ) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(my_username)
      .collection("Requests")
      .document(frndUsername);
    docRef.delete();
  }

  public static void CancelFrndRequest(
    String my_username,
    String frndUsername
  ) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(frndUsername)
      .collection("Requests")
      .document(my_username);
    docRef.delete();
  }

  public static void UnFriend(String my_username, String frndUsername) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(my_username)
      .collection("Friends")
      .document(frndUsername);
    docRef.delete();
    docRef =
      db
        .collection("Friendship")
        .document(frndUsername)
        .collection("Friends")
        .document(my_username);
    docRef.delete();
  }

  public static void setLastMessage(
    String my_username,
    String frndUsername,
    String message
  ) {
    MessageStruct messageStruct = new MessageStruct(
      my_username,
      frndUsername,
      message,
      Timestamp.now()
    );
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(my_username)
      .collection("Friends")
      .document(frndUsername);
    docRef.set(messageStruct);
    docRef =
      db
        .collection("Friendship")
        .document(frndUsername)
        .collection("Friends")
        .document(my_username);
    docRef.set(messageStruct);
  }

  public static void ThrowOnMessagePoll(
    String my_username,
    String frndUsername,
    String message
  ) {
    System.out.println(
      "Throwing on message poll " +
      my_username +
      " " +
      frndUsername +
      " " +
      message
    );
    MessageStruct messageStruct = new MessageStruct(
      my_username,
      frndUsername,
      message,
      Timestamp.now()
    );
    String a = my_username;
    String b = frndUsername;
    if (a.compareTo(b) > 0) {
      String temp = a;
      a = b;
      b = temp;
    }
    String msg_name = a + ":" + b;
    Firestore db = FirestoreClient.getFirestore();
    CollectionReference docRef = db
      .collection("MessagePoll")
      .document(msg_name)
      .collection("Messages");
    docRef.add(messageStruct);
  }

  List<MessageStruct> getFromMessagePoll(
    String my_username,
    String frndUsername
  ) {
    String a = my_username;
    String b = frndUsername;
    if (a.compareTo(b) > 0) {
      String temp = a;
      a = b;
      b = temp;
    }
    String msg_name = a + ":" + b;
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("MessagePoll")
      .document(msg_name)
      .collection("Messages")
      .orderBy("timestamp", Query.Direction.DESCENDING);
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<MessageStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        MessageStruct post = document.toObject(MessageStruct.class);
        ret.add(post);
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static List<ProfileStruct> allFriends(String username) {
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("Friendship")
      .document(username)
      .collection("Friends");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<ProfileStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        FriendRequestStruct singleFriend = document.toObject(
          FriendRequestStruct.class
        );
        ProfileStruct frnd = getProfile(singleFriend.my_username);
        frnd.username = getProfile(frnd.username).username;
        System.out.println(frnd.username);
        ret.add(frnd);
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  public static List<MessageTitleStruct> allMessageTitleStructs(
    String username
  ) {
    List<ProfileStruct> friends = allFriends(username);
    List<MessageTitleStruct> ret = new ArrayList<>();
    for (ProfileStruct p : friends) {
      String a = username;
      String b = p.getUsername();
      if (a.compareTo(b) > 0) {
        String temp = a;
        a = b;
        b = temp;
      }
      String msg_name = a + ":" + b;
      Firestore db = FirestoreClient.getFirestore();
      Query query = db
        .collection("MessagePoll")
        .document(msg_name)
        .collection("Messages")
        .orderBy("time", Query.Direction.DESCENDING)
        .limit(2);
      ApiFuture<QuerySnapshot> querySnapshot = query.get();
      try {
        QuerySnapshot documents = querySnapshot.get();
        if (documents.size() > 0) {
          MessageStruct messageStruct = documents
            .getDocuments()
            .get(0)
            .toObject(MessageStruct.class);
          System.out.println(p.username);
          MessageTitleStruct messageTitleStruct = new MessageTitleStruct(
            p.username,
            p.userImage,
            messageStruct.getTime()
          );
          ret.add(messageTitleStruct);
        }
      } catch (Exception e) {
        System.out.println(e);
      }
    }
    return ret;
  }

  public static List<ProfileStruct> allFriendRequests(String username) {
    Firestore db = FirestoreClient.getFirestore();
    Query query = db
      .collection("Friendship")
      .document(username)
      .collection("Requests");
    ApiFuture<QuerySnapshot> querySnapshot = query.get();
    List<ProfileStruct> ret = new ArrayList<>();
    try {
      QuerySnapshot documents = querySnapshot.get();
      for (DocumentSnapshot document : documents) {
        FriendRequestStruct singleFriend = document.toObject(
          FriendRequestStruct.class
        );
        ProfileStruct frnd = getProfile(singleFriend.my_username);
        ret.add(frnd);
      }
      return ret;
    } catch (Exception e) {
      System.out.println(e);
    }
    return null;
  }

  // 0 means not friend, 1 means i sent friend req, 2 means i get frnd req, 3
  // means friend
  public static int isFriend(String my_username, String frndUsername) {
    Firestore db = FirestoreClient.getFirestore();
    DocumentReference docRef = db
      .collection("Friendship")
      .document(my_username)
      .collection("Friends")
      .document(frndUsername);
    ApiFuture<DocumentSnapshot> document = docRef.get();
    DocumentSnapshot docSnap = null;
    try {
      docSnap = document.get();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (docSnap != null && !docSnap.exists()) {
      docRef =
        db
          .collection("Friendship")
          .document(frndUsername)
          .collection("Friends")
          .document(my_username);
      document = docRef.get();
      try {
        docSnap = document.get();
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (docSnap != null && !docSnap.exists()) {
        docRef =
          db
            .collection("Friendship")
            .document(my_username)
            .collection("Requests")
            .document(frndUsername);
        document = docRef.get();
        try {
          docSnap = document.get();
        } catch (Exception e) {
          e.printStackTrace();
        }
        if (docSnap != null && !docSnap.exists()) {
          docRef =
            db
              .collection("Friendship")
              .document(frndUsername)
              .collection("Requests")
              .document(my_username);
          document = docRef.get();
          try {
            docSnap = document.get();
          } catch (Exception e) {
            e.printStackTrace();
          }
          if (docSnap != null && !docSnap.exists()) {
            return 0;
          } else {
            return 1;
          }
        } else {
          return 2;
        }
      } else {
        return 3;
      }
    } else {
      return 3;
    }
  }
}
