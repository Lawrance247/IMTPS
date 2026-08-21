package models;

public class ComparisonResult {

  private final ScheduleMetrics scheduleAMetrics;
  private final ScheduleMetrics scheduleBMetrics;

  public ComparisonResult(ScheduleMetrics scheduleAMetrics, ScheduleMetrics scheduleBMetrics) {
    this.scheduleAMetrics = scheduleAMetrics;
    this.scheduleBMetrics = scheduleBMetrics;
  }

  public ScheduleMetrics getScheduleAMetrics() {
    return scheduleAMetrics;
  }

  public ScheduleMetrics getScheduleBMetrics() {
    return scheduleBMetrics;
  }
}
