package com.example.messenger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;


import java.util.Map;
import java.util.function.Consumer;


public class Controller {
    @FXML
    private TextField inputID;
    @FXML
    private Button addChat;
    private Consumer<String> IDListener;
    public void setIDListener(Consumer<String> listener) {
        this.IDListener = listener;
    }
    @FXML
    public void onAddChartButtonClick() {
        String text = inputID.getText();
        System.out.println(text);
        if (IDListener != null) {
            IDListener.accept(text);
        }
    }


    @FXML
    private Text userName;
    @FXML
    public void updateUserName(String text) {
        userName.setText(text);
    }
    @FXML
    private Text userIdText;
    @FXML
    public void updateUserIdText(String text) {
        userIdText.setText(text);
    }
//    @FXML
//    protected void onHelloButtonClick() {
//        welcomeText.setText("Welcome to JavaFX Application!");
//    }
}