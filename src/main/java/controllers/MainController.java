package controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import app.App;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.Schedule;
import models.ScheduledTask;
import models.Task;
import utils.Category;
import utils.Effort;
import utils.Priority;
import utils.SceneManager;
public class MainController {

    @FXML
    private Button addTaskButton;

    @FXML
    private Button compareButton;

    @FXML
    private TableColumn<ScheduledTask, String> deadlineColumn;

    @FXML
    private Button deleteTaskButton;

    @FXML
    private Button editTaskButton;

    @FXML
    private ComboBox<String> focusModeComboBox;

    @FXML
    private Spinner<Integer> availabilityEndHrSpinner;

    @FXML
    private Spinner<Integer> availabilityEndMinSpinner;

    @FXML
    private Spinner<Integer> availabilityStartHrSpinner;

    @FXML
    private Spinner<Integer> availabilityStartMinSpinner;

    @FXML
    private TableColumn<ScheduledTask, String> importanceColumn;

    @FXML
    private TableColumn<ScheduledTask, String> nameColumn;

    @FXML
    private TableColumn<ScheduledTask, String> scheduledEndColumn;

    @FXML
    private TableColumn<ScheduledTask, String> scheduledStartColumn;

    @FXML
    private Label taskCountLabel;

    @FXML
    private TableView<ScheduledTask> taskTable;

    @FXML
    private Label nameOfStrategyLabel;

    private Task activeSelectedTask = null;

    @FXML
    void initialize() {
      //use enums later
      ObservableList<String> modes = FXCollections.observableArrayList("ACADEMIC", "FAMILY", "LEISURE", "HEALTH");
      focusModeComboBox.setItems(modes);

        // Availability time spinners -> allows the user to select availability time.
        setSpinner(availabilityStartHrSpinner, availabilityStartMinSpinner, 0, 23, 0, 59);
        setSpinner(availabilityEndHrSpinner, availabilityEndMinSpinner, 0, 23, 0, 59);

      List<Task> allTasks = App.taskManager.getAllTasks();

      if(allTasks.isEmpty()){
        allTasks = createTasks();
      }

      setUpSchedule(allTasks);
      taskTable.getSelectionModel().selectedItemProperty().addListener((observable, oldTask, selectedTask) -> {
        if (selectedTask != null) {
          activeSelectedTask = selectedTask.getTask();
          editTaskButton.setDisable(false);
          deleteTaskButton.setDisable(false);
        }else{
          editTaskButton.setDisable(true);
          deleteTaskButton.setDisable(true);               
        }
      });
        nameOfStrategyLabel.setText(App.scheduler.getStrategy().getStrategyName());
    }

    private void setSpinner(Spinner<Integer> hrSpinner, Spinner<Integer> minSpinner, int hrMin, int hrMax, int minutesMin, int minutesMax){
        SpinnerValueFactory<Integer> hours = new SpinnerValueFactory.IntegerSpinnerValueFactory(hrMin, hrMax);
        SpinnerValueFactory<Integer> minutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(minutesMin, minutesMax);
        hrSpinner.setValueFactory(hours);
        minSpinner.setValueFactory(minutes);
    }

    private void setUpSchedule(List<Task> allTasks){
      

      if (!allTasks.isEmpty()){
        Schedule schedule = App.scheduler.generateSchedule(allTasks, null); //make the schedulingStart dynamic

        List<ScheduledTask> scheduledTasks = schedule.getScheduledTasks();
        setUpScheduleTable(scheduledTasks);
        taskCountLabel.setText(scheduledTasks.size() + " Tasks");
      }
    }

