package taskmanagement;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;

import repositories.SessionDB;

/** Keeps login sessions in the database so they survive an API restart. */
public class SessionManager {
    private static final int TOKEN_BYTES = 32;
    private static final Duration SESSION_LIFETIME = Duration.ofDays(30);

    private final SecureRandom secureRandom = new SecureRandom();
    private final SessionDB sessionDB;

    public SessionManager() {
        this(new SessionDB());
    }

    public SessionManager(SessionDB sessionDB) {
        this.sessionDB = sessionDB;
        this.sessionDB.removeExpired();
    }

    public String createSession(User user) {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);

        LocalDateTime createdAt = LocalDateTime.now();
        sessionDB.add(token, user.getId(), createdAt, createdAt.plus(SESSION_LIFETIME));
        return token;
    }

    public String getUserId(String token) {
        if (token == null || token.isBlank()) return null;
        return sessionDB.findUserId(token);
    }

    public void removeSession(String token) {
        if (token != null && !token.isBlank()) sessionDB.remove(token);
    }
}
