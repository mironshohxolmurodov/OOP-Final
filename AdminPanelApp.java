public class AdminPanelApp {
    public static void main(String[] args) {
        LibraryDatabase database = LibraryDatabase.withSampleData();
        AdminAuthService authService = new AdminAuthService(database);
        LibraryService libraryService = new LibraryService(database, authService);

        ConsoleAdminPanel panel = new ConsoleAdminPanel(authService, libraryService);
        panel.start();
    }
}
