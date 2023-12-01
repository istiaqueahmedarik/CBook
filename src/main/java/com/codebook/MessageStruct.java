package com.codebook;

import com.google.cloud.Timestamp;

import javafx.scene.layout.VBox;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageStruct {
    public String sender, receiver, message;
    Timestamp time;

    public void Print(MessageStruct messageStruct, VBox msg1Box, VBox msg2Box) {
    }
}
