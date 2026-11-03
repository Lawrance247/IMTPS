package taskmanagement;

import java.time.LocalDateTime;
import java.util.UUID;

public class Note {
    private final String id;
    private final String userId;
    private String title;
    private String body;
    private String tag;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note(String userId, String title, String body, String tag) {
        this(UUID.randomUUID().toString(), userId, title, body, tag, LocalDateTime.now(), LocalDateTime.now());
    }

    public Note(String id, String userId, String title, String body, String tag,
                LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.body = body;
        this.tag = tag;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getBody() { return body; }
    public String getTag() { return tag; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void update(String title, String body, String tag) {
        this.title = title;
        this.body = body;
        this.tag = tag;
        this.updatedAt = LocalDateTime.now();
    }
}
