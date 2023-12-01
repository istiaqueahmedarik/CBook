package com.codebook;

import java.util.List;

import com.google.cloud.firestore.DocumentChange;
import com.google.cloud.firestore.EventListener;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreException;
import com.google.cloud.firestore.ListenerRegistration;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firebase.cloud.FirestoreClient;

import eu.iamgio.animated.transition.container.AnimatedVBox;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;

public class ChangeToMessenger {
    Firestore db = FirestoreClient.getFirestore();
    private ListenerRegistration currentListener;

    public void changeToMsg(AnimatedVBox vBox, VBox post) {
        vBox.getChildren().clear();
        if (!post.getChildren().isEmpty() && post.getChildren().get(post.getChildren().size() - 1) instanceof Button) {
        }
        System.out.println("ChangeToSearch");
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("message.fxml"));
        AnchorPane anchorPane = null;
        try {
            anchorPane = fxmlLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }

        MessengerController messengerController = fxmlLoader.getController();

        List<ProfileStruct> friends = new Methods().allFriends(MainUser.username);
        messengerController.getFriendsList().getChildren().clear();
        if (friends.isEmpty()) {
            Label label = new Label("No Friends");
            label.setStyle("-fx-text-fill: white;");
            messengerController.getFriendsList().getChildren().add(label);
        }
        for (ProfileStruct p : friends) {

            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("single_active_member.fxml"));
            AnchorPane pane = null;
            try {
                pane = fxmlLoader1.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SingleOnlineMember singleOnlineMember = fxmlLoader1.getController();
            singleOnlineMember.getImageUrl().setFill(new ImagePattern(new Image(p.getUserImage())));
            singleOnlineMember.getUserContainer().setOnMouseClicked(e -> {
                messengerController.getCurrentChatName().setText(p.getUsername());
                messengerController.getCurrentImage().setFill(new ImagePattern(new Image(p.getUserImage())));
                messengerController.getSendMsg().setOnAction(null);
                messengerController.getSendMsg().setOnAction(evvv -> {
                    ThrowMessage(messengerController, messengerController.getMessageTextBox().getText(),
                            p.username);
                });
                try {
                    messageSystem(p.getUsername(), messengerController.MessageTextBox, messengerController.SendMsg,
                            messengerController.getMessagebox1(), messengerController.getMessagebox2(),
                            messengerController.getScrollPaneChat());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                messengerController.getMessageTextBox().setVisible(true);
                messengerController.getCurrentChatName().setVisible(true);
                messengerController.getCurrentImage().setVisible(true);
                messengerController.getSendMsg().setVisible(true);
            });

            messengerController.getFriendsList().getChildren().add(pane);
        }

        List<MessageTitleStruct> messages = Methods.allMessageTitleStructs(MainUser.username);
        messages.sort((a, b) -> {
            return b.getLastTime().compareTo(a.getLastTime());
        });
        messengerController.getMessageList().getChildren().clear();
        if (messages.isEmpty()) {
            Label label = new Label("No Recent Messages");
            label.setStyle("-fx-text-fill: white;");
            messengerController.getMessageList().getChildren().add(label);
        }
        for (MessageTitleStruct m : messages) {
            FXMLLoader fxmlLoader1 = new FXMLLoader(getClass().getResource("single_mesage.fxml"));
            AnchorPane pane = null;
            try {
                pane = fxmlLoader1.load();
            } catch (Exception e) {
                e.printStackTrace();
            }
            SingleMessageController singleMessageController = fxmlLoader1.getController();
            singleMessageController.getIdToken().setText(m.getSender());
            singleMessageController.getIdToken().setOnMouseClicked(e -> {
                messengerController.getSendMsg().setOnAction(null);
                messengerController.getCurrentChatName().setText(m.getSender());
                messengerController.getCurrentImage().setFill(new ImagePattern(new Image(m.getUserImage())));
                try {
                    messageSystem(singleMessageController.getIdToken().getText(), messengerController.MessageTextBox,
                            messengerController.SendMsg,
                            messengerController.getMessagebox1(), messengerController.getMessagebox2(),
                            messengerController.getScrollPaneChat());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                messengerController.getSendMsg().setOnAction(evvv -> {
                    ThrowMessage(messengerController, messengerController.getMessageTextBox().getText(),
                            singleMessageController.getIdToken().getText());
                });
                messengerController.getMessageTextBox().setVisible(true);
                messengerController.getSendMsg().setVisible(true);
                messengerController.getCurrentChatName().setVisible(true);
                messengerController.getCurrentImage().setVisible(true);
            });
            singleMessageController.getUserImage().setFill(new ImagePattern(new Image(m.getUserImage())));
            messengerController.getMessageList().getChildren().add(pane);
        }
        vBox.getChildren().add(anchorPane);
    }

    public void messageSystem(String frnd, TextField msg, Button send, VBox msg1Box, VBox msg2Box,
            ScrollPane scrollPaneChat) throws Exception {

        if (currentListener != null) {
            currentListener.remove();
        }
        System.out.println("Message System of " + frnd + " called");
        msg1Box.getChildren().clear();
        msg2Box.getChildren().clear();
        System.out.println("Message System");

        String a = MainUser.username;
        String b = frnd;
        if (a.compareTo(b) > 0) {
            String temp = a;
            a = b;
            b = temp;
        }
        String msg_name = a + ":" + b;
        currentListener = db.collection("MessagePoll").document(msg_name).collection("Messages")
                .orderBy("time", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots, FirestoreException error) {
                        if (error != null) {
                            System.err.println("Listen failed: " + error);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            scrollPaneChat.setVvalue(scrollPaneChat.getVvalue() + 1.0);
                            System.out.println("Scrolling " + scrollPaneChat.getVvalue());
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                System.out.println("New Msg: " + dc.getDocument().getString("message"));
                                Platform.runLater(() -> {
                                    if (dc.getDocument().getString("sender").equals(MainUser.username)) {
                                        MessageStruct messageStruct = new MessageSender(
                                                dc.getDocument().getString("sender"),
                                                dc.getDocument().getString("receiver"),
                                                dc.getDocument().getString("message"),
                                                dc.getDocument().getTimestamp("time"));
                                        messageStruct.Print(messageStruct, msg1Box, msg2Box);
                                    } else {
                                        MessageStruct messageStruct = new MessageReciever(
                                                dc.getDocument().getString("sender"),
                                                dc.getDocument().getString("receiver"),
                                                dc.getDocument().getString("message"),
                                                dc.getDocument().getTimestamp("time"));
                                        messageStruct.Print(messageStruct, msg1Box, msg2Box);

                                    }

                                });
                            }
                        }
                    }
                });

    }

    public void ThrowMessage(MessengerController messengerController, String msg, String reciever) {
        Methods.ThrowOnMessagePoll(MainUser.username, reciever, msg);
        messengerController.getMessageTextBox().setText("");
    }

}
