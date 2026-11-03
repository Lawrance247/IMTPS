package repositories;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles the connection between IMTPS and the SQLite database.
 */
public class DatabaseConnection {

    private static final String DATABASE_FILE = resolveDatabaseFile();
    private static final String DATABASE_URL = "jdbc:sqlite:" + DATABASE_FILE;

    private static volatile boolean initialized = false;

    /**
     * Resolves the database to one fixed absolute file so the same data is used
     * no matter which working directory the server is started from.
     */
    private static String resolveDatabaseFile() {
        String configured = System.getProperty("imtps.db.path");

        if (configured == null || configured.isBlank()) {
            configured = System.getenv("IMTPS_DB_PATH");
        }
        if (configured == null || configured.isBlank()) {
            configured = "imtps.db";
        }

        File file = new File(configured);

        if (!file.isAbsolute()) {
            file = new File(System.getProperty("user.dir"), configured);
        }

        File parent = file.getParentFile();

        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }

        return file.getAbsolutePath();
    }

    public static String getDatabaseFile() {
        return DATABASE_FILE;
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA journal_mode = WAL");
            statement.execute("PRAGMA synchronous = NORMAL");
            statement.execute("PRAGMA busy_timeout = 5000");
        }

        return connection;
    }

    /**
     * Creates the tables used by the application and applies small schema
     * migrations needed by older project databases.
     */
    public static void initializeDatabase() {
        if (initialized) {
            return;
        }

        synchronized (DatabaseConnection.class) {
            if (initialized) {
                return;
            }

            String usersTable =
                "CREATE TABLE IF NOT EXISTS users (" +
                "id TEXT PRIMARY KEY, " +
                "full_name TEXT NOT NULL, " +
                "email TEXT NOT NULL UNIQUE, " +
                "password_hash TEXT NOT NULL, " +
                "created_at TEXT NOT NULL DEFAULT (datetime('now'))" +
                ")";

            // New databases get user_id immediately. It is nullable during the
            // migration stage so old tasks are not deleted or assigned to the
            // wrong user before authenticated ownership is connected.
            String tasksTable =
                "CREATE TABLE IF NOT EXISTS tasks (" +
                "id TEXT PRIMARY KEY, " +
                "user_id TEXT, " +
                "title TEXT NOT NULL, " +
                "description TEXT, " +
                "category TEXT NOT NULL, " +
                "deadline TEXT NOT NULL, " +
                "duration_minutes INTEGER NOT NULL, " +
                "importance TEXT NOT NULL, " +
                "effort TEXT NOT NULL, " +
                "status TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

            String notesTable =
                "CREATE TABLE IF NOT EXISTS notes (" +
                "id TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "title TEXT NOT NULL, " +
                "body TEXT NOT NULL, " +
                "tag TEXT NOT NULL DEFAULT 'General', " +
                "created_at TEXT NOT NULL, " +
                "updated_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

            String settingsTable =
                "CREATE TABLE IF NOT EXISTS user_settings (" +
                "user_id TEXT PRIMARY KEY, " +
                "theme TEXT NOT NULL DEFAULT 'dark', " +
                "default_strategy TEXT NOT NULL DEFAULT 'focus', " +
                "default_focus_minutes INTEGER NOT NULL DEFAULT 60, " +
                "daily_reminder INTEGER NOT NULL DEFAULT 1, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

            String schedulesTable =
                "CREATE TABLE IF NOT EXISTS schedules (" +
                "id TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "strategy TEXT NOT NULL, " +
                "strategy_name TEXT NOT NULL, " +
                "focus_category TEXT NOT NULL, " +
                "schedule_date TEXT NOT NULL, " +
                "total_scheduled_minutes INTEGER NOT NULL, " +
                "deadline_clashes INTEGER NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

            String scheduledTasksTable =
                "CREATE TABLE IF NOT EXISTS scheduled_tasks (" +
                "schedule_id TEXT NOT NULL, " +
                "task_id TEXT NOT NULL, " +
                "position INTEGER NOT NULL, " +
                "start_time TEXT NOT NULL, " +
                "end_time TEXT NOT NULL, " +
                "deadline_clash INTEGER NOT NULL, " +
                "PRIMARY KEY (schedule_id, position), " +
                "FOREIGN KEY (schedule_id) REFERENCES schedules(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (task_id) REFERENCES tasks(id) ON DELETE CASCADE" +
                ")";

            String sessionsTable =
                "CREATE TABLE IF NOT EXISTS sessions (" +
                "token TEXT PRIMARY KEY, " +
                "user_id TEXT NOT NULL, " +
                "created_at TEXT NOT NULL, " +
                "expires_at TEXT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
                ")";

            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement()) {

                statement.execute(usersTable);
                statement.execute(tasksTable);
                statement.execute(notesTable);
                statement.execute(settingsTable);
                statement.execute(schedulesTable);
                statement.execute(scheduledTasksTable);
                statement.execute(sessionsTable);
                addTaskOwnerColumnIfMissing(connection);
                statement.execute("CREATE INDEX IF NOT EXISTS idx_tasks_user_id ON tasks(user_id)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_notes_user_id ON notes(user_id)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_schedules_user_id ON schedules(user_id)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_schedules_user_date ON schedules(user_id, schedule_date)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_scheduled_tasks_schedule_id ON scheduled_tasks(schedule_id)");
                statement.execute("CREATE INDEX IF NOT EXISTS idx_sessions_user_id ON sessions(user_id)");

                initialized = true;
                System.out.println("Database initialized successfully at " + DATABASE_FILE);
            } catch (SQLException e) {
                throw new RuntimeException("Could not initialize database.", e);
            }
        }
    }

    /**
     * Older IMTPS databases already have a tasks table without user_id.
     * SQLite keeps all existing rows when this nullable column is added.
     */
    private static void addTaskOwnerColumnIfMissing(Connection connection) throws SQLException {
        boolean hasUserId = false;

        try (Statement statement = connection.createStatement();
             ResultSet columns = statement.executeQuery("PRAGMA table_info(tasks)")) {
            while (columns.next()) {
                if ("user_id".equalsIgnoreCase(columns.getString("name"))) {
                    hasUserId = true;
                    break;
                }
            }
        }

        if (!hasUserId) {
            try (Statement statement = connection.createStatement()) {
                statement.execute(
                    "ALTER TABLE tasks ADD COLUMN user_id TEXT " +
                    "REFERENCES users(id) ON DELETE CASCADE"
                );
            }
        }
    }
}
