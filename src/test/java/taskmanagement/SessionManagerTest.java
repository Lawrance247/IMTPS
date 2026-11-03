package taskmanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.DatabaseConnection;
import repositories.SessionDB;
import repositories.UserDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {
    private final AuthManager auth = new AuthManager(new UserDB());
    private User user;

    @BeforeEach
    void setUp() {
        String suffix = UUID.randomUUID().toString();
        user = auth.register("Session User", "session-" + suffix + "@example.com", "StrongPass123");
    }

    @AfterEach
    void cleanUp() throws Exception {
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement("DELETE FROM users WHERE id=?")) {
            p.setString(1, user.getId()); p.executeUpdate();
        }
    }

    @Test
    void createdSessionCanBeResolvedAndRemoved() {
        SessionManager sessions = new SessionManager(new SessionDB());
        String token = sessions.createSession(user);

        assertEquals(user.getId(), sessions.getUserId(token));
        sessions.removeSession(token);
        assertNull(sessions.getUserId(token));
    }

    @Test
    void expiredSessionIsRejected() {
        SessionDB db = new SessionDB();
        String token = "expired-" + UUID.randomUUID();
        db.add(token, user.getId(), LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1));

        assertNull(new SessionManager(db).getUserId(token));
    }
}
