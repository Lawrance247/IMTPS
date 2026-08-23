package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class ComparisonController {

    @FXML
    private Button closeButton;

    @FXML
    private TableColumn<?, ?> deadlineColumnA;

    @FXML
    private TableColumn<?, ?> deadlineColumnB;

    @FXML
    private TableColumn<?, ?> endColumnA;

    @FXML
    private TableColumn<?, ?> endColumnB;

    @FXML
    private Label focusModeLabel;

    @FXML
    private TableColumn<?, ?> importanceColumnA;

    @FXML
    private TableColumn<?, ?> importanceColumnB;

    @FXML
    private TableColumn<?, ?> metricAColumn;

    @FXML
    private TableColumn<?, ?> metricBColumn;

    @FXML
    private TableColumn<?, ?> metricDefinitionColumn;

    @FXML
    private TableColumn<?, ?> metricDirectionColumn;

    @FXML
    private TableColumn<?, ?> metricNameColumn;

    @FXML
    private TableView<?> metricsTable;

    @FXML
    private TableColumn<?, ?> nameColumnA;

    @FXML
    private TableColumn<?, ?> nameColumnB;

    @FXML
    private TableView<?> scheduleATable;

    @FXML
    private TableView<?> scheduleBTable;

    @FXML
    private TableColumn<?, ?> startColumnA;

    @FXML
    private TableColumn<?, ?> startColumnB;

    @FXML
    private Label strategyALabel;

    @FXML
    private Label strategyBLabel;

    @FXML
    void handleClose(ActionEvent event) {

    }

}
