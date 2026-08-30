package strategies;

import models.Task;
import org.junit.jupiter.api.Test;
import utils.Category;
import utils.Weights;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EarliestDeadlineFirstStrategyTest {

    private final LocalDateTime now =
            LocalDateTime.of(2026, 8, 30, 10, 0);

    private Task createTask(
            String title,
            LocalDateTime deadline,
            int duration,
            Weights importance) {

        return new Task(
                title,
                "",
                Category.ACADEMIC,
                deadline,
                duration,
                importance,
                Weights.MEDIUM
        );
    }

    @Test
    void earlierDeadlineShouldComeFirst() {

        Task later = createTask(
                "Later",
                now.plusDays(3),
                60,
                Weights.HIGH
        );

        Task earlier = createTask(
                "Earlier",
                now.plusDays(1),
                60,
                Weights.LOW
        );

        EarliestDeadlineFirstStrategy strategy =
                new EarliestDeadlineFirstStrategy();

        List<Task> result =
                strategy.generateSchedule(List.of(later, earlier), now);

        assertEquals(earlier, result.get(0));
        assertEquals(later, result.get(1));
    }

    @Test
    void higherImportanceShouldBreakDeadlineTie() {

        LocalDateTime sameDeadline = now.plusDays(2);

        Task low = createTask(
                "Low Importance",
                sameDeadline,
                60,
                Weights.LOW
        );

        Task high = createTask(
                "High Importance",
                sameDeadline,
                60,
                Weights.HIGH
        );

        EarliestDeadlineFirstStrategy strategy =
                new EarliestDeadlineFirstStrategy();

        List<Task> result =
                strategy.generateSchedule(List.of(low, high), now);

        assertEquals(high, result.get(0));
        assertEquals(low, result.get(1));
    }

    @Test
    void shorterDurationShouldBreakSecondTie() {

        LocalDateTime sameDeadline = now.plusDays(2);

        Task longTask = createTask(
                "Long",
                sameDeadline,
                120,
                Weights.HIGH
        );

        Task shortTask = createTask(
                "Short",
                sameDeadline,
                30,
                Weights.HIGH
        );

        EarliestDeadlineFirstStrategy strategy =
                new EarliestDeadlineFirstStrategy();

        List<Task> result =
                strategy.generateSchedule(
                        List.of(longTask, shortTask),
                        now
                );

        assertEquals(shortTask, result.get(0));
        assertEquals(longTask, result.get(1));
    }

    @Test
    void overdueTaskShouldStillBePrioritised() {

        Task future = createTask(
                "Future",
                now.plusDays(2),
                60,
                Weights.HIGH
        );

        Task overdue = createTask(
                "Overdue",
                now.minusDays(1),
                60,
                Weights.LOW
        );

        EarliestDeadlineFirstStrategy strategy =
                new EarliestDeadlineFirstStrategy();

        List<Task> result =
                strategy.generateSchedule(List.of(future, overdue), now);

        assertEquals(overdue, result.get(0));
    }

    @Test
    void shouldNotModifyOriginalList() {

        Task later = createTask(
                "Later",
                now.plusDays(3),
                60,
                Weights.MEDIUM
        );

        Task earlier = createTask(
                "Earlier",
                now.plusDays(1),
                60,
                Weights.MEDIUM
        );

        List<Task> original = new ArrayList<>();
        original.add(later);
        original.add(earlier);

        EarliestDeadlineFirstStrategy strategy =
                new EarliestDeadlineFirstStrategy();

        strategy.generateSchedule(original, now);

        assertEquals(later, original.get(0));
        assertEquals(earlier, original.get(1));
    }

    @Test
    void emptyListShouldReturnEmptyList() {

        EarliestDeadlineFirstStrategy strategy =
                new EarliestDeadlineFirstStrategy();

        List<Task> result =
                strategy.generateSchedule(new ArrayList<>(), now);

        assertTrue(result.isEmpty());
    }
}