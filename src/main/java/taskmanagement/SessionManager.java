package taskmanagement;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/** Keeps short-lived login sessions in memory while the API is running. */
public class SessionManager {
    private static final int TOKEN_BYTES = 32;

    private final SecureRandom secureRandom = new SecureRandom();
    private final Map<String, String> sessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        byte[] bytes = new byte[TOKEN_BYTES];
        secureRandom.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        sessions.put(token, user.getId());
        return token;
    }

    public String getUserId(String token) {
        if (token == null || token.isBlank()) return null;
        return sessions.get(token);
    }

    public void removeSession(String token) {
        if (token != null) sessions.remove(token);
    }
}
