import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LibraryService {
    private final LibraryDatabase database;
    private final AdminAuthService authService;
    private final NotificationService notificationService;

    public LibraryService(LibraryDatabase database, AdminAuthService authService) {
        if (database == null) {
            throw new IllegalArgumentException("Database cannot be null");
        }
        if (authService == null) {
            throw new IllegalArgumentException("Auth service cannot be null");
        }
        this.database = database;
        this.authService = authService;
        this.notificationService = new NotificationService(database);
    }

    public DashboardStats getDashboardStats() {
        requireAdmin();
        int totalBooks = 0;
        for (BookRecord book : database.getBooks()) {
            totalBooks += book.getTotalQuantity();
        }

        int issuedBooks = 0;
        int returnedBooks = 0;
        int overdueBooks = 0;
        LocalDate today = LocalDate.now();
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getStatus() == LoanStatus.ISSUED) {
                issuedBooks++;
                if (loan.isOverdue(today)) {
                    overdueBooks++;
                }
            } else if (loan.getStatus() == LoanStatus.RETURNED) {
                returnedBooks++;
            }
        }

        return new DashboardStats(totalBooks, database.getStudents().size(),
                issuedBooks, returnedBooks, overdueBooks);
    }

    public List<ActivityRecord> getRecentActivities(int limit) {
        requireAdmin();
        List<ActivityRecord> activities = new ArrayList<>(database.getActivities());
        activities.sort(Comparator.comparing(ActivityRecord::getTimestamp).reversed());
        if (limit > 0 && activities.size() > limit) {
            return new ArrayList<>(activities.subList(0, limit));
        }
        return activities;
    }

    public List<BookRecord> listBooks() {
        requireAdmin();
        List<BookRecord> books = new ArrayList<>(database.getBooks());
        books.sort(Comparator.comparing(BookRecord::getBookId));
        return books;
    }

    public List<BookRecord> searchBooks(String searchText) {
        requireAdmin();
        List<BookRecord> matches = new ArrayList<>();
        for (BookRecord book : listBooks()) {
            if (book.matches(searchText)) {
                matches.add(book);
            }
        }
        return matches;
    }

    public BookRecord getBook(String bookId) {
        requireAdmin();
        return database.getBook(bookId);
    }

    public BookRecord addBook(String bookId, String title, String author, String category,
                              String isbn, String publisher, int year, int totalQuantity) {
        requireAdmin();
        if (database.getBook(bookId) != null) {
            throw new IllegalArgumentException("Book ID already exists");
        }
        ensureUniqueIsbn(isbn, null);

        BookRecord book = new BookRecord(bookId, title, author, category, isbn, publisher,
                year, totalQuantity, totalQuantity);
        database.putBook(book);
        database.recordActivity("Added book " + book.getBookId() + " - " + book.getTitle());
        return book;
    }

    public BookRecord editBook(String bookId, String title, String author, String category,
                               String isbn, String publisher, int year, int totalQuantity) {
        requireAdmin();
        BookRecord book = requireBook(bookId);
        ensureUniqueIsbn(isbn, book.getBookId());

        int activeIssues = countActiveLoansForBook(book.getBookId());
        if (totalQuantity < activeIssues) {
            throw new IllegalArgumentException("Total quantity cannot be less than active issued copies: " + activeIssues);
        }

        book.setTitle(title);
        book.setAuthor(author);
        book.setCategory(category);
        book.setIsbn(isbn);
        book.setPublisher(publisher);
        book.setYear(year);
        book.setQuantities(totalQuantity, totalQuantity - activeIssues);
        database.recordActivity("Updated book " + book.getBookId() + " - " + book.getTitle());
        return book;
    }

    public void deleteBook(String bookId) {
        requireAdmin();
        BookRecord book = requireBook(bookId);
        if (countActiveLoansForBook(book.getBookId()) > 0) {
            throw new IllegalStateException("Cannot delete a book with active issued copies");
        }
        database.removeBook(book.getBookId());
        database.recordActivity("Deleted book " + book.getBookId() + " - " + book.getTitle());
    }

    public List<StudentRecord> listStudents() {
        requireAdmin();
        List<StudentRecord> students = new ArrayList<>(database.getStudents());
        students.sort(Comparator.comparing(StudentRecord::getUserId));
        return students;
    }

    public List<StudentRecord> searchStudents(String searchText) {
        requireAdmin();
        List<StudentRecord> matches = new ArrayList<>();
        for (StudentRecord student : listStudents()) {
            if (student.matches(searchText)) {
                matches.add(student);
            }
        }
        return matches;
    }

    public StudentRecord getStudent(String userId) {
        requireAdmin();
        return database.getStudent(userId);
    }

    public StudentRecord addStudent(String userId, String name, String email,
                                    String department, String phone) {
        return addStudent(userId, name, email, department, phone, StudentRecord.defaultPasswordFor(userId, name));
    }

    public StudentRecord addStudent(String userId, String name, String email,
                                    String department, String phone, String password) {
        requireAdmin();
        if (database.getStudent(userId) != null) {
            throw new IllegalArgumentException("User ID already exists");
        }
        ensureUniqueEmail(email, null);

        StudentRecord student = new StudentRecord(userId, name, email, department, phone, password);
        database.putStudent(student);
        database.recordActivity("Added user " + student.getUserId() + " - " + student.getName());
        return student;
    }

    public StudentRecord editStudent(String userId, String name, String email,
                                     String department, String phone) {
        return editStudent(userId, name, email, department, phone, StudentRecord.defaultPasswordFor(userId, name));
    }

    public StudentRecord editStudent(String userId, String name, String email,
                                     String department, String phone, String password) {
        requireAdmin();
        StudentRecord student = requireStudent(userId);
        ensureUniqueEmail(email, student.getUserId());

        student.setName(name);
        student.setEmail(email);
        student.setDepartment(department);
        student.setPhone(phone);
        student.resetPassword(password);
        database.recordActivity("Updated user " + student.getUserId() + " - " + student.getName());
        return student;
    }

    public void deleteStudent(String userId) {
        requireAdmin();
        StudentRecord student = requireStudent(userId);
        if (countActiveLoansForUser(student.getUserId()) > 0) {
            throw new IllegalStateException("Cannot delete a user with active issued books");
        }
        database.removeStudent(student.getUserId());
        database.recordActivity("Deleted user " + student.getUserId() + " - " + student.getName());
    }

    public LoanRecord issueBook(String bookId, String userId, LocalDate dueDate) {
        requireAdmin();
        BookRecord book = requireBook(bookId);
        StudentRecord student = requireStudent(userId);
        LocalDate issueDate = LocalDate.now();

        if (dueDate == null || dueDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Due date cannot be before today");
        }
        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies are available for this book");
        }
        if (countActiveLoansForUser(student.getUserId()) >= Member.MAX_BOOKS_CHECKED_OUT) {
            throw new IllegalStateException("User has reached the checkout limit of "
                    + Member.MAX_BOOKS_CHECKED_OUT + " books");
        }

        LoanRecord loan = new LoanRecord(database.nextLoanId(), book.getBookId(),
                student.getUserId(), issueDate, dueDate);
        book.checkoutCopy();
        database.addLoan(loan);
        database.recordActivity("Issued " + book.getTitle() + " to " + student.getName()
                + " (due " + dueDate + ")");
        return loan;
    }

    public LoanRecord returnBook(String loanId, LocalDate returnDate) {
        requireAdmin();
        LoanRecord loan = requireLoan(loanId);
        if (!loan.isIssued()) {
            throw new IllegalStateException("This loan has already been returned");
        }

        BookRecord book = requireBook(loan.getBookId());
        StudentRecord student = requireStudent(loan.getUserId());
        loan.returnBook(returnDate);
        book.returnCopy();
        database.recordActivity("Returned " + book.getTitle() + " from " + student.getName()
                + " (fine $" + formatMoney(loan.getFineAmount()) + ")");
        return loan;
    }

    public List<LoanRecord> getIssuedLoans() {
        requireAdmin();
        List<LoanRecord> loans = new ArrayList<>();
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getStatus() == LoanStatus.ISSUED) {
                loans.add(loan);
            }
        }
        loans.sort(Comparator.comparing(LoanRecord::getDueDate));
        return loans;
    }

    public List<LoanRecord> getReturnedLoans() {
        requireAdmin();
        List<LoanRecord> loans = new ArrayList<>();
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getStatus() == LoanStatus.RETURNED) {
                loans.add(loan);
            }
        }
        loans.sort(Comparator.comparing(LoanRecord::getReturnDate).reversed());
        return loans;
    }

    public List<LoanRecord> getOverdueLoans() {
        requireAdmin();
        List<LoanRecord> loans = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getStatus() == LoanStatus.ISSUED && loan.isOverdue(today)) {
                loans.add(loan);
            }
        }
        loans.sort(Comparator.comparing(LoanRecord::getDueDate));
        return loans;
    }

    public List<LoanRecord> getBorrowingHistory(String userId) {
        requireAdmin();
        StudentRecord student = requireStudent(userId);
        List<LoanRecord> loans = new ArrayList<>();
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getUserId().equalsIgnoreCase(student.getUserId())) {
                loans.add(loan);
            }
        }
        loans.sort(Comparator.comparing(LoanRecord::getIssueDate).reversed());
        return loans;
    }

    public LoanRecord getLoan(String loanId) {
        requireAdmin();
        return requireLoan(loanId);
    }

    public String getBookTitle(String bookId) {
        BookRecord book = database.getBook(bookId);
        return book == null ? "(missing book)" : book.getTitle();
    }

    public String getStudentName(String userId) {
        StudentRecord student = database.getStudent(userId);
        return student == null ? "(missing user)" : student.getName();
    }

    public List<BookRecord> listBooksForMember() {
        List<BookRecord> books = new ArrayList<>(database.getBooks());
        books.sort(Comparator.comparing(BookRecord::getBookId));
        return books;
    }

    public List<BookRecord> searchBooksForMember(String searchText) {
        List<BookRecord> matches = new ArrayList<>();
        for (BookRecord book : listBooksForMember()) {
            if (book.matches(searchText)) {
                matches.add(book);
            }
        }
        return matches;
    }

    public LoanRecord checkoutBookForMember(String userId, String bookId) {
        BookRecord book = requireBook(bookId);
        StudentRecord student = requireStudent(userId);
        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(BookLending.DEFAULT_LENDING_DAYS);

        if (book.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No copies are available for this book");
        }
        if (countActiveLoansForUser(student.getUserId()) >= Member.MAX_BOOKS_CHECKED_OUT) {
            throw new IllegalStateException("You have reached the checkout limit");
        }

        ReservationRecord firstReservation = getFirstActiveReservation(book.getBookId());
        if (firstReservation != null && !firstReservation.getUserId().equalsIgnoreCase(student.getUserId())) {
            throw new IllegalStateException("This book is reserved by another member");
        }

        LoanRecord loan = new LoanRecord(database.nextLoanId(), book.getBookId(), student.getUserId(), issueDate, dueDate);
        book.checkoutCopy();
        database.addLoan(loan);
        if (firstReservation != null) {
            firstReservation.complete();
        }
        database.recordActivity("Member " + student.getUserId() + " checked out " + book.getTitle());
        return loan;
    }

    public ReservationRecord reserveBookForMember(String userId, String bookId) {
        BookRecord book = requireBook(bookId);
        StudentRecord student = requireStudent(userId);
        if (book.getAvailableCopies() > 0) {
            throw new IllegalStateException("This book is available now. You can check it out directly.");
        }
        for (ReservationRecord reservation : database.getReservations()) {
            if (reservation.isActive()
                    && reservation.getBookId().equalsIgnoreCase(bookId)
                    && reservation.getUserId().equalsIgnoreCase(userId)) {
                throw new IllegalStateException("You already reserved this book");
            }
        }
        ReservationRecord reservation = new ReservationRecord(database.nextReservationId(), bookId, userId, LocalDate.now());
        database.addReservation(reservation);
        database.recordActivity("Member " + student.getUserId() + " reserved " + book.getTitle());
        return reservation;
    }

    public LoanRecord renewBookForMember(String userId, String loanId) {
        LoanRecord loan = requireLoan(loanId);
        if (!loan.getUserId().equalsIgnoreCase(trim(userId))) {
            throw new IllegalArgumentException("Loan does not belong to this member");
        }
        if (!loan.isIssued()) {
            throw new IllegalStateException("This loan is already closed");
        }
        ReservationRecord reservation = getFirstActiveReservation(loan.getBookId());
        if (reservation != null && !reservation.getUserId().equalsIgnoreCase(userId)) {
            throw new IllegalStateException("This book is reserved by another member");
        }
        LocalDate newDueDate = loan.getDueDate().plusDays(BookLending.DEFAULT_LENDING_DAYS);
        if (!loan.renew(newDueDate)) {
            throw new IllegalStateException("This loan cannot be renewed");
        }
        database.recordActivity("Member " + trim(userId) + " renewed loan " + loan.getLoanId());
        return loan;
    }

    public LoanRecord returnBookForMember(String userId, String loanId) {
        LoanRecord loan = requireLoan(loanId);
        if (!loan.getUserId().equalsIgnoreCase(trim(userId))) {
            throw new IllegalArgumentException("Loan does not belong to this member");
        }
        if (!loan.isIssued()) {
            throw new IllegalStateException("This loan is already closed");
        }
        BookRecord book = requireBook(loan.getBookId());
        loan.returnBook(LocalDate.now());
        book.returnCopy();
        ReservationRecord nextReservation = getFirstActiveReservation(book.getBookId());
        if (nextReservation != null) {
            notificationService.sendReservationAvailableNotification(nextReservation.getUserId(), book.getTitle());
        }
        database.recordActivity("Member " + trim(userId) + " returned " + book.getTitle());
        return loan;
    }

    public List<LoanRecord> getMemberActiveLoans(String userId) {
        List<LoanRecord> loans = new ArrayList<>();
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getUserId().equalsIgnoreCase(trim(userId)) && loan.isIssued()) {
                loans.add(loan);
            }
        }
        loans.sort(Comparator.comparing(LoanRecord::getDueDate));
        return loans;
    }

    public List<ReservationRecord> getMemberReservations(String userId) {
        List<ReservationRecord> reservations = new ArrayList<>();
        for (ReservationRecord reservation : database.getReservations()) {
            if (reservation.getUserId().equalsIgnoreCase(trim(userId)) && reservation.isActive()) {
                reservations.add(reservation);
            }
        }
        return reservations;
    }

    public List<NotificationRecord> getAllNotifications() {
        requireAdmin();
        return new ArrayList<>(database.getNotifications());
    }

    public List<NotificationRecord> getMemberNotifications(String userId) {
        List<NotificationRecord> notifications = new ArrayList<>();
        for (NotificationRecord notification : database.getNotifications()) {
            if (notification.getUserId().equalsIgnoreCase(trim(userId))) {
                notifications.add(notification);
            }
        }
        notifications.sort(Comparator.comparing(NotificationRecord::getCreatedAt).reversed());
        return notifications;
    }

    public List<NotificationRecord> processOverdueNotifications() {
        requireAdmin();
        return notificationService.sendOutstandingOverdueNotifications();
    }

    private void requireAdmin() {
        authService.requireAuthenticated();
    }

    private BookRecord requireBook(String bookId) {
        BookRecord book = database.getBook(bookId);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + bookId);
        }
        return book;
    }

    private StudentRecord requireStudent(String userId) {
        StudentRecord student = database.getStudent(userId);
        if (student == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        return student;
    }

    private LoanRecord requireLoan(String loanId) {
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getLoanId().equalsIgnoreCase(trim(loanId))) {
                return loan;
            }
        }
        throw new IllegalArgumentException("Loan not found: " + loanId);
    }

    private void ensureUniqueIsbn(String isbn, String excludingBookId) {
        String normalizedIsbn = trim(isbn);
        for (BookRecord book : database.getBooks()) {
            boolean sameBook = excludingBookId != null && book.getBookId().equalsIgnoreCase(excludingBookId);
            if (!sameBook && book.getIsbn().equalsIgnoreCase(normalizedIsbn)) {
                throw new IllegalArgumentException("ISBN already exists for book " + book.getBookId());
            }
        }
    }

    private void ensureUniqueEmail(String email, String excludingUserId) {
        String normalizedEmail = trim(email);
        for (StudentRecord student : database.getStudents()) {
            boolean sameUser = excludingUserId != null && student.getUserId().equalsIgnoreCase(excludingUserId);
            if (!sameUser && student.getEmail().equalsIgnoreCase(normalizedEmail)) {
                throw new IllegalArgumentException("Email already exists for user " + student.getUserId());
            }
        }
    }

    private int countActiveLoansForBook(String bookId) {
        int count = 0;
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getStatus() == LoanStatus.ISSUED && loan.getBookId().equalsIgnoreCase(bookId)) {
                count++;
            }
        }
        return count;
    }

    private int countActiveLoansForUser(String userId) {
        int count = 0;
        for (LoanRecord loan : database.getLoans()) {
            if (loan.getStatus() == LoanStatus.ISSUED && loan.getUserId().equalsIgnoreCase(userId)) {
                count++;
            }
        }
        return count;
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private ReservationRecord getFirstActiveReservation(String bookId) {
        for (ReservationRecord reservation : database.getReservations()) {
            if (reservation.isActive() && reservation.getBookId().equalsIgnoreCase(bookId)) {
                return reservation;
            }
        }
        return null;
    }

    private static String formatMoney(double amount) {
        return String.format("%.2f", amount);
    }
}
