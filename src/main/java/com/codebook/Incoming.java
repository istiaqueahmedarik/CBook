package com.codebook;

import com.itextpdf.text.Anchor;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class Incoming {

    @FXML
    public Label msg;
    @FXML
    public AnchorPane mainContainer;

}
