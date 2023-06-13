module com.example.messenger {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.xml;


    opens com.example.messenger to javafx.fxml;
    exports com.example.messenger;
}