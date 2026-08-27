package strategies;

import java.time.LocalDateTime;
import java.util.List;

import models.Task;


/**
 * Strategy interface for deciding the priority order tasks should be scheduled in.
 *
 * <p>Implementations only decide <em>order</em>, turning that order into actual start/end timestamps is {@code services.Scheduler}'s job, not
 * this interface's. Current implementations: {@link EarliestDeadlineFirstStrategy} (sorts by deadline) and {@link WeightedFocusStrategy}
 * (sorts by a weighted score combining focus match, importance, urgency, and effort).</p>
 *
 * <p><b>Collaborators:</b> held and invoked by {@code services.Scheduler}; implemented by {@link EarliestDeadlineFirstStrategy} and
 * {@link WeightedFocusStrategy}.</p>
 */
public interface SchedulingStrategy {

    /**
     * sinenkhosi modified the method adding the referenceTime parameter
     * 
     * @param tasks         the tasks to order; implementations should not
     *                      mutate this list, returning a new ordered list instead
     * @param referenceTime the "now" to measure urgency/overdue-ness against
     * @return {@code tasks} reordered by this strategy's priority rules
     */
    List<Task> generateSchedule(List<Task> tasks, LocalDateTime referenceTime);


    /**
     *  sinenkhosi added this method to get the name of the implemented strategy
     * 
     *  @return a human-readable name for this strategy, shown in the UI. 
    */
    String getStrategyName();
}
