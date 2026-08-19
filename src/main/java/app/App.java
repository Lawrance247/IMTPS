package app;
import javafx.application.Application;
import javafx.stage.Stage;
import repositories.TaskDB;
import services.TaskManager;
import utils.SceneManager;

/**
 * JavaFX App
 */
public class App extends Application {
    public final static TaskManager TaskManager = new TaskManager(new TaskDB());

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