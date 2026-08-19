package strategies;

import java.util.List;
import models.Task;

public interface SchedulingStrategy {

    List<Task> generateSchedule(List<Task> tasks);
}
