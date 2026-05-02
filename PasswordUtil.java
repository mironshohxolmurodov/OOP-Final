import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {
    private static final String PREFIX = "pbkdf2";
    private static final int ITERATIONS = 120000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    public static String hashPassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }

        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);

        return PREFIX + "$" + ITERATIONS + "$"
                + Base64.getEncoder().encodeToString(salt) + "$"
                + Base64.getEncoder().encodeToString(hash);
    }

    public static boolean verifyPassword(String attemptedPassword, String storedPassword) {
        if (attemptedPassword == null || storedPassword == null) {
            return false;
        }

        if (!isHashed(storedPassword)) {
            byte[] attempted = attemptedPassword.getBytes(StandardCharsets.UTF_8);
            byte[] stored = storedPassword.getBytes(StandardCharsets.UTF_8);
            return MessageDigest.isEqual(attempted, stored);
        }

        String[] parts = storedPassword.split("\\$");
        if (parts.length != 4) {
            return false;
        }

        try {
            int iterations = Integer.parseInt(parts[1]);
            byte[] salt = Base64.getDecoder().decode(parts[2]);
            byte[] expectedHash = Base64.getDecoder().decode(parts[3]);
            byte[] attemptedHash = pbkdf2(attemptedPassword.toCharArray(), salt, iterations, expectedHash.length * 8);
            return MessageDigest.isEqual(expectedHash, attemptedHash);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public static boolean isHashed(String passwordValue) {
        return passwordValue != null && passwordValue.startsWith(PREFIX + "$");
    }

    private static byte[] pbkdf2(char[] password, byte[] salt, int iterations, int keyLength) {
        try {
            KeySpec spec = new PBEKeySpec(password, salt, iterations, keyLength);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            return factory.generateSecret(spec).getEncoded();
        } catch (Exception ex) {
            throw new IllegalStateException("Unable to hash password", ex);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}
