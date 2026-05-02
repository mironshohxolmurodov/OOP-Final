import java.time.LocalDate;

public class ReservationRecord {
    private final String reservationId;
    private final String bookId;
    private final String userId;
    private final LocalDate reservationDate;
    private boolean active;

    public ReservationRecord(String reservationId, String bookId, String userId, LocalDate reservationDate) {
        if (reservationId == null || reservationId.isBlank()) {
            throw new IllegalArgumentException("Reservation ID cannot be empty");
        }
        if (bookId == null || bookId.isBlank()) {
            throw new IllegalArgumentException("Book ID cannot be empty");
        }
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (reservationDate == null) {
            throw new IllegalArgumentException("Reservation date cannot be null");
        }
        this.reservationId = reservationId.trim();
        this.bookId = bookId.trim();
        this.userId = userId.trim();
        this.reservationDate = reservationDate;
        this.active = true;
    }

    public String getReservationId() { return reservationId; }
    public String getBookId() { return bookId; }
    public String getUserId() { return userId; }
    public LocalDate getReservationDate() { return reservationDate; }
    public boolean isActive() { return active; }

    public void complete() {
        this.active = false;
    }
}
