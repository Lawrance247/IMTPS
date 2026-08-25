package models;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import utils.Category;
import utils.TaskStatus;
import utils.Weights;  // Universal Unique Id (128 bit values, type String): exmaple f47ac10b-58cc-4372-a567-0e02b2c3d479




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

    public boolean isOverdue()
    {
        return (status != TaskStatus.DONE && deadline.isBefore(LocalDateTime.now()));
    }
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
