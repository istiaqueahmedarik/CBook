package com.codebook;

import java.util.concurrent.ExecutionException;

import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateController {

    @FXML
    private TextField CC;

    @FXML
    private TextField CF;

    @FXML
    private TextField Github;

    @FXML
    private TextField LinkedIn;

    @FXML
    private TextField Name;

    @FXML
    private StackPane stackPane;

    @FXML
    private Label warning;

    @FXML
    void Update(ActionEvent event) {
        if (Name.getText().isEmpty()) {
            warning.setText("Name cannot be empty");
            warning.setVisible(true);
            return;
        }
        ProgressIndicator progressIndicator = new ProgressIndicator();
        stackPane.getChildren().add(progressIndicator);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (Name.getText().equals(MainUser.displayName) == false) {
                    Methods.updateName(MainUser.idToken, Name.getText());
                }
                ExtraDetails ex = new ExtraDetails(LinkedIn.getText(), Github.getText(), CF.getText(), CC.getText());
                Methods.updateExtra(ex);
                MainUser.displayName = Name.getText();
                PageVariable.extraDetails = ex;
                return null;
            }
        };
        task.setOnSucceeded(event1 -> {
            Platform.runLater(() -> {
                System.out.println("Updated");
                progressIndicator.setVisible(false);
                stackPane.getChildren().remove(progressIndicator);
            });
        });
        new Thread(task).start();
        progressIndicator.progressProperty().bind(task.progressProperty());
    }

    public void initialize() throws InterruptedException, ExecutionException {
        ExtraDetails extra = new ExtraDetails();
        Firestore db = FirestoreClient.getFirestore();
        extra = db.collection("Extra").document(MainUser.username).get().get().toObject(ExtraDetails.class);
        PageVariable.extraDetails = extra;
        System.out.println(extra.getLinkedin());
        this.LinkedIn.setText(extra.getLinkedin());
        Github.setText(extra.getGithub());
        CF.setText(extra.getCf());
        CC.setText(extra.getCchef());
        Name.setText(MainUser.displayName);
    }

}
