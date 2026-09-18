package repositories;

import taskmanagement.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * Handles database operations for users.
 */
public class UserDB {

    /**
     * Saves a new user in the database.
     *
     * @param user user to save
     * @return true when the user was added successfully
     */
    public boolean add(User user) {
        String sql =
            "INSERT INTO users (id, full_name, email, password_hash, created_at) " +
            "VALUES (?, ?, ?, ?, ?)";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, user.getId());
            statement.setString(2, user.getFullName());
            statement.setString(3, user.getEmail());
            statement.setString(4, user.getPasswordHash());
            statement.setString(5, user.getCreatedAt().toString());

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            throw new RuntimeException("Could not add user.", e);
        }
    }

    /**
     * Finds a user by email address.
     *
     * @param email email to search for
     * @return matching user, or null if no user exists
     */
    public User findByEmail(String email) {
        String sql =
            "SELECT id, full_name, email, password_hash, created_at " +
            "FROM users WHERE lower(email) = lower(?)";

        try (
            Connection connection = DatabaseConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)
        ) {
            statement.setString(1, email);

            try (ResultSet result = statement.executeQuery()) {
                if (!result.next()) {
                    return null;
                }

                return new User(
                    result.getString("id"),
                    result.getString("full_name"),
                    result.getString("email"),
                    result.getString("password_hash"),
                    LocalDateTime.parse(result.getString("created_at"))
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Could not find user.", e);
        }
    }

    /**
     * Checks whether an email address is already registered.
     */
    public boolean emailExists(String email) {
        return findByEmail(email) != null;
    }
}
