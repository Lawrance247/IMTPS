package controllers;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import models.ComparisonResult;
import models.MetricRow;
import models.Schedule;
import models.ScheduleMetrics;
import models.ScheduledTask;
import models.Task;
import services.Comparison;
import utils.Category;
import utils.SceneManager;


/**
 * Controller for the strategy comparison view ({@code /fxml/comparison-view.fxml}).
 *
 * <p>On {@link #initialize()}, this controller takes every task currently held by {@code App.taskManager}, generates two schedules from them — one
 * using {@code strategies.EarliestDeadlineFirstStrategy} ("Strategy A") and one using {@code strategies.WeightedFocusStrategy}
 * for {@link #focusMode} ("Strategy B") — runs both through {@code services.Comparison}, and displays the two schedules side by
 * side plus a metric-by-metric comparison table. The user can then pick a preferred strategy from a dropdown before returning to
 * the main view, which becomes the scheduler's active strategy going forward.</p>
 *
 * <p><b>Collaborators:</b> reads tasks via {@code App.taskManager}; builds schedules via {@code App.scheduler} together with
 * {@code strategies.EarliestDeadlineFirstStrategy}/{@code strategies.WeightedFocusStrategy}; scores them via {@code services.Comparison},
 * consuming its {@link ComparisonResult} and {@link ScheduleMetrics}; renders rows via {@link MetricRow} and {@link ScheduledTask}; navigates
 *  back to {@link MainController} via {@code utils.SceneManager}.</p>
 */
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
    private ComboBox<String> pickPrefferedStrategy;

    @FXML
    private TableColumn<ScheduledTask, String> importanceColumnA;

    @FXML
    private TableColumn<ScheduledTask, String> importanceColumnB;

    @FXML
    private TableColumn<MetricRow, String> metricAColumn;

    @FXML
    private TableColumn<MetricRow, String> metricBColumn;

    @FXML
    private TableColumn<MetricRow, String> metricDefinitionColumn;

    @FXML
    private TableColumn<MetricRow, String> metricDirectionColumn;

    @FXML
    private TableColumn<MetricRow, String> metricNameColumn;

    @FXML
    private TableView<MetricRow> metricsTable;

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


    /**
     * The focus category used for Strategy B (WeightedFocusStrategy) and for scoring focus alignment. Defaults to {@code Category.ACADEMIC} until
     * {@link #setFocusMode(Category)} is called.
     */
    private Category focusMode = Category.ACADEMIC;


    /**
     * FXML lifecycle callback. Builds both candidate schedules (Earliest Deadline First and Weighted Focus), compares them via
     * {@code services.Comparison}, and populates the two schedule tables plus the metrics table. Does nothing if there are no tasks to schedule.
     */
    @FXML
    void initialize(){
      ObservableList<String> availableStrategies = FXCollections.observableArrayList();
      //get tasks
      List<Task> tasks = App.taskManager.getAllTasks();

      // generate schedules
      if (!tasks.isEmpty()){
        // ==== SET UP FIRST STRATEGY ====
        App.scheduler.setStrategy(App.edfStrategy);
        Schedule edfSchedule = App.scheduler.generateSchedule(tasks, null);
        setUpScheduleTable(edfSchedule.getScheduledTasks(), "A");
        strategyALabel.setText("Earliest Deadline First Strategy");

        availableStrategies.add(App.scheduler.getStrategy().getStrategyName());
        
        // ==== SET UP SECOND STRATEGY ===
        App.scheduler.setStrategy(App.wfStrategy);
        Schedule wfSchedule = App.scheduler.generateSchedule(tasks, null);
        setUpScheduleTable(wfSchedule.getScheduledTasks(), "B");
        focusMode = App.wfStrategy.getFocusMode(); //set focus mode
        strategyBLabel.setText("Weighted Focus Strategy");

        availableStrategies.add(App.scheduler.getStrategy().getStrategyName());

        // ===== COMPARING STRATEGIES =====
        Comparison scheduleComparison = new Comparison(edfSchedule, wfSchedule, focusMode);
        ComparisonResult compResults = scheduleComparison.compare();

        ScheduleMetrics edfMetrics = compResults.getScheduleAMetrics();
        ScheduleMetrics wfMetrics = compResults.getScheduleBMetrics();

        List<MetricRow> metrics = new ArrayList<>();

        // deadline clashes
        metrics.add(new MetricRow("Deadline Clashes","Number of tasks whose scheduled finish is after their deadline.","Lower", String.valueOf(edfMetrics.getDeadlineClashes()), String.valueOf(wfMetrics.getDeadlineClashes())));

        // Total lateness
        metrics.add(new MetricRow("Total Lateness", "Total amount of time deadlines are exceeded.", "Lower", formatMinutes(edfMetrics.getTotalLateness()), formatMinutes(wfMetrics.getTotalLateness())));
        
        // Average Slack
        metrics.add(new MetricRow("Average Slack", "Average deadline minus scheduled finish, including negative values.", "Higher", formatMinutes(Math.round(edfMetrics.getAverageSlack())), formatMinutes(Math.round(wfMetrics.getAverageSlack()))));

        // Weighted Lateness
        metrics.add(new MetricRow("Weighted Lateness", "Lateness weighted by task importance.", "Lower", String.valueOf(edfMetrics.getWeightedLateness()), String.valueOf(wfMetrics.getWeightedLateness())
        ));

        // Focus Alignment
        metrics.add(new MetricRow("Focus Alignment", "Importance-weighted proportion of the first half of prioritised tasks matching the focus mode.", "Higher", String.format("%.1f%%", edfMetrics.getFocusAlignment()), String.format("%.1f%%", wfMetrics.getFocusAlignment())
        ));

        setUpMetricsTable(metrics, "EDF Strategy", "WF Strategy");

        pickPrefferedStrategy.setItems(availableStrategies);

        System.out.println(focusMode.toString());
        focusModeLabel.setText(focusMode.toString());
      }
    }



    /**
     * Formats a signed minute count as {@code "<sign><h>h <m>m"} (hours omitted when zero), e.g. {@code -95} -> {@code "-1h 35m"}.
     *
     * @param totalMinutes the minute value to format; may be negative
     * @return a human-readable duration string
     */
    private String formatMinutes(long totalMinutes) {
        String sign = totalMinutes < 0 ? "-" : "";
        long abs = Math.abs(totalMinutes);
        long hours = abs / 60;
        long mins = abs % 60;
        return hours > 0 ? sign + hours + "h " + mins + "m" : sign + mins + "m";
    }



    /**
     * Binds either the "A" or "B" schedule table's columns to the given scheduled tasks, formatting dates with {@code "dd MMM yyyy, HH:mm"}.
     *
     * @param schedule     the scheduled tasks to display, one per row
     * @param scheduleType {@code "A"} or {@code "B"}; any other value is
     *                     treated the same as {@code "A"}
     */
    private void setUpScheduleTable(List<ScheduledTask> schedule, String scheduleType){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm");

        if (scheduleType.equals("B")){
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


    /**
     * Binds the metrics table's columns to the given {@link MetricRow} list and labels the value columns with each strategy's name.
     *
     * @param metrics       the rows to display, one per metric
     * @param strategyAName header text for the Strategy A value column
     * @param strategyBName header text for the Strategy B value column
     */
    private void setUpMetricsTable(List<MetricRow> metrics, String strategyAName, String strategyBName){
      metricNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMetricName()));
      metricDefinitionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDefinition()));
      metricDirectionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDirection()));
      metricAColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValueA()));
      metricBColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValueB()));

      metricAColumn.setText(strategyAName);
      metricBColumn.setText(strategyBName);

      metricsTable.setItems(FXCollections.observableArrayList(metrics));
    }


    /**
     * "Close" button handler. Applies whichever strategy the user picked in {@code pickPrefferedStrategy} to {@code App.scheduler} (matched by
     * strategy display name; anything other than an exact match on "Earliest Deadline First Strategy" falls back to {@code WeightedFocusStrategy}),
     * then returns to the main view.
     */
    @FXML
    void handleClose(ActionEvent event) {
      try {
        String prefferedStrategy = pickPrefferedStrategy.getValue();
        if ((prefferedStrategy != null) && prefferedStrategy.equals("Earliest Deadline First Strategy")){
          App.scheduler.setStrategy(App.edfStrategy);
        }
        else{
          App.scheduler.setStrategy(App.wfStrategy);
          System.out.println(focusMode.toString());
        }
        MainController controller = SceneManager.switchTo("/fxml/main-view.fxml");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

}
