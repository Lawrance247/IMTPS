package repositories;

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
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TaskDBIsolationTest {
    private final TaskDB taskDB = new TaskDB();
    private final AuthManager auth = new AuthManager(new UserDB());
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString();
        firstUser = auth.register("First User", "tasks-a-" + suffix + "@example.com", "StrongPass123");
        secondUser = auth.register("Second User", "tasks-b-" + suffix + "@example.com", "StrongPass123");
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM users WHERE id IN (?,?)")) {
            p.setString(1, firstUser.getId());
            p.setString(2, secondUser.getId());
            p.executeUpdate();
        }
    }

    @Test
    void usersOnlySeeTheirOwnTasks() {
        Task firstTask = task("First task");
        Task secondTask = task("Second task");
        taskDB.add(firstTask, firstUser.getId());
        taskDB.add(secondTask, secondUser.getId());

        assertEquals(1, taskDB.findAll(firstUser.getId()).size());
        assertEquals(firstTask.getId(), taskDB.findAll(firstUser.getId()).get(0).getId());
        assertNull(taskDB.findById(secondTask.getId(), firstUser.getId()));
    }

    @Test
    void userCannotUpdateOrDeleteAnotherUsersTask() {
        Task ownedBySecondUser = task("Private task");
        taskDB.add(ownedBySecondUser, secondUser.getId());
        ownedBySecondUser.setTitle("Changed by wrong user");

        assertThrows(NoSuchElementException.class,
            () -> taskDB.update(ownedBySecondUser, firstUser.getId()));
        assertThrows(NoSuchElementException.class,
            () -> taskDB.remove(ownedBySecondUser.getId(), firstUser.getId()));

        assertEquals("Private task",
            taskDB.findById(ownedBySecondUser.getId(), secondUser.getId()).getTitle());
    }

    private Task task(String title) {
        return new Task(title, "test", Category.ACADEMIC,
            LocalDateTime.now().plusDays(2), 60, Weights.HIGH, Weights.MEDIUM);
    }
}
