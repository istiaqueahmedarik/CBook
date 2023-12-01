package com.codebook;

import java.net.URL;
import java.util.Base64;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SinglePostController {
  private String input, output, time, memory, aiResponse;

  @FXML
  private ResourceBundle resources;

  @FXML
  private URL location;

  @FXML
  private ImageView PostPic;

  @FXML
  private Label PostTitle;

  @FXML
  private Label PostCode;

  @FXML
  private StackPane stackPane;
  @FXML
  private BorderPane detailed;
  @FXML
  private Button add;
  @FXML
  private ScrollPane scrollBar;
  @FXML
  private Button downloadNumbers;
  @FXML
  private Button deleteBtn;

  @FXML
  private Button likeNumber;

  @FXML
  private Button editBtn;

  @FXML
  void initialize() {
    assert PostPic != null : "fx:id=\"PostPic\" was not injected: check your FXML file 'Post.fxml'.";
    assert PostTitle != null : "fx:id=\"PostTitle\" was not injected: check your FXML file 'Post.fxml'.";

  }

  public static String decode(String base64String) {
    byte[] decodedBytes = Base64.getUrlDecoder().decode(base64String);
    return new String(decodedBytes);
  }

  public void setPostPic(String url) {
    PostPic.setImage(new javafx.scene.image.Image(url));
  }

  public void setPostTitle(String title) {
    PostTitle.setText(title);
  }

  public void setCode(String code) {
    PostCode.setText(decode(code));
  }

  public void setData(String title, String code, String url, String input, String output, String time,
      String memory, String aiResponse) {
    setPostTitle(title);
    setCode(code);
    setPostPic(url);
    this.input = input;
    this.output = output;
    this.time = time;
    this.memory = memory;
    this.aiResponse = aiResponse;

  }

  public void deletePost() {
    Alert alert = new Alert(AlertType.CONFIRMATION);
    alert.setTitle("Date format warning");
    Optional<ButtonType> result = alert.showAndWait();
    if (result.get() == ButtonType.OK) {
      System.out.println("OK");
    } else {
    }
  }

}