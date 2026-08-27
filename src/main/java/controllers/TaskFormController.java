package controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import app.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import models.Task;
import utils.Category;
import utils.SceneManager;
import utils.Weights;



/**
 * Controller for the add/edit task form ({@code /fxml/task-form.fxml}).
 *
 * <p>This single form serves two purposes, distinguished by whether {@link #taskToEdit} is {@code null}: when {@code null}, saving creates a
 * new {@link Task}; when set (via {@link #setTaskToEdit(Task)}), saving updates that existing task instead. Both paths delegate persistence to
 * {@code App.taskManager}.</p>
 *
 * <p><b>Collaborators:</b> reads/writes tasks through the static {@code App.taskManager} ({@code services.TaskManager}); reads
 * {@code utils.Category} and {@code utils.Weights} for the dropdown/sliders; navigates back to {@link MainController} via
 * {@code utils.SceneManager}.</p>
 */
public class TaskFormController {

    @FXML
    private Button cancelButton;

    @FXML
    private ComboBox<Category> categoryComboBox;

    @FXML
    private DatePicker deadlinePicker;

    @FXML
    private Spinner<Integer> hourSpinner;


    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private TextField durationField;

    @FXML
    private Slider effortSlider;

    @FXML
    private Label effortValueLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Slider importanceSlider;

    @FXML
    private Label importanceValueLabel;

    @FXML
    private TextField nameField;

    @FXML
    private Button saveButton;

    @FXML
    private TextField taskDescription;


    /** The task being edited, or {@code null} when the form is in "create" mode. */
    private Task taskToEdit = null;


