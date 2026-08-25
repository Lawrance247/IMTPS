package strategies;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Task;
import utils.Category;

public class WeightedFocusStrategy implements SchedulingStrategy {

    // Weight given to each factor in the final score.
    private static final double FOCUS_WEIGHT = 0.40;
    private static final double IMPORTANCE_WEIGHT = 0.30;
    private static final double URGENCY_WEIGHT = 0.20;
    private static final double EFFORT_WEIGHT = 0.10;

    private final Category focusMode;

    public WeightedFocusStrategy(Category focusMode) {
        this.focusMode = focusMode;
    }

    @Override
    public List<Task> generateSchedule(List<Task> tasks, LocalDateTime referenceTime) {

        // Create a copy so the original task list is not changed.
        List<Task> schedule = new ArrayList<>(tasks);

        // Higher weighted scores are scheduled first.
        schedule.sort(
            Comparator
                .<Task>comparingDouble(task -> calculateScore(task, referenceTime))
                .reversed()
        );

        return schedule;
    }

    /*
     * Calculate the final weighted score for a task.
     *
     * The score considers:
     * 1. Focus match
     * 2. Importance
     * 3. Urgency
     * 4. Effort
     */
    private double calculateScore(Task task, LocalDateTime referenceTime) {

        double focusScore = calculateFocusScore(task);
        double importanceScore = task.getImportance().getWeight();
        double urgencyScore = calculateUrgencyScore(task, referenceTime);
        double effortScore = task.getEffort().getWeight();

        return (focusScore * FOCUS_WEIGHT)
             + (importanceScore * IMPORTANCE_WEIGHT)
             + (urgencyScore * URGENCY_WEIGHT)
             + (effortScore * EFFORT_WEIGHT);
    }

    /*
     * A task receives a higher focus score when its category
     * matches the user's selected focus mode.
     */
    private double calculateFocusScore(Task task) {

        if (task.getCategory().name().equals(focusMode.name())) {
            return 3;
        }

        return 1;
    }

    /*
     * Calculate urgency using the amount of time remaining
     * before the deadline.
     *
     * 3 = very urgent
     * 2 = moderately urgent
     * 1 = less urgent
     */
    private double calculateUrgencyScore(Task task, LocalDateTime referenceTime) {

        // LocalDateTime now = referenceTime;

        // Overdue tasks receive the highest urgency.
        if (task.getDeadline().isBefore(referenceTime)) {
            return 3;
        }

        Duration remaining = Duration.between(referenceTime, task.getDeadline());

        long hoursRemaining = remaining.toHours();

        if (hoursRemaining <= 24) {
            return 3;
        } else if (hoursRemaining <= 72) {
            return 2;
        } else {
            return 1;
        }
    }

    @Override
    public String getStrategyName(){
        return "Weighted Focus Strategy";
    }
}