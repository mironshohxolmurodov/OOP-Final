public class CreditCardTransaction extends FineTransaction {
    private String nameOnCard;
    private String cardNumber;

    public CreditCardTransaction(double amount, String nameOnCard, String cardNumber) {
        super(amount);
        this.nameOnCard = nameOnCard;
        this.cardNumber = cardNumber;
    }

    public boolean initiateTransaction() {
        if (nameOnCard == null || nameOnCard.isEmpty()) {
            setStatus(TransactionStatus.FAILED);
            return false;
        }
        if (cardNumber == null || cardNumber.length() < 12) {
            setStatus(TransactionStatus.FAILED);
            return false;
        }
        return super.initiateTransaction();
    }

    public String getNameOnCard() {
        return nameOnCard;
    }

    public String getCardNumber() {
        return cardNumber;
    }
}
