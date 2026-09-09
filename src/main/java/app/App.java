package app;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.application.Application;
import javafx.stage.Stage;
import repositories.TaskDB;
import services.Scheduler;
import services.TaskManager;
import strategies.EarliestDeadlineFirstStrategy;
import strategies.WeightedFocusStrategy;
import utils.SceneManager;

import utils.Category;
import utils.Weights;

/**
 * JavaFX application entry point.
 *
 * <p>{@code App} owns the app's shared, single-instance state as public static fields — {@link #taskManager} and {@link #scheduler} (plus the two
 * strategy instances {@link #edfStrategy}/{@link #wfStrategy}) — so that every controller can reach the same task list and scheduler without
 * passing references around by hand. This is a deliberate simplification for a single-window MVP app rather than a full dependency-injection setup.
 * {@link #start(Stage)} performs the one-time JavaFX bootstrapping: it hands the primary {@link Stage} to {@code SceneManager} and loads the first
 * view.</p>
 *
 * <p><b>Collaborators:</b> constructs {@code services.TaskManager} (over a fresh {@code repositories.TaskDB}) and {@code services.Scheduler} (over
 * {@link #edfStrategy}, the default strategy); every controller (`MainController`, `TaskFormController`, `ComparisonController`) reads
 * these static fields directly; hands off to {@code utils.SceneManager} to load the first FXML view.</p>
 */
public class App extends Application {
    /** Shared task store/CRUD service, backed by an in-memory {@code TaskDB}. */
    public final static TaskManager taskManager = new TaskManager(new TaskDB());

    /** Shared "Earliest Deadline First" strategy instance, reused (not re-created) wherever that strategy is needed. */
    public final static EarliestDeadlineFirstStrategy edfStrategy = new EarliestDeadlineFirstStrategy();

    /**
     * Shared "Weighted Focus" strategy instance. Because it is a single shared instance, its focus mode (set via {@code setFocusMode}) is
     * effectively app-wide state.
     */
    public final static WeightedFocusStrategy wfStrategy = new WeightedFocusStrategy();

     /** Shared scheduler, starting out on {@link #edfStrategy} with an 08:00–18:00 daily availability window. */
    public final static Scheduler scheduler = new Scheduler(edfStrategy, LocalTime.of(8, 0), LocalTime.of(18, 0));


    /**
     * Standard Java entry point; delegates straight to JavaFX's {@link Application#launch(String...)}, which eventually calls
     * {@link #start(Stage)}.
     */
    public static void main(String[] args){
        launch(args);
    }


    /**
     * JavaFX lifecycle callback, called once at startup with the primary stage. Registers the stage with {@code SceneManager} and loads
     * {@code main-view.fxml} as the first screen.
     */
    @Override
    public void start(Stage stage) throws Exception {
            App.taskManager.createTask(
                    "Database Test Task",
                    "Testing SQLite persistence",
                    Category.ACADEMIC,
                    LocalDateTime.now().plusDays(2),
                    60,
                    Weights.HIGH,
                    Weights.MEDIUM
            );
//        stage.initStyle(StageStyle.UNDECORATED); // --> this removes the exit bar
//        stage.initStyle(StageStyle.TRANSPARENT);
        SceneManager.setStage(stage);
        SceneManager.switchTo("/fxml/main-view.fxml");

        //stage.setResizable(false);
        stage.show();
    }

}