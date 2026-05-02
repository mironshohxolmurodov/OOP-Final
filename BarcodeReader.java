import java.util.Date;

public class BarcodeReader {

    private String  id;
    private Date    registeredAt;
    private boolean active;

    public BarcodeReader(String id) {
        this.id           = id;
        this.registeredAt = new Date();
        this.active       = true;
    }

    public String  getId()           { return id; }
    public Date    getRegisteredAt() { return registeredAt; }
    public boolean isActive()        { return active; }
    public void    setActive(boolean b){ this.active = b; }

    public String scanLibraryCard(LibraryCard card) {
        if (!active) {
            System.out.println("[BarcodeReader " + id + "] Reader is inactive.");
            return null;
        }
        if (card == null || !card.isActive()) {
            System.out.println("[BarcodeReader " + id + "] Invalid or inactive card.");
            return null;
        }
        System.out.println("[BarcodeReader " + id + "] Scanned library card: " + card.getBarcode());
        return card.getBarcode();
    }

    public String scanBookBarcode(String bookBarcode) {
        if (!active) {
            System.out.println("[BarcodeReader " + id + "] Reader is inactive.");
            return null;
        }
        System.out.println("[BarcodeReader " + id + "] Scanned book barcode: " + bookBarcode);
        return bookBarcode;
    }

    @Override
    public String toString() {
        return "BarcodeReader{id='" + id + "', active=" + active + "}";
    }
}
