package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Stores login sessions so a token stays valid across server restarts.
 */
public class SessionDB {

    public SessionDB() {
        DatabaseConnection.initializeDatabase();
    }

    public void add(String token, String userId, LocalDateTime createdAt, LocalDateTime expiresAt) {
        String sql = "INSERT INTO sessions (token, user_id, created_at, expires_at) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            statement.setString(2, userId);
            statement.setString(3, createdAt.toString());
            statement.setString(4, expiresAt.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not save session.", e);
        }
    }

    public String findUserId(String token) {
        String sql = "SELECT user_id, expires_at FROM sessions WHERE token = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                LocalDateTime expiresAt = LocalDateTime.parse(result.getString("expires_at"));

                if (expiresAt.isBefore(LocalDateTime.now())) {
                    remove(token);
                    return null;
                }

                return result.getString("user_id");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load session.", e);
        }
    }

    public void remove(String token) {
        String sql = "DELETE FROM sessions WHERE token = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, token);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not remove session.", e);
        }
    }

    public void removeExpired() {
        String sql = "DELETE FROM sessions WHERE expires_at <= ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, LocalDateTime.now().toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Could not clean up expired sessions.", e);
        }
    }
}
