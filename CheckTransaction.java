public class CheckTransaction extends FineTransaction {
    private String bankName;
    private String checkNumber;

    public CheckTransaction(double amount, String bankName, String checkNumber) {
        super(amount);
        this.bankName = bankName;
        this.checkNumber = checkNumber;
    }

    public boolean initiateTransaction() {
        if (bankName == null || bankName.isEmpty()) {
            setStatus(TransactionStatus.FAILED);
            return false;
        }
        if (checkNumber == null || checkNumber.isEmpty()) {
            setStatus(TransactionStatus.FAILED);
            return false;
        }
        return super.initiateTransaction();
    }

    public String getBankName() {
        return bankName;
    }

    public String getCheckNumber() {
        return checkNumber;
    }
}
