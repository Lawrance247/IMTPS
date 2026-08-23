package services;

import java.util.List;

import models.ComparisonResult;
import models.Schedule;
import models.ScheduleMetrics;
import models.ScheduledTask;
import models.Task;
import utils.FocusMode;

public class Comparison {

  private final Schedule scheduleA;
  private final Schedule scheduleB;
  private final FocusMode focusMode;

  public Comparison(Schedule scheduleA, Schedule scheduleB, FocusMode focusMode) {
    this.scheduleA = scheduleA;
    this.scheduleB = scheduleB;
    this.focusMode = focusMode;
  }

  public ComparisonResult compare() {
    return new ComparisonResult(calculateMetrics(scheduleA), calculateMetrics(scheduleB));
  }

  private ScheduleMetrics calculateMetrics(Schedule schedule) {
    int deadlineClashes = schedule.getDeadlineClashCount();

    long totalLateness = 0;
    long totalSlack = 0;
    long weightedLateness = 0;

    for (ScheduledTask scheduledTask : schedule.getScheduledTasks()) {

      long slack = scheduledTask.getDeadlineSlackMinutes();
      totalSlack += slack;

      
      // Negative slack means the task finishes after its deadline.
      if (slack < 0) {

        long lateness = -slack;

        totalLateness += lateness;

        // How badly did the schedule fail when we consider how important the late tasks were? -> low is better.
        weightedLateness += lateness * scheduledTask.getTask().getImportance().getWeight();

      }
    }

    int taskCount = schedule.getTaskCount();

    // how much spare time on average a task has before its deadline? -> higher is better.
    double averageSlack = (taskCount == 0) ? 0 : (double) totalSlack / taskCount;

    double focusAlignment = calculateFocusAlignment(schedule);

    return new ScheduleMetrics(deadlineClashes, totalLateness, averageSlack, weightedLateness, focusAlignment);
  }

  private double calculateFocusAlignment(Schedule schedule) {
    if (schedule.getTaskCount() == 0) {
      return 0;
    }

    // Consider the first half of the schedule. This lets us measure whether the strategy puts focus-relevant tasks earlier.
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
