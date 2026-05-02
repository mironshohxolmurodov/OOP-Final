import java.io.Console;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

public class ConsoleAdminPanel {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private final AdminAuthService authService;
    private final LibraryService libraryService;
    private final Scanner scanner;

    public ConsoleAdminPanel(AdminAuthService authService, LibraryService libraryService) {
        this.authService = authService;
        this.libraryService = libraryService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        printBanner();
        boolean running = true;

        while (running) {
            if (!authService.isAuthenticated() && !showLogin()) {
                break;
            }

            showDashboard();
            running = showMainMenu();
        }

        System.out.println("Goodbye.");
    }

    private boolean showLogin() {
        int attempts = 0;
        while (attempts < 3) {
            System.out.println();
            System.out.println("Admin Login (enter 0 as Admin ID to exit)");
            String adminId = prompt("Admin ID");
            if ("0".equals(adminId)) {
                return false;
            }
            String password = promptPassword("Password");

            if (authService.login(adminId, password)) {
                success("Login successful. Welcome, " + authService.getCurrentAdmin().getPerson().getName() + ".");
                return true;
            }

            attempts++;
            error("Invalid admin ID or password. Attempts left: " + (3 - attempts));
        }

        error("Too many failed login attempts.");
        return false;
    }

    private boolean showMainMenu() {
        boolean sessionRunning = true;
        while (sessionRunning && authService.isAuthenticated()) {
            printSidebar();
            String choice = prompt("Choose an option");
            try {
                switch (choice) {
                    case "1":
                        showDashboard();
                        pause();
                        break;
                    case "2":
                        showBookMenu();
                        break;
                    case "3":
                        showUserMenu();
                        break;
                    case "4":
                        issueBook();
                        pause();
                        break;
                    case "5":
                        returnBook();
                        pause();
                        break;
                    case "6":
                        showReportsMenu();
                        break;
                    case "7":
                        authService.logout();
                        success("Logged out.");
                        sessionRunning = false;
                        break;
                    case "0":
                        authService.logout();
                        return false;
                    default:
                        error("Please choose a valid option.");
                }
            } catch (RuntimeException ex) {
                error(ex.getMessage());
                pause();
            }
        }
        return true;
    }

    private void showDashboard() {
        DashboardStats stats = libraryService.getDashboardStats();
        System.out.println();
        System.out.println("============================================================");
        System.out.println("Admin Dashboard");
        System.out.println("============================================================");
        printCard("Total Books", stats.getTotalBooks());
        printCard("Total Users", stats.getTotalUsers());
        printCard("Issued Books", stats.getIssuedBooks());
        printCard("Returned Books", stats.getReturnedBooks());
        printCard("Overdue Books", stats.getOverdueBooks());
        System.out.println();
        System.out.println("Recent Activities");
        List<ActivityRecord> activities = libraryService.getRecentActivities(6);
        if (activities.isEmpty()) {
            System.out.println("No recent activities.");
            return;
        }
        for (ActivityRecord activity : activities) {
            System.out.println("- " + formatDateTime(activity.getTimestamp()) + "  " + activity.getDescription());
        }
    }

    private void showBookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Book Management");
            System.out.println("1. View all books");
            System.out.println("2. Search books");
            System.out.println("3. Add new book");
            System.out.println("4. Edit book details");
            System.out.println("5. Delete book");
            System.out.println("0. Back");
            String choice = prompt("Choose an option");

