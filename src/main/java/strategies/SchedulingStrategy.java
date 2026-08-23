package strategies;

import java.time.LocalDateTime;
import java.util.List;

import models.Task;

public interface SchedulingStrategy {
    // sinenkhosi modified the method adding the referenceTime parameter
    List<Task> generateSchedule(List<Task> tasks, LocalDateTime referenceTime);
}
