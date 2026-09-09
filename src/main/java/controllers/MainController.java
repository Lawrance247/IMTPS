package controllers;

import java.io.IOException;
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
import repositories.InitializeTasks;
import utils.Category;
import utils.SceneManager;

/**
 * Controller for the main task list view ({@code /fxml/main-view.fxml}).
 *
 * <p>This is the app's home screen. It shows every task currently held by {@code App.taskManager} as a generated schedule (one row per
 * {@link ScheduledTask}), lets the user add, edit, or delete tasks, set the daily availability window used by {@code App.scheduler}, and navigate to
 * the strategy comparison view.</p>
 *
 * <p><b>Collaborators:</b> reads/writes tasks through the static {@code App.taskManager} ({@code services.TaskManager}); generates the
 * displayed schedule through the static {@code App.scheduler} ({@code services.Scheduler}); navigates to {@link TaskFormController}
 * (add/edit) and {@link ComparisonController} (compare strategies) via {@code utils.SceneManager}; seeds demo data via
 * {@code repositories.InitializeTasks} on first run. Table rows are backed by {@link ScheduledTask} (and, transitively, {@link Task}).</p>
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
      /** Populate focusModeComboBox */
      ObservableList<Category> modes = FXCollections.observableArrayList(Category.ACADEMIC, Category.FAMILY, Category.LEISURE, Category.HEALTH);
      focusModeComboBox.setItems(modes);

      /** Availability time spinners -> allows the user to select availability time. */
      setSpinner(availabilityStartHrSpinner, availabilityStartMinSpinner, 0, 23, 0, 59);
      setSpinner(availabilityEndHrSpinner, availabilityEndMinSpinner, 0, 23, 0, 59);

      List<Task> allTasks = App.taskManager.getAllTasks();

      /*if(allTasks.isEmpty()){
        allTasks = InitializeTasks.createTasks(); //for testing purposes
      }*/

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
     * "Compare Strategies" button handler. If a focus mode is selected, applies it to the shared {@code App.wfStrategy} instance before switching views,
     * so that {@code ComparisonController.initialize()}, which runs synchronously as part of the {@code SceneManager.switchTo} call below, already sees
     * the chosen focus mode when it generates the Strategy B schedule.
     */
    @FXML
    void handleCompareStrategies(ActionEvent event) {
      try {
          setUserFocusMode();
          ComparisonController controller = SceneManager.switchTo("/fxml/comparison-view.fxml");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /** added after documentation */
    private void setUserFocusMode(){
      Category mode = focusModeComboBox.getValue();
      if(mode != null){
        App.wfStrategy.setFocusMode(mode);
      }
    }


    /**
     * "Delete" button handler. Removes {@link #activeSelectedTask} from {@code App.taskManager} and refreshes the schedule. No-op if nothing
     * is selected.
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
        setUserFocusMode(); /** added after documentation */
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
}
