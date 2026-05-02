public class CashTransaction extends FineTransaction {
    private double cashTendered;

    public CashTransaction(double amount, double cashTendered) {
        super(amount);
        this.cashTendered = cashTendered;
    }

    public boolean initiateTransaction() {
        if (cashTendered < getAmount()) {
            setStatus(TransactionStatus.FAILED);
            return false;
        }
        return super.initiateTransaction();
    }

    public double getCashTendered() {
        return cashTendered;
    }

    public double getChange() {
        if (getStatus() == TransactionStatus.COMPLETED) {
            return cashTendered - getAmount();
        }
        return 0.0;
    }
}
