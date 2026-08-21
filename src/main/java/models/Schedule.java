  /**
   * Represents the complete generated schedule.
   */
package models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Schedule {

  private final List<ScheduledTask> scheduledTasks;

  public Schedule(List<ScheduledTask> scheduledTasks) {

    this.scheduledTasks = Collections.unmodifiableList(new ArrayList<>(scheduledTasks));
  }

  public List<ScheduledTask> getScheduledTasks() {
    return scheduledTasks;
  }

  /**
   * Returns all tasks that finish after their deadlines.
   */
  public List<ScheduledTask> getDeadlineClashes() {

    List<ScheduledTask> clashes = new ArrayList<>();

    for (ScheduledTask task : scheduledTasks) {

      if (task.hasDeadlineClash()) {
        clashes.add(task);
      }
    }

    return Collections.unmodifiableList(clashes);
  }

  public int getDeadlineClashCount() {
    return getDeadlineClashes().size();
  }

  /**
   * Total duration of work scheduled.
   */
  public long getTotalScheduledMinutes() {

    long total = 0;

    for (ScheduledTask task : scheduledTasks) {
      total += task.getDurationMinutes();
    }

    return total;
  }

  public int getTaskCount() {
    return scheduledTasks.size();
  }
}