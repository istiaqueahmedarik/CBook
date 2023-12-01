package com.codebook;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.google.cloud.Timestamp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Alert.AlertType;

public class UpdateResource extends AddResourceController {
    public void updateFXML(AddResourceController addResourceController) {
        this.ResourceTitle = (addResourceController.getResourceTitle());
        this.ResourcePost = (addResourceController.getResourcePost());
        this.uploadButton = (addResourceController.getUploadButton());
        // this.selectedFile = addResourceController.getSelectedFile();
    }

    public void update() {
        String title = this.ResourceTitle.getText();
        String POST = this.ResourcePost.getText();
        if (title.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Title cannot be empty!");
            alert.showAndWait();
            return;
        }
        if (POST.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Post cannot be empty!");
            alert.showAndWait();
            return;
        }
        ProgressIndicator progressIndicator = new ProgressIndicator();
        loading.getChildren().add(progressIndicator);

        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {

                // String Image = ImageToBase64();
                System.out.println(title);
                System.out.println(POST);
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
                // String req = "http://localhost:8080/updateResource";
                // HashMap<String, String> map = new HashMap<>();
                // map.put("Secret", validator);
                // map.put("id", MainUser.idToken);
                // map.put("res_id", res_id);
                // // map.put("ImagePost", Image);
                // map.put("title", title);
                // map.put("mainPost", post);
                // String res = requestHandler.PostRequest(req, map);
                // System.out.println(res);
                ResourceStruct post = new ResourceStruct(MainUser.userEmail, title, POST, "",
                        Timestamp.now(), "", MainUser.idToken);
                Methods.updateResource(post);
                return null;
            }
        };
        progressIndicator.visibleProperty().bind(task.runningProperty());
        new Thread(task).start();

    }

    public void setEditResource(String title, String post, String res_id) {
        System.out.println("Edit Resource " + res_id);
        this.ResourceTitle.setText(title);
        this.ResourcePost.setText(post);
        this.res_id = res_id;
        this.uploadButton.setOnAction(e -> {
            update();
        });
    }
}
