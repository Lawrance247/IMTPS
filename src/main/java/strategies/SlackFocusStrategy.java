package strategies;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Task;
import utils.Category;

/**
 * Prioritises tasks using slack time together with the selected focus category.
 * Critical tasks are always placed before non-critical tasks.
 */
public class SlackFocusStrategy implements SchedulingStrategy {
    private static final double URGENCY_WEIGHT = 0.60;
    private static final double FOCUS_WEIGHT = 0.40;
    private static final double MAX_REASONABLE_SLACK_RATIO = 3.0;

    private Category focusMode;

    public SlackFocusStrategy(Category focusMode) {
        this.focusMode = focusMode;
    }

    public SlackFocusStrategy() {
        this.focusMode = Category.ACADEMIC;
    }

    @Override
    public List<Task> generateSchedule(List<Task> tasks, LocalDateTime referenceTime) {
        List<Task> schedule = new ArrayList<>(tasks);

        schedule.sort(
            Comparator
                .comparing((Task task) -> isCritical(task, referenceTime) ? 0 : 1)
                .thenComparing(
                    Comparator.<Task>comparingDouble(
                        task -> calculateScore(task, referenceTime)).reversed())
                .thenComparingDouble(
                    task -> calculateSlackRatio(task, referenceTime))
                .thenComparing(Task::getDeadline)
                .thenComparing(
                    Comparator.<Task, Integer>comparing(
                        task -> task.getImportance().getWeight()).reversed())
        );

        return schedule;
    }

    private boolean isCritical(Task task, LocalDateTime referenceTime) {
        return calculateSlackMinutes(task, referenceTime) <= 0;
    }

    private long calculateSlackMinutes(Task task, LocalDateTime referenceTime) {
        Duration remaining = Duration.between(referenceTime, task.getDeadline());
        return remaining.toMinutes() - task.getDurationMinutes();
    }

    private double calculateSlackRatio(Task task, LocalDateTime referenceTime) {
        int duration = task.getDurationMinutes();

        if (duration <= 0) {
            return calculateSlackMinutes(task, referenceTime) <= 0
                ? -MAX_REASONABLE_SLACK_RATIO
                : MAX_REASONABLE_SLACK_RATIO;
        }

        return (double) calculateSlackMinutes(task, referenceTime) / duration;
    }

    private double calculateScore(Task task, LocalDateTime referenceTime) {
        double urgencyScore = calculateUrgencyScore(task, referenceTime);
        double focusScore = calculateFocusScore(task);

        return (urgencyScore * URGENCY_WEIGHT) + (focusScore * FOCUS_WEIGHT);
    }

    private double calculateUrgencyScore(Task task, LocalDateTime referenceTime) {
        double ratio = calculateSlackRatio(task, referenceTime);

        double clamped = Math.max(
            -MAX_REASONABLE_SLACK_RATIO,
            Math.min(MAX_REASONABLE_SLACK_RATIO, ratio));

        return 1.0 - ((clamped + MAX_REASONABLE_SLACK_RATIO)
            / (2 * MAX_REASONABLE_SLACK_RATIO));
    }

    private double calculateFocusScore(Task task) {
        return task.getCategory() == focusMode ? 1.0 : 0.0;
    }

    public Category getFocusMode() {
        return focusMode;
    }

    public void setFocusMode(Category mode) {
        this.focusMode = mode;
    }

    @Override
    public String getStrategyName() {
        return "Slack Focus Strategy";
    }
}
