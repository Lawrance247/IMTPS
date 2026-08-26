package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Represents the complete generated schedule.
 *
 * <p>A {@code Schedule} is an immutable wrapper around a list of {@link ScheduledTask}s produced by {@code services.Scheduler#generateSchedule}.
 * It does not perform any scheduling itself — it is a read-only result object that also exposes a few convenience calculations (deadline
 * clashes, total scheduled time) that are used both by the UI (controllers) and by {@code services.Comparison} when computing {@link
 * ScheduleMetrics}.</p>
 *
 * <p><b>Collaborators:</b> built by {@code services.Scheduler}; read by {@code controllers.MainController}, {@code
 * controllers.ComparisonController}, and {@code services.Comparison}; contains {@link ScheduledTask} instances, each
 * of which wraps a {@link Task}.</p>
 */
public class Schedule {
  /**
   * Stores the list of scheduled tasks
   */
  private final List<ScheduledTask> scheduledTasks;
  private final List<ScheduledTask> scheduleClashes;
  private final long totalScheduledMinutes;


  /**
   * Creates a schedule from an already-ordered list of scheduled tasks.
   *
   * <p>The incoming list is defensively copied and wrapped as unmodifiable, so {@code Schedule} instances are effectively immutable once built —
   * callers cannot mutate the schedule after the fact, and mutating the original list passed in has no effect on this schedule.</p>
   *
   * @param scheduledTasks The list of scheduled tasks.
   *
   */
  public Schedule(List<ScheduledTask> scheduledTasks) {

    this.scheduledTasks = Collections.unmodifiableList(new ArrayList<>(scheduledTasks));
    this.scheduleClashes = searchForDeadlineClashes();
    this.totalScheduledMinutes = calculateTotalScheduledMinutes();
  }


  /**
   * @return an unmodifiable view of every task in this schedule, in the
   *         order the scheduler placed them.
   */
  public List<ScheduledTask> getScheduledTasks() {
    return scheduledTasks;
  }


  /**
   * @return all tasks that finish after their deadlines.
   */
  public List<ScheduledTask> getDeadlineClashes() {
    return scheduleClashes;
  }


  /**
   * Searches for all tasks that finish after their deadlines.
   * 
   * @ return all found clashes.
   */
  private List<ScheduledTask> searchForDeadlineClashes(){
    List<ScheduledTask> clashes = new ArrayList<>();

    for (ScheduledTask task : scheduledTasks) {

      if (task.hasDeadlineClash()) {
        clashes.add(task);
      }
    }

    return Collections.unmodifiableList(clashes);
  }


  /**
   * @return the number of scheduled tasks whose end time is after their
   *         deadline. Convenience wrapper around.
   */
  public int getDeadlineClashCount() {
    return this.scheduleClashes.size();
  }

  /**
   * Total duration of work scheduled.
   *
   * @return the sum, in minutes, of every scheduled task's duration.
   */
  public long getTotalScheduledMinutes() {
    return this.totalScheduledMinutes;
  }


  /**
   * Calculates total duration of work scheduled.
   *
   * @return the sum, in minutes, of every scheduled task's duration.
   */
  private long calculateTotalScheduledMinutes(){
    long total = 0;

    for (ScheduledTask task : scheduledTasks) {
      total += task.getDurationMinutes();
    }

    return total;
  }


  /**
   * @return how many tasks are in this schedule.
   */
  public int getTaskCount() {
    return scheduledTasks.size();
  }
}