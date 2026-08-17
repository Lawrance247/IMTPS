package app;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.SceneManager;


/**
 * JavaFX App
 */
public class App extends Application {

    public static void main(String[] args){

        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
//        stage.initStyle(StageStyle.UNDECORATED); // --> this removes the exit bar
//        stage.initStyle(StageStyle.TRANSPARENT);
        SceneManager.setStage(stage);
        SceneManager.switchTo("/fxml/dashboard.fxml");

        //stage.setResizable(false);
        stage.show();
    }
}