package repositories;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import taskmanagement.Note;

public class NoteDB {
    public List<Note> findAll(String userId) {
        String sql = "SELECT * FROM notes WHERE user_id = ? ORDER BY updated_at DESC";
        List<Note> notes = new ArrayList<>();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, userId);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) notes.add(fromRow(r));
            }
            return notes;
        } catch (SQLException e) {
            throw new RuntimeException("Could not load notes.", e);
        }
    }

    public Note add(Note note) {
        String sql = "INSERT INTO notes (id,user_id,title,body,tag,created_at,updated_at) VALUES (?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, note.getId());
            p.setString(2, note.getUserId());
            p.setString(3, note.getTitle());
            p.setString(4, note.getBody());
            p.setString(5, note.getTag());
            p.setString(6, note.getCreatedAt().toString());
            p.setString(7, note.getUpdatedAt().toString());
            p.executeUpdate();
            return note;
        } catch (SQLException e) {
            throw new RuntimeException("Could not save note.", e);
        }
    }

    public Note update(String id, String userId, String title, String body, String tag) {
        String sql = "UPDATE notes SET title=?, body=?, tag=?, updated_at=? WHERE id=? AND user_id=?";
        LocalDateTime updatedAt = LocalDateTime.now();
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, title); p.setString(2, body); p.setString(3, tag);
            p.setString(4, updatedAt.toString()); p.setString(5, id); p.setString(6, userId);
            if (p.executeUpdate() == 0) return null;
            return findById(id, userId);
        } catch (SQLException e) {
            throw new RuntimeException("Could not update note.", e);
        }
    }

    public boolean remove(String id, String userId) {
        String sql = "DELETE FROM notes WHERE id=? AND user_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id); p.setString(2, userId);
            return p.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Could not delete note.", e);
        }
    }

    private Note findById(String id, String userId) {
        String sql = "SELECT * FROM notes WHERE id=? AND user_id=?";
        try (Connection c = DatabaseConnection.getConnection();
             PreparedStatement p = c.prepareStatement(sql)) {
            p.setString(1, id); p.setString(2, userId);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? fromRow(r) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Could not load note.", e);
        }
    }

    private Note fromRow(ResultSet r) throws SQLException {
        return new Note(r.getString("id"), r.getString("user_id"), r.getString("title"),
            r.getString("body"), r.getString("tag"),
            LocalDateTime.parse(r.getString("created_at")),
            LocalDateTime.parse(r.getString("updated_at")));
    }
}
