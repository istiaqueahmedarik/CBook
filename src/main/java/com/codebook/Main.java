package com.codebook;

import com.google.firebase.FirebaseApp;
import eu.iamgio.animated.transition.AnimatedSwitcher;
import eu.iamgio.animated.transition.AnimationPair;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
  public static Scene scene;
  private int index = 0;
  static AnimatedSwitcher switcher = new AnimatedSwitcher(
    AnimationPair.fadeDown()
  );

  public void setUpEnv() {
    try (BufferedReader br = new BufferedReader(new FileReader(".env"))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] parts = line.split("=");
        if (parts.length == 2) {
          String key = parts[0].trim();
          String value = parts[1].trim();
          System.setProperty(key, value);
          System.out.println(key + " " + value);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void start(Stage stage) throws IOException, URISyntaxException {
    setUpEnv();
    // System.out.println(System.getProperty("FIREBASE_API_KEY"));
    PageVariable.posts = new ArrayList<>();
    PageVariable.resources = new ArrayList<>();
    PageVariable.carts = new ArrayList<>();
    PageVariable.personal = new ArrayList<>();
    PageVariable.contests = new ArrayList<>();
    PageVariable.contributions = new ArrayList<>();
    PageVariable.personalResource = new ArrayList<>();
    PageVariable.extraDetails = new ExtraDetails();
    FirebaseApp.initializeApp(FirestoreController.initFirestore());
    Parent root = loadFXML("login");
    scene = new Scene(switcher);

    Screen screen = Screen.getPrimary();
    Task<Boolean> loginValidate = new Task<Boolean>() {

      @Override
      protected Boolean call() throws Exception {
        return LoginValidator.validatePassword();
      }
    };

    loginValidate.setOnSucceeded(
      e -> {
        // stage.hide();
        Boolean result = loginValidate.getValue();
        System.out.println(result);
        Rectangle2D bounds = screen.getVisualBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());

        Platform.runLater(
          () -> {
            if (result) {
              System.out.println("Logged in");
              try {
                switcher.of(loadFXML("dashboard"));
              } catch (IOException e1) {
                e1.printStackTrace();
              }
              scene
                .getStylesheets()
                .add(getClass().getResource("styles.css").toExternalForm());
              stage.setResizable(false);
              stage.setScene(scene);

              stage.show();
            } else {
              System.out.println("Not logged in");
              try {
                switcher.of(loadFXML("login"));
              } catch (IOException e1) {
                e1.printStackTrace();
              }
              scene
                .getStylesheets()
                .add(getClass().getResource("styles.css").toExternalForm());
              stage.setResizable(false);
              stage.setScene(scene);
              stage.show();
            }
          }
        );
      }
    );

    Thread thread = new Thread(loginValidate);
    thread.start();
  }

  static void setRoot(String fxml) throws IOException {
    switcher.setChild(loadFXML(fxml));
  }

  private static Parent loadFXML(String fxml) throws IOException {
    FXMLLoader fxmlLoader = new FXMLLoader(
      Main.class.getResource(fxml + ".fxml")
    );
    return fxmlLoader.load();
  }

  public static void main(String[] args) {
    launch();
  }
}
