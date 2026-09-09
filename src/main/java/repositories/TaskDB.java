// Sisa (comments are for me not to forget design choices)
package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import java.sql.ResultSet;

import utils.Category;
import utils.TaskStatus;
import utils.Weights;

import java.time.LocalDateTime;

import models.Task;
/**
 * In-memory storage for {@link Task}s, keyed by task ID.
 *
 * <p>This is the app's only persistence layer for the MVP — there is no database, so every {@code Task} lives only as long as the app is running.
 * {@code TaskDB} is intentionally "dumb": it does no validation of its own (that's {@code services.TaskManager}'s job) and just enforces the basic
 * invariants of a keyed store — you can't add a task whose ID already exists, and you can't remove/update one that doesn't.</p>
 *
 * <p><b>Collaborators:</b> constructed once by {@code app.App} and wrapped by the single {@code services.TaskManager} instance; never accessed
 * directly by controllers.</p>
 */
public class TaskDB 
{
    // HashMaps are quiker for lookups than lists; no need to scan the list so lookups are O(1) time
    private final Map<String, Task> tasks = new HashMap<>();

    public TaskDB() {
        DatabaseConnection.initializeDatabase();
    }
    /**
     * Stores a new task.
     *
     * @param task the task to store
     * @throws IllegalArgumentException if a task with the same ID is already stored
     
    public void add(Task task) 
    {
        if (tasks.containsKey(task.getId())) 
        {
            throw new IllegalArgumentException("Task with id " + task.getId() + " already exists");
        }
        tasks.put(task.getId(), task);
    }*/

    /**
     * Adds a task to the SQLite database.
     *
     * A PreparedStatement is used to safely insert the task values
     * into the database.
     *
     * @param task the task to be saved
     */
    public void add(Task task) {

        String sql =
                "INSERT INTO tasks " +
                "(id, title, description, category, deadline, " +
                "duration_minutes, importance, effort, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            // Convert the Task object's values into values SQLite can store.
            statement.setString(1, task.getId());
            statement.setString(2, task.getTitle());
            statement.setString(3, task.getDescription());
            statement.setString(4, task.getCategory().name());
            statement.setString(5, task.getDeadline().toString());
            statement.setInt(6, task.getDurationMinutes());
            statement.setString(7, task.getImportance().name());
            statement.setString(8, task.getEffort().name());
            statement.setString(9, task.getStatus().name());

            // Execute the INSERT statement.
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not save task to database.",
                    e
            );
        }
    }

    /**
     * Removes the task with the given ID.
     *
     * @param id the ID of the task to remove
     * @throws NoSuchElementException if no task with that ID exists
     */
    public void remove(String id) 
    {
        // check before removing so that no silent nulls are thrown
        if (!tasks.containsKey(id)) 
        {
            throw new NoSuchElementException("No task with id " + id);
        }
        tasks.remove(id);
    }


    
    /**
     * Overwrites the stored task that shares this task's ID with the given,
     * presumably-modified, task object.
     *
     * @param task the (already-modified) task to save
     * @throws NoSuchElementException if no task with a matching ID is currently stored
     */
    public void update(Task task) 
    {
        // check before removing so that no silent nulls are thrown (Guard clause)
        if (!tasks.containsKey(task.getId())) 
        {
            throw new NoSuchElementException("No task with id " + task.getId());
        }
        // updating entails inserting or overwritng the key value pair  (Guard clause)
        tasks.put(task.getId(), task);
    }


    /**
     * @param id the task ID to look up
     * @return the matching task, or {@code null} if no task with that ID exists
     */
    public Task findById(String id) 
    {
        // returns null if no such task exists (UI implementantion of this should handle that exception)
        return tasks.get(id);
    }

    /**
     * Retrieves all tasks stored in the SQLite database.
     *
     * Each database row is converted back into a Task object.
     *
     * @return a list containing all stored tasks
     */
    public List<Task> findAll() {

        List<Task> tasks = new ArrayList<>();

        String sql = "SELECT * FROM tasks";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery()
        ) {

            while (resultSet.next()) {

                // Convert the database values back into Java types.
                String id = resultSet.getString("id");
                String title = resultSet.getString("title");
                String description = resultSet.getString("description");

                Category category = Category.valueOf(
                        resultSet.getString("category")
                );

                LocalDateTime deadline = LocalDateTime.parse(
                        resultSet.getString("deadline")
                );

                int durationMinutes =
                        resultSet.getInt("duration_minutes");

                Weights importance = Weights.valueOf(
                        resultSet.getString("importance")
                );

                Weights effort = Weights.valueOf(
                        resultSet.getString("effort")
                );

                TaskStatus status = TaskStatus.valueOf(
                        resultSet.getString("status")
                );

                // Rebuild the Task using its existing database ID.
                Task task = new Task(
                        id,
                        title,
                        description,
                        category,
                        deadline,
                        durationMinutes,
                        importance,
                        effort,
                        status
                );

                tasks.add(task);
            }

            return tasks;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not retrieve tasks from database.",
                    e
            );
        }
    }
}