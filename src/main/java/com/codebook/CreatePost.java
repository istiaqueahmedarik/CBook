package com.codebook;

import java.io.FileReader;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

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
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreatePost implements Initializable {
    @FXML
    public ChoiceBox<String> languageSelector;
    @FXML
    public TextField title;
    @FXML
    public TextArea code;
    @FXML
    public TextArea input;
    @FXML
    public StackPane stackPane;
    @FXML
    public Button addPost;

    public String id;

    public String time = "";
    public String memory = "";
    public String output = "";

    public String[] language = { "CPP", "Java", "Python" };

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        languageSelector.getItems().addAll(language);
    }

    public String encodeToBase64(String input) {
        return Base64.getUrlEncoder().encodeToString(input.getBytes());
    }

    public String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    public void createPost() {
        String lang = languageSelector.getValue();
        String title = this.title.getText();
        String code = this.code.getText();
        String input = this.input.getText();
        try {
            InputValidity.codeInput(title, code, lang);
        } catch (ExceptionHandler e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
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
                // String req = "http://localhost:8080/createPost?Secret=" + validator +
                // "&userImage=" + MainUser.photoUrl
                // + "&id=" + MainUser.idToken + "&code=" + encodeToBase64(code) + "&input="
                // + encodeToBase64(input)
                // + "&languege=" + lang + "&title=" + title + "&time=" + time + "&memory=" +
                // memory + "&output="
                // + output;
                // System.out.println(validator);
                // String res = requestHandler.GetRequest(req);

                Methods.AddCode(MainUser.username, MainUser.userEmail, MainUser.displayName, MainUser.photoUrl,
                        encodeToBase64(code),
                        encodeToBase64(input), lang, title, time, memory, output);
                return null;
            }
        };
        addThePost.setOnSucceeded(e -> {
            Platform.runLater(() -> {
                if (code.equals("")) {
                    return;
                }

                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Post created successfully!");
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
                if (!res.getDescription().equals("Accepted")) {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText(res.getDescription());
                    alert.showAndWait();
                    return;
                }
                progressIndicator.visibleProperty().bind(addThePost.runningProperty());
                new Thread(addThePost).start();
            });
        });

        progressIndicator.visibleProperty().bind(task.runningProperty());
        new Thread(task).start();
    }

}