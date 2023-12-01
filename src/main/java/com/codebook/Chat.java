package com.codebook;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import org.json.JSONObject;

public class Chat {
  @FXML
  private TextArea inputChat;

  @FXML
  private ScrollPane otherChat;

  @FXML
  private VBox messageBox;

  @FXML
  private VBox messageBox1;

  @FXML
  private Button sendButton;

  public String messagePoll;

  public String continiousMessage;

  final String API_KEY = System.getProperty("CODEBOOK_PALM_API_KEY");

  public Chat() {
    continiousMessage = "";
    messagePoll = "";
  }

  public String Alternative(String message) {
    message = "\"" + message + " ";
    System.out.println(message);

    try {
      System.out.println("hello");

      Unirest.setTimeouts(0, 0);
      HttpResponse<String> response = Unirest
        .post(
          "https://generativelanguage.googleapis.com/v1beta3/models/text-bison-001:generateText?key=" +
          API_KEY
        )
        .header("Content-Type", "application/json")
        .body(
          "{\"prompt\":{\"text\": " +
          message +
          "\n the response of this message is:\"},\"temperature\":0.7,\"top_k\":40,\"top_p\":0.95,\"candidate_count\":1,\"max_output_tokens\":1024,\"stop_sequences\":[],\"safety_settings\":[{\"category\":\"HARM_CATEGORY_DEROGATORY\",\"threshold\":1},{\"category\":\"HARM_CATEGORY_TOXICITY\",\"threshold\":1},{\"category\":\"HARM_CATEGORY_VIOLENCE\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_SEXUAL\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_MEDICAL\",\"threshold\":2},{\"category\":\"HARM_CATEGORY_DANGEROUS\",\"threshold\":2}]}"
        )
        .asString();

      String body = response.getBody();
      System.out.println(body);
      JSONObject jsonObject = new JSONObject(body);
      String content = jsonObject
        .getJSONArray("candidates")
        .getJSONObject(0)
        .getString("output");
      System.out.println("Output: " + content);
      return content;
    } catch (Exception e) {
      System.out.println(e);
      return null;
    }
  }

  // ai_reply
  private void addMessage(String body) throws IOException, UnirestException {
    System.out.println(body);
    JSONObject jsonObject = new JSONObject(body);
    String content = "";
    try {
      content =
        jsonObject
          .getJSONArray("candidates")
          .getJSONObject(0)
          .getString("content");
    } catch (Exception e) {
      content = Alternative(continiousMessage);
    }
    Label label = new Label(content);
    String cleanContent = "";
    for (int i = 0; i < content.length(); i++) {
      if (content.charAt(i) == '"') {
        cleanContent += "\\\"";
      } else if (content.charAt(i) == '\n') {
        cleanContent += "\\n";
      } else if (content.charAt(i) == '\t') {
        cleanContent += "\\t";
      } else {
        cleanContent += content.charAt(i);
      }
    }

    System.out.println(content);

    messagePoll = messagePoll + ",{\"content\":\"" + cleanContent + "\"}";
    continiousMessage = continiousMessage + " " + cleanContent + " ";
    // label.setStyle(
    // "-fx-background-color: #e6e6e6; -fx-padding: 10px; -fx-border-radius:
    // 10px;fx-min-height: USE_COMPUTED_SIZE;fx-max-height: USE_COMPUTED_SIZE;");
    // // auto resize height

    // label.setWrapText(true);
    // messageBox.getChildren().add(label);
    FXMLLoader loader1 = new FXMLLoader(
      getClass().getResource("message_incoming.fxml")
    );
    AnchorPane anchorPane = loader1.load();
    Incoming messageIncoming = loader1.getController();
    messageIncoming.getMsg().setText(content);

    FXMLLoader loader2 = new FXMLLoader(
      getClass().getResource("message_incoming.fxml")
    );
    AnchorPane anchorPane2 = loader2.load();
    Incoming messageIncoming2 = loader2.getController();
    messageIncoming2.getMsg().setText(content);
    messageIncoming2
      .getMainContainer()
      .setStyle("-fx-background-color: transparent;");
    messageIncoming2.getMsg().setStyle("-fx-text-fill:transparent;");

    messageBox.getChildren().add(anchorPane);
    messageBox1.getChildren().add(anchorPane2);
  }

