package com.codebook;

import com.google.cloud.Timestamp;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class MessageReciever extends MessageStruct {
    public MessageReciever(String sender,
            String reciever,
            String message,
            Timestamp time) {
        super(sender, reciever, message, time);
    }

    public void Print(MessageStruct messageStruct, VBox msg1Box, VBox msg2Box) {
        FXMLLoader outLoader = new FXMLLoader(
                getClass().getResource("message_incoming.fxml"));
        AnchorPane outPane = null;
        try {
            outPane = outLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Incoming outgoing = outLoader.getController();
        outgoing.getMsg().setText(messageStruct.getMessage());
        msg2Box.getChildren().add(outPane);

        FXMLLoader InLoader = new FXMLLoader(
                getClass().getResource("message_incoming.fxml"));
        AnchorPane InPane = null;
        try {
            InPane = InLoader.load();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Incoming incoming = InLoader.getController();
        incoming.getMsg().setText(messageStruct.getMessage());
        incoming.getMsg().setStyle("-fx-text-fill: transparent;");
        incoming.getMainContainer().setStyle("-fx-background-color: transparent;");
        msg1Box.getChildren().add(InPane);
    }

}
