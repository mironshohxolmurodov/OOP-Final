import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.charset.StandardCharsets;
import java.io.IOException;

public class LibraryDatabase {
    private static final String[] DEPARTMENTS = {
            "Economics and Data Science",
            "Cybersecurity",
            "AI and Robotics",
            "STEM",
            "Mechanical Engineering",
            "chemichal Engineering",
            "Software Engineering",
            "Industrial management"
    };

    private final Map<String, BookRecord> books;
    private final Map<String, StudentRecord> students;
    private final Map<String, Librarian> admins;
    private final List<LoanRecord> loans;
    private final List<ReservationRecord> reservations;
    private final List<NotificationRecord> notifications;
    private final List<ActivityRecord> activities;
    private int nextLoanNumber;
    private int nextReservationNumber;
    private int nextNotificationNumber;

    public LibraryDatabase() {
        this.books = new LinkedHashMap<>();
        this.students = new LinkedHashMap<>();
        this.admins = new LinkedHashMap<>();
        this.loans = new ArrayList<>();
        this.reservations = new ArrayList<>();
        this.notifications = new ArrayList<>();
        this.activities = new ArrayList<>();
        this.nextLoanNumber = 1001;
        this.nextReservationNumber = 501;
        this.nextNotificationNumber = 1;
    }

    public static LibraryDatabase withSampleData() {
        LibraryDatabase database = new LibraryDatabase();
        database.seedAdmins();
        database.seedBooks();
        database.seedStudents();
        database.seedLoans();
        database.recordActivity("Library data loaded");
        return database;
    }

    public Collection<BookRecord> getBooks() {
        return books.values();
    }

    public Collection<StudentRecord> getStudents() {
        return students.values();
    }

    public Collection<Librarian> getAdmins() {
        return admins.values();
    }

    public List<LoanRecord> getLoans() {
        return loans;
    }

    public List<ActivityRecord> getActivities() {
        return activities;
    }

    public List<ReservationRecord> getReservations() {
        return reservations;
    }

    public List<NotificationRecord> getNotifications() {
        return notifications;
    }

    public BookRecord getBook(String bookId) {
        return books.get(normalizeKey(bookId));
    }

    public StudentRecord getStudent(String userId) {
        return students.get(normalizeKey(userId));
    }

    public Librarian getAdmin(String adminId) {
        return admins.get(normalizeKey(adminId));
    }

    public void putBook(BookRecord book) {
        books.put(normalizeKey(book.getBookId()), book);
    }

    public void putStudent(StudentRecord student) {
        students.put(normalizeKey(student.getUserId()), student);
    }

    public void putAdmin(Librarian admin) {
        admins.put(normalizeKey(admin.getId()), admin);
    }

    public void removeBook(String bookId) {
        books.remove(normalizeKey(bookId));
    }

    public void removeStudent(String userId) {
        students.remove(normalizeKey(userId));
    }

    public String nextLoanId() {
        return String.format("LEND-%04d", nextLoanNumber++);
    }

    public void addLoan(LoanRecord loan) {
        loans.add(loan);
    }

    public void addReservation(ReservationRecord reservation) {
        reservations.add(reservation);
    }

    public void addNotification(NotificationRecord notification) {
        notifications.add(notification);
    }

    public void recordActivity(String description) {
        activities.add(new ActivityRecord(description));
    }

    public String nextReservationId() {
        return String.format("RES-%04d", nextReservationNumber++);
    }

    public int nextNotificationId() {
        return nextNotificationNumber++;
    }

    private void seedAdmins() {
        String adminId = valueOrDefault(System.getenv("LIBRARY_ADMIN_ID"), "admin");
        String adminPassword = valueOrDefault(System.getenv("LIBRARY_ADMIN_PASSWORD"), "plsgive100pointsprof");
        Address address = new Address("1 Library Plaza", "Campus", "State", "10000", "USA");
        Person person = new Person("System Administrator", address, "admin@library.local", "000-000-0000");
        putAdmin(new Librarian(adminId, adminPassword, person));
    }

    private void seedBooks() {
        putBook(new BookRecord("UZ001", "O'tkan kunlar", "Abdulla Qodiriy", "Classic Novel",
                "UZ-1926-001", "Sharq", 1926, 5, 5));
        putBook(new BookRecord("UZ002", "Mehrobdan chayon", "Abdulla Qodiriy", "Classic Novel",
                "UZ-1929-002", "Sharq", 1929, 4, 4));
        putBook(new BookRecord("UZ003", "Kecha va kunduz", "Cho'lpon", "Classic Novel",
                "UZ-1936-003", "Yangi Asr Avlodi", 1936, 4, 4));
        putBook(new BookRecord("UZ004", "Shum bola", "G'afur G'ulom", "Children's Literature",
                "UZ-1936-004", "O'qituvchi", 1936, 6, 6));
        putBook(new BookRecord("UZ005", "Navoiy", "Oybek", "Historical Novel",
                "UZ-1944-005", "G'afur G'ulom", 1944, 4, 4));
        putBook(new BookRecord("UZ006", "Yulduzli tunlar", "Pirimqul Qodirov", "Historical Novel",
                "UZ-1978-006", "Sharq", 1978, 5, 5));
        putBook(new BookRecord("UZ007", "Dunyoning ishlari", "O'tkir Hoshimov", "Short Stories",
                "UZ-1982-007", "Yangi Asr Avlodi", 1982, 5, 5));
        putBook(new BookRecord("UZ008", "Ikki eshik orasi", "O'tkir Hoshimov", "Novel",
                "UZ-1986-008", "Sharq", 1986, 5, 5));
        putBook(new BookRecord("UZ009", "Sariq devni minib", "Xudoyberdi To'xtaboyev", "Children's Literature",
                "UZ-1968-009", "Yosh Gvardiya", 1968, 6, 6));
        putBook(new BookRecord("UZ010", "Shaytanat", "Tohir Malik", "Detective",
                "UZ-1994-010", "Sharq", 1994, 5, 5));
        putBook(new BookRecord("UZ011", "Alvido bolalik", "Tohir Malik", "Novel",
                "UZ-1971-011", "Sharq", 1971, 4, 4));
        putBook(new BookRecord("UZ012", "Bahor qaytmaydi", "O'tkir Hoshimov", "Novel",
                "UZ-1970-012", "Yangi Asr Avlodi", 1970, 4, 4));
    }

    private void seedStudents() {
        Path seedFile = Path.of("students_seed.csv");
        if (Files.exists(seedFile)) {
            try {
                List<String> lines = Files.readAllLines(seedFile, StandardCharsets.UTF_8);
                for (int i = 1; i < lines.size(); i++) {
                    String line = lines.get(i).trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = parseCsvLine(line);
                    if (parts.length < 2) {
                        continue;
                    }
                    String id = unquote(parts[0]);
                    String name = unquote(parts[1]);
                    String department = parts.length >= 4 ? unquote(parts[3]) : departmentFor(id);
                    String password = StudentRecord.defaultPasswordFor(id, name);
                    String email = id + "@students.library.local";
                    putStudent(new StudentRecord(id, name, email, department, id, password));
                }
                return;
            } catch (IOException ex) {
                throw new IllegalStateException("Unable to load students_seed.csv", ex);
            }
        }

        putStudent(new StudentRecord("S001", "Aisha Karim", "aisha.karim@example.com",
                "Computer Science", "555-0101"));
        putStudent(new StudentRecord("S002", "Bilal Ahmed", "bilal.ahmed@example.com",
                "Information Systems", "555-0102"));
        putStudent(new StudentRecord("S003", "Dana Lee", "dana.lee@example.com",
                "Software Engineering", "555-0103"));
    }

    private void seedLoans() {
        LocalDate today = LocalDate.now();

        String firstStudentId = firstStudentIdOrDefault();
        String secondStudentId = secondStudentIdOrDefault(firstStudentId);

        LoanRecord overdueLoan = new LoanRecord(nextLoanId(), "UZ001", firstStudentId,
                today.minusDays(21), today.minusDays(7));
        getBook("UZ001").checkoutCopy();
        addLoan(overdueLoan);

        LoanRecord activeLoan = new LoanRecord(nextLoanId(), "UZ004", secondStudentId,
                today.minusDays(2), today.plusDays(12));
        getBook("UZ004").checkoutCopy();
        addLoan(activeLoan);

        LoanRecord returnedLoan = new LoanRecord(nextLoanId(), "UZ007", firstStudentId,
                today.minusDays(12), today.minusDays(2));
        getBook("UZ007").checkoutCopy();
        returnedLoan.returnBook(today.minusDays(1));
        getBook("UZ007").returnCopy();
        addLoan(returnedLoan);
    }

    private static String normalizeKey(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().toUpperCase();
    }

    private static String valueOrDefault(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }

    private static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);
            if (ch == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (ch == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    private static String unquote(String value) {
        return value == null ? "" : value.trim();
    }

    private static String departmentFor(String userId) {
        String normalized = normalizeKey(userId);
        int index = Math.abs(normalized.hashCode()) % DEPARTMENTS.length;
        return DEPARTMENTS[index];
    }

    private String firstStudentIdOrDefault() {
        if (!students.isEmpty()) {
            return students.values().iterator().next().getUserId();
        }
        return "S001";
    }

    private String secondStudentIdOrDefault(String fallback) {
        if (students.size() >= 2) {
            return new ArrayList<>(students.values()).get(1).getUserId();
        }
        return fallback;
    }
}