  // my text
  public void send() throws IOException, UnirestException {
    String message = inputChat.getText();
    inputChat.setText("");
    // Label label1 = new Label(message);
    // label1.setStyle("-fx-background-color: #c2d4f2; -fx-padding: 10px;
    // -fx-border-radius: 10px;-fx-spacing: 10px;");
    // label1.setWrapText(true);
    // messageBox.getChildren().add(label1);
    FXMLLoader loader1 = new FXMLLoader(
      getClass().getResource("message_outgoing.fxml")
    );
    AnchorPane anchorPane = loader1.load();
    Incoming messageIncoming = loader1.getController();
    messageIncoming.getMsg().setText(message);

    FXMLLoader loader2 = new FXMLLoader(
      getClass().getResource("message_outgoing.fxml")
    );
    AnchorPane anchorPane2 = loader2.load();
    Incoming messageIncoming2 = loader2.getController();

    messageIncoming2.getMsg().setText(message);
    messageIncoming2
      .getMainContainer()
      .setStyle("-fx-background-color: transparent;");
    messageIncoming2.getMsg().setStyle("-fx-text-fill:transparent;");

    messageBox.getChildren().add(anchorPane2);
    messageBox1.getChildren().add(anchorPane);

    String cleanMessage = "";
    for (int i = 0; i < message.length(); i++) {
      if (message.charAt(i) == '"') {
        cleanMessage += "\\\"";
      } else if (message.charAt(i) == '\n') {
        cleanMessage += "\\n";
      } else if (message.charAt(i) == '\t') {
        cleanMessage += "\\t";
      } else {
        cleanMessage += message.charAt(i);
      }
    }

    Task<String> task = new Task<String>() {

      @Override
      protected String call() throws Exception {
        Unirest.setTimeouts(0, 0);
        HttpResponse<String> response = Unirest
          .post(
            "https://generativelanguage.googleapis.com/v1beta2/models/chat-bison-001:generateMessage?key=" +
            API_KEY
          )
          .header("Content-Type", "text/plain")
          .body(
            "{ \r\n      \"prompt\": { \"context\": \"You are a great teacher who is always inspired student and for any given message, you try to give the theoretical answer first then a real life example\", \"messages\": [" +
            messagePoll +
            "]} \r\n}"
          )
          .asString();
        return response.getBody();
      }
    };
    System.out.println(messagePoll);
    if (messagePoll.length() == 0) {
      messagePoll = messagePoll + "{\"content\":\"" + cleanMessage + "\"}";
    } else {
      messagePoll = messagePoll + ",{\"content\":\"" + cleanMessage + "\"}";
    }
    continiousMessage = continiousMessage + " " + cleanMessage + " ";

    task.setOnSucceeded(
      e -> {
        String body = task.getValue();
        Platform.runLater(
          () -> {
            try {
              sendButton.setDisable(false);
              messageBox
                .getChildren()
                .remove(messageBox.getChildren().size() - 1);
              messageBox1
                .getChildren()
                .remove(messageBox1.getChildren().size() - 1);
              addMessage(body);
            } catch (IOException | UnirestException e1) {
              // TODO Auto-generated catch block
              e1.printStackTrace();
            }
          }
        );
      }
    );

    task.setOnRunning(
      e -> {
        sendButton.setDisable(true);
        // Label label2 = new Label("...");
        // label2.setStyle("-fx-background-color: #e6e6e6; -fx-padding: 10px;
        // -fx-border-radius: 10px;");
        // label2.setWrapText(true);
        // messageBox.getChildren().add(label2);
        FXMLLoader loader3 = new FXMLLoader(
          getClass().getResource("message_incoming.fxml")
        );
        FXMLLoader loader4 = new FXMLLoader(
          getClass().getResource("message_outgoing.fxml")
        );
        try {
          AnchorPane anchorPane3 = loader3.load();
          Incoming messageIncoming3 = loader3.getController();
          messageIncoming3.getMsg().setText("...");

          AnchorPane anchorPane4 = loader4.load();
          Incoming messageIncoming4 = loader4.getController();
          messageIncoming4.getMsg().setText("...");
          messageIncoming4
            .getMainContainer()
            .setStyle("-fx-background-color: transparent;");
          messageIncoming4.getMsg().setStyle("-fx-text-fill:transparent;");

          messageBox.getChildren().add(anchorPane3);
          messageBox1.getChildren().add(anchorPane4);
        } catch (IOException e1) {
          // TODO Auto-generated catch block
          e1.printStackTrace();
        }
      }
    );

    Thread thread = new Thread(task);
    thread.start();
    return;
  }
}
