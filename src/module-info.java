module miniprojet {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.base;
    
    exports controller;
    exports application;
    exports model;  // Add this if you use model classes in FXML
    
    opens controller to javafx.fxml;
    opens application to javafx.graphics;
    opens model to javafx.fxml;  // Add this if you use model classes in FXML
    opens view to javafx.fxml;   // Add this for FXML files
}
