// Sisa (comments are for me not to forget design choices)
package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import java.sql.ResultSet;

import utils.Category;
import utils.TaskStatus;
import utils.Weights;

import java.time.LocalDateTime;

import models.Task;
/**
 * Repository responsible for storing and retrieving tasks.
 *
 * Task data is persisted in an SQLite database using JDBC.
 * This class provides CRUD operations for Task objects.
 */
public class TaskDB {
    

    public TaskDB() {
        DatabaseConnection.initializeDatabase();
    }

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
     * Removes a task from the SQLite database using its ID.
     *
     * @param id the ID of the task to remove
     */
    public void remove(String id) {

        String sql = "DELETE FROM tasks WHERE id = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, id);

            int rowsDeleted = statement.executeUpdate();

            // If no row was deleted, the task does not exist.
            if (rowsDeleted == 0) {
                throw new NoSuchElementException(
                        "No task with id " + id
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not remove task from database.",
                    e
            );
        }
    }

    /**
     * Updates an existing task in the SQLite database.
     *
     * @param task the task containing the updated values
     */
    public void update(Task task) {

        String sql =
                "UPDATE tasks SET " +
                "title = ?, " +
                "description = ?, " +
                "category = ?, " +
                "deadline = ?, " +
                "duration_minutes = ?, " +
                "importance = ?, " +
                "effort = ?, " +
                "status = ? " +
                "WHERE id = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getCategory().name());
            statement.setString(4, task.getDeadline().toString());
            statement.setInt(5, task.getDurationMinutes());
            statement.setString(6, task.getImportance().name());
            statement.setString(7, task.getEffort().name());
            statement.setString(8, task.getStatus().name());

            // The ID identifies which existing task must be updated.
            statement.setString(9, task.getId());

            int rowsUpdated = statement.executeUpdate();

            // If no row was updated, the task does not exist.
            if (rowsUpdated == 0) {
                throw new NoSuchElementException(
                        "No task with id " + task.getId()
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not update task in database.",
                    e
            );
        }
    }


    /**
     * Finds a task in the database using its unique ID.
     *
     * @param id the ID of the task to find
     * @return the matching Task, or null if no task exists with that ID
     */
    public Task findById(String id) {

        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapToTask(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not retrieve task from database.",
                    e
            );
        }
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
                tasks.add(mapToTask(resultSet));
            }

            return tasks;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not retrieve tasks from database.",
                    e
            );
        }
    }
    
    /**
     * Converts the current row of a ResultSet into a Task object.
     *
     * @param resultSet the database result containing task data
     * @return a Task created from the current database row
     * @throws SQLException if the database values cannot be read
     */
    private Task mapToTask(ResultSet resultSet) throws SQLException {

        return new Task(
                resultSet.getString("id"),
                resultSet.getString("title"),
                resultSet.getString("description"),
                Category.valueOf(resultSet.getString("category")),
                LocalDateTime.parse(resultSet.getString("deadline")),
                resultSet.getInt("duration_minutes"),
                Weights.valueOf(resultSet.getString("importance")),
                Weights.valueOf(resultSet.getString("effort")),
                TaskStatus.valueOf(resultSet.getString("status"))
        );
    }
}