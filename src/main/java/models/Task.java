package models;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import utils.Category;
import utils.TaskStatus;
import utils.Weights;  // Universal Unique Id (128 bit values, type String): exmaple f47ac10b-58cc-4372-a567-0e02b2c3d479



/**
 * A single to-do item as entered by the user, before any scheduling happens.
 *
 * <p>{@code Task} is the app's core mutable domain object — everything else (schedules, comparisons, metrics) is derived from a list of these. It
 * carries its own identity ({@link #id}, generated once at construction and never changed) plus the fields the scheduling strategies read: deadline,
 * duration, {@link Category}, and the two independent {@link Weights} dimensions {@link #importance} and {@link #effort}.</p>
 *
 * <p><b>Collaborators:</b> created and mutated by {@code services.TaskManager} (on behalf of {@code controllers.TaskFormController}); stored by
 * {@code repositories.TaskDB}; read by {@code strategies.EarliestDeadlineFirstStrategy} and {@code strategies.WeightedFocusStrategy} to decide
 * ordering; wrapped by {@link ScheduledTask} once the scheduler assigns it a time slot.</p>
 */
public class Task 
{
   private final String id;
    private String title;
    private String description;
    private Category category;
    private LocalDateTime deadline;
    private int durationMinutes;
    private Weights effort;
    private TaskStatus status;
    private Weights importance;


    /**
     * Creates a new task with a freshly generated {@link #id} and
     * {@link TaskStatus#TO_DO} status.
     *
     * @param title           short display name
     * @param description     longer free-text description
     * @param category        which {@link Category} this task belongs to
     * @param deadline        when the task must be finished by
     * @param durationMinutes how long the task is expected to take
     * @param importance      how important the task is (independent of effort)
     * @param effort          how much effort the task takes (independent of importance)
     */
    public Task(String title, String description, Category category,
                LocalDateTime deadline, int durationMinutes,
                Weights importance, Weights effort)
    {
        this.id = UUID.randomUUID().toString(); //convert to string for easier storage and comparison
        this.title = title;
        this.description = description;
        this.category = category;
        this.deadline = deadline;
        this.importance = importance;
        this.effort = effort;
        this.durationMinutes = durationMinutes; 

        this.status = TaskStatus.TO_DO; // new task defaults to TO_DO
    }


    /**
     * @return {@code true} if the task isn't marked {@link TaskStatus#DONE}
     *         and its deadline has already passed (compared against the
     *         current system time).
     */
    public boolean isOverdue()
    {
        return (status != TaskStatus.DONE && deadline.isBefore(LocalDateTime.now()));
    }


    /**
     * @return the time remaining until {@link #deadline}, measured from now.
     *         Negative if the deadline has already passed.
     */
    public Duration timeUntilDeadline()
    {
        return Duration.between(LocalDateTime.now(), deadline);
    }

    //getters
    public String getId() 
    { 
        return id; 
    }
    public String getTitle() 
    { 
        return title; 
    }
    public String getDescription() 
    { 
        return description; 
    }
    public Category getCategory() 
    { 
        return category; 
    }
    public LocalDateTime getDeadline() 
    { 
        return deadline; 
    }
    public int getDurationMinutes() 
    { 
        return durationMinutes; 
    }
   public Weights getEffort()
    {
        return effort;
    }

    public Weights getImportance()
    {
        return importance;
    }
   
    public TaskStatus getStatus() 
    { 
        return status; 
    }
    // setters
    public void setTitle(String title) 
    { 
        this.title = title; 
    }
    public void setDescription(String description) 
    { 
        this.description = description; 
    }
    public void setCategory(Category category)
    { 
        this.category = category; 
    }
    public void setDeadline(LocalDateTime deadline) 
    { 
        this.deadline = deadline;
    }
    public void setDurationMinutes(int durationMinutes) 
    { 
        this.durationMinutes = durationMinutes; 
    }
    public void setEffort(Weights effort)
    {
        this.effort = effort;
    }

    public void setImportance(Weights importance)
    {
        this.importance = importance;
    }
    public void setStatus(TaskStatus status) 
    { 
        this.status = status; 
    }

    @Override
    public String toString()
    {
        return "Task{id=" + id + ", title='" + title + "', status=" + status + ", deadline=" + deadline + "}";
    
    }

}
