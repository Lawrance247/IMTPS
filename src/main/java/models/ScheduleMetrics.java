package models;

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
