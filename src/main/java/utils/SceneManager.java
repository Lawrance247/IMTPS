package utils;

import java.io.IOException;
import java.util.Objects;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneManager {

    public static Stage mainStage;
    public static Stage popUp;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    public static void switchTo(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                SceneManager.class.getResource(fxmlPath)
        ));

        Parent root = loader.load();
        Scene scene = new Scene(root, 1125, 650);    // My personal default
        // Scene scene = new Scene(root);
        // scene.setFill(Color.TRANSPARENT);
        mainStage.setScene(scene);
        mainStage.show();

        FadeTransition ft = new FadeTransition(Duration.millis(1000), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
    }

}
