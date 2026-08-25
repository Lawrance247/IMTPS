package strategies;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import models.Task;
public class EarliestDeadlineFirstStrategy implements SchedulingStrategy {

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