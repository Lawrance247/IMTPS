package strategies;

import models.Task;
import org.junit.jupiter.api.Test;
import utils.Category;
import utils.Weights;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WeightedFocusStrategyTest {

    private final LocalDateTime now =
            LocalDateTime.of(2026, 8, 30, 10, 0);

    private Task createTask(
            String title,
            Category category,
            LocalDateTime deadline,
            Weights importance,
            Weights effort) {

        return new Task(
                title,
                "",
                category,
                deadline,
                60,
                importance,
                effort
        );
    }

    @Test
    void matchingFocusModeShouldBePrioritised() {

        Task academic = createTask(
                "Study",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        Task leisure = createTask(
                "Gaming",
                Category.LEISURE,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> result =
                strategy.generateSchedule(List.of(leisure, academic), now);

        assertEquals(academic, result.get(0));
    }

    @Test
    void changingFocusModeShouldChangePriority() {

        Task academic = createTask(
                "Study",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        Task health = createTask(
                "Exercise",
                Category.HEALTH,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> academicResult =
                strategy.generateSchedule(List.of(health, academic), now);

        assertEquals(academic, academicResult.get(0));

        strategy.setFocusMode(Category.HEALTH);

        List<Task> healthResult =
                strategy.generateSchedule(List.of(academic, health), now);

        assertEquals(health, healthResult.get(0));
    }

    @Test
    void higherImportanceShouldIncreasePriority() {

        Task low = createTask(
                "Low Importance",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.LOW,
                Weights.MEDIUM
        );

        Task high = createTask(
                "High Importance",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.HIGH,
                Weights.MEDIUM
        );

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> result =
                strategy.generateSchedule(List.of(low, high), now);

        assertEquals(high, result.get(0));
    }

    @Test
    void moreUrgentTaskShouldIncreasePriority() {

        Task later = createTask(
                "Later",
                Category.ACADEMIC,
                now.plusDays(7),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        Task urgent = createTask(
                "Urgent",
                Category.ACADEMIC,
                now.plusHours(12),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> result =
                strategy.generateSchedule(List.of(later, urgent), now);

        assertEquals(urgent, result.get(0));
    }

    @Test
    void effortShouldAffectPriority() {

        Task lowEffort = createTask(
                "Low Effort",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.LOW
        );

        Task highEffort = createTask(
                "High Effort",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.HIGH
        );

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> result =
                strategy.generateSchedule(
                        List.of(lowEffort, highEffort),
                        now
                );

        // Your current formula gives a larger score to HIGH effort.
        assertEquals(highEffort, result.get(0));
    }

    @Test
    void overdueTaskShouldReceiveHighUrgency() {

        Task normal = createTask(
                "Normal",
                Category.ACADEMIC,
                now.plusDays(7),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        Task overdue = createTask(
                "Overdue",
                Category.ACADEMIC,
                now.minusHours(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> result =
                strategy.generateSchedule(List.of(normal, overdue), now);

        assertEquals(overdue, result.get(0));
    }

    @Test
    void shouldNotModifyOriginalList() {

        Task leisure = createTask(
                "Gaming",
                Category.LEISURE,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        Task academic = createTask(
                "Study",
                Category.ACADEMIC,
                now.plusDays(5),
                Weights.MEDIUM,
                Weights.MEDIUM
        );

        List<Task> original = new ArrayList<>();
        original.add(leisure);
        original.add(academic);

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        strategy.generateSchedule(original, now);

        assertEquals(leisure, original.get(0));
        assertEquals(academic, original.get(1));
    }

    @Test
    void emptyListShouldReturnEmptyList() {

        WeightedFocusStrategy strategy =
                new WeightedFocusStrategy(Category.ACADEMIC);

        List<Task> result =
                strategy.generateSchedule(new ArrayList<>(), now);

        assertTrue(result.isEmpty());
    }
}