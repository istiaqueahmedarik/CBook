package com.codebook;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javafx.event.ActionEvent;
import org.json.JSONObject;

public class FetchDataController {

  public void addText(ActionEvent event) {
    String rest =
      "https://ce.judge0.com/submissions?base64_encoded=true&wait=true";
    JSONObject payload = new JSONObject();
    payload.put("command_line_arguments", "");
    payload.put(
      "compiler_options",
      "-O3 --std=c++17 -Wall -Wextra -Wold-style-cast -Wuseless-cast -Wnull-dereference -Werror -Wfatal-errors -pedantic -pedantic-errors"
    );
    payload.put("language_id", 54);
    payload.put("redirect_stderr_to_stdout", true);
    payload.put(
      "source_code",
      "I2luY2x1ZGU8Yml0cy9zdGRjKysuaD4KdXNpbmcgbmFtZXNwYWNlIHN0ZDsKaW50IG1haW4oKQp7CiAgICBjb3V0PDwidGVzdCI8PGVuZGw7Cn0="
    );
    payload.put("stdin", "MQ==");

    String jsonString = payload.toString();

    System.out.println(jsonString);
    try {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request = HttpRequest
        .newBuilder()
        .uri(new URI(rest))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(jsonString))
        .build();
      HttpResponse<String> response = client.send(
        request,
        HttpResponse.BodyHandlers.ofString()
      );
      System.out.println(response);
    } catch (Exception e) {
      System.out.println(e);
    }
  }
}
