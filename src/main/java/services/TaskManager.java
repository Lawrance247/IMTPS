package services;

import java.time.LocalDateTime;
import java.util.List;

import models.Task;
import repositories.TaskDB;
import utils.Category;
import utils.Weights;

public class TaskManager 
{
    private final TaskDB taskDB;

    // not full dependancy, TaskDB already exists and gets shared; for unit testing swap in a fake db here
    public TaskManager(TaskDB taskDB) 
    {
        this.taskDB = taskDB;
    }


    public Task createTask(String title, String description, Category category, LocalDateTime deadline, int durationMinutes, Weights importance, Weights effort) 
    {
        validateDeadline(deadline);
        validateTaskFields(title, category, durationMinutes, effort, importance);
        Task task = new Task(title, description, category, deadline, durationMinutes, effort, importance);
        taskDB.add(task);
        return task;
    }

    public Task getTaskById(String id) 
    {
    return taskDB.findById(id);
    }

    public void updateTask(Task task) 
    {
        validateDeadline(task.getDeadline());
        validateTaskFields(task.getTitle(), task.getCategory(), task.getDurationMinutes(), task.getImportance(), task.getEffort());
        taskDB.update(task);
    }

    public void deleteTask(String id) 
    {
        taskDB.remove(id);
    }

    public List<Task> getAllTasks() 
    {
        return taskDB.findAll();
    }

    // helper which helps reject past deadlines
    private void validateDeadline(LocalDateTime deadline) 
    {
        if (deadline == null) 
        {
            throw new IllegalArgumentException("Deadline cannot be null");
        }
        if (deadline.isBefore(LocalDateTime.now())) 
        {
            throw new IllegalArgumentException("Deadline cannot be in the past");
        }
    }

    // helper which validates task fields
    private void validateTaskFields(String title, Category category, int durationMinutes, Weights effort, Weights importance) {
    if (title == null || title.isBlank()) 
    {
        throw new IllegalArgumentException("Title cannot be empty");
    }
    if (category == null) 
    {
        throw new IllegalArgumentException("Category cannot be null");
    }
    if (durationMinutes <= 0) 
    {
        throw new IllegalArgumentException("Duration must be positive");
    }
    if (effort == null)
    {
        throw new IllegalArgumentException("Field cannot be empty");
    }

    if (importance == null)
    {
        throw new IllegalArgumentException("Field cannot be empty");
    }
}

    
}
