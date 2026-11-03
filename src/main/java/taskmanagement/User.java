package taskmanagement;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user of the IMTPS application.
 */
public class User {

    private final String id;
    private String fullName;
    private String email;
    private String passwordHash;
    private final LocalDateTime createdAt;

    /**
     * Creates a new user before it is saved to the database.
     */
    public User(String fullName, String email, String passwordHash) {
        this(
            UUID.randomUUID().toString(),
            fullName,
            email,
            passwordHash,
            LocalDateTime.now()
        );
    }

    /**
     * Recreates an existing user loaded from the database.
     */
    public User(
        String id,
        String fullName,
        String email,
        String passwordHash,
        LocalDateTime createdAt
    ) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
