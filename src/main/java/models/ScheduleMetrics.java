package models;


/**
 * Immutable value object holding the computed quality metrics for a single
 * {@link Schedule}.
 *
 * <p>Instances are produced exclusively by {@code services.Comparison} (specifically its private {@code calculateMetrics(Schedule)} method) — this
 * class does no calculation itself, it only stores the results. Two {@code ScheduleMetrics} objects (one per strategy) are bundled together
 * inside a {@link ComparisonResult}.</p>
 *
 * <p><b>Collaborators:</b> created by {@code services.Comparison}; carried by {@link ComparisonResult}; read by {@code
 * controllers.ComparisonController} to populate {@link MetricRow} rows for the metrics table.</p>
 *
 * <p><b>Fields, what they mean, and which direction is "better":</b></p>
 * <ul>
 *   <li>{@code deadlineClashes} — count of tasks finishing after their
 *       deadline. Lower is better.</li>
 *   <li>{@code totalLateness} — total minutes by which late tasks overran
 *       their deadlines, summed. Lower is better.</li>
 *   <li>{@code averageSlack} — average minutes of slack (deadline minus
 *       finish time) per task, including negative values for late tasks.
 *       Higher is better.</li>
 *   <li>{@code weightedLateness} — lateness weighted by each late task's
 *       importance, so being late on a HIGH-importance task counts for more
 *       than being late on a LOW-importance one. Lower is better.</li>
 *   <li>{@code focusAlignment} — percentage indicating how much of the
 *       importance-weighted early half of the schedule matches the selected
 *       focus category. Higher is better.</li>
 * </ul>
 */
public class ScheduleMetrics {

  private final int deadlineClashes;
  private final long totalLateness;
  private final double averageSlack;
  private final long weightedLateness;
  private final double focusAlignment;

  public ScheduleMetrics(int deadlineClashes, long totalLateness, double averageSlack, long weightedLateness, double focusAlignment) {
    this.deadlineClashes = deadlineClashes;
    this.totalLateness = totalLateness;
    this.averageSlack = averageSlack;
    this.weightedLateness = weightedLateness;
    this.focusAlignment = focusAlignment;
  }

  public int getDeadlineClashes() {
    return deadlineClashes;
  }

  public long getTotalLateness() {
    return totalLateness;
  }

  public double getAverageSlack() {
    return averageSlack;
  }

  public long getWeightedLateness() {
    return weightedLateness;
  }

  public double getFocusAlignment() {
    return focusAlignment;
  }

  @Override
  public String toString(){
    String metrics = "Deadline Clash: "+this.deadlineClashes+", Total Lateness: "+this.totalLateness+", Average Slack: "+this.averageSlack+", Weighted Lateness: "+this.weightedLateness+", Focus Alignment: "+this.focusAlignment;
    return metrics;
  }
}
