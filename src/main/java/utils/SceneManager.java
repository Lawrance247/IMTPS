package utils;

import java.io.IOException;
import java.util.Objects;

import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;


/**
 * Loads an FXML view and swaps it in as the primary stage's whole scene, with a short fade-in.
 *
 * <p>This is the only navigation mechanism in the app, there's no multi-window or "swap just the center panel" support, every screen
 * change is a full scene replacement via {@link #switchTo(String)}. Because it fully reloads the FXML each time, every controller's
 * {@code initialize()} runs fresh on every navigation (nothing about a controller's state survives a round trip away and back).</p>
 *
 * <p><b>Collaborators:</b> {@code app.App} calls {@link #setStage(Stage)} once at startup; every controller (`MainController`, `TaskFormController`,
 * `ComparisonController`) calls {@link #switchTo(String)} to navigate to another screen and get a reference to that screen's controller.</p>
 */
public class SceneManager {

    /** The app's single primary window, set once via {@link #setStage(Stage)}. */
    public static Stage mainStage;

    /** Declared for a possible secondary/pop-up window; not currently used anywhere. */
    public static Stage popUp;


    /** Registers the primary stage. Must be called once, before the first {@link #switchTo(String)}. */
    public static void setStage(Stage stage) {
        mainStage = stage;
    }
    

    /**
     * Switches the content of the main window (full view change, e.g. main-view -> comparison-view).
     *
     * <p>Loads the given FXML, replaces {@link #mainStage}'s scene with it (fixed at 650x650), plays a 0.5s fade-in, and returns the
     * controller the {@link FXMLLoader} created for that FXML; note that by the time this method returns, the new controller's
     * {@code @FXML initialize()} has already run as part of {@code loader.load()}.</p>
     *
     * @param fxmlPath classpath-relative path to the FXML file, e.g. {@code "/fxml/main-view.fxml"}
     * @param <T>       the FXML's declared controller type, inferred from the assignment target
     * @return the controller instance for the newly loaded view
     * @throws IOException if the FXML fails to load
     */
    public static <T> T switchTo(String fxmlPath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(
                SceneManager.class.getResource(fxmlPath)
        ));

        Parent root = loader.load();
        Scene scene = new Scene(root, 650, 650);    // My personal default
        // Scene scene = new Scene(root);
        // scene.setFill(Color.TRANSPARENT);
        mainStage.setScene(scene);
        mainStage.show();

        FadeTransition ft = new FadeTransition(Duration.millis(500), root);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();

        return loader.getController();
    }

}
