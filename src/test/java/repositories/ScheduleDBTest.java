package repositories;

import models.Schedule;
import models.ScheduledTask;
import models.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.AuthManager;
import taskmanagement.User;
import utils.Category;
import utils.Weights;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ScheduleDBTest {
    private final TaskDB taskDB = new TaskDB();
    private final ScheduleDB scheduleDB = new ScheduleDB();
    private final AuthManager auth = new AuthManager(new UserDB());
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString();
        firstUser = auth.register("Schedule A", "schedule-a-" + suffix + "@example.com", "StrongPass123");
        secondUser = auth.register("Schedule B", "schedule-b-" + suffix + "@example.com", "StrongPass123");
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM users WHERE id IN (?,?)")) {
            p.setString(1, firstUser.getId()); p.setString(2, secondUser.getId()); p.executeUpdate();
        }
    }

    @Test
    void savedSchedulePersistsAndIsOnlyVisibleToOwner() {
        LocalDate date = LocalDate.now().plusDays(1);
        LocalDateTime start = date.atTime(8, 0);
        Task task = new Task("Study", "Test schedule", Category.ACADEMIC,
            start.plusHours(3), 60, Weights.HIGH, Weights.MEDIUM);
        taskDB.add(task, firstUser.getId());

        Schedule schedule = new Schedule(List.of(new ScheduledTask(task, start, start.plusHours(1))));
        scheduleDB.save(firstUser.getId(), "deadline", "Earliest Deadline First", "ACADEMIC", date, schedule);

        ScheduleDB.SavedSchedule saved = scheduleDB.findByDate(firstUser.getId(), date);
        assertNotNull(saved);
        assertEquals("deadline", saved.strategy);
        assertEquals(60, saved.totalScheduledMinutes);
        assertEquals(1, saved.items.size());
        assertEquals(task.getId(), saved.items.get(0).taskId);
        assertNull(scheduleDB.findByDate(secondUser.getId(), date));
    }

    @Test
    void savingSameDateReplacesPreviousSchedule() {
        LocalDate date = LocalDate.now().plusDays(2);
        scheduleDB.save(firstUser.getId(), "focus", "Weighted Focus Mode", "ACADEMIC", date,
            new Schedule(List.of()));
        scheduleDB.save(firstUser.getId(), "balance", "Category Balance", "HEALTH", date,
            new Schedule(List.of()));

        ScheduleDB.SavedSchedule saved = scheduleDB.findByDate(firstUser.getId(), date);
        assertNotNull(saved);
        assertEquals("balance", saved.strategy);
        assertEquals("HEALTH", saved.focusCategory);
    }
}
