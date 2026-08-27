package strategies;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Task;
import utils.Category;


/**
 * Orders tasks by a weighted score combining four factors — focus-category match, importance, urgency, and effort, so that tasks relevant to the
 * user's chosen {@link #focusMode} tend to rise to the top. Used as "Strategy B" in the comparison view.
 *
 * <p>Unlike {@link EarliestDeadlineFirstStrategy}, this strategy holds mutable state ({@link #focusMode}) rather than being purely stateless,
 * see {@link #setFocusMode(Category)}.</p>
 *
 * <p><b>Collaborators:</b> a single shared instance is held by {@code app.App} as {@code App.wfStrategy}; its focus mode is set by
 * {@code controllers.MainController} (from the focus-mode dropdown) before {@code controllers.ComparisonController} generates a schedule with it;
 * used by {@code services.Scheduler}.</p>
 */
public class WeightedFocusStrategy implements SchedulingStrategy {

    // Weight given to each factor in the final score.
    private static final double FOCUS_WEIGHT = 0.40;
    private static final double IMPORTANCE_WEIGHT = 0.30;
    private static final double URGENCY_WEIGHT = 0.20;
    private static final double EFFORT_WEIGHT = 0.10;

    private Category focusMode; /** Sinenkhosi changed this to be modifiable */

    /** @param focusMode the category to weight tasks toward */
    public WeightedFocusStrategy(Category focusMode) {
        this.focusMode = focusMode;
    }


    /**
     * Creates the strategy with a default focus mode of {@link Category#ACADEMIC}, to be changed later via {@link #setFocusMode(Category)}.
     * This is the constructor {@code app.App} uses for its shared {@code wfStrategy} instance, since the actual focus mode isn't known until
     * the user picks one in the UI.
     */
    public WeightedFocusStrategy(){
        this.focusMode = Category.ACADEMIC;
    }


    /** Sorts tasks by {@link #calculateScore(Task, LocalDateTime)}, highest score first. */
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

    /**
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

    /** A task receives a higher focus score when its category matches the user's selected focus mode. */
    private double calculateFocusScore(Task task) {

        if (task.getCategory().name().equals(focusMode.name())) {
            return 3;
        }

        return 1;
    }

    /**
      * Calculate urgency using the amount of time remaining
      * before the deadline.
      *
      * 3 = very urgent
      * 2 = moderately urgent
      * 1 = less urgent
      */
    private double calculateUrgencyScore(Task task, LocalDateTime referenceTime) {

        /**
         * Sinenkhosi made a change
         * LocalDateTime now = referenceTime; --> referenceTime is used for reproducibility (all tasks must be checked against the same now) 
         * 
        */

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

    /** @return the category tasks are currently being weighted toward. */
    public Category getFocusMode(){
        return this.focusMode;
    }


    /**
     * Changes the focus category. Because {@code App.wfStrategy} is a single shared instance, calling this affects every subsequent read of
     * {@link #getFocusMode()} and every subsequent {@link #generateSchedule} call on this strategy, from anywhere in the app, there's no
     * per-caller isolation.
     *
     * @param mode the new focus category
     */
    public void setFocusMode(Category mode){
        this.focusMode = mode;
    }

    @Override
    public String getStrategyName(){
        return "Weighted Focus Strategy";
    }
}