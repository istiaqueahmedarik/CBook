package com.codebook;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SingleContestPost {

    @FXML
    private ImageView contest_image;

    @FXML
    private Label contest_name;

    @FXML
    private Label date;
    @FXML
    private Label duration;
    @FXML
    private Hyperlink link;

    void setPostDate(String platform, String contestName, String date, String duration, String url) {
        this.contest_name.setText(contestName);
        this.date.setText(date);
        String imgUrl = "";
        if (platform.toLowerCase().contains("codeforces")) {
            imgUrl = "https://sta.codeforces.com/s/91837/images/codeforces-logo-with-telegram.png";

        } else if (platform.toLowerCase().contains("codechef")) {
            imgUrl = "https://s3.amazonaws.com/codechef_shared/misc/fb-image-icon.png";
        } else {
            imgUrl = "com/codebook/images/contest.png";
        }

        this.contest_image.setImage(new ImageView(
                imgUrl)
                .getImage());
        this.duration.setText(duration);
        this.link.setOnMouseClicked(e -> {
            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(url));
            } catch (java.io.IOException e1) {
                System.out.println(e1.getMessage());
            }
        });
    }
}
