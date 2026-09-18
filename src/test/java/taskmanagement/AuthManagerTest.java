package taskmanagement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repositories.DatabaseConnection;
import repositories.UserDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthManagerTest {

    private AuthManager authManager;
    private String testEmail;

    @BeforeEach
    void setUp() {
        DatabaseConnection.initializeDatabase();
        authManager = new AuthManager(new UserDB());

        // A different email is used for every test run so the test
        // does not depend on data already stored in imtps.db.
        testEmail = "auth-test-" + UUID.randomUUID() + "@example.com";
    }

    @AfterEach
    void cleanUp() throws SQLException {
        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement =
                connection.prepareStatement("DELETE FROM users WHERE email = ?")
        ) {
            statement.setString(1, testEmail.toLowerCase());
            statement.executeUpdate();
        }
    }

    @Test
    void registerCreatesUserWithoutStoringPlainPassword() {
        String password = "StrongPass123";

        User user = authManager.register(
            "Test User",
            testEmail,
            password
        );

        assertNotNull(user.getId());
        assertEquals("Test User", user.getFullName());
        assertEquals(testEmail.toLowerCase(), user.getEmail());
        assertNotEquals(password, user.getPasswordHash());
        assertFalse(user.getPasswordHash().contains(password));
    }

    @Test
    void loginReturnsUserWhenCredentialsAreCorrect() {
        authManager.register("Test User", testEmail, "StrongPass123");

        User loggedIn = authManager.login(testEmail, "StrongPass123");

        assertEquals(testEmail.toLowerCase(), loggedIn.getEmail());
        assertEquals("Test User", loggedIn.getFullName());
    }

    @Test
    void duplicateEmailIsRejected() {
        authManager.register("First User", testEmail, "StrongPass123");

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> authManager.register(
                "Second User",
                testEmail.toUpperCase(),
                "AnotherPass123"
            )
        );

        assertEquals(
            "An account with this email already exists.",
            error.getMessage()
        );
    }

    @Test
    void invalidEmailIsRejected() {
        assertThrows(
            IllegalArgumentException.class,
            () -> authManager.register(
                "Test User",
                "not-an-email",
                "StrongPass123"
            )
        );
    }

    @Test
    void shortPasswordIsRejected() {
        assertThrows(
            IllegalArgumentException.class,
            () -> authManager.register(
                "Test User",
                testEmail,
                "short"
            )
        );
    }

    @Test
    void wrongPasswordIsRejected() {
        authManager.register("Test User", testEmail, "StrongPass123");

        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> authManager.login(testEmail, "WrongPassword123")
        );

        assertEquals("Invalid email or password.", error.getMessage());
    }

    @Test
    void unknownEmailIsRejected() {
        IllegalArgumentException error = assertThrows(
            IllegalArgumentException.class,
            () -> authManager.login(testEmail, "StrongPass123")
        );

        assertEquals("Invalid email or password.", error.getMessage());
    }
}
