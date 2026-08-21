package controllers;

import java.io.IOException;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import utils.SceneManager;


public class MainController {

    @FXML
    private Button addTaskButton;

    @FXML
    private TableColumn<?, ?> categoryColumn;

    @FXML
    private Button compareButton;

    @FXML
    private TableColumn<?, ?> deadlineColumn;

    @FXML
    private Button deleteTaskButton;

    @FXML
    private TableColumn<?, ?> durationColumn;

    @FXML
    private Button editTaskButton;

    @FXML
    private TableColumn<?, ?> effortColumn;

    @FXML
    private ComboBox<String> focusModeComboBox;

    @FXML
    private Button generateScheduleBtn;

    @FXML
    private TableColumn<?, ?> importanceColumn;

    @FXML
    private TableColumn<?, ?> nameColumn;

    @FXML
    private Label statusLabel;

    @FXML
    private Label taskCountLabel;

    @FXML
    private TableColumn<?, ?> descriptionColumn;

    @FXML
    private TableView<?> taskTable;

    @FXML
    void initialize() {
      //use enums later
        ObservableList<String> modes = FXCollections.observableArrayList(
                "ACADEMIC", "FAMILY", "LEISURE", "HEALTH"
            );
        focusModeComboBox.setItems(modes);
    }

    @FXML
    void handleAddTask(ActionEvent event) {
      try {
          SceneManager.switchTo("/fxml/task-form.fxml");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @FXML
    void handleCompareStrategies(ActionEvent event) {

    }

    @FXML
    void handleDeleteTask(ActionEvent event) {

    }

    @FXML
    void handleEditTask(ActionEvent event) {

    }

    @FXML
    void handleStrategyGeneration(ActionEvent event) {

    }

}
