import java.util.Calendar;
import java.util.Date;

public class Student4Demo {
    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        Date returnDate = calendar.getTime();
        calendar.add(Calendar.DAY_OF_MONTH, -3);
        Date dueDate = calendar.getTime();

        Fine fine = new Fine(dueDate, returnDate);
        System.out.println("Fine amount: " + fine.getAmount());

        FineTransaction cashTransaction = new CashTransaction(fine.getAmount(), 10.0);
        System.out.println("Cash transaction: " + fine.collectFine(cashTransaction));
        System.out.println("Transaction status: " + cashTransaction.getStatus());

        EmailNotification emailNotification = new EmailNotification(1, "Your book is overdue. Fine amount: " + fine.getAmount(), "member@example.com");
        emailNotification.sendNotification();
    }
}
