package models;

import java.time.Duration;
import java.time.LocalDateTime;


/**
 * Represents one task after the scheduler has assigned a start and end time.
 *
 * <p>A {@code ScheduledTask} pairs an existing {@link Task} with the concrete {@code startTime}/{@code endTime} window
 * {@code services.Scheduler} placed it in. It also knows how to evaluate itself against the underlying task's
 * deadline, which is what lets {@link Schedule} and {@code services.Comparison} report on deadline clashes and slack
 * without re-implementing that logic themselves.</p>
 *
 * <p><b>Collaborators:</b> created by {@code services.Scheduler}; held in a list inside {@link Schedule}; read by the table-rendering code in
 * {@code controllers.MainController} and {@code controllers.ComparisonController}; its lateness/slack numbers feed {@code services.Comparison}'s
 * {@link ScheduleMetrics} calculations.</p>
 */
public class ScheduledTask {

  private final Task task;
  private final LocalDateTime startTime;
  private final LocalDateTime endTime;


  /**
   * @param task      the underlying task being scheduled
   * @param startTime the time the scheduler assigned this task to begin
   * @param endTime   the time the scheduler assigned this task to finish
   */
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


  /**
   * @return the task's duration in minutes. Delegates to the underlying
   *         {@link Task} rather than deriving it from
   *         {@code startTime}/{@code endTime}, so it is always in sync with
   *         the task's own duration field even if the scheduled window is
   *         somehow shorter/longer.
   */
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