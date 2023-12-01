package com.codebook;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class LoginController {
	@FXML
	private TextField email;

	@FXML
	private TextField password;
	@FXML
	private Label warning;
	@FXML
	private StackPane stackPane;

	private String response;
	private String refreshToken;
	private String idToken;
	private String Email;

	public void login() throws IOException, InterruptedException, InvocationTargetException {
		try {
			try {
				InputValidity.LoginInput(email.getText(), password.getText());
			} catch (ExceptionHandler e) {
				warning.setText(e.getMessage());
				Timeline timeline = new Timeline(
						new KeyFrame(Duration.millis(2000), ev -> warning.setText("")));
				timeline.play();
				warning.setVisible(true);
				return;
			}
			System.out.println("Login");

			ProgressIndicator progressIndicator = new ProgressIndicator();
			stackPane.getChildren().add(progressIndicator);
			Task<UserDetails> task1 = new Task<UserDetails>() {
				@Override
				protected UserDetails call() throws Exception {
					UserDetails res = Methods.getUserDetails(idToken);
					return res;
				}
			};

			task1.setOnSucceeded(event -> {
				UserDetails response = task1.getValue();
				System.out.println(response);
				Platform.runLater(() -> {
					if (response == null) {
						warning.setText("Invalid Credentials");
						Timeline timeline = new Timeline(
								new KeyFrame(Duration.millis(2000), ev -> warning.setText("")));
						timeline.play();
						warning.setVisible(true);
						System.out.println("Invalid Credentials");
						return;
					}
					try {
						LoginValidator.encryptor(Email, password.getText());
						String emailVerified = response.getEmailVerified();
						String displayName = response.getDisplayName();
						String photoUrl = response.getPhotoUrl();
						MainUser.displayName = displayName;
						MainUser.photoUrl = photoUrl;
						System.out.println(photoUrl);
						MainUser.emailVerified = emailVerified;
						MainUser.idToken = idToken;
						MainUser.userEmail = email.getText();
						MainUser.username = Methods.getProfile(email.getText()).username;

					} catch (IOException e) {
						e.printStackTrace();
					}
					try {
						if (MainUser.emailVerified.equals("true"))
							Main.setRoot("dashboard");
						else
							Main.setRoot("verify");
					} catch (IOException e) {
						e.printStackTrace();
					}
				});
			});
			Task<BasicAuthResponse> task = new Task<BasicAuthResponse>() {
				@Override
				protected BasicAuthResponse call() throws Exception {
					User user = new User(email.getText(), password.getText());
					BasicAuthResponse response = Methods.login(user);
					System.out.println(response);
					return response;
				}

			};
			task.setOnSucceeded(event -> {
				BasicAuthResponse response = task.getValue();
				System.out.println(response);

				Platform.runLater(() -> {
					Email = response.getEmail();

					if (Email == null) {
						warning.setText("Invalid Credentials");
						Timeline timeline = new Timeline(
								new KeyFrame(Duration.millis(2000), ev -> warning.setText("")));
						timeline.play();
						warning.setVisible(true);
						System.out.println("Invalid Credentials");
						return;
					}
					try {
						LoginValidator.encryptor(Email, password.getText());
					} catch (IOException e) {
						e.printStackTrace();
					}
					refreshToken = response.refreshToken;
					idToken = response.idToken;

					progressIndicator.visibleProperty().bind(task1.runningProperty());
					new Thread(task1).start();

				});
			});
			progressIndicator.visibleProperty().bind(task.runningProperty());
			new Thread(task).start();

		} catch (Exception e) {
			e.getCause().printStackTrace();
		}
		MainUser.userEmail = email.getText();
	}

	public void signup() throws IOException {
		Main.setRoot("signup");
	}

	public void resetPass() throws IOException {
		Main.setRoot("forgot");
	}

}