import java.util.Calendar;
import java.util.Date;

public class BookLending {
    public static final int DEFAULT_LENDING_DAYS = 10;

    private Date creationDate;
    private Date dueDate;
    private Date returnDate;
    private Member member;
    private BookItem book;

    public BookLending(Member member, BookItem book) {
        this.creationDate = new Date();
        this.dueDate = calculateDueDate();
        this.member = member;
        this.book = book;
    }

    private Date calculateDueDate() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, DEFAULT_LENDING_DAYS);
        return cal.getTime();
    }

    public void returnBook() {
        this.returnDate = new Date();
    }

    public boolean isOverdue() {
        if (returnDate == null) {
            return new Date().after(dueDate);
        }
        return returnDate.after(dueDate);
    }

    public Member getMember() {
        return member;
    }

    public BookItem getBook() {
        return book;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public Date getReturnDate() {
        return returnDate;
    }

    public boolean renew() {
        if (returnDate != null || isOverdue()) {
            return false;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dueDate);
        cal.add(Calendar.DAY_OF_MONTH, DEFAULT_LENDING_DAYS);
        dueDate = cal.getTime();
        return true;
    }
}