    private void setUpScheduleTable(List<ScheduledTask> schedule){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        nameColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getTitle()) );

        importanceColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getImportance().toString()) );

        deadlineColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getDeadline().format(formatter)) );

        scheduledStartColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)) );

        scheduledEndColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(formatter)) );

        taskTable.setItems( FXCollections.observableArrayList(schedule) );    
    }

    @FXML
    void handleAddTask(ActionEvent event) {
      try {
          TaskFormController controller = SceneManager.switchTo("/fxml/task-form.fxml");
          controller.setTaskToEdit(null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @FXML
    void handleCompareStrategies(ActionEvent event) {
      try {
          ComparisonController controller = SceneManager.switchTo("/fxml/comparison-view.fxml");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @FXML
    void handleDeleteTask(ActionEvent event) {
        if (activeSelectedTask != null){
          App.taskManager.deleteTask(activeSelectedTask.getId());
          setUpSchedule(App.taskManager.getAllTasks());
        }
    }

    @FXML
    void handleGenerateSchedule(ActionEvent event) {
        LocalTime availabilityStartTime = LocalTime.of(availabilityStartHrSpinner.getValue(), availabilityStartMinSpinner.getValue());
        LocalTime availabilityEndTime = LocalTime.of(availabilityEndHrSpinner.getValue(), availabilityEndHrSpinner.getValue());

        App.scheduler.setAvailability(availabilityStartTime, availabilityEndTime);
        setUpSchedule(App.taskManager.getAllTasks());
    }

    @FXML
    void handleEditTask(ActionEvent event) {
      if (activeSelectedTask != null) {
        try {
            TaskFormController controller = SceneManager.switchTo("/fxml/task-form.fxml");
            controller.setTaskToEdit(activeSelectedTask);
        } catch (Exception e) {
        }
      }
    }

  // Temporary method for testing otherwise user adds tasks via addtask
  private List<Task> createTasks(){
        App.taskManager.createTask(
                "Finish Programming Lab",
                "Complete the remaining programming lab exercises.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Review Design Patterns",
                "Study common software design patterns and examples.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 1, 14, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Finance Problem Set",
                "Complete the assigned corporate finance calculations.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 2, 16, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Update Project README",
                "Add setup instructions and usage information to the project README.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 2, 19, 0),
                60,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Study Probability",
                "Revise probability concepts and solve practice problems.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 3, 11, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Prepare Tutorial Questions",
                "Work through questions before the next tutorial.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 3, 15, 0),
                75,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Refactor Java Classes",
                "Improve the structure and readability of the Java project.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 4, 13, 0),
                150,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Review Exam Notes",
                "Review previous lecture notes and create a revision summary.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 4, 18, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Complete Research",
                "Finish collecting information for the research assignment.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 5, 12, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Practice Coding Questions",
                "Solve programming questions under timed conditions.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 6, 10, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Call Sibling",
                "Catch up with a sibling over the phone.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 1, 20, 0),
                30,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Clean Living Area",
                "Clean and organise the shared living area.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 2, 10, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Prepare Breakfast",
                "Prepare breakfast for the household.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 2, 7, 30),
                45,
                Priority.MEDIUM,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Buy Cleaning Supplies",
                "Purchase cleaning products and household supplies.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 3, 17, 0),
                60,
                Priority.MEDIUM,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Cook Family Dinner",
                "Prepare and serve dinner for the family.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 3, 18, 30),
                120,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Organise Wardrobe",
                "Sort clothes and organise the wardrobe.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 4, 11, 0),
                90,
                Priority.LOW,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Clean Bathroom",
                "Thoroughly clean and organise the bathroom.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 5, 10, 0),
                75,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Family Breakfast",
                "Have breakfast together with the family.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 6, 9, 0),
                90,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Help With Errands",
                "Help a family member complete weekend errands.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 6, 13, 0),
                150,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Prepare Lunch",
                "Prepare lunch for the household.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 7, 12, 0),
                60,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Upper Body Workout",
                "Complete an upper body strength workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 1, 16, 0),
                75,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Cycling Session",
                "Go cycling for cardiovascular exercise.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 2, 17, 0),
                90,
                Priority.MEDIUM,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Mobility Routine",
                "Complete a full-body mobility routine.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 3, 7, 0),
                30,
                Priority.MEDIUM,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Lower Body Workout",
                "Complete a lower body strength training session.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 4, 16, 0),
                75,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Swimming",
                "Complete a recreational swimming workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 5, 9, 0),
                90,
                Priority.MEDIUM,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Recovery Session",
                "Complete stretching and light recovery exercises.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 5, 19, 0),
                45,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Interval Running",
                "Complete a high-intensity interval running session.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 6, 8, 0),
                60,
                Priority.HIGH,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Prepare Healthy Snacks",
                "Prepare healthy snacks for the upcoming week.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 6, 16, 0),
                60,
                Priority.LOW,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Core Workout",
                "Complete a focused abdominal and core workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 7, 17, 0),
                45,
                Priority.MEDIUM,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Long Walk",
                "Go for a long walk to stay active and clear the mind.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 7, 18, 30),
                75,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Visit Coffee Shop",
                "Go to a coffee shop and relax for a while.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 1, 18, 0),
                90,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Play Board Game",
                "Spend the evening playing a board game.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 2, 20, 0),
                120,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Listen to Podcast",
                "Listen to a podcast while relaxing.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 3, 21, 0),
                60,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Go for a Hike",
                "Spend the afternoon hiking and exploring nature.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 4, 9, 0),
                180,
                Priority.MEDIUM,
                Effort.HIGH
        );

        App.taskManager.createTask(
                "Visit Bookstore",
                "Browse books and spend some time reading.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 5, 14, 0),
                90,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Play Pool",
                "Play pool with friends.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 5, 19, 0),
                120,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Try New Restaurant",
                "Have dinner at a restaurant that you have not visited before.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 6, 19, 0),
                120,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Watch Documentary",
                "Watch an interesting documentary.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 7, 20, 0),
                120,
                Priority.LOW,
                Effort.LOW
        );

        App.taskManager.createTask(
                "Go Shopping",
                "Spend some free time browsing shops.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 8, 14, 0),
                150,
                Priority.LOW,
                Effort.MEDIUM
        );

        App.taskManager.createTask(
                "Photography Walk",
                "Take a walk around the city and practise photography.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 8, 16, 30),
                120,
                Priority.LOW,
                Effort.MEDIUM
        );

        return App.taskManager.getAllTasks();
  }
}
