package com.codebook;

import java.util.concurrent.ExecutionException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddAdminController {

    @FXML
    public Button submitAdmin;

    @FXML
    public TextField username;

    @FXML
    public void addAdmin(ActionEvent event) throws InterruptedException, ExecutionException {
        if (username.getText().equals("")) {
            return;
        }
        Methods.addAdmin(username.getText());
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Admin added successfully");
        alert.showAndWait();

    }

}
