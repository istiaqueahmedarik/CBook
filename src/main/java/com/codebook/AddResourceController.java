package com.codebook;

import java.io.File;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import lombok.Getter;
import lombok.Setter;

import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.cloud.Timestamp;

@Getter
@Setter
public class AddResourceController {
    @FXML
    public TextArea ResourcePost;

    @FXML
    public TextField ResourceTitle;

    @FXML
    public Button uploadButton;

    @FXML
    public StackPane loading;
    // public File selectedFile;

    public String res_id;

    // public void uploadImage() {
    // FileChooser fileChooser = new FileChooser();
    // fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
    // fileChooser.getExtensionFilters().addAll(
    // new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg",
    // "*.gif"));
    // selectedFile = fileChooser.showOpenDialog(null);
    // if (selectedFile != null) {
    // System.out.println("File selected: " + selectedFile.getName());
    // } else {
    // System.out.println("File selection cancelled.");
    // }
    // }

    // public String ImageToBase64() {
    // String base64Image = "";
    // if (selectedFile != null) {
    // try {
    // FileInputStream fileInputStream = new FileInputStream(selectedFile);
    // byte[] imageBytes = new byte[(int) selectedFile.length()];
    // fileInputStream.read(imageBytes);
    // base64Image = Base64.getEncoder().encodeToString(imageBytes);
    // fileInputStream.close();
    // } catch (IOException e) {
    // System.out.println("Error reading image file: " + e.getMessage());
    // }
    // }
    // return base64Image;

    // }

    public void addResource() {
        ProgressIndicator progressIndicator = new ProgressIndicator();
        loading.getChildren().add(progressIndicator);
        System.out.println("Add Resource: " + ResourceTitle.getText() + " " + ResourcePost.getText() + " "
                + MainUser.userEmail + " " + MainUser.idToken);
        String title = ResourceTitle.getText();
        String post = ResourcePost.getText();
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                if (title.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Title cannot be empty!");
                    alert.showAndWait();
                    return null;
                }
                if (post.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Post cannot be empty!");
                    alert.showAndWait();
                    return null;
                }
                // String Image = ImageToBase64();
                System.out.println(title);
                System.out.println("Post: ->" + post);
                // System.out.println(Image);
                String validator = "";
                try (BufferedReader br = new BufferedReader(new FileReader("validator.txt"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        validator += line;
                    }
                } catch (IOException e) {
                    System.out.println("Error reading validator file: " + e.getMessage());
                }
                // RequestHandler requestHandler = new RequestHandler();
                // String req = "http://localhost:8080/createResource";
                // HashMap<String, String> map = new HashMap<>();
                // map.put("Secret", validator);
                // map.put("id", MainUser.idToken);
                // // map.put("ImagePost", Image);
                // map.put("title", title);
                // map.put("mainPost", post);
                // String res = requestHandler.PostRequest(req, map);
                // System.out.println(res);
                System.out.println("post: " + post);
                ResourceStruct resourceStruct = new ResourceStruct(MainUser.userEmail, title, post, "", Timestamp.now(),
                        "", MainUser.idToken);
                Methods.AddResource(resourceStruct);
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Resource Added Successfully!");
                alert.showAndWait();
                return null;
            }
        };
        progressIndicator.visibleProperty().bind(task.runningProperty());
        new Thread(task).start();

    }
}
