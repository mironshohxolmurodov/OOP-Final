import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NotificationService {
    private final LibraryDatabase database;

    public NotificationService(LibraryDatabase database) {
        this.database = database;
    }

    public NotificationRecord sendReservationAvailableNotification(String userId, String bookTitle) {
        StudentRecord student = database.getStudent(userId);
        if (student == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        String content = "Reserved book available: " + bookTitle;
        EmailNotification email = new EmailNotification(database.nextNotificationId(), content, student.getEmail());
        email.sendNotification();
        NotificationRecord record = new NotificationRecord(email.getNotificationId(), userId, "Email", content);
        database.addNotification(record);
        database.recordActivity("Notification sent to " + userId + " for available reservation");
        return record;
    }

    public NotificationRecord sendOverdueNotification(String userId, String bookTitle, LocalDate dueDate) {
        StudentRecord student = database.getStudent(userId);
        if (student == null) {
            throw new IllegalArgumentException("User not found: " + userId);
        }
        String content = "Overdue book: " + bookTitle + " was due on " + dueDate;
        EmailNotification email = new EmailNotification(database.nextNotificationId(), content, student.getEmail());
        email.sendNotification();
        NotificationRecord record = new NotificationRecord(email.getNotificationId(), userId, "Email", content);
        database.addNotification(record);
        database.recordActivity("Overdue notification sent to " + userId);
        return record;
    }

    public List<NotificationRecord> sendOutstandingOverdueNotifications() {
        List<NotificationRecord> records = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (LoanRecord loan : database.getLoans()) {
            if (!loan.isIssued() || !loan.isOverdue(today)) {
                continue;
            }
            BookRecord book = database.getBook(loan.getBookId());
            String bookTitle = book == null ? loan.getBookId() : book.getTitle();
            if (hasExistingNotification(loan.getUserId(), "Overdue book: " + bookTitle)) {
                continue;
            }
            records.add(sendOverdueNotification(loan.getUserId(), bookTitle, loan.getDueDate()));
        }
        return records;
    }

    private boolean hasExistingNotification(String userId, String prefix) {
        for (NotificationRecord record : database.getNotifications()) {
            if (record.getUserId().equalsIgnoreCase(userId) && record.getContent().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }
}
