package taskmanagement;

import repositories.UserDB;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Handles registration, login validation and password hashing.
 */
public class AuthManager {

    private static final int SALT_LENGTH = 16;
    private static final int ITERATIONS = 120_000;
    private static final int KEY_LENGTH = 256;

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final UserDB userDB;

    public AuthManager() {
        this(new UserDB());
    }

    public AuthManager(UserDB userDB) {
        this.userDB = userDB;
    }

    /**
     * Registers a new user after validating their details.
     */
    public User register(String fullName, String email, String password) {
        String cleanName = requireText(fullName, "Full name");
        String cleanEmail = normalizeEmail(email);

        validateEmail(cleanEmail);
        validatePassword(password);

        if (userDB.emailExists(cleanEmail)) {
            throw new IllegalArgumentException(
                "An account with this email already exists."
            );
        }

        User user = new User(
            cleanName,
            cleanEmail,
            hashPassword(password)
        );

        userDB.add(user);
        return user;
    }

    /**
     * Checks the supplied login details and returns the matching user.
     */
    public User login(String email, String password) {
        String cleanEmail = normalizeEmail(email);

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password is required.");
        }

        User user = userDB.findByEmail(cleanEmail);

        if (user == null || !verifyPassword(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        return user;
    }

    private String normalizeEmail(String email) {
        return requireText(email, "Email").toLowerCase();
    }

    private String requireText(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required.");
        }

        return value.trim();
    }

    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Enter a valid email address.");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException(
                "Password must be at least 8 characters."
            );
        }
    }

    /**
     * Stores passwords as PBKDF2 hashes with a random salt.
     * The plain password is never stored in the database.
     */
    private String hashPassword(String password) {
        byte[] salt = new byte[SALT_LENGTH];
        new SecureRandom().nextBytes(salt);

        byte[] hash = deriveKey(password, salt, ITERATIONS);

        return ITERATIONS
            + ":"
            + Base64.getEncoder().encodeToString(salt)
            + ":"
            + Base64.getEncoder().encodeToString(hash);
    }

    private boolean verifyPassword(String password, String storedHash) {
        if (storedHash == null) {
            return false;
        }

        String[] parts = storedHash.split(":");

        if (parts.length != 3) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[2]);
            byte[] actualHash = deriveKey(password, salt, iterations);

            return MessageDigest.isEqual(expectedHash, actualHash);

        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private byte[] deriveKey(String password, byte[] salt, int iterations) {
        PBEKeySpec specification =
            new PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH);

        try {
            SecretKeyFactory factory =
                SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            return factory.generateSecret(specification).getEncoded();

        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed.", e);

        } finally {
            specification.clearPassword();
        }
    }
}
