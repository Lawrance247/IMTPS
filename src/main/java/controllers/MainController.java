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
import utils.SceneManager;
import utils.Weights;
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
    private ComboBox<Category> focusModeComboBox;

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
      ObservableList<Category> modes = FXCollections.observableArrayList(Category.ACADEMIC, Category.FAMILY, Category.LEISURE, Category.HEALTH);
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
          Category mode = focusModeComboBox.getValue();
          if(mode != null){
            controller.setFocusMode(mode);
          }
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
                "Complete Database Queries",
                "Finish the remaining SQL queries for the database assignment.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 1, 11, 0),
                90,
                Weights.HIGH,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Review Lecture Notes",
                "Review notes from the latest computer science lecture.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 1, 15, 0),
                60,
                Weights.MEDIUM,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Study Algorithms",
                "Revise sorting, searching, and graph algorithms.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 2, 10, 0),
                120,
                Weights.HIGH,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Complete Finance Exercises",
                "Work through the assigned finance calculation exercises.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 2, 14, 0),
                90,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Debug Java Project",
                "Find and fix errors in the current Java project.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 3, 16, 0),
                150,
                Weights.HIGH,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Prepare Tutorial",
                "Complete questions before the upcoming tutorial.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 3, 12, 0),
                75,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Read Research Paper",
                "Read and summarise the assigned research paper.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 4, 13, 0),
                90,
                Weights.LOW,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Update Project Documentation",
                "Update documentation for the latest project changes.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 4, 18, 0),
                60,
                Weights.MEDIUM,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Practice Coding Questions",
                "Solve programming questions under timed conditions.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 5, 10, 0),
                120,
                Weights.HIGH,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Organise Study Material",
                "Organise lecture notes, assignments, and study resources.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 6, 11, 0),
                60,
                Weights.LOW,
                Weights.LOW
        );


        App.taskManager.createTask(
                "Clean Bedroom",
                "Clean and organise the bedroom.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 1, 18, 0),
                60,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Buy Groceries",
                "Purchase groceries and household food supplies.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 2, 17, 0),
                75,
                Weights.HIGH,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Call Family",
                "Call family members and catch up with them.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 2, 20, 0),
                30,
                Weights.MEDIUM,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Prepare Dinner",
                "Prepare dinner for the household.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 3, 18, 0),
                90,
                Weights.HIGH,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Wash Clothes",
                "Do the weekly laundry and put clothes away.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 4, 9, 0),
                120,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Clean Kitchen",
                "Clean the kitchen and organise the cupboards.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 4, 16, 0),
                75,
                Weights.MEDIUM,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Help With Shopping",
                "Help a family member complete the weekly shopping.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 5, 13, 0),
                90,
                Weights.LOW,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Visit Relative",
                "Visit a relative and spend some time together.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 6, 14, 0),
                180,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Organise Household Documents",
                "Sort and organise important household documents.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 7, 11, 0),
                90,
                Weights.LOW,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Prepare Family Lunch",
                "Prepare lunch for the family.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 8, 12, 0),
                90,
                Weights.MEDIUM,
                Weights.HIGH
        );


        App.taskManager.createTask(
                "Upper Body Workout",
                "Complete an upper body strength training session.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 1, 16, 30),
                75,
                Weights.HIGH,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Morning Run",
                "Go for a short morning run.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 2, 7, 30),
                45,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Stretching Session",
                "Complete a full-body stretching and mobility routine.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 2, 19, 0),
                30,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Football Training",
                "Attend football training and complete drills.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 3, 15, 0),
                120,
                Weights.HIGH,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Meal Preparation",
                "Prepare healthy meals for the next few days.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 4, 17, 0),
                120,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Lower Body Workout",
                "Complete a lower body strength workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 5, 10, 0),
                75,
                Weights.HIGH,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Cycling Session",
                "Go cycling for cardiovascular exercise.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 6, 9, 0),
                90,
                Weights.MEDIUM,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Recovery Session",
                "Complete light stretching and recovery exercises.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 6, 19, 0),
                45,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Core Workout",
                "Complete a focused abdominal and core workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 7, 17, 0),
                45,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Long Walk",
                "Take a long walk to stay active and relax.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 8, 18, 0),
                75,
                Weights.LOW,
                Weights.LOW
        );


        App.taskManager.createTask(
                "Watch Movie",
                "Relax and watch a movie.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 1, 20, 0),
                150,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Play Video Games",
                "Spend some time playing video games.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 2, 21, 0),
                120,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Meet Friends",
                "Meet friends and spend some time socialising.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 3, 19, 0),
                180,
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Read a Book",
                "Read several chapters of a book.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 4, 20, 0),
                90,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Watch Football",
                "Watch a football match in the evening.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 5, 20, 0),
                120,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Visit Coffee Shop",
                "Spend some time relaxing at a coffee shop.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 6, 14, 0),
                90,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Go Hiking",
                "Spend the afternoon hiking and exploring outdoors.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 7, 10, 0),
                180,
                Weights.MEDIUM,
                Weights.HIGH
        );

        App.taskManager.createTask(
                "Play Pool",
                "Play pool with friends.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 7, 19, 0),
                120,
                Weights.LOW,
                Weights.LOW
        );

        App.taskManager.createTask(
                "Photography Walk",
                "Walk around the city and practise photography.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 8, 15, 0),
                120,
                Weights.LOW,
                Weights.MEDIUM
        );

        App.taskManager.createTask(
                "Try New Restaurant",
                "Have dinner at a restaurant you have never visited.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 8, 19, 0),
                120,
                Weights.LOW,
                Weights.LOW
        );

        return App.taskManager.getAllTasks();
  }
}
