package strategies;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import models.Task;
import utils.Category;

/**
 * Balances tasks across categories using weighted round robin.
 * Critical tasks are always scheduled first.
 */
public class CategoryBalanceStrategy implements SchedulingStrategy {
    private static final int FOCUS_CATEGORY_WEIGHT = 3;
    private static final int OTHER_CATEGORY_WEIGHT = 1;

    private Category focusMode;

    public CategoryBalanceStrategy(Category focusMode) {
        this.focusMode = focusMode;
    }

    public CategoryBalanceStrategy() {
        this.focusMode = Category.ACADEMIC;
    }

    @Override
    public List<Task> generateSchedule(List<Task> tasks, LocalDateTime referenceTime) {
        List<Task> critical = new ArrayList<>();
        Map<Category, Deque<Task>> nonCriticalByCategory = new EnumMap<>(Category.class);

        for (Task task : tasks) {
            if (isCritical(task, referenceTime)) {
                critical.add(task);
            } else {
                nonCriticalByCategory
                    .computeIfAbsent(task.getCategory(), category -> new ArrayDeque<>())
                    .add(task);
            }
        }

        critical.sort(Comparator.comparingLong(
            task -> calculateSlackMinutes(task, referenceTime)));

        for (Deque<Task> queue : nonCriticalByCategory.values()) {
            List<Task> sorted = new ArrayList<>(queue);
            sorted.sort(Comparator.comparingLong(
                task -> calculateSlackMinutes(task, referenceTime)));
            queue.clear();
            queue.addAll(sorted);
        }

        List<Task> schedule = new ArrayList<>(critical);
        schedule.addAll(weightedRoundRobin(nonCriticalByCategory));
        return schedule;
    }

    private boolean isCritical(Task task, LocalDateTime referenceTime) {
        return calculateSlackMinutes(task, referenceTime) <= 0;
    }

    private long calculateSlackMinutes(Task task, LocalDateTime referenceTime) {
        Duration remaining = Duration.between(referenceTime, task.getDeadline());
        return remaining.toMinutes() - task.getDurationMinutes();
    }

    private int weightFor(Category category) {
        return category == focusMode ? FOCUS_CATEGORY_WEIGHT : OTHER_CATEGORY_WEIGHT;
    }

    private List<Task> weightedRoundRobin(Map<Category, Deque<Task>> queuesByCategory) {
        List<Task> result = new ArrayList<>();
        Map<Category, Integer> currentValue = new EnumMap<>(Category.class);

        for (Category category : queuesByCategory.keySet()) {
            currentValue.put(category, 0);
        }

        while (!queuesByCategory.isEmpty()) {
            int totalActiveWeight = queuesByCategory.keySet()
                .stream()
                .mapToInt(this::weightFor)
                .sum();

            Category picked = null;
            int highestValue = Integer.MIN_VALUE;

            List<Category> activeCategories = new ArrayList<>(queuesByCategory.keySet());
            activeCategories.sort(Comparator.comparing(Enum::name));

            for (Category category : activeCategories) {
                int updated = currentValue.get(category) + weightFor(category);
                currentValue.put(category, updated);

                if (updated > highestValue) {
                    highestValue = updated;
                    picked = category;
                }
            }

            Deque<Task> queue = queuesByCategory.get(picked);
            result.add(queue.poll());
            currentValue.put(picked, currentValue.get(picked) - totalActiveWeight);

            if (queue.isEmpty()) {
                queuesByCategory.remove(picked);
                currentValue.remove(picked);
            }
        }

        return result;
    }

    public Category getFocusMode() {
        return focusMode;
    }

    public void setFocusMode(Category mode) {
        this.focusMode = mode;
    }

    @Override
    public String getStrategyName() {
        return "Category Balance Strategy";
    }
}
