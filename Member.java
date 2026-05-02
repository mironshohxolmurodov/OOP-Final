import java.util.Date;

public class Member extends Account {

    public static final int MAX_BOOKS_CHECKED_OUT = 5;

    private Date dateOfMembership;
    private int  totalBooksCheckedout;

    public Member(String id, String password, Person person, Date dateOfMembership) {
        super(id, password, person);
        this.dateOfMembership    = dateOfMembership;
        this.totalBooksCheckedout = 0;
    }

    public Date getDateOfMembership()    { return dateOfMembership; }
    public int  getTotalBooksCheckedout(){ return totalBooksCheckedout; }

    public boolean canCheckoutBook() {
        return isActive() && totalBooksCheckedout < MAX_BOOKS_CHECKED_OUT;
    }

    public void incrementCheckedoutBookCount() {
        if (!canCheckoutBook()) {
            throw new IllegalStateException(
                "Member " + getId() + " has already reached the checkout limit of "
                + MAX_BOOKS_CHECKED_OUT + " books.");
        }
        totalBooksCheckedout++;
    }

    public void decrementCheckedoutBookCount() {
        if (totalBooksCheckedout > 0) {
            totalBooksCheckedout--;
        }
    }

    @Override
    public String toString() {
        return "Member{id='" + getId() + "', name='" + getPerson().getName()
                + "', status=" + getStatus()
                + ", booksOut=" + totalBooksCheckedout + "/" + MAX_BOOKS_CHECKED_OUT + "}";
    }
}
