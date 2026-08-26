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

/**
 * Controller for the main task list view ({@code /fxml/main-view.fxml}).
 *
 * <p>This is the app's home screen. It shows every task currently held by {@code App.taskManager} as a generated schedule (one row per
 * {@link ScheduledTask}), lets the user add, edit, or delete tasks, set the daily availability window used by {@code App.scheduler}, and navigate to
 * the strategy comparison view.</p>
 *
 * <p><b>Collaborators:</b> reads/writes tasks through the static {@code App.taskManager} ({@code services.TaskManager}); generates the
 * displayed schedule through the static {@code App.scheduler} ({@code services.Scheduler}); navigates to {@link TaskFormController}
 * (add/edit) and {@link ComparisonController} (compare strategies) via {@code utils.SceneManager}. Table rows are backed by {@link ScheduledTask}
 * (and, transitively, {@link Task}).</p>
 */
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


    /** The task currently selected in {@code taskTable}, or {@code null} if none is selected. */
    private Task activeSelectedTask = null;


    /**
     * FXML lifecycle callback, invoked automatically after the view's fields are injected. Populates the focus-mode dropdown and availability
     * spinners, loads (or, on first run, seeds) the task list, renders the initial schedule, wires up table-selection-driven button enabling, and
     * shows the scheduler's current strategy name.
     */
    @FXML
    void initialize() {
      //Populate focusModeComboBox
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


    /**
     * Configures a paired hour/minute {@link Spinner} combo with the given min/max bounds.
     *
     * @param hrSpinner    the hour spinner to configure
     * @param minSpinner   the minute spinner to configure
     * @param hrMin        minimum selectable hour
     * @param hrMax        maximum selectable hour
     * @param minutesMin   minimum selectable minute
     * @param minutesMax   maximum selectable minute
     */
    private void setSpinner(Spinner<Integer> hrSpinner, Spinner<Integer> minSpinner, int hrMin, int hrMax, int minutesMin, int minutesMax){
        SpinnerValueFactory<Integer> hours = new SpinnerValueFactory.IntegerSpinnerValueFactory(hrMin, hrMax);
        SpinnerValueFactory<Integer> minutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(minutesMin, minutesMax);
        hrSpinner.setValueFactory(hours);
        minSpinner.setValueFactory(minutes);
    }


    /**
     * Regenerates the schedule for the given tasks via {@code App.scheduler} and refreshes the table and task-count label. Called on init, and
     * again after any add/edit/delete/availability change.
     *
     * @param allTasks the current full task list to schedule and display
     */
    private void setUpSchedule(List<Task> allTasks){
      if (!allTasks.isEmpty()){
        /** schedulingStart is always passed with null because default value at is always used for now */
        Schedule schedule = App.scheduler.generateSchedule(allTasks, null);

        List<ScheduledTask> scheduledTasks = schedule.getScheduledTasks();
        setUpScheduleTable(scheduledTasks);
        taskCountLabel.setText(scheduledTasks.size() + " Tasks");
      }
    }


    /**
     * Binds {@code taskTable}'s columns to the given scheduled tasks, formatting dates with {@code "dd MMM yyyy, HH:mm"}.
     *
     * @param schedule the scheduled tasks to display, one per row
     */
    private void setUpScheduleTable(List<ScheduledTask> schedule){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");
        nameColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getTitle()) );

        importanceColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getImportance().toString()) );

        deadlineColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getDeadline().format(formatter)) );

        scheduledStartColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)) );

        scheduledEndColumn.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(formatter)) );

        taskTable.setItems( FXCollections.observableArrayList(schedule) );    
    }


    /**
     * "+ Add Task" button handler. Switches to the task form with no task pre-loaded, so the form behaves in "create" mode.
     */
    @FXML
    void handleAddTask(ActionEvent event) {
      try {
          TaskFormController controller = SceneManager.switchTo("/fxml/task-form.fxml");
          controller.setTaskToEdit(null);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    /**
     * "Compare Strategies" button handler. Switches to the comparison view and, if a focus mode is selected, passes it through via
     * {@link ComparisonController#setFocusMode(Category)}.
     */
    @FXML
    void handleCompareStrategies(ActionEvent event) {
      try {
          Category mode = focusModeComboBox.getValue();
          if(mode != null){
            App.wfStrategy.setFocusMode(mode);
          }
          ComparisonController controller = SceneManager.switchTo("/fxml/comparison-view.fxml");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    /**
     * "Delete" button handler. Removes {@link #activeSelectedTask} from {@code App.taskManager} and refreshes the schedule. No-op if nothing
     * is selected (button is disabled in that case, but this guards defensively too).
     */
    @FXML
    void handleDeleteTask(ActionEvent event) {
        if (activeSelectedTask != null){
          App.taskManager.deleteTask(activeSelectedTask.getId());
          setUpSchedule(App.taskManager.getAllTasks());
        }
    }


    /**
     * "Generate Schedule" button handler. Reads the availability spinners, applies them to {@code App.scheduler}, and regenerates the schedule.
     */
    @FXML
    void handleGenerateSchedule(ActionEvent event) {
        LocalTime availabilityStartTime = LocalTime.of(availabilityStartHrSpinner.getValue(), availabilityStartMinSpinner.getValue());
        LocalTime availabilityEndTime = LocalTime.of(availabilityEndHrSpinner.getValue(), availabilityEndMinSpinner.getValue());

        App.scheduler.setAvailability(availabilityStartTime, availabilityEndTime);
        setUpSchedule(App.taskManager.getAllTasks());
    }


    /**
     * "Edit" button handler. Switches to the task form pre-loaded with
     * {@link #activeSelectedTask}, so the form behaves in "edit" mode.
     */
    @FXML
    void handleEditTask(ActionEvent event) {
      if (activeSelectedTask != null) {
        try {
            TaskFormController controller = SceneManager.switchTo("/fxml/task-form.fxml");
            controller.setTaskToEdit(activeSelectedTask);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }



  /**
   * Temporary method for testing otherwise user adds tasks via addtask.
   *
   * <p>Populates {@code App.taskManager} with a fixed set of demo tasks
   * spanning all four {@link Category} values, used only when the task
   * manager is empty on startup (see {@link #initialize()}). This is seed
   * data for development/demo purposes, not part of the app's normal
   * runtime behaviour — see refactor notes for a suggestion on where this
   * responsibility should live instead of the controller.</p>
   *
   * @return the full list of tasks after seeding (via {@code App.taskManager.getAllTasks()})
   */
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
