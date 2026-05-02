public class AdminAuthService {
    private final LibraryDatabase database;
    private Librarian currentAdmin;

    public AdminAuthService(LibraryDatabase database) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null");
        }
        this.database = database;
    }

    public boolean login(String adminId, String password) {
        Librarian admin = database.getAdmin(adminId);
        if (admin == null || !admin.isActive() || !admin.verifyPassword(password)) {
            return false;
        }

        currentAdmin = admin;
        database.recordActivity("Admin " + admin.getId() + " logged in");
        return true;
    }

    public void logout() {
        if (currentAdmin != null) {
            database.recordActivity("Admin " + currentAdmin.getId() + " logged out");
        }
        currentAdmin = null;
    }

    public boolean isAuthenticated() {
        return currentAdmin != null;
    }

    public Librarian getCurrentAdmin() {
        return currentAdmin;
    }

    public void requireAuthenticated() {
        if (!isAuthenticated()) {
            throw new SecurityException("Admin login is required");
        }
    }
}
