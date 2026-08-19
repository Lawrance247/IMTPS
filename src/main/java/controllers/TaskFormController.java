package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import app.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import  models.Task;
import utils.Category;
import utils.Effort;
import utils.Priority;
import utils.SceneManager;

public class TaskFormController {

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<String> categoryComboBox;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private Spinner<Integer> hourSpinner;


    @FXML
    private Spinner<Integer> minuteSpinner;

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
    private TextField taskDescription;

    @FXML
    void initialize() {
        //use enums later for categories
        ObservableList<String> categories = FXCollections.observableArrayList(
            "ACADEMIC",
            "FAMILY",
            "LEISURE",
            "HEALTH"
        );

        //set spinners for user to select times (24hr clock)
        SpinnerValueFactory<Integer> hours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
        SpinnerValueFactory<Integer> minutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);

        categoryComboBox.setItems(categories);
        hourSpinner.setValueFactory(hours);
        minuteSpinner.setValueFactory(minutes);
        
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
          List<Task> tasks = App.taskManager.getAllTasks();

        for(Task t : tasks){
            System.out.println(t.toString());
        }

        SceneManager.switchTo("/fxml/main-view.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        try{
            String taskName = nameField.getText().toUpperCase();
            String description = taskDescription.getText();
            Category category = Category.valueOf(categoryComboBox.getValue().toUpperCase());

            LocalDate date = deadlinePicker.getValue();
            LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
            LocalDateTime deadline = LocalDateTime.of(date, time);

            Integer duration = Integer.parseInt(durationField.getText());

            Priority priority = (importanceValueLabel.getText().equals("1")) ? Priority.LOW :
            (importanceValueLabel.getText().equals("2")) ? Priority.MEDIUM : Priority.HIGH;
            Effort effort = (effortValueLabel.getText().equals("1")) ? Effort.LOW :
            (effortValueLabel.getText().equals("2")) ? Effort.MEDIUM : Effort.HIGH;

            Task task = App.taskManager.createTask(
                taskName,
                description,
                category,
                deadline,
                duration,
                priority,
                effort
            );

            System.out.println("Task added");
            //SceneManager.switchTo("/fxml/task-form.fxml");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
