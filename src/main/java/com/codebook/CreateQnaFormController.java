package com.codebook;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateQnaFormController {

    @FXML
    private Label QuestionTitle;

    @FXML
    private Label QuestionTitle1;

    @FXML
    private TextField QuestionTitleText;

    @FXML
    private TextArea QuestionTxtArea;

}
