package com.codebook;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContributionSingleController {
    @FXML
    private ImageView ContributionImage;

    @FXML
    private Label ContributionUser;

    public ContributionSingleController(String imgUrl, String name) {
        this.ContributionImage = new ImageView(imgUrl);
        this.ContributionUser = new Label(name);
    }

    public void setImage(String imgUrl) {
        this.ContributionImage.setImage(new Image(imgUrl));
    }
}
