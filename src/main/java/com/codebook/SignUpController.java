package com.codebook;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class SignUpController {
  @FXML
  private TextField Name;

  @FXML
  private TextField Email;

  @FXML
  private PasswordField Password;

  @FXML
  private PasswordField RepeatPass;

  @FXML
  private TextField Username;

  @FXML
  private Label warning;

  @FXML
  private StackPane stackPane;
  private String response, req, refreshToken, idToken;

  public void signup() throws IOException, InterruptedException, ExecutionException {
    try {
      InputValidity.signUpValidator(Name.getText(), Email.getText(), Password.getText(), RepeatPass.getText(),
          Username.getText());
    } catch (ExceptionHandler e) {
      warning.setText(e.getMessage());
      Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), ev -> warning.setText("")));
      timeline.play();
      warning.setVisible(true);
      return;
    }

    RequestHandler requestHandler = new RequestHandler();
    GetjsonData getjsonData = new GetjsonData();
    ProgressIndicator progressIndicator = new ProgressIndicator();
    stackPane.getChildren().add(progressIndicator);

    Task<UserDetails> task2 = new Task<UserDetails>() {
      @Override
      protected UserDetails call() throws Exception {
        UserDetails response = Methods.getUserDetails(idToken);
        return response;
      }
    };
    task2.setOnSucceeded(event -> {
      UserDetails response = task2.getValue();
      Platform.runLater(() -> {
        String isEmailVerified = response.getEmailVerified();

        if (isEmailVerified.equals("false")) {
          // req = "http://localhost:8080/send_mail?idToken=" +
          // idToken;
          // response = requestHandler.GetRequest(req);
          Id_data id_data = new Id_data(idToken);
          Methods.sendMail(id_data);
          try {
            Main.setRoot("verify");
          } catch (IOException e) {
            e.printStackTrace();
          }
          return;
        }
        try {
          Main.setRoot("login");
        } catch (IOException e) {
          e.printStackTrace();
        }

      });
    });

    Task<BasicResponse> task = new Task<BasicResponse>() {
      @Override
      protected BasicResponse call() throws Exception {
        User user = new User(Username.getText(), Password.getText(), Email.getText(), Name.getText(), "idToken");
        BasicResponse response = Methods.signUp(user);
        return response;
      }
    };
    task.setOnSucceeded(event -> {
      BasicResponse response = task.getValue();
      Platform.runLater(() -> {
        System.out.println(response);
        String Status_code = response.getStatus_code().toString();

        try {
          InputValidity.intermediateSignUpValidator(Integer.valueOf(Status_code));
        } catch (ExceptionHandler e) {
          warning.setText(e.getMessage());
          Timeline timeline = new Timeline(new KeyFrame(Duration.millis(2000), ev -> warning.setText("")));
          timeline.play();
          warning.setVisible(true);
          return;
        }

        if (Status_code == Integer.toString(201)) {
          try {
            Main.setRoot("verify");
          } catch (IOException e) {
            e.printStackTrace();
          }
          return;
        }
        refreshToken = response.getRefreshToken();

        idToken = response.getIdToken();

        progressIndicator.visibleProperty().bind(task2.runningProperty());
        new Thread(task2).start();
      });

    });
    new Thread(task).start();
    progressIndicator.visibleProperty().bind(task.runningProperty());
    MainUser.userEmail = Email.getText();

  }

  public void login() throws IOException {
    Main.setRoot("login");
  }

}