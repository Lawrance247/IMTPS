package strategies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Task;


/**
 * Orders tasks strictly by deadline, the simpler, non-focus-aware scheduling strategy used as "Strategy A" in the comparison view.
 *
 * <p><b>Collaborators:</b> a single shared instance is held by {@code app.App} as {@code App.edfStrategy}; used by {@code services.Scheduler}
 * and read by {@code controllers.ComparisonController}.</p>
 */
public class EarliestDeadlineFirstStrategy implements SchedulingStrategy {

    /**
     * Sorts by, in order: earliest deadline first, then (for ties) higher importance first, then (for further ties) shorter duration first.
     * {@code referenceTime} is unused by this strategy, deadline order doesn't depend on "now".
     */
    @Override
    public List<Task> generateSchedule(List<Task> tasks, LocalDateTime referenceTime) {

        // Create a copy so the original task list is not changed.
        List<Task> schedule = new ArrayList<>(tasks);

        // 1. Earliest deadline first.
        // 2. If deadlines are equal, higher importance first.
        // 3. If both are equal, shorter duration first.
        schedule.sort(
            Comparator
                .comparing(Task::getDeadline)
                .thenComparing(
                    Comparator.comparingInt(
                        (Task task) -> task.getImportance().getWeight()
                    ).reversed()
                )
                .thenComparingInt(Task::getDurationMinutes)
        );

        return schedule;
    }

    @Override
    public String getStrategyName(){
        return "Earliest Deadline First Strategy";
    }
}