package com.codebook;

import java.util.Base64;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DetailedPageController {
    @FXML
    private TextArea Title;

    @FXML
    private TextArea ai;

    @FXML
    private TextArea code;

    @FXML
    private TextArea Input;

    @FXML
    private TextField memory;

    @FXML
    private TextArea output;

    @FXML
    private TextField time;
    @FXML
    private Button back;

    public String decodeBase64(String encodedString) {
        byte[] decodedBytes = Base64.getUrlDecoder().decode(encodedString);
        return new String(decodedBytes);
    }

    public void setCode(String code) {
        this.code.setText(decodeBase64(code));
    }

    public void setTitle(String title) {
        this.Title.setText(title);
    }

    public void setInput(String input) {
        this.Input.setText(decodeBase64(input));
    }

    public void setOutput(String output) {
        this.output.setText(output);
    }

    public void setTime(String time) {
        this.time.setText(time);
    }

    public void setMemory(String memory) {
        this.memory.setText(memory);
    }

    public void setAi(String ai) {
        this.ai.setText(ai);
    }

    public Button getBack() {
        return back;
    }

    public void setData(String title, String code, String input, String output, String time, String memory,
            String aiResponse) {
        setTitle(title);
        setCode(code);
        setInput(input);
        setOutput(output);
        setTime(time);
        setMemory(memory);
        setAi(aiResponse);
    }

}