            try {
                switch (choice) {
                    case "1":
                        printBooks(libraryService.listBooks());
                        pause();
                        break;
                    case "2":
                        searchBooks();
                        pause();
                        break;
                    case "3":
                        addBook();
                        pause();
                        break;
                    case "4":
                        editBook();
                        pause();
                        break;
                    case "5":
                        deleteBook();
                        pause();
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        error("Please choose a valid option.");
                }
            } catch (RuntimeException ex) {
                error(ex.getMessage());
                pause();
            }
        }
    }

    private void showUserMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("User / Student Management");
            System.out.println("1. View all users");
            System.out.println("2. Search users");
            System.out.println("3. Add user");
            System.out.println("4. Edit user");
            System.out.println("5. Delete user");
            System.out.println("0. Back");
            String choice = prompt("Choose an option");

            try {
                switch (choice) {
                    case "1":
                        printStudents(libraryService.listStudents());
                        pause();
                        break;
                    case "2":
                        searchStudents();
                        pause();
                        break;
                    case "3":
                        addStudent();
                        pause();
                        break;
                    case "4":
                        editStudent();
                        pause();
                        break;
                    case "5":
                        deleteStudent();
                        pause();
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        error("Please choose a valid option.");
                }
            } catch (RuntimeException ex) {
                error(ex.getMessage());
                pause();
            }
        }
    }

    private void showReportsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println();
            System.out.println("Reports / Records");
            System.out.println("1. All issued books");
            System.out.println("2. Returned books");
            System.out.println("3. Overdue books");
            System.out.println("4. Book availability report");
            System.out.println("5. User borrowing history");
            System.out.println("0. Back");
            String choice = prompt("Choose an option");

            try {
                switch (choice) {
                    case "1":
                        printLoans(libraryService.getIssuedLoans());
                        pause();
                        break;
                    case "2":
                        printLoans(libraryService.getReturnedLoans());
                        pause();
                        break;
                    case "3":
                        printLoans(libraryService.getOverdueLoans());
                        pause();
                        break;
                    case "4":
                        printBooks(libraryService.listBooks());
                        pause();
                        break;
                    case "5":
                        String userId = prompt("User ID");
                        printLoans(libraryService.getBorrowingHistory(userId));
                        pause();
                        break;
                    case "0":
                        back = true;
                        break;
                    default:
                        error("Please choose a valid option.");
                }
            } catch (RuntimeException ex) {
                error(ex.getMessage());
                pause();
            }
        }
    }

    private void searchBooks() {
        String searchText = prompt("Search by title, author, category, ISBN, publisher, year, or ID");
        printBooks(libraryService.searchBooks(searchText));
    }

    private void addBook() {
        System.out.println();
        System.out.println("Add New Book");
        String bookId = prompt("Book ID");
        String title = prompt("Title");
        String author = prompt("Author");
        String category = prompt("Category");
        String isbn = prompt("ISBN");
        String publisher = prompt("Publisher");
        int year = promptInt("Year");
        int quantity = promptInt("Quantity");

        BookRecord book = libraryService.addBook(bookId, title, author, category,
                isbn, publisher, year, quantity);
        success("Book added: " + book.getBookId() + " - " + book.getTitle());
    }

    private void editBook() {
        String bookId = prompt("Book ID to edit");
        BookRecord existing = libraryService.getBook(bookId);
        if (existing == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }

        System.out.println("Press Enter to keep the current value.");
        String title = promptDefault("Title", existing.getTitle());
        String author = promptDefault("Author", existing.getAuthor());
        String category = promptDefault("Category", existing.getCategory());
        String isbn = promptDefault("ISBN", existing.getIsbn());
        String publisher = promptDefault("Publisher", existing.getPublisher());
        int year = promptIntDefault("Year", existing.getYear());
        int quantity = promptIntDefault("Total quantity", existing.getTotalQuantity());

        BookRecord book = libraryService.editBook(existing.getBookId(), title, author,
                category, isbn, publisher, year, quantity);
        success("Book updated: " + book.getBookId() + " - " + book.getTitle());
    }

    private void deleteBook() {
        String bookId = prompt("Book ID to delete");
        BookRecord book = libraryService.getBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        if (confirm("Delete " + book.getTitle() + "?")) {
            libraryService.deleteBook(book.getBookId());
            success("Book deleted.");
        } else {
            success("Delete cancelled.");
        }
    }

    private void searchStudents() {
        String searchText = prompt("Search by name, ID, email, department, or phone");
        printStudents(libraryService.searchStudents(searchText));
    }

    private void addStudent() {
        System.out.println();
        System.out.println("Add User / Student");
        String userId = prompt("User ID");
        String name = prompt("Name");
        String email = prompt("Email");
        String department = prompt("Department");
        String phone = prompt("Phone");

        StudentRecord student = libraryService.addStudent(userId, name, email, department, phone);
        success("User added: " + student.getUserId() + " - " + student.getName());
    }

    private void editStudent() {
        String userId = prompt("User ID to edit");
        StudentRecord existing = libraryService.getStudent(userId);
        if (existing == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        System.out.println("Press Enter to keep the current value.");
        String name = promptDefault("Name", existing.getName());
        String email = promptDefault("Email", existing.getEmail());
        String department = promptDefault("Department", existing.getDepartment());
        String phone = promptDefault("Phone", existing.getPhone());

        StudentRecord student = libraryService.editStudent(existing.getUserId(), name, email,
                department, phone);
        success("User updated: " + student.getUserId() + " - " + student.getName());
    }

    private void deleteStudent() {
        String userId = prompt("User ID to delete");
        StudentRecord student = libraryService.getStudent(userId);
        if (student == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        if (confirm("Delete " + student.getName() + "?")) {
            libraryService.deleteStudent(student.getUserId());
            success("User deleted.");
        } else {
            success("Delete cancelled.");
        }
    }

    private void issueBook() {
        System.out.println();
        System.out.println("Issue Book");
        printBooks(libraryService.listBooks());
        String bookId = prompt("Book ID");
        String userId = prompt("User ID");
        LocalDate dueDate = promptDateDefault("Due date", LocalDate.now().plusDays(14));

        LoanRecord loan = libraryService.issueBook(bookId, userId, dueDate);
        success("Book issued. Loan ID: " + loan.getLoanId() + ", due date: " + loan.getDueDate());
    }

    private void returnBook() {
        System.out.println();
        System.out.println("Return Book");
        List<LoanRecord> issuedLoans = libraryService.getIssuedLoans();
        if (issuedLoans.isEmpty()) {
            success("No issued books are waiting for return.");
            return;
        }
        printLoans(issuedLoans);
        String loanId = prompt("Loan ID");
        LocalDate returnDate = promptDateDefault("Return date", LocalDate.now());

        LoanRecord loan = libraryService.returnBook(loanId, returnDate);
        success("Return completed. Fine amount: $" + String.format("%.2f", loan.getFineAmount()));
    }

    private void printBooks(List<BookRecord> books) {
        System.out.println();
        if (books == null || books.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        System.out.printf("%-8s %-28s %-20s %-18s %-15s %-16s %-6s %-8s %-9s%n",
                "ID", "Title", "Author", "Category", "ISBN", "Publisher", "Year", "Total", "Available");
        System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
        for (BookRecord book : books) {
            System.out.printf("%-8s %-28s %-20s %-18s %-15s %-16s %-6d %-8d %-9d%n",
                    truncate(book.getBookId(), 8),
                    truncate(book.getTitle(), 28),
                    truncate(book.getAuthor(), 20),
                    truncate(book.getCategory(), 18),
                    truncate(book.getIsbn(), 15),
                    truncate(book.getPublisher(), 16),
                    book.getYear(),
                    book.getTotalQuantity(),
                    book.getAvailableCopies());
        }
    }

    private void printStudents(List<StudentRecord> students) {
        System.out.println();
        if (students == null || students.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        System.out.printf("%-8s %-24s %-30s %-24s %-14s%n",
                "ID", "Name", "Email", "Department", "Phone");
        System.out.println("----------------------------------------------------------------------------------------------------------");
        for (StudentRecord student : students) {
            System.out.printf("%-8s %-24s %-30s %-24s %-14s%n",
                    truncate(student.getUserId(), 8),
                    truncate(student.getName(), 24),
                    truncate(student.getEmail(), 30),
                    truncate(student.getDepartment(), 24),
                    truncate(student.getPhone(), 14));
        }
    }

    private void printLoans(List<LoanRecord> loans) {
        System.out.println();
        if (loans == null || loans.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-10s %-8s %-26s %-8s %-22s %-12s %-12s %-12s %-9s %-8s%n",
                "Loan ID", "Book", "Title", "User", "Name", "Issued", "Due", "Returned", "Status", "Fine");
        System.out.println("-------------------------------------------------------------------------------------------------------------------------------------");
        LocalDate today = LocalDate.now();
        for (LoanRecord loan : loans) {
            String status = loan.getStatus().toString();
            if (loan.getStatus() == LoanStatus.ISSUED && loan.isOverdue(today)) {
                status = "OVERDUE";
            }
            System.out.printf("%-10s %-8s %-26s %-8s %-22s %-12s %-12s %-12s %-9s $%-7.2f%n",
                    truncate(loan.getLoanId(), 10),
                    truncate(loan.getBookId(), 8),
                    truncate(libraryService.getBookTitle(loan.getBookId()), 26),
                    truncate(loan.getUserId(), 8),
                    truncate(libraryService.getStudentName(loan.getUserId()), 22),
                    formatDate(loan.getIssueDate()),
                    formatDate(loan.getDueDate()),
                    loan.getReturnDate() == null ? "-" : formatDate(loan.getReturnDate()),
                    status,
                    loan.getFineAmount());
        }
    }

    private void printBanner() {
        System.out.println("============================================================");
        System.out.println("Library Management System - Admin Panel");
        System.out.println("============================================================");
    }

    private void printSidebar() {
        String adminName = authService.getCurrentAdmin() == null
                ? "Unknown"
                : authService.getCurrentAdmin().getPerson().getName();
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println("Admin Panel | Logged in: " + adminName);
        System.out.println("------------------------------------------------------------");
        System.out.println("1. Dashboard");
        System.out.println("2. Book Management");
        System.out.println("3. User / Student Management");
        System.out.println("4. Issue Book");
        System.out.println("5. Return Book");
        System.out.println("6. Reports / Records");
        System.out.println("7. Logout");
        System.out.println("0. Exit");
    }

    private void printCard(String label, int value) {
        System.out.printf("[ %-18s : %-5d ]%n", label, value);
    }

    private String prompt(String label) {
        System.out.print(label + ": ");
        if (!scanner.hasNextLine()) {
            return "";
        }
        return scanner.nextLine().trim();
    }

    private String promptPassword(String label) {
        Console console = System.console();
        if (console != null) {
            char[] password = console.readPassword(label + ": ");
            if (password == null) {
                return "";
            }
            return new String(password);
        }
        return prompt(label);
    }

    private String promptDefault(String label, String currentValue) {
        System.out.print(label + " [" + currentValue + "]: ");
        if (!scanner.hasNextLine()) {
            return currentValue;
        }
        String value = scanner.nextLine().trim();
        return value.isEmpty() ? currentValue : value;
    }

    private int promptInt(String label) {
        while (true) {
            String value = prompt(label);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                error("Please enter a valid whole number.");
            }
        }
    }

    private int promptIntDefault(String label, int currentValue) {
        while (true) {
            System.out.print(label + " [" + currentValue + "]: ");
            if (!scanner.hasNextLine()) {
                return currentValue;
            }
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                return currentValue;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                error("Please enter a valid whole number.");
            }
        }
    }

    private LocalDate promptDateDefault(String label, LocalDate currentValue) {
        while (true) {
            System.out.print(label + " [" + currentValue.format(DATE_FORMAT) + "]: ");
            if (!scanner.hasNextLine()) {
                return currentValue;
            }
            String value = scanner.nextLine().trim();
            if (value.isEmpty()) {
                return currentValue;
            }
            try {
                return LocalDate.parse(value, DATE_FORMAT);
            } catch (DateTimeParseException ex) {
                error("Please use date format YYYY-MM-DD.");
            }
        }
    }

    private boolean confirm(String message) {
        String answer = prompt(message + " (y/N)");
        return "y".equalsIgnoreCase(answer) || "yes".equalsIgnoreCase(answer);
    }

    private void pause() {
        System.out.print("Press Enter to continue...");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
        }
    }

    private void success(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    private void error(String message) {
        System.out.println("[ERROR] " + message);
    }

    private String formatDate(LocalDate date) {
        return date == null ? "-" : date.format(DATE_FORMAT);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(TIME_FORMAT);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        if (value.length() <= maxLength) {
            return value;
        }
        if (maxLength <= 3) {
            return value.substring(0, maxLength);
        }
        return value.substring(0, maxLength - 3) + "...";
    }
}