    /**
     * FXML lifecycle callback. Populates the category dropdown, configures the hour/minute spinners for a 24-hour clock, and wires the
     * importance/effort sliders to keep their numeric labels in sync as the user drags them.
     */
    @FXML
    void initialize() {
      ObservableList<Category> categories = FXCollections.observableArrayList(Category.ACADEMIC, Category.FAMILY, Category.LEISURE, Category.HEALTH);

      /** set spinners for user to select times (24hr clock) */
      SpinnerValueFactory<Integer> hours = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23);
      SpinnerValueFactory<Integer> minutes = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59);

      categoryComboBox.setItems(categories);
      hourSpinner.setValueFactory(hours);
      minuteSpinner.setValueFactory(minutes);
      
      /** keeps slider label showing slider's current value */
      importanceSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
          importanceValueLabel.setText(String.valueOf(newValue.intValue()));
      });
      effortSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
          effortValueLabel.setText(String.valueOf(newValue.intValue()));
      });
    }


    /**
     * "Cancel" button handler. Discards any changes and returns to the main view.
     */
    @FXML
    void handleCancel(ActionEvent event) {
        try {
        SceneManager.switchTo("/fxml/main-view.fxml");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * "Save" button handler. Reads every field, builds the deadline from the date picker plus hour/minute spinners, converts the importance/effort
     * slider values (1/2/3) to {@link Weights}, and; based on whether {@link #taskToEdit} is set and its title still matches the name field,
     * either updates the existing task via {@link #updateEditedTask} or creates a new one via {@link #createNewTask}. Any exception (parsing,
     * validation from {@code TaskManager}, etc.) is caught and surfaced to the user through {@link #errorLabel} rather than propagating.
     */
    @FXML
    void handleSave(ActionEvent event) {
        try{
            String taskName = nameField.getText();
            String description = taskDescription.getText();
            Category category = categoryComboBox.getValue();

            /** deadline */
            LocalDate date = deadlinePicker.getValue();
            LocalTime time = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
            LocalDateTime deadline = LocalDateTime.of(date, time);

            Integer duration = Integer.parseInt(durationField.getText());

            Weights importance = (importanceValueLabel.getText().equals("1")) ? Weights.LOW :
            (importanceValueLabel.getText().equals("2")) ? Weights.MEDIUM : Weights.HIGH;

            Weights effort = (effortValueLabel.getText().equals("1")) ? Weights.LOW :
            (effortValueLabel.getText().equals("2")) ? Weights.MEDIUM : Weights.HIGH;

            if ( (taskToEdit != null) && ( taskName.equals(taskToEdit.getTitle()) ) ){
                updateEditedTask(description, category, deadline, duration, importance, effort);
            }else{
                createNewTask(taskName, description, category, deadline, duration, importance, effort);
                resetFields();
            }
        }catch(Exception e){
            //let user know task was not added to db
            errorLabel.setText("Task not added error occured. Please try again.");
            errorLabel.setTextFill(Color.RED);
            e.printStackTrace();
        }
    }


    /**
     * Applies the given field values to {@link #taskToEdit}, persists the change via {@code App.taskManager.updateTask}, shows a success message,
     * and returns to the main view.
     *
     * @param description new description
     * @param category    new category
     * @param deadline    new deadline
     * @param duration    new duration, in minutes
     * @param importance    new importance weight
     * @param effort      new effort weight
     */
    private void updateEditedTask(String description, Category category, LocalDateTime deadline, int duration, Weights importance, Weights effort){
        taskToEdit.setDescription(description);
        taskToEdit.setCategory(category);
        taskToEdit.setDeadline(deadline);
        taskToEdit.setDurationMinutes(duration);
        taskToEdit.setImportance(importance);
        taskToEdit.setEffort(effort);

        try {
            App.taskManager.updateTask(taskToEdit);
            errorLabel.setText("Task updated successfully.");
            errorLabel.setTextFill(Color.GREEN);
            MainController controller = SceneManager.switchTo("/fxml/main-view.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Creates a new {@link Task} via {@code App.taskManager.createTask} from the given field values and shows a success message. Unlike
     * {@link #updateEditedTask}, this does not navigate away afterwards, the form stays open.
     *
     * @param taskName    new task's title
     * @param description new task's description
     * @param category    new task's category
     * @param deadline    new task's deadline
     * @param duration    new task's duration, in minutes
     * @param importance    new task's importance weight
     * @param effort      new task's effort weight
     * @return the newly created task
     */
    private Task createNewTask(String taskName, String description, Category category, LocalDateTime deadline, int duration, Weights importance, Weights effort){
        Task task = App.taskManager.createTask(
            taskName,
            description,
            category,
            deadline,
            duration,
            importance,
            effort
        );
        errorLabel.setText("Task added successfully.");
        errorLabel.setTextFill(Color.GREEN);

        return task;
    }


    /**
     * Clears the form back to a blank "create" state: name/description fields, category prompt, deadline picker, and both sliders (reset to
     * the default value of 2 / MEDIUM). Called from {@link #handleSave} after successfully creating a new task.
     */
    private void resetFields(){
        nameField.setText("");
        taskDescription.setText("");
        categoryComboBox.setPromptText("");
        deadlinePicker.setValue(null);
        importanceSlider.setValue(2);
        importanceValueLabel.setText("2");
        effortSlider.setValue(2);
        effortValueLabel.setText("2");
    }


    /**
     * Puts the form into "edit" mode for the given task, pre-filling every field from it. The name field is disabled while editing, since
     * {@link #handleSave} uses an unchanged name as the signal that this is an edit rather than a new task.
     *
     * @param task the task to edit; passing {@code null} opens a blank form.
     */
    public void setTaskToEdit(Task task){
        this.taskToEdit = task;
        if (taskToEdit != null) {
            nameField.setText(task.getTitle());
            nameField.setDisable(true);

            taskDescription.setText(task.getDescription());
            categoryComboBox.setValue(task.getCategory());
            
            LocalDateTime dateTime = task.getDeadline();
            LocalDate date = LocalDate.of(dateTime.getYear(), dateTime.getMonthValue(), dateTime.getDayOfMonth());
            Integer hrs = dateTime.getHour();
            Integer min = dateTime.getMinute();

            deadlinePicker.setValue(date);
            hourSpinner.getValueFactory().setValue(hrs);
            minuteSpinner.getValueFactory().setValue(min);

            durationField.setText(String.valueOf(task.getDurationMinutes()));

            importanceSlider.setValue(task.getImportance().getWeight());
            importanceValueLabel.setText(String.valueOf(task.getImportance().getWeight()));
            effortSlider.setValue(task.getEffort().getWeight());
            effortValueLabel.setText(String.valueOf(task.getEffort().getWeight()));
        }
    }

}
