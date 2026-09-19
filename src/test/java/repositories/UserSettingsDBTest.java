package repositories;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import taskmanagement.AuthManager;
import taskmanagement.User;
import taskmanagement.UserSettings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UserSettingsDBTest {
    private final UserSettingsDB settingsDB = new UserSettingsDB();
    private final AuthManager auth = new AuthManager(new UserDB());
    private User firstUser;
    private User secondUser;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString();
        firstUser = auth.register("Settings A", "settings-a-" + suffix + "@example.com", "StrongPass123");
        secondUser = auth.register("Settings B", "settings-b-" + suffix + "@example.com", "StrongPass123");
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM users WHERE id IN (?,?)")) {
            p.setString(1, firstUser.getId()); p.setString(2, secondUser.getId()); p.executeUpdate();
        }
    }

    @Test
    void settingsPersistAndRemainSeparatePerUser() {
        settingsDB.save(new UserSettings(firstUser.getId(), "light", "balance", 90, false));

        UserSettings saved = settingsDB.findOrCreate(firstUser.getId());
        assertEquals("light", saved.getTheme());
        assertEquals("balance", saved.getDefaultStrategy());
        assertEquals(90, saved.getDefaultFocusMinutes());
        assertFalse(saved.isDailyReminder());

        UserSettings other = settingsDB.findOrCreate(secondUser.getId());
        assertEquals("dark", other.getTheme());
        assertEquals("focus", other.getDefaultStrategy());
        assertEquals(60, other.getDefaultFocusMinutes());
        assertTrue(other.isDailyReminder());
    }
}
