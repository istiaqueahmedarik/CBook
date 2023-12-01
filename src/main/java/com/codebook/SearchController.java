package com.codebook;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchController {
    @FXML
    private TextField search;

    @FXML
    private Button searchButton;
}
