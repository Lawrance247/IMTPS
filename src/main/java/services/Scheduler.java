package services;

import models.Task;
import strategies.SchedulingStrategy;

import java.util.List;

public class Scheduler {

    private SchedulingStrategy strategy;

    // The strategy is supplied from outside so that Scheduler
    // can work with different scheduling algorithms.
    public Scheduler(SchedulingStrategy strategy) {
        setStrategy(strategy);
    }

    // Generate a recommended schedule using the current strategy.
    public List<Task> generateSchedule(List<Task> tasks) {
        return strategy.generateSchedule(tasks);
    }

    // Allows the application to change the scheduling strategy.
    public void setStrategy(SchedulingStrategy strategy) {

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "Scheduling strategy cannot be null"
            );
        }

        this.strategy = strategy;
    }
}