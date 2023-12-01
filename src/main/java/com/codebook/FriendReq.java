package com.codebook;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FriendReq {

    @FXML
    private Button Accept;

    @FXML
    private Label IdToken;

    @FXML
    private Button Reject;

    @FXML
    private ImageView UserImage;

}
