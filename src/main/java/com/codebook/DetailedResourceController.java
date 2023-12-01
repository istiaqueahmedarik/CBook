package com.codebook;

import com.itextpdf.text.Anchor;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DetailedResourceController {
  @FXML
  private Label mainResource;

  @FXML
  private TextArea SingleComment;

  @FXML
  private VBox Comments;

  @FXML
  private Label title;

  @FXML
  private Button backButton;

  private String id;

  public void setMainResource(String mainResource) {
    this.mainResource.setText(mainResource);
  }

  public void setTitle(String title) {
    this.title.setText(title);
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setSingleComment(String singleComment) {
    this.SingleComment.setText(singleComment);
  }

  public void getAll() {
    RequestHandler requestHandler = new RequestHandler();
    List<CommentStruct> all = requestHandler.getComments(id);
    if (all == null) {
      System.out.println("No comments");
      return;
    }
    for (CommentStruct commentStruct : all) {
      AnchorPane SingleComment = new AnchorPane();
      FXMLLoader loader = new FXMLLoader(
        getClass().getResource("SingleComment.fxml")
      );
      try {
        SingleComment = loader.load();
      } catch (IOException e) {
        e.printStackTrace();
      }
      SingleCommentController singleCommentController = loader.getController();
      singleCommentController.getUsername().setText(commentStruct.username);
      singleCommentController.getDetailComment().setText(commentStruct.comment);
      Comments.getChildren().add(SingleComment);
    }
  }

  public void sendComment() {
    String my_id = MainUser.idToken;
    String sendToId = id;
    String comment = SingleComment.getText();
    SingleComment.setText("");
    // RequestHandler requestHandler = new RequestHandler();
    // String res = requestHandler.GetRequest("http://localhost:8080/comment?" +
    // "id=" + sendToId
    // + "&comment=" + comment + "&my_id=" + my_id);
    // System.out.println(res);
    Methods.commentOnId(sendToId, comment, my_id, MainUser.username);
    AnchorPane SingleComment = new AnchorPane();
    FXMLLoader loader = new FXMLLoader(
      getClass().getResource("SingleComment.fxml")
    );
    try {
      SingleComment = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }
    SingleCommentController singleCommentController = loader.getController();
    singleCommentController.getUsername().setText(MainUser.username);
    singleCommentController.getDetailComment().setText(comment);
    Comments.getChildren().add(SingleComment);
  }
}
