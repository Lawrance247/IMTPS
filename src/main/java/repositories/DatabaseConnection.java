package repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the connection between the IMTPS application and
 * the SQLite database.
 *
 * It is also responsible for creating the required database
 * tables when the application starts.
 */
public class DatabaseConnection {

    private static final String DATABASE_URL = "jdbc:sqlite:imtps.db";

    /**
     * Opens and returns a connection to the IMTPS SQLite database.
     *
     * @return an active database connection
     * @throws SQLException if the connection cannot be opened
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        // SQLite does not enforce foreign keys unless this is enabled
        // for each database connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    /**
     * Sets up the tables needed by the application.
     * Existing data is kept because each table uses IF NOT EXISTS.
     */
    public static void initializeDatabase() {

        String usersTable =
            "CREATE TABLE IF NOT EXISTS users (" +
            "id TEXT PRIMARY KEY, " +
            "full_name TEXT NOT NULL, " +
            "email TEXT NOT NULL UNIQUE, " +
            "password_hash TEXT NOT NULL, " +
            "created_at TEXT NOT NULL DEFAULT (datetime('now'))" +
            ")";

        // Keep the current task table unchanged for now.
        // We will link tasks to users after authentication is working.
        String tasksTable =
            "CREATE TABLE IF NOT EXISTS tasks (" +
            "id TEXT PRIMARY KEY, " +
            "title TEXT NOT NULL, " +
            "description TEXT, " +
            "category TEXT NOT NULL, " +
            "deadline TEXT NOT NULL, " +
            "duration_minutes INTEGER NOT NULL, " +
            "importance TEXT NOT NULL, " +
            "effort TEXT NOT NULL, " +
            "status TEXT NOT NULL" +
            ")";

        try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement()
        ) {
            statement.execute(usersTable);
            statement.execute(tasksTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {
            throw new RuntimeException(
                "Could not initialize database.",
                e
            );
        }
    }
}
