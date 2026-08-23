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
import models.ComparisonResult;
import models.Schedule;
import models.ScheduledTask;
import models.Task;
import services.Comparison;
import services.Scheduler;
import strategies.EarliestDeadlineFirstStrategy;
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
      //create a WFS schedule
      WeightedFocusStrategy strategy = new WeightedFocusStrategy(FocusMode.LEISURE);
      Scheduler scheduler = new Scheduler(strategy, LocalTime.of(8, 0), LocalTime.of(18, 0));
      Schedule schedule = scheduler.generateSchedule(createTasks(), LocalDateTime.of(2026, 8, 28, 8, 0));

      //create an EDF schedule
      EarliestDeadlineFirstStrategy EDFstrategy = new EarliestDeadlineFirstStrategy();
      scheduler.setStrategy(EDFstrategy);
      Schedule EDFschedule = scheduler.generateSchedule(createTasks(), LocalDateTime.of(2026, 8, 28, 8, 0));


      //display schedule on CL
      System.out.println("\nWeighted Focus Strategy");
      List<ScheduledTask> scheduledTasks = schedule.getScheduledTasks();
      List<ScheduledTask> clashes = schedule.getDeadlineClashes();

      for(ScheduledTask t : scheduledTasks){
       System.out.println(t.toString());
      }
      System.out.printf("\nDeadline Clashes %d\nScheduled Minutes %d\nNumber of Tasks %d\n", schedule.getDeadlineClashCount(), schedule.getTotalScheduledMinutes(), schedule.getTaskCount());
      System.out.println("Clashes:");
      for(ScheduledTask t : clashes){
       System.out.println(t.toString());
      }

      System.out.println("\nEarliest Deadline First Strategy");
      List<ScheduledTask> EDFScheduledTasks = EDFschedule.getScheduledTasks();
      for(ScheduledTask t : EDFScheduledTasks){
        System.out.println(t.toString());
      }

      System.out.printf("\nDeadline Clashes %d\nScheduled Minutes %d\nNumber of Tasks %d\n", EDFschedule.getDeadlineClashCount(), EDFschedule.getTotalScheduledMinutes(), EDFschedule.getTaskCount());
      System.out.println();

      //Comparison
      Comparison comparisonService = new Comparison(schedule, EDFschedule, FocusMode.LEISURE);
      ComparisonResult compResults = comparisonService.compare();
      System.out.printf("WFS ---> %s\n", compResults.getScheduleAMetrics().toString());
      System.out.printf("EDFS --> %s\n",compResults.getScheduleBMetrics().toString());

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

  // Temporary method for testing
  private List<Task> createTasks(){
        List<Task> tasks = new ArrayList<>();
        tasks.add(new Task(
                "Finish Programming Lab",
                "Complete the remaining programming lab exercises.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Review Design Patterns",
                "Study common software design patterns and examples.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 1, 14, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Finance Problem Set",
                "Complete the assigned corporate finance calculations.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 2, 16, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Update Project README",
                "Add setup instructions and usage information to the project README.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 2, 19, 0),
                60,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Study Probability",
                "Revise probability concepts and solve practice problems.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 3, 11, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Prepare Tutorial Questions",
                "Work through questions before the next tutorial.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 3, 15, 0),
                75,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Refactor Java Classes",
                "Improve the structure and readability of the Java project.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 4, 13, 0),
                150,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Review Exam Notes",
                "Review previous lecture notes and create a revision summary.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 4, 18, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Complete Research",
                "Finish collecting information for the research assignment.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 5, 12, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Practice Coding Questions",
                "Solve programming questions under timed conditions.",
                Category.ACADEMIC,
                LocalDateTime.of(2026, 9, 6, 10, 0),
                120,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Call Sibling",
                "Catch up with a sibling over the phone.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 1, 20, 0),
                30,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Clean Living Area",
                "Clean and organise the shared living area.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 2, 10, 0),
                90,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Prepare Breakfast",
                "Prepare breakfast for the household.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 2, 7, 30),
                45,
                Priority.MEDIUM,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Buy Cleaning Supplies",
                "Purchase cleaning products and household supplies.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 3, 17, 0),
                60,
                Priority.MEDIUM,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Cook Family Dinner",
                "Prepare and serve dinner for the family.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 3, 18, 30),
                120,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Organise Wardrobe",
                "Sort clothes and organise the wardrobe.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 4, 11, 0),
                90,
                Priority.LOW,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Clean Bathroom",
                "Thoroughly clean and organise the bathroom.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 5, 10, 0),
                75,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Family Breakfast",
                "Have breakfast together with the family.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 6, 9, 0),
                90,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Help With Errands",
                "Help a family member complete weekend errands.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 6, 13, 0),
                150,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Prepare Lunch",
                "Prepare lunch for the household.",
                Category.FAMILY,
                LocalDateTime.of(2026, 9, 7, 12, 0),
                60,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Upper Body Workout",
                "Complete an upper body strength workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 1, 16, 0),
                75,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Cycling Session",
                "Go cycling for cardiovascular exercise.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 2, 17, 0),
                90,
                Priority.MEDIUM,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Mobility Routine",
                "Complete a full-body mobility routine.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 3, 7, 0),
                30,
                Priority.MEDIUM,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Lower Body Workout",
                "Complete a lower body strength training session.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 4, 16, 0),
                75,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Swimming",
                "Complete a recreational swimming workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 5, 9, 0),
                90,
                Priority.MEDIUM,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Recovery Session",
                "Complete stretching and light recovery exercises.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 5, 19, 0),
                45,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Interval Running",
                "Complete a high-intensity interval running session.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 6, 8, 0),
                60,
                Priority.HIGH,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Prepare Healthy Snacks",
                "Prepare healthy snacks for the upcoming week.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 6, 16, 0),
                60,
                Priority.LOW,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Core Workout",
                "Complete a focused abdominal and core workout.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 7, 17, 0),
                45,
                Priority.MEDIUM,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Long Walk",
                "Go for a long walk to stay active and clear the mind.",
                Category.HEALTH,
                LocalDateTime.of(2026, 9, 7, 18, 30),
                75,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Visit Coffee Shop",
                "Go to a coffee shop and relax for a while.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 1, 18, 0),
                90,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Play Board Game",
                "Spend the evening playing a board game.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 2, 20, 0),
                120,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Listen to Podcast",
                "Listen to a podcast while relaxing.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 3, 21, 0),
                60,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Go for a Hike",
                "Spend the afternoon hiking and exploring nature.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 4, 9, 0),
                180,
                Priority.MEDIUM,
                Effort.HIGH
        ));

        tasks.add(new Task(
                "Visit Bookstore",
                "Browse books and spend some time reading.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 5, 14, 0),
                90,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Play Pool",
                "Play pool with friends.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 5, 19, 0),
                120,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Try New Restaurant",
                "Have dinner at a restaurant that you have not visited before.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 6, 19, 0),
                120,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Watch Documentary",
                "Watch an interesting documentary.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 7, 20, 0),
                120,
                Priority.LOW,
                Effort.LOW
        ));

        tasks.add(new Task(
                "Go Shopping",
                "Spend some free time browsing shops.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 8, 14, 0),
                150,
                Priority.LOW,
                Effort.MEDIUM
        ));

        tasks.add(new Task(
                "Photography Walk",
                "Take a walk around the city and practise photography.",
                Category.LEISURE,
                LocalDateTime.of(2026, 9, 8, 16, 30),
                120,
                Priority.LOW,
                Effort.MEDIUM
        ));
        return tasks;
  }
}
