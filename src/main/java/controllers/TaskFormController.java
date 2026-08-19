package controllers;

import java.io.IOException;
import java.time.LocalDate;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import utils.SceneManager;


public class TaskFormController {

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private TextField durationField;

    @FXML
    private Slider effortSlider;

    @FXML
    private Label effortValueLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Slider importanceSlider;

    @FXML
    private Label importanceValueLabel;

    @FXML
    private TextField nameField;

    @FXML
    private Button saveButton;

    @FXML
    void initialize() {
        //use enums later for categories
        ObservableList<String> categories = FXCollections.observableArrayList(
                "ACADEMIC", "FAMILY", "LEISURE", "HEALTH"
            );
        categoryComboBox.setItems(categories);
        
        //keeps slider label showing slider's current value
        importanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            importanceValueLabel.setText(String.valueOf(newValue.intValue()));
        });
        effortSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            effortValueLabel.setText(String.valueOf(newValue.intValue()));
        });
    }

    @FXML
    void handleCancel(ActionEvent event) {
        System.out.println("going out");
        try {
          SceneManager.switchTo("/fxml/main-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        String taskName = nameField.getText();
        String category = categoryComboBox.getValue();
        LocalDate deadline = deadlinePicker.getValue();
        String duration = durationField.getText();
        
    }

}
