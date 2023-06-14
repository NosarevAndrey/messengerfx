package com.example.messenger;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.InetAddress;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;

public class MessengerApp extends Application {
    private static Controller controller;
    @Override
    public void start(Stage stage) throws IOException {

        Stage initialStage = new Stage();
        VBox initialLayout = new VBox(10);

        TextField usernameField = new TextField();
        usernameField.setPrefWidth(200); // Set the preferred width of the text field
        TextField ipAddressField = new TextField();
        ipAddressField.setPrefWidth(200);
        ipAddressField.setText("localhost");
        ipAddressField.setPromptText("192.168.0.106 or localhost");

        Button continueButton = new Button("Continue");
        continueButton.setStyle("-fx-font-size: 13px; -fx-padding: 10px 20px;");
        Label usernameLabel = new Label("Enter your username (letters, digits and underscores only)");
        Label ipAddressLabel = new Label("Enter the server IP address");

        VBox.setMargin(usernameLabel, new Insets(50, 50, 0, 50)); // Set margin for the label
        VBox.setMargin(usernameField, new Insets(0, 50, 10, 50)); // Set margin for the username field
        VBox.setMargin(ipAddressLabel, new Insets(10, 50, 0, 50)); // Set margin for the label
        VBox.setMargin(ipAddressField, new Insets(0, 50, 10, 50)); // Set margin for the IP address field
        VBox.setMargin(continueButton, new Insets(10, 50, 50, 50)); // Set margin for the continue button

        initialLayout.getChildren().addAll(usernameLabel, usernameField, ipAddressLabel, ipAddressField, continueButton);
        initialLayout.setAlignment(Pos.CENTER);
        initialStage.setScene(new Scene(initialLayout));
        initialStage.setTitle("Welcome!");
        initialStage.show();

        // Handle continue button click event
        continueButton.setOnAction(event -> {
            String username = usernameField.getText();
            String ipAddress = ipAddressField.getText();

            if (!UserNameisValid(username,30)) {
                usernameField.setText("");
                System.out.println("not valid");
            }
            else if (!InetAddressisValid(ipAddress)) {
                ipAddressField.setText("");
                System.out.println("not valid");
            }
            else {
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
                        Client client = new Client(controller, username, ipAddress);
                        try {
                            pStage.setOnCloseRequest(ev -> {
                                // Stop the client thread and perform cleanup operations
                                client.shutDown(); // Interrupt the client thread
                            });
                            client.run();
                            System.out.println("Ended");

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    clientThread.start();
                    System.out.println("Tread started after this");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            });
    }

    private boolean InetAddressisValid(String addr) {
        return addr.equals("192.168.0.106") || addr.equals("localhost");
    }

    private boolean UserNameisValid(String username, int maxlength) {
        String pattern = "^[\\p{L}\\p{N}_]+$";
        return username.matches(pattern) && username.length() < maxlength;
    }

    public static void main(String[] args) {
        launch(args);
    }
}