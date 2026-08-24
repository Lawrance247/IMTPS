package controllers;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import app.App;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.ComparisonResult;
import models.Schedule;
import models.ScheduleMetrics;
import models.ScheduledTask;
import models.Task;
import services.Comparison;
import strategies.EarliestDeadlineFirstStrategy;
import strategies.WeightedFocusStrategy;
import utils.FocusMode;
import utils.SceneManager;

public class ComparisonController {

    @FXML
    private Button closeButton;

    @FXML
    private TableColumn<ScheduledTask, String> deadlineColumnA;

    @FXML
    private TableColumn<ScheduledTask, String> deadlineColumnB;

    @FXML
    private TableColumn<ScheduledTask, String> endColumnA;

    @FXML
    private TableColumn<ScheduledTask, String> endColumnB;

    @FXML
    private Label focusModeLabel;

    @FXML
    private TableColumn<ScheduledTask, String> importanceColumnA;

    @FXML
    private TableColumn<ScheduledTask, String> importanceColumnB;

    @FXML
    private TableColumn<ScheduleMetrics, String> metricAColumn;

    @FXML
    private TableColumn<ScheduleMetrics, String> metricBColumn;

    @FXML
    private TableColumn<ScheduleMetrics, String> metricDefinitionColumn;

    @FXML
    private TableColumn<ScheduleMetrics, String> metricDirectionColumn;

    @FXML
    private TableColumn<ScheduleMetrics, String> metricNameColumn;

    @FXML
    private TableView<ScheduleMetrics> metricsTable;

    @FXML
    private TableColumn<ScheduledTask, String> nameColumnA;

    @FXML
    private TableColumn<ScheduledTask, String> nameColumnB;

    @FXML
    private TableView<ScheduledTask> scheduleATable;

    @FXML
    private TableView<ScheduledTask> scheduleBTable;

    @FXML
    private TableColumn<ScheduledTask, String> startColumnA;

    @FXML
    private TableColumn<ScheduledTask, String> startColumnB;

    @FXML
    private Label strategyALabel;

    @FXML
    private Label strategyBLabel;


    @FXML
    void initialize(){
      //get tasks
      List<Task> tasks = App.taskManager.getAllTasks();

      // generate schedules
      if (!tasks.isEmpty()){
        App.scheduler.setStrategy(new EarliestDeadlineFirstStrategy());
        Schedule edfSchedule = App.scheduler.generateSchedule(tasks, LocalDateTime.of(2026, 8, 28, 8, 0));
        setUpScheduleTable(edfSchedule.getScheduledTasks(), "A");
        strategyALabel.setText("Earliest Deadline First Strategy");
        strategyBLabel.setText("Weighted Focus Strategy");

        App.scheduler.setStrategy(new WeightedFocusStrategy(FocusMode.ACADEMIC));
        Schedule wfSchedule = App.scheduler.generateSchedule(tasks, LocalDateTime.of(2026, 8, 28, 8, 0));
        setUpScheduleTable(wfSchedule.getScheduledTasks(), "B");

        Comparison scheduleComparison = new Comparison(edfSchedule, wfSchedule, FocusMode.ACADEMIC);

        ComparisonResult compResults = scheduleComparison.compare();

        ScheduleMetrics edfMetrics = compResults.getScheduleAMetrics();
        ScheduleMetrics wfMetrics = compResults.getScheduleBMetrics();

        focusModeLabel.setText(FocusMode.ACADEMIC.toString());
      }
    }

    private void setUpScheduleTable(List<ScheduledTask> schedule, String scheduleType){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        if (scheduleType.equals("B")){
          nameColumnB.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getTitle()) );
          nameColumnB.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getTitle()) );

          importanceColumnB.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getImportance().toString()) );

          deadlineColumnB.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getDeadline().format(formatter)) );

          startColumnB.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)) );

          endColumnB.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(formatter)) );

          scheduleBTable.setItems( FXCollections.observableArrayList(schedule) );  
        }else{
          nameColumnA.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getTitle()) );

          importanceColumnA.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getImportance().toString()) );

          deadlineColumnA.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getTask().getDeadline().format(formatter)) );

          startColumnA.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getStartTime().format(formatter)) );

          endColumnA.setCellValueFactory( cellData -> new SimpleStringProperty(cellData.getValue().getEndTime().format(formatter)) );

          scheduleATable.setItems( FXCollections.observableArrayList(schedule) ); 
        }    
    }

    @FXML
    void handleClose(ActionEvent event) {
      try {
          MainController controller = SceneManager.switchTo("/fxml/main-view.fxml");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

}
