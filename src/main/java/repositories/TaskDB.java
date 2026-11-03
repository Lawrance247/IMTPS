// Sisa (comments are for me not to forget design choices)
package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import models.Task;
import utils.Category;
import utils.TaskStatus;
import utils.Weights;

/**
 * Repository responsible for storing and retrieving tasks.
 */
public class TaskDB {

    public TaskDB() {
        DatabaseConnection.initializeDatabase();
    }

    /** Existing behaviour kept until the API is connected to authenticated users. */
    public void add(Task task) {
        add(task, null);
    }

    /** Saves a task for a specific user. */
    public void add(Task task, String userId) {
        String sql =
            "INSERT INTO tasks " +
            "(id, user_id, title, description, category, deadline, " +
            "duration_minutes, importance, effort, status) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getId());
            statement.setString(2, userId);
            statement.setString(3, task.getTitle());
            statement.setString(4, task.getDescription());
            statement.setString(5, task.getCategory().name());
            statement.setString(6, task.getDeadline().toString());
            statement.setInt(7, task.getDurationMinutes());
            statement.setString(8, task.getImportance().name());
            statement.setString(9, task.getEffort().name());
            statement.setString(10, task.getStatus().name());
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Could not save task to database.", e);
        }
    }

    public void remove(String id) {
        String sql = "DELETE FROM tasks WHERE id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            ensureChanged(statement.executeUpdate(), id);
        } catch (SQLException e) {
            throw new RuntimeException("Could not remove task from database.", e);
        }
    }

    /** Deletes only a task owned by the supplied user. */
    public void remove(String id, String userId) {
        String sql = "DELETE FROM tasks WHERE id = ? AND user_id = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            statement.setString(2, userId);
            ensureChanged(statement.executeUpdate(), id);
        } catch (SQLException e) {
            throw new RuntimeException("Could not remove task from database.", e);
        }
    }

    public void update(Task task) {
        updateInternal(task, null, false);
    }

    /** Updates only a task owned by the supplied user. */
    public void update(Task task, String userId) {
        updateInternal(task, userId, true);
    }

    private void updateInternal(Task task, String userId, boolean checkOwner) {
        String sql =
            "UPDATE tasks SET title = ?, description = ?, category = ?, deadline = ?, " +
            "duration_minutes = ?, importance = ?, effort = ?, status = ? " +
            "WHERE id = ?" + (checkOwner ? " AND user_id = ?" : "");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, task.getTitle());
            statement.setString(2, task.getDescription());
            statement.setString(3, task.getCategory().name());
            statement.setString(4, task.getDeadline().toString());
            statement.setInt(5, task.getDurationMinutes());
            statement.setString(6, task.getImportance().name());
            statement.setString(7, task.getEffort().name());
            statement.setString(8, task.getStatus().name());
            statement.setString(9, task.getId());
            if (checkOwner) statement.setString(10, userId);

            ensureChanged(statement.executeUpdate(), task.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Could not update task in database.", e);
        }
    }

    public Task findById(String id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        return findOne(sql, id, null);
    }

    /** Finds a task only when it belongs to the supplied user. */
    public Task findById(String id, String userId) {
        String sql = "SELECT * FROM tasks WHERE id = ? AND user_id = ?";
        return findOne(sql, id, userId);
    }

    private Task findOne(String sql, String id, String userId) {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);
            if (userId != null) statement.setString(2, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? mapToTask(resultSet) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve task from database.", e);
        }
    }

    public List<Task> findAll() {
        return findAllInternal(null, false);
    }

    /** Returns only tasks belonging to the supplied user. */
    public List<Task> findAll(String userId) {
        return findAllInternal(userId, true);
    }

    private List<Task> findAllInternal(String userId, boolean checkOwner) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks" + (checkOwner ? " WHERE user_id = ?" : "");

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (checkOwner) statement.setString(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) tasks.add(mapToTask(resultSet));
            }
            return tasks;
        } catch (SQLException e) {
            throw new RuntimeException("Could not retrieve tasks from database.", e);
        }
    }

    private void ensureChanged(int rowsChanged, String id) {
        if (rowsChanged == 0) {
            throw new NoSuchElementException("No task with id " + id);
        }
    }

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
