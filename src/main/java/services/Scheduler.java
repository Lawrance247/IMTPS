package services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import models.Schedule;
import models.ScheduledTask;
import models.Task;
import strategies.SchedulingStrategy;
public class Scheduler {

    private final SchedulingStrategy strategy;
    private final LocalTime dailyStartTime;
    private final LocalTime dailyEndTime;

    /**
     * Creates a scheduler with:
     *
     * @param strategy        strategy used to prioritise tasks
     * @param dailyStartTime  time at which scheduling starts each day
     * @param dailyEndTime    time at which scheduling ends each day
     */
    public Scheduler(SchedulingStrategy strategy, LocalTime dailyStartTime, LocalTime dailyEndTime) {

        if (strategy == null) {
            throw new IllegalArgumentException("Scheduling strategy cannot be null.");
        }

        if (dailyStartTime == null || dailyEndTime == null) {
            throw new IllegalArgumentException("Daily availability times cannot be null.");
        }

        if (!dailyStartTime.isBefore(dailyEndTime)) {
            throw new IllegalArgumentException("Daily start time must be before daily end time.");
        }

        this.strategy = strategy;
        this.dailyStartTime = dailyStartTime;
        this.dailyEndTime = dailyEndTime;
    }

    /**
     * Generates a complete time-based schedule.
     * Tasks are NOT removed when they cause problems.
     * Instead, the generated schedule records those problems
     * so that they can later be reported and compared.
     *@param tasks tasks to schedule
     *@param schedulingStart The time to start scheduling at.
     *
     */
    public Schedule generateSchedule(List<Task> tasks, LocalDateTime schedulingStart) {

        if (tasks == null) {
            throw new IllegalArgumentException("Tasks cannot be null.");
        }

        if (schedulingStart == null) {
            throw new IllegalArgumentException("Scheduling start time cannot be null.");
        }

        // The strategy determines the priority/order.
        List<Task> prioritisedTasks = strategy.generateSchedule(new ArrayList<>(tasks));

        List<ScheduledTask> scheduledTasks = new ArrayList<>();

        // First available point from which to schedule.
        LocalDateTime nextAvailableTime = moveToNextAvailableTime(schedulingStart);

        // The scheduler assigns every task a start and end time..
        for (Task task : prioritisedTasks) {
            // Find the next available scheduling position.
            LocalDateTime taskStart = findNextSchedulingTime(nextAvailableTime, task.getDurationMinutes());

            LocalDateTime taskEnd = taskStart.plusMinutes(task.getDurationMinutes());

            ScheduledTask scheduledTask = new ScheduledTask(task, taskStart, taskEnd);

            scheduledTasks.add(scheduledTask);

            
            //The next task begins after this task.
            nextAvailableTime = taskEnd;
        }

        return new Schedule(scheduledTasks);
    }

    /**
     * Finds the next position where a task can be placed.
     * If there is not enough time remaining today, the task is moved to the next day's availability window.
     * A task is never rejected.
     *@param startingPoint task start time.
     *@param durationMinutes task's duration.
     * 
     */
    private LocalDateTime findNextSchedulingTime(LocalDateTime startingPoint, int durationMinutes) {

        LocalDateTime candidate = moveToNextAvailableTime(startingPoint);

        long dailyAvailableMinutes = Duration.between(dailyStartTime, dailyEndTime).toMinutes();

        /*
         * If a task is longer than the whole daily window, we still schedule it.
         * It will automatically be reported as problematic because it extends outside the normal availabilitywindow.
         */
        if (durationMinutes > dailyAvailableMinutes) {
            return candidate;
        }

        while (true) {
            LocalDateTime dayEnd = LocalDateTime.of(candidate.toLocalDate(),dailyEndTime);

            LocalDateTime taskEnd = candidate.plusMinutes(durationMinutes);

            //Task fits into today's available window.
            if (!taskEnd.isAfter(dayEnd)) {
                return candidate;
            }

            //Move to next day's availability.
            LocalDate nextDay = candidate.toLocalDate().plusDays(1);

            candidate = LocalDateTime.of(nextDay, dailyStartTime);
        }
    }

    /**
     * Ensures that the given time is inside the daily scheduling window.
     * @param dateTime The date and time to be check.
     */
    private LocalDateTime moveToNextAvailableTime(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();

        LocalTime time = dateTime.toLocalTime();

        //Before today's availability.
        if (time.isBefore(dailyStartTime)) {
            return LocalDateTime.of(date, dailyStartTime);
        }

        /*
            * At or after today's availability has ended.
            */
        if (!time.isBefore(dailyEndTime)) {
            return LocalDateTime.of(date.plusDays(1), dailyStartTime);
        }

        /*
            * Already inside the window.
            */
        return dateTime;
    }
}