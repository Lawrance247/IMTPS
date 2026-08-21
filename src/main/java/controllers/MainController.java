package controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Schedule;
import models.ScheduledTask;
import models.Task;
import services.Scheduler;
import strategies.WeightedFocusStrategy;
import utils.Category;
import utils.Effort;
import utils.FocusMode;
import utils.Priority;
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
      ObservableList<String> modes = FXCollections.observableArrayList("ACADEMIC", "FAMILY", "LEISURE", "HEALTH");
      focusModeComboBox.setItems(modes);


      // ===== TEMPORAL SECTION FOR TESTING =====
      //create a schedule
      WeightedFocusStrategy strategy = new WeightedFocusStrategy(FocusMode.FAMILY);
      Scheduler scheduler = new Scheduler(strategy, LocalTime.of(8, 0), LocalTime.of(22, 0));

      Schedule schedule = scheduler.generateSchedule(createTasks(), LocalDateTime.now());

      //display schedule on CL
      System.out.println();
      List<ScheduledTask> scheduledTasks = schedule.getScheduledTasks();
      for(ScheduledTask t : scheduledTasks){
       System.out.println(t.toString());
      }
      System.out.printf("\nDeadline Clashes %d\nScheduled Minutes %d\nNumber of Tasks %d", schedule.getDeadlineClashCount(), schedule.getTotalScheduledMinutes(), schedule.getTaskCount());
      // ==== END OF TEMPORAL SECTION ====
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

  // Temporary class for testing
  private List<Task> createTasks(){
      List<Task> tasks = new ArrayList<>();
      tasks.add(new Task(
              "Study Java",
              "Study for the test",
              Category.ACADEMIC,
              LocalDateTime.of(2026, 8, 23, 11, 0),
              180,
              Priority.MEDIUM,
              Effort.MEDIUM
      ));

      // Family task
      tasks.add(new Task(
              "Family meeting",
              "Attend family meeting",
              Category.FAMILY,
              LocalDateTime.of(2026, 8, 23, 10, 0),
              60,
              Priority.HIGH,
              Effort.LOW
      ));

      // Health task
      tasks.add(new Task(
              "Exercise",
              "Go for a run",
              Category.HEALTH,
              LocalDateTime.of(2026, 8, 22, 11, 0),
              45,
              Priority.MEDIUM,
              Effort.MEDIUM
      ));

      // Leisure task
      tasks.add(new Task(
              "Gaming",
              "Play games",
              Category.LEISURE,
              LocalDateTime.of(2026, 8, 23, 16, 0),
              120,
              Priority.LOW,
              Effort.LOW
      ));

      tasks.add(new Task(
              "Write Code",
              "Finish Capstone assignment.",
              Category.ACADEMIC,
              LocalDateTime.of(2026, 8, 23, 16, 0),
              120,
              Priority.MEDIUM,
              Effort.MEDIUM
      ));
      return tasks;
  }
}
