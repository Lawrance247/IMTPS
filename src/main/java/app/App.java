package app;
import java.time.LocalTime;

import javafx.application.Application;
import javafx.stage.Stage;
import repositories.TaskDB;
import services.Scheduler;
import services.TaskManager;
import strategies.EarliestDeadlineFirstStrategy;
import utils.SceneManager;

/**
 * JavaFX App
 */
public class App extends Application {
    public final static TaskManager taskManager = new TaskManager(new TaskDB());
    public final static Scheduler scheduler = new Scheduler(new EarliestDeadlineFirstStrategy(), LocalTime.of(8, 0), LocalTime.of(18, 0));

    public static void main(String[] args){
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        stage.initStyle(StageStyle.UNDECORATED); // --> this removes the exit bar
//        stage.initStyle(StageStyle.TRANSPARENT);
        SceneManager.setStage(stage);
        SceneManager.switchTo("/fxml/main-view.fxml");

        //stage.setResizable(false);
        stage.show();
    }
}