package services;

import java.util.List;

import models.ComparisonResult;
import models.Schedule;
import models.ScheduleMetrics;
import models.ScheduledTask;
import models.Task;
import utils.Category;


/**
 * Computes and compares quality metrics for two generated {@link Schedule}s.
 *
 * <p>{@code Comparison} is created fresh for a single comparison (Schedule A vs Schedule B, evaluated against one focus mode) — it is
 * not a shared or long-lived service like {@code TaskManager} or {@code Scheduler}. Given both schedules it computes independent
 * {@link ScheduleMetrics} for each and hands them back as a {@link ComparisonResult}.</p>
 *
 * <p><b>Collaborators:</b> constructed and invoked by {@code controllers.ComparisonController}, which supplies two{@link Schedule}s
 * (typically one generated with {@code strategies.EarliestDeadlineFirstStrategy} and one with {@code strategies.WeightedFocusStrategy})
 * plus the user's selected {@link Category focus mode}; reads {@link ScheduledTask} and {@link Task} data out of each schedule; produces
 * {@link ScheduleMetrics} and {@link ComparisonResult}.</p>
 */
public class Comparison {

  private final Schedule scheduleA;
  private final Schedule scheduleB;
  private final Category focusMode;


  /**
   * @param scheduleA the first schedule to compare (displayed/labelled as "Strategy A")
   * @param scheduleB the second schedule to compare (displayed/labelled as "Strategy B")
   * @param focusMode the focus category used to score {@link #calculateFocusAlignment(Schedule)}
   */
  public Comparison(Schedule scheduleA, Schedule scheduleB, Category focusMode) {
    this.scheduleA = scheduleA;
    this.scheduleB = scheduleB;
    this.focusMode = focusMode;
  }


  /**
   * Computes metrics for both schedules independently.
   *
   * @return a {@link ComparisonResult} wrapping schedule A's and schedule B's
   *         {@link ScheduleMetrics}.
   */
  public ComparisonResult compare() {
    return new ComparisonResult(calculateMetrics(scheduleA), calculateMetrics(scheduleB));
  }


  /**
   * Derives every {@link ScheduleMetrics} field for one schedule: deadline clash count, total lateness, average slack, importance-weighted
   * lateness, and focus alignment.
   *
   * @param schedule the schedule to score
   * @return the computed metrics for that schedule
   */
  private ScheduleMetrics calculateMetrics(Schedule schedule) {
    int deadlineClashes = schedule.getDeadlineClashCount();

    long totalLateness = 0;
    long totalSlack = 0;
    long weightedLateness = 0;

    for (ScheduledTask scheduledTask : schedule.getScheduledTasks()) {

      long slack = scheduledTask.getDeadlineSlackMinutes();
      totalSlack += slack;

      
      /** Negative slack means the task finishes after its deadline. */
      if (slack < 0) {

        long lateness = -slack;

        totalLateness += lateness;

        /** How badly did the schedule fail when we consider how important the late tasks were? -> low is better. */
        weightedLateness += lateness * scheduledTask.getTask().getImportance().getWeight();

      }
    }

    int taskCount = schedule.getTaskCount();

    /** how much spare time on average a task has before its deadline? -> higher is better. */
    double averageSlack = (taskCount == 0) ? 0 : (double) totalSlack / taskCount;

    double focusAlignment = calculateFocusAlignment(schedule);

    return new ScheduleMetrics(deadlineClashes, totalLateness, averageSlack, weightedLateness, focusAlignment);
  }


  /**
   * Measures how well the earlier half of the schedule lines up with the selected focus category, weighted by task importance.
   *
   * <p>Only the first half of the schedule (rounded up) is considered, on the theory that a good focus-aware strategy should front-load
   * focus-relevant tasks rather than scatter them evenly. The result is the proportion of importance in that early half that belongs to
   * {@link #focusMode}, expressed as a percentage.</p>
   *
   * @param schedule the schedule to score
   * @return a percentage (0–100) — higher means the early tasks are more
   *         heavily weighted toward the focus category; 0 if the schedule is empty.
   */
  private double calculateFocusAlignment(Schedule schedule) {
    if (schedule.getTaskCount() == 0) {
      return 0;
    }

    /** Consider the first half of the schedule. This lets us measure whether the strategy puts focus-relevant tasks earlier. */
    int earlyTaskCount = Math.max(1, (int) Math.ceil(schedule.getTaskCount() / 2.0));

    double totalImportance = 0;
    double focusImportance = 0;

    List<ScheduledTask> tasks = schedule.getScheduledTasks();

    for (int i = 0; i < earlyTaskCount; i++) {

      Task task = tasks.get(i).getTask();

      double importance = task.getImportance().getWeight();

      totalImportance += importance;

      if (task.getCategory().name().equals(focusMode.name())) {
        focusImportance += importance;
      }
    }

    if (totalImportance == 0) {
      return 0;
    }

    return (focusImportance / totalImportance) * 100;
  } 
}
