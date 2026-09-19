package repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import models.Schedule;
import models.ScheduledTask;

/** Stores each user's latest generated schedules in SQLite. */
public class ScheduleDB {

    public ScheduleDB() {
        DatabaseConnection.initializeDatabase();
    }

    public void save(String userId, String strategy, String strategyName, String focusCategory,
                     LocalDate scheduleDate, Schedule schedule) {
        String scheduleId = UUID.randomUUID().toString();
        String deleteExisting = "DELETE FROM schedules WHERE user_id = ? AND schedule_date = ?";
        String insertSchedule = "INSERT INTO schedules " +
            "(id,user_id,strategy,strategy_name,focus_category,schedule_date,total_scheduled_minutes,deadline_clashes,created_at) " +
            "VALUES (?,?,?,?,?,?,?,?,?)";
        String insertItem = "INSERT INTO scheduled_tasks " +
            "(schedule_id,task_id,position,start_time,end_time,deadline_clash) VALUES (?,?,?,?,?,?)";

        try (Connection connection = DatabaseConnection.getConnection()) {
            connection.setAutoCommit(false);
            try {
                try (PreparedStatement statement = connection.prepareStatement(deleteExisting)) {
                    statement.setString(1, userId);
                    statement.setString(2, scheduleDate.toString());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(insertSchedule)) {
                    statement.setString(1, scheduleId);
                    statement.setString(2, userId);
                    statement.setString(3, strategy);
                    statement.setString(4, strategyName);
                    statement.setString(5, focusCategory);
                    statement.setString(6, scheduleDate.toString());
                    statement.setLong(7, schedule.getTotalScheduledMinutes());
                    statement.setInt(8, schedule.getDeadlineClashes().size());
                    statement.setString(9, LocalDateTime.now().toString());
                    statement.executeUpdate();
                }

                try (PreparedStatement statement = connection.prepareStatement(insertItem)) {
                    int position = 0;
                    for (ScheduledTask item : schedule.getScheduledTasks()) {
                        statement.setString(1, scheduleId);
                        statement.setString(2, item.getTask().getId());
                        statement.setInt(3, position++);
                        statement.setString(4, item.getStartTime().toString());
                        statement.setString(5, item.getEndTime().toString());
                        statement.setInt(6, item.hasDeadlineClash() ? 1 : 0);
                        statement.addBatch();
                    }
                    statement.executeBatch();
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not save schedule.", e);
        }
    }

    public SavedSchedule findLatest(String userId) {
        String sql = "SELECT * FROM schedules WHERE user_id=? ORDER BY created_at DESC LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            return readSchedule(connection, statement, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Could not load saved schedule.", e);
        }
    }

    public SavedSchedule findByDate(String userId, LocalDate scheduleDate) {
        String sql = "SELECT * FROM schedules WHERE user_id=? AND schedule_date=? ORDER BY created_at DESC LIMIT 1";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, userId);
            statement.setString(2, scheduleDate.toString());
            return readSchedule(connection, statement, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Could not load saved schedule.", e);
        }
    }

    public boolean updateFocusCategory(String userId, LocalDate scheduleDate, String focusCategory) {
        String sql = "UPDATE schedules SET focus_category=? WHERE user_id=? AND schedule_date=?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, focusCategory);
            statement.setString(2, userId);
            statement.setString(3, scheduleDate.toString());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not update saved schedule.", e);
        }
    }

    private SavedSchedule readSchedule(Connection connection, PreparedStatement statement, String userId)
            throws SQLException {
        try (ResultSet result = statement.executeQuery()) {
            if (!result.next()) return null;
            String scheduleId = result.getString("id");
            return new SavedSchedule(
                result.getString("strategy"), result.getString("strategy_name"),
                result.getString("focus_category"), result.getString("schedule_date"),
                result.getLong("total_scheduled_minutes"), result.getInt("deadline_clashes"),
                findItems(connection, scheduleId, userId));
        }
    }

    private List<SavedItem> findItems(Connection connection, String scheduleId, String userId) throws SQLException {
        String sql = "SELECT st.start_time,st.end_time,st.deadline_clash,t.id,t.title,t.category,t.duration_minutes " +
            "FROM scheduled_tasks st JOIN tasks t ON t.id=st.task_id " +
            "WHERE st.schedule_id=? AND t.user_id=? ORDER BY st.position";
        List<SavedItem> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, scheduleId);
            statement.setString(2, userId);
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    items.add(new SavedItem(result.getString("id"), result.getString("title"),
                        result.getString("category"), result.getInt("duration_minutes"),
                        result.getString("start_time"), result.getString("end_time"),
                        result.getInt("deadline_clash") != 0));
                }
            }
        }
        return items;
    }

    public static class SavedSchedule {
        public final String strategy, strategyName, focusCategory, date;
        public final long totalScheduledMinutes;
        public final int deadlineClashes;
        public final List<SavedItem> items;
        public SavedSchedule(String strategy, String strategyName, String focusCategory, String date,
                             long totalScheduledMinutes, int deadlineClashes, List<SavedItem> items) {
            this.strategy=strategy; this.strategyName=strategyName; this.focusCategory=focusCategory; this.date=date;
            this.totalScheduledMinutes=totalScheduledMinutes; this.deadlineClashes=deadlineClashes; this.items=items;
        }
    }

    public static class SavedItem {
        public final String taskId, title, category, startTime, endTime;
        public final int durationMinutes;
        public final boolean deadlineClash;
        public SavedItem(String taskId, String title, String category, int durationMinutes,
                         String startTime, String endTime, boolean deadlineClash) {
            this.taskId=taskId; this.title=title; this.category=category; this.durationMinutes=durationMinutes;
            this.startTime=startTime; this.endTime=endTime; this.deadlineClash=deadlineClash;
        }
    }
}
