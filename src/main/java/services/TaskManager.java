package services;

import java.time.LocalDateTime;
import java.util.List;

import models.Task;
import repositories.TaskDB;
import utils.Category;
import utils.Weights;



/**
 * Validates and coordinates all {@link Task} CRUD operations on behalf of the controllers, delegating actual storage to {@link TaskDB}.
 *
 * <p>This is the only class controllers should go through to create, read, update, or delete tasks — it's the layer that guarantees a task
 * can never be persisted with a null/blank required field, a non-positive duration, or a deadline in the past.</p>
 *
 * <p><b>Collaborators:</b> a single shared instance is held by {@code app.App}; wraps one {@link TaskDB}; called directly by
 * {@code controllers.MainController} and {@code controllers.TaskFormController}.</p>
 */
public class TaskManager 
{
    private final TaskDB taskDB;

    /**
     * @param taskDB the store to delegate persistence to (injected rather
     *               than constructed here, so a test double can be swapped
     *               in for unit tests)
     */
    public TaskManager(TaskDB taskDB) 
    {
        // not full dependancy, TaskDB already exists and gets shared; for unit testing swap in a fake db here
        this.taskDB = taskDB;
    }

    public TaskManager(){
        this.taskDB = new TaskDB(); //added by Sinenkhosi
    }



    /**
     * Validates the given fields, builds a new {@link Task}, and persists it.
     *
     * @param title           short display name
     * @param description     longer free-text description
     * @param category        which {@link Category} the task belongs to
     * @param deadline        must not be null or in the past
     * @param durationMinutes must be positive
     * @param effort          how much effort the task takes
     * @param importance      how important the task is
     * @return the newly created, already-persisted task
     * @throws IllegalArgumentException if any field fails validation (see {@link #validateDeadline} / {@link #validateTaskFields})
     */
    public Task createTask(String title, String description, Category category, LocalDateTime deadline, int durationMinutes, Weights importance, Weights effort) 
    {
        validateDeadline(deadline);
        validateTaskFields(title, category, durationMinutes, effort, importance);
        Task task = new Task(title, description, category, deadline, durationMinutes, effort, importance);
        taskDB.add(task);
        return task;
    }


    /**
     * @param id the task ID to look up
     * @return the matching task, or {@code null} if none exists
     */
    public Task getTaskById(String id) 
    {
    return taskDB.findById(id);
    }


    /**
     * Re-validates an already-modified task (deadline and required fields) and persists the change.
     *
     * @param task the task to save, with its fields already updated by the caller
     * @throws IllegalArgumentException if any field fails validation
     */
    public void updateTask(Task task) 
    {
        validateDeadline(task.getDeadline());
        validateTaskFields(task.getTitle(), task.getCategory(), task.getDurationMinutes(), task.getImportance(), task.getEffort());
        taskDB.update(task);
    }


    /** @param id the ID of the task to delete */
    public void deleteTask(String id) 
    {
        taskDB.remove(id);
    }


    /** @return every currently stored task, as a fresh list (see {@link TaskDB#findAll()}) */
    public List<Task> getAllTasks() 
    {
        return taskDB.findAll();
    }


    
    /**
     * helper which helps reject past deadlines
     * 
     * @param deadline the deadline to check
     * @throws IllegalArgumentException if {@code deadline} is {@code null} or before the current time
     */
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


    
    /**
     * Checks the remaining required fields (everything except deadline, which {@link #validateDeadline} covers separately).
     *
     * @throws IllegalArgumentException if title is null/blank, category is
     *         null, duration isn't positive, or either weight is null
     */
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
