package com.codebook;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter

public class SingleCommentController {
    @FXML
    private Label DetailComment;

    @FXML
    private Label username;
}
