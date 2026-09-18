package repositories;

import java.sql.*;
import taskmanagement.UserSettings;

public class UserSettingsDB {
    public UserSettings findOrCreate(String userId) {
        UserSettings settings = find(userId);
        if (settings != null) return settings;

        settings = new UserSettings(userId, "dark", "focus", 60, true);
        save(settings);
        return settings;
    }

    public UserSettings save(UserSettings settings) {
        String sql =
            "INSERT INTO user_settings (user_id,theme,default_strategy,default_focus_minutes,daily_reminder) " +
            "VALUES (?,?,?,?,?) ON CONFLICT(user_id) DO UPDATE SET " +
            "theme=excluded.theme, default_strategy=excluded.default_strategy, " +
            "default_focus_minutes=excluded.default_focus_minutes, daily_reminder=excluded.daily_reminder";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, settings.getUserId());
            p.setString(2, settings.getTheme());
            p.setString(3, settings.getDefaultStrategy());
            p.setInt(4, settings.getDefaultFocusMinutes());
            p.setInt(5, settings.isDailyReminder() ? 1 : 0);
            p.executeUpdate();
            return settings;
        } catch (SQLException e) {
            throw new RuntimeException("Could not save settings.", e);
        }
    }

    private UserSettings find(String userId) {
        String sql = "SELECT * FROM user_settings WHERE user_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, userId);
            try (ResultSet r = p.executeQuery()) {
                if (!r.next()) return null;
                return new UserSettings(
                    userId, r.getString("theme"), r.getString("default_strategy"),
                    r.getInt("default_focus_minutes"), r.getInt("daily_reminder") != 0);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load settings.", e);
        }
    }
}
