import java.util.Calendar;
import java.util.Date;

public class BookLending {

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
        cal.add(Calendar.DAY_OF_MONTH, 10);
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
}
