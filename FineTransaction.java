import java.util.Date;

public abstract class FineTransaction {
    private Date creationDate;
    private double amount;
    private TransactionStatus status;

    public FineTransaction(double amount) {
        this.creationDate = new Date();
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
    }

    public boolean initiateTransaction() {
        if (amount <= 0) {
            status = TransactionStatus.FAILED;
            return false;
        }
        status = TransactionStatus.COMPLETED;
        return true;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }
}
