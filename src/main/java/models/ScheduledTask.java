package models;


/**
 * Represents one task after the scheduler has assigned a start and end time.
 */
import java.time.Duration;
import java.time.LocalDateTime;

public class ScheduledTask {

  private final Task task;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;

  public ScheduledTask(Task task, LocalDateTime startTime, LocalDateTime endTime) {
    this.task = task;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public Task getTask() {
    return task;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public int getDurationMinutes() {
    return task.getDurationMinutes();
  }

  /**
   * True when the task finishes after its deadline.
   */
  public boolean hasDeadlineClash() {
    return endTime.isAfter(task.getDeadline());
  }

  /**
   * Number of minutes between the task's end time and its deadline.
   *
   * Positive = completed before deadline.
   * Zero     = completes exactly at deadline.
   * Negative = finishes after deadline.
   */
  public long getDeadlineSlackMinutes() {
    return Duration.between(endTime, task.getDeadline()).toMinutes();
  }

  @Override
  public String toString(){
    String taskString = this.task.getTitle()+" | Category: " + this.task.getCategory()+ " | Deadline: " + this.task.getDeadline() + " | Start Time: " + this.startTime + " | End Time:" + this.endTime;

    return taskString;
  }
}