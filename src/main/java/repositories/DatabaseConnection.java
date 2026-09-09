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

    // Location of the SQLite database.
    // If imtps.db does not exist, SQLite will create it automatically.
    private static final String DATABASE_URL = "jdbc:sqlite:imtps.db";

    /**
     * Opens and returns a connection to the IMTPS SQLite database.
     *
     * @return an active database connection
     * @throws SQLException if the connection cannot be opened
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DATABASE_URL);
    }

    /**
     * Sets up the database when the application starts.
     * The tasks table is only created if it does not already exist,
     * so existing task data will not be deleted.
     */
    public static void initializeDatabase() {

        // SQL statement used to create the table that stores tasks.
        String sql =
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

        /*
         * try-with-resources automatically closes the database
         * connection and statement when this block finishes.
         */
        try (
            Connection connection = getConnection();
            Statement statement = connection.createStatement()
        ) {

            // Create the tasks table if this is the first time
            // the application is using the database.
            statement.execute(sql);

            System.out.println("Database initialized successfully.");

        } catch (SQLException e) {

            // Stop initialization and keep the original SQL error
            // available so that the cause can be diagnosed.
            throw new RuntimeException(
                "Could not initialize database.",
                e
            );
        }
    }
}