module app {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    opens controllers to javafx.fxml;
    exports app;
}
