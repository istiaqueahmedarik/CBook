package com.codebook;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

public class SendingMessageController implements Initializable {
    @FXML
    public Button sendBtn;
    @FXML
    private VBox messageBox1;

    @FXML
    private VBox messageBox2;

    public void initialize(java.net.URL arg0, java.util.ResourceBundle arg1) {
    }
}
