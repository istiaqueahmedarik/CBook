package com.codebook;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import com.google.cloud.Timestamp;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

public class UpdatePost extends CreatePost {

    private static UpdatePost instance;

    public String id;
    public String like, download;

    public void update() {
        String lang = languageSelector.getValue();
        String title = this.title.getText();
        String code = this.code.getText();
        String input = this.input.getText();
        if (title.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Title cannot be empty!");
            alert.showAndWait();
            return;
        }
        if (code.isEmpty()) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Code cannot be empty!");
            alert.showAndWait();
            return;
        }
        if (lang == null) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Please select a language!");
            alert.showAndWait();
            return;
        }

        RequestHandler requestHandler = new RequestHandler();
        GetjsonData getjsonData = new GetjsonData();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        stackPane.getChildren().add(progressIndicator);

        Task<Void> addThePost = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                String validator = "";
                FileReader fr = new FileReader("validator.txt");
                while (fr.read() != -1) {
                    validator += fr.read();
                }
                fr.close();
                // String req = "http://localhost:8080/updatePost?id=" + id + "&username=" +
                // MainUser.userEmail
                // + "&userImage=" + MainUser.photoUrl + "&code=" + encodeToBase64(code) +
                // "&input="
                // + encodeToBase64(input)
                // + "&languege=" + lang + "&title=" + title + "&time=" + time + "&memory=" +
                // memory + "&output="
                // + output;
                // System.out.println(validator);
                // String res = requestHandler.GetRequest(req);
                PostCodeStruct post = new PostCodeStruct(MainUser.username, MainUser.displayName, MainUser.userEmail,
                        encodeToBase64(code), title, id, MainUser.photoUrl, lang, time, memory, output, "", input,
                        Timestamp.now(), Integer.valueOf(like), 0, Integer.valueOf(download), false, MainUser.idToken,
                        new ProfileStruct(MainUser.displayName, MainUser.username, MainUser.photoUrl,
                                MainUser.idToken, Methods.isAdmin(MainUser.username)));
                Methods.updatePost(post);
                return null;
            }
        };
        addThePost.setOnSucceeded(e -> {
            // String res = addThePost.getValue();
            // System.out.println(res);
            Platform.runLater(() -> {
                if (code.equals("")) {
                    return;
                }

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Post Edited successfully!");
                alert.showAndWait();

            });
        });
        Task<CompileResponse> task = new Task<CompileResponse>() {
            @Override
            protected CompileResponse call() throws Exception {
                // String req = "http://localhost:8080/compile";
                // HashMap<String, String> params = new HashMap<>();
                // params.put("source_code", encodeToBase64(code));
                // params.put("language", lang);
                // params.put("input", encodeToBase64(input));
                // String res = requestHandler.PostRequest(req, params);
                // System.out.println(req);
                String langCode = "0";
                if (lang.equals("CPP"))
                    langCode = "54";
                if (lang.equals("Java"))
                    langCode = "4";
                if (lang.equals("Python"))
                    langCode = "71";

                CompileResponse res = Methods.compile(encodeToBase64(code), langCode, encodeToBase64(input));

                return res;
            }
        };

        task.setOnSucceeded(e -> {
            CompileResponse res = task.getValue();
            System.out.println(res);
            Platform.runLater(() -> {
                time = res.getTime();
                memory = res.getMemory();
                output = res.getStdout();
                progressIndicator.visibleProperty().bind(addThePost.runningProperty());
                new Thread(addThePost).start();
            });
        });

        progressIndicator.visibleProperty().bind(task.runningProperty());
        new Thread(task).start();
    }

    public static UpdatePost getInstance() {
        if (instance == null) {
            instance = new UpdatePost();
        }
        return instance;
    }

    public void updateFXML(CreatePost cp) {
        this.setLanguageSelector(cp.languageSelector);
        this.setTitle(cp.title);
        this.setCode(cp.code);
        this.setInput(cp.input);
        this.setStackPane(cp.stackPane);
        this.setAddPost(cp.addPost);
        this.getAddPost().setOnAction(e -> {
            update();
        });
        this.getAddPost().setText("Update");
    }

    public void setEditData(String id, String title, String code, String input, String output, String time,
            String memory, String aiResponse) {
        this.id = id;
        this.title.setText(title);
        this.code.setText(decodeBase64(code));
        this.input.setText(decodeBase64(input));
        this.output = output;
        this.time = time;
        this.memory = memory;
    }
}
