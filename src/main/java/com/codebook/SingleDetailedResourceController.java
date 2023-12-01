package com.codebook;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import lombok.Getter;
import lombok.Setter;
import javafx.stage.StageStyle;

@Getter
@Setter
public class SingleDetailedResourceController {

    @FXML
    public Label ResourceTitle;

    public String ResourcePost;
    @FXML
    public Label date;

    @FXML
    public Label email;

    @FXML
    public Button dltBtn;

    @FXML
    public Button editBtn;

    public String image;

    public String id;

    public void setResource(String title, String date, String email, String post, String image, String id) {
        this.ResourceTitle.setText(title);
        this.date.setText(date);
        this.email.setText(email);
        this.ResourcePost = (post);
        this.image = image;
        this.id = id;
    }

}
