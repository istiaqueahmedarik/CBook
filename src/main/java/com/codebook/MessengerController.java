package com.codebook;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessengerController {
    @FXML
    public HBox FriendsList;
    @FXML
    public VBox MessageList;
    @FXML
    public TextField MessageTextBox;
    @FXML
    public Button SendMsg;

    @FXML
    private VBox messagebox1;
    @FXML
    private VBox messagebox2;
    @FXML
    private ScrollPane scrollPaneChat;
    @FXML
    private Label CurrentChatName;

    @FXML
    private Circle CurrentImage;

}
