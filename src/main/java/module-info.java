module app {
    requires javafx.controls;
    requires javafx.fxml;
    opens controllers to javafx.fxml;
    exports app;
}
