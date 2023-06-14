package com.example.messenger;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


public class Controller {
    private MultipleSelectionModel<String> langsSelectionModel;
    private Consumer<String> MessageListener;
    public void setMessageListener(Consumer<String> listener) {
        this.MessageListener = listener;
    }
    @FXML
    private Button SendButton;
    @FXML
    private TextField MessageInput;
    @FXML
    public void onSendButtonClick(){
        String text = MessageInput.getText();
        System.out.println(text);
        if (text == null || text.trim().isEmpty()) {
            MessageInput.setText("");
            return;
        }
        MessageInput.setText("");
        if (MessageListener != null) {
            MessageListener.accept(text);
        }

    }
    @FXML
    private Button copyIdButton;
    @FXML
    public void copyTextToClipboard() {
        String text = userIdText.getText();
        String userId = text.substring("User ID: ".length());

        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(userId);
        clipboard.setContent(content);
    }
    @FXML
    private ListView<String> currentChats;

    private static ObservableList<String> langs;
    public void addChat(String name){
        langs.add(name);
    }
    public void setSelectionListener(ChangeListener<String> listener) {
        langsSelectionModel.selectedItemProperty().addListener(listener);
    }
    public void initialize() {
        langs = FXCollections.observableArrayList();
        currentChats.setItems(langs);
        langsSelectionModel = currentChats.getSelectionModel();

    }

    @FXML
    private TextField IDinput;
    @FXML
    private Button addChat;
    private Consumer<String> IDListener;
    public void setIDListener(Consumer<String> listener) {
        this.IDListener = listener;
    }
    @FXML
    public void onAddChatButtonClick() {
        String text = IDinput.getText();
        System.out.println(text);
        IDinput.setText("");
        if (IDListener != null) {
            IDListener.accept(text);
        }
    }


    @FXML
    private Text userName;
    @FXML
    public void updateUserName(String text) {
        userName.setText("Username: "+ text);
    }
    @FXML
    private Text userIdText;
    @FXML
    public void updateUserIdText(String text) {
        userIdText.setText("User ID: " + text);
    }
    @FXML
    private ScrollPane MessageScroller;
    @FXML
    private VBox MessageVBox;
    public void displayClient(String ClientID, Map<String, List<cMessage>> messages, String outName, String inName) {
        List<cMessage> clientMessages = messages.get(ClientID);
        if (clientMessages == null) {
            return; // Client ID not found
        }
        if (clientMessages.isEmpty()) {
            MessageVBox.getChildren().clear();
            return;
        }
        MessageVBox.getChildren().clear();

        for (cMessage message : clientMessages) {
            String name = (message.source.equals("in"))? inName : outName;
            String formattedMessage = name + ": " + message.text + "\n";
            Text messageText = new Text(formattedMessage);
            messageText.setWrappingWidth(MessageVBox.getWidth() - 10);

            Rectangle messageBackground = new Rectangle(MessageVBox.getWidth(), messageText.getLayoutBounds().getHeight());
            if (message.source.equals("in")) {
                messageBackground.setFill(Color.web("#aeb1d1"));
                messageText.setTextAlignment(TextAlignment.LEFT);
            } else {
                messageBackground.setFill(Color.web("#dedede"));
                messageText.setTextAlignment(TextAlignment.RIGHT);
            }

            // Create a StackPane to hold the messageText and messageBackground
            StackPane messagePane = new StackPane();
            messagePane.getChildren().addAll(messageBackground, messageText);
            messagePane.setAlignment(messageText, Pos.CENTER);


            MessageVBox.getChildren().add(messagePane);

        }
        MessageScroller.layout();
        MessageScroller.setVvalue(1.0);

    }

}