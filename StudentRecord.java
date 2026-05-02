public class StudentRecord {
    private String userId;
    private String name;
    private String email;
    private String department;
    private String phone;
    private String passwordHash;
    private String passwordHint;

    public StudentRecord(String userId, String name, String email, String department, String phone) {
        this(userId, name, email, department, phone, defaultPasswordFor(userId, name));
    }

    public StudentRecord(String userId, String name, String email, String department, String phone, String password) {
        this.userId = requireText(userId, "User ID");
        this.name = requireText(name, "Name");
        this.email = requireText(email, "Email");
        this.department = requireText(department, "Department");
        this.phone = requireText(phone, "Phone");
        resetPassword(password);
        validateEmail(this.email);
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getPhone() { return phone; }
    public String getPasswordStatus() { return passwordHint; }

    public void setName(String name) {
        this.name = requireText(name, "Name");
    }

    public void setEmail(String email) {
        this.email = requireText(email, "Email");
        validateEmail(this.email);
    }

    public void setDepartment(String department) {
        this.department = requireText(department, "Department");
    }

    public void setPhone(String phone) {
        this.phone = requireText(phone, "Phone");
    }

    public boolean resetPassword(String password) {
        if (password == null || password.isBlank()) {
            return false;
        }
        this.passwordHint = password.trim();
        this.passwordHash = PasswordUtil.hashPassword(password);
        return true;
    }

    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, passwordHash);
    }

    public boolean matches(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return true;
        }

        String needle = searchText.toLowerCase();
        return contains(userId, needle)
                || contains(name, needle)
                || contains(email, needle)
                || contains(department, needle)
                || contains(phone, needle);
    }

    private static boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    private static void validateEmail(String email) {
        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Email address is not valid");
        }
    }

    public static String defaultPasswordFor(String userId) {
        return defaultPasswordFor(userId, userId);
    }

    public static String defaultPasswordFor(String userId, String name) {
        String normalizedId = requireText(userId, "User ID");
        String normalizedName = requireText(name, "Name")
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
        if (normalizedName.isEmpty()) {
            normalizedName = normalizedId;
        }
        return normalizedName + "@" + normalizedId;
    }
}
