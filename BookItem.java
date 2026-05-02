import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class BookItem extends Book {

    private final String barcode;
    private final boolean isReferenceOnly;
    private final double price;
    private final BookFormat format;
    private final LocalDate publicationDate;
    private final LocalDate dateOfPurchase;
    private final Rack rack;

    private BookStatus status;
    private LocalDate borrowed;
    private LocalDate dueDate;
    private BookReservation reservation;

    public BookItem(String barcode, boolean isReferenceOnly,
                    double price, BookFormat format,
                    LocalDate publicationDate, LocalDate dateOfPurchase,
                    Rack rack,
                    String isbn, String title, String subject,
                    String publisher, String language,
                    int numberOfPages, List<Author> authors) {

        super(isbn, title, subject, publisher, language, numberOfPages, authors);

        Objects.requireNonNull(barcode, "Barcode cannot be null");
        Objects.requireNonNull(format, "BookFormat cannot be null");
        Objects.requireNonNull(publicationDate, "Publication date cannot be null");
        Objects.requireNonNull(dateOfPurchase, "Date of purchase cannot be null");
        Objects.requireNonNull(rack, "Rack cannot be null");
        if (barcode.isBlank()) throw new IllegalArgumentException("Barcode cannot be empty");
        if (price < 0) throw new IllegalArgumentException("Price cannot be negative");

        this.barcode = barcode;
        this.isReferenceOnly = isReferenceOnly;
        this.price = price;
        this.format = format;
        this.publicationDate = publicationDate;
        this.dateOfPurchase = dateOfPurchase;
        this.rack = rack;
        this.status = BookStatus.AVAILABLE;
        this.borrowed = null;
        this.dueDate = null;
        this.reservation = null;
    }

    public String getBarcode() { return barcode; }

    public boolean isReferenceOnly() { return isReferenceOnly; }

    public double getPrice() { return price; }

    public BookFormat getFormat() { return format; }

    public LocalDate getPublicationDate() { return publicationDate; }

    public LocalDate getDateOfPurchase() { return dateOfPurchase; }

    public Rack getRack() { return rack; }

    public BookStatus getStatus() { return status; }

    public LocalDate getBorrowed() { return borrowed; }

    public LocalDate getDueDate() { return dueDate; }

    public BookReservation getReservation() { return reservation; }

    public boolean isAvailable() { return status == BookStatus.AVAILABLE && !isReferenceOnly; }

    public void setStatus(BookStatus status) {
        Objects.requireNonNull(status, "Status cannot be null");
        this.status = status;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReservation(BookReservation reservation) {
        this.reservation = reservation;
    }

    public boolean checkout() {
        if (isReferenceOnly || status != BookStatus.AVAILABLE) {
            return false;
        }
        this.status = BookStatus.LOANED;
        this.borrowed = LocalDate.now();
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BookItem)) return false;
        BookItem other = (BookItem) o;
        return barcode.equalsIgnoreCase(other.barcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(barcode.toLowerCase());
    }

    @Override
    public String toString() {
        return "BookItem{" +
               "barcode='" + barcode + '\'' +
               ", title='" + getTitle() + '\'' +
               ", format=" + format +
               ", status=" + status +
               ", referenceOnly=" + isReferenceOnly +
               ", rack=" + rack.getFullLocation() +
               ", dueDate=" + dueDate +
               '}';
    }
}
