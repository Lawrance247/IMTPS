package services;

import models.Task;
import repositories.TaskDB;
import utils.Category;
import utils.Effort;
import utils.Priority;

import java.time.LocalDateTime;
import java.util.List;

public class TaskManager {

    private final TaskDB taskDB;

    // TaskDB already exists and gets shared through the constructor.
    // This also makes it possible to replace TaskDB with a fake database
    // when unit testing.
    public TaskManager(TaskDB taskDB) {
        this.taskDB = taskDB;
    }

    public Task createTask(
            String title,
            String description,
            Category category,
            LocalDateTime deadline,
            int durationMinutes,
            Priority importance,
            Effort effort) {

        // Reject invalid deadlines before creating the task.
        validateDeadline(deadline);

        Task task = new Task(
                title,
                description,
                category,
                deadline,
                durationMinutes,
                importance,
                effort
        );

        taskDB.add(task);

        return task;
    }

    public Task getTaskById(String id) {
        return taskDB.findById(id);
    }

    public void updateTask(Task task) {

        // Make sure the updated task still has a valid deadline.
        validateDeadline(task.getDeadline());

        taskDB.update(task);
    }

    public void deleteTask(String id) {
        taskDB.remove(id);
    }

    public List<Task> getAllTasks() {
        return taskDB.findAll();
    }

    // Helper method that rejects deadlines that are null or in the past.
    private void validateDeadline(LocalDateTime deadline) {

        if (deadline == null) {
            throw new IllegalArgumentException(
                    "Deadline cannot be null"
            );
        }

        if (deadline.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "Deadline cannot be in the past"
            );
        }
    }
}