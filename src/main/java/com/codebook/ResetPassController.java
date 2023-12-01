package com.codebook;

import java.io.IOException;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class ResetPassController {
    @FXML
    private TextField email;
    @FXML
    private Label warning;
    @FXML
    private StackPane stackPane;
    @FXML
    private Button bt;

    public void resetPass() {
        if (email.getText().equals("")) {
            warning.setText("Please enter your email.");
            return;
        }
        ProgressIndicator progressIndicator = new ProgressIndicator();
        stackPane.getChildren().add(progressIndicator);
        Task<Void> sentResetEmail = new Task<Void>() {

            @Override
            protected Void call() throws Exception {

                Methods.resetPassEmailSent(email.getText());
                return null;
            }
        };
        sentResetEmail.setOnSucceeded(e -> {
            bt.setDisable(true);
            bt.setText("Email Sent. Please check your inbox.");
            warning.setText("Email sent. Please check your inbox.");
            System.out.println("Resetting password for " + email.getText());
        });
        Thread thread = new Thread(sentResetEmail);
        thread.start();
        progressIndicator.visibleProperty().bind(sentResetEmail.runningProperty());

    }

    public void goToLogin() throws IOException {
        Main.setRoot("login");
    }
}