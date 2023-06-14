package com.example.messenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.FlowPane;
import javafx.scene.control.ListView;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

public class MessengerApp extends Application {
    private static Controller controller;
    @Override
    public void start(Stage stage) throws IOException {

        Stage initialStage = new Stage();
        VBox initialLayout = new VBox(10);

        TextField usernameField = new TextField();
        usernameField.setPrefWidth(200); // Set the preferred width of the text field
        Button continueButton = new Button("Continue");
        Label usernameLabel = new Label("Enter your username (letters, digits and underscores only)");

        VBox.setMargin(usernameLabel, new Insets(50, 50, 0, 50)); // Set margin for the label
        VBox.setMargin(usernameField, new Insets(0, 50, 10, 50)); // Set margin for the username field
        VBox.setMargin(continueButton, new Insets(10, 50, 50, 50)); // Set margin for the continue button

        initialLayout.getChildren().addAll(usernameLabel, usernameField, continueButton);
        initialLayout.setAlignment(Pos.CENTER);
        initialStage.setScene(new Scene(initialLayout));
        initialStage.setTitle("Welcome!");
        initialStage.show();

        // Handle continue button click event
        continueButton.setOnAction(event -> {
            String username = usernameField.getText();

            if(isValid(username,30)){
                // Close the initial window
                initialStage.close();

                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(MessengerApp.class.getResource("messenger.fxml"));
                    Scene scene = new Scene(fxmlLoader.load());
                    controller = fxmlLoader.getController();
                    controller.updateUserName(username);
                    controller.initialize();

                    stage.setTitle("Messenger. Client( " + username + " )");
                    stage.setScene(scene);
                    stage.show();

                    Stage pStage = (Stage) scene.getWindow();

                    Thread clientThread = new Thread(() -> {
                        Client client = new Client(controller, username);
                        try {
                            pStage.setOnCloseRequest(ev -> {
                                // Stop the client thread and perform cleanup operations
                                client.shutDown(); // Interrupt the client thread
                            });
                            client.run();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    clientThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                usernameField.setText("");
                System.out.println("Name not valid");
            }

            });
    }

    private boolean isValid(String username, int maxlength) {
        String pattern = "^[a-zA-Z0-9_]+$";
        return username.matches(pattern) && username.length() < maxlength;
    }

    public static void main(String[] args) {
        launch(args);
    }
}