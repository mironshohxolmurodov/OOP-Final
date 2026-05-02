import java.util.Date;

public class LibraryCard {

    private String  cardNumber;
    private String  barcode;
    private Date    issuedAt;
    private boolean active;
    private Account owner;

    public LibraryCard(String cardNumber, String barcode, Account owner) {
        this.cardNumber = cardNumber;
        this.barcode    = barcode;
        this.owner      = owner;
        this.issuedAt   = new Date();
        this.active     = true;
    }

    public String  getCardNumber() { return cardNumber; }
    public String  getBarcode()    { return barcode; }
    public Date    getIssuedAt()   { return issuedAt; }
    public Account getOwner()      { return owner; }

    public boolean isActive()          { return active; }
    public void    setActive(boolean b){ this.active = b; }

    @Override
    public String toString() {
        return "LibraryCard{cardNumber='" + cardNumber + "', barcode='" + barcode
                + "', active=" + active + ", owner=" + owner.getId() + "}";
    }
}
