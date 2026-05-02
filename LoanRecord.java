import java.sql.Date;
import java.time.LocalDate;

public class LoanRecord {
    public static final double DAILY_FINE_AMOUNT = 1.0;

    private final String loanId;
    private final String bookId;
    private final String userId;
    private final LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private double fineAmount;
    private LoanStatus status;

    public LoanRecord(String loanId, String bookId, String userId,
                      LocalDate issueDate, LocalDate dueDate) {
        if (loanId == null || loanId.isBlank()) {
            throw new IllegalArgumentException("Loan ID cannot be empty");
        }
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID cannot be empty");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (issueDate == null) {
            throw new IllegalArgumentException("Issue date cannot be null");
        }
        if (dueDate == null || dueDate.isBefore(issueDate)) {
            throw new IllegalArgumentException("Due date cannot be before issue date");
        }

        this.loanId = loanId.trim();
        this.bookId = bookId.trim();
        this.userId = userId.trim();
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = LoanStatus.ISSUED;
    }

    public String getLoanId() { return loanId; }
    public String getBookId() { return bookId; }
    public String getUserId() { return userId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFineAmount() { return fineAmount; }
    public LoanStatus getStatus() { return status; }

    public boolean isIssued() {
        return status == LoanStatus.ISSUED;
    }

    public boolean isOverdue(LocalDate onDate) {
        LocalDate comparisonDate = onDate == null ? LocalDate.now() : onDate;
        if (status == LoanStatus.RETURNED && returnDate != null) {
            comparisonDate = returnDate;
        }
        return comparisonDate.isAfter(dueDate);
    }

    public double calculateFine(LocalDate returnedOn) {
        if (returnedOn == null || !returnedOn.isAfter(dueDate)) {
            return 0.0;
        }
        return Fine.calculateFine(Date.valueOf(dueDate), Date.valueOf(returnedOn));
    }

    public double returnBook(LocalDate returnedOn) {
        if (status == LoanStatus.RETURNED) {
            throw new IllegalStateException("Loan " + loanId + " is already returned");
        }
        if (returnedOn == null) {
            throw new IllegalArgumentException("Return date cannot be null");
        }
        if (returnedOn.isBefore(issueDate)) {
            throw new IllegalArgumentException("Return date cannot be before issue date");
        }

        this.returnDate = returnedOn;
        this.fineAmount = calculateFine(returnedOn);
        this.status = LoanStatus.RETURNED;
        return fineAmount;
    }

    public boolean renew(LocalDate renewedDueDate) {
        if (status != LoanStatus.ISSUED) {
            return false;
        }
        if (renewedDueDate == null || renewedDueDate.isBefore(dueDate)) {
            return false;
        }
        if (isOverdue(LocalDate.now())) {
            return false;
        }
        this.dueDate = renewedDueDate;
        return true;
    }
}
