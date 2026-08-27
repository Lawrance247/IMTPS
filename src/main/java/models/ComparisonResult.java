package models;


/**
 * Immutable pairing of the {@link ScheduleMetrics} produced for "Schedule A" and "Schedule B" in a single strategy comparison.
 *
 * <p>This is the return value of {@code services.Comparison#compare()}. It carries no logic of its own, it exists purely to move both metric sets
 * back to the caller ({@code controllers.ComparisonController}) as one object, instead of returning two separate values.</p>
 *
 * <p><b>Collaborators:</b> created by {@code services.Comparison}; consumed by {@code controllers.ComparisonController}, which reads both
 * {@link ScheduleMetrics} out of it to build the {@link MetricRow} list shown in the metrics table.</p>
 */
public class ComparisonResult {

  private final ScheduleMetrics scheduleAMetrics;
  private final ScheduleMetrics scheduleBMetrics;


  /**
   * @param scheduleAMetrics metrics computed for the first schedule being compared (Strategy A)
   * @param scheduleBMetrics metrics computed for the second schedule being compared (Strategy B)
   */
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
