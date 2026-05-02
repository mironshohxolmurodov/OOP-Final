import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Fine {
    private double amount;

    public Fine(double amount) {
        this.amount = amount;
    }

    public Fine(Date dueDate, Date returnDate) {
        this.amount = calculateFine(dueDate, returnDate);
    }

    public static double calculateFine(Date dueDate, Date returnDate) {
        if (dueDate == null || returnDate == null) {
            return 0.0;
        }
        if (!returnDate.after(dueDate)) {
            return 0.0;
        }
        long difference = returnDate.getTime() - dueDate.getTime();
        long lateDays = TimeUnit.MILLISECONDS.toDays(difference);
        if (difference % TimeUnit.DAYS.toMillis(1) != 0) {
            lateDays++;
        }
        return lateDays * 1.0;
    }

    public boolean collectFine(FineTransaction transaction) {
        if (transaction == null) {
            return false;
        }
        if (transaction.getAmount() < amount) {
            return false;
        }
        return transaction.initiateTransaction();
    }

    public double getAmount() {
        return amount;
    }
}
