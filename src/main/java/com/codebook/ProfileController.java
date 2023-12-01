package com.codebook;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileController {
    @FXML
    public ImageView CcIcon;

    @FXML
    public ImageView EmailIcon;

    @FXML
    public ImageView LnIcon;

    @FXML
    public ImageView cFicon;

    @FXML
    public Label conNumber;

    @FXML
    public Label displayName;

    @FXML
    public ImageView displayUrl;

    @FXML
    public Button editProfile;

    @FXML
    public Button AddAdmin;

    @FXML
    private Button addFrnd;

    @FXML
    private Button FrndReq;

    @FXML
    public ImageView ghIcon;

    @FXML
    public Label username;

    private void downloadImage(String url, String localFilename) {
        try {
            // Code to download the image goes here
            URI uri = new URI("https://robohash.org/" + MainUser.username);
            InputStream in = uri.toURL().openStream();
            Files.copy(in, Paths.get("com/codebook/images/" + localFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setDisplayUrl(String url) {
        if (url == "") {
            url = "com/codebook/images/3.png";
        }
        this.displayUrl.setImage(new ImageView(url).getImage());
    }

    public void updateInfo() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdatePost.fxml"));
        Parent root = (Parent) loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(scene);

        stage.show();

    }
}
