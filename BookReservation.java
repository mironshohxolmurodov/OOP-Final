import java.util.Date;

public class BookReservation {

    private Date creationDate;
    private ReservationStatus status;
    private Member member;
    private BookItem book;

    public BookReservation(Member member, BookItem book) {
        this.creationDate = new Date();
        this.status = ReservationStatus.WAITING;
        this.member = member;
        this.book = book;
    }

    public void completeReservation() {
        this.status = ReservationStatus.COMPLETED;
    }

    public boolean isActive() {
        return status == ReservationStatus.WAITING;
    }

    public Member getMember() {
        return member;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}
