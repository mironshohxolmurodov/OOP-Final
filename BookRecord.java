import java.time.LocalDate;

public class BookRecord {
    private String bookId;
    private String title;
    private String author;
    private String category;
    private String isbn;
    private String publisher;
    private int year;
    private int totalQuantity;
    private int availableCopies;

    public BookRecord(String bookId, String title, String author, String category,
                      String isbn, String publisher, int year,
                      int totalQuantity, int availableCopies) {
        this.bookId = requireText(bookId, "Book ID");
        this.title = requireText(title, "Title");
        this.author = requireText(author, "Author");
        this.category = requireText(category, "Category");
        this.isbn = requireText(isbn, "ISBN");
        this.publisher = requireText(publisher, "Publisher");
        validateYear(year);
        validateQuantities(totalQuantity, availableCopies);
        this.year = year;
        this.totalQuantity = totalQuantity;
        this.availableCopies = availableCopies;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public String getIsbn() { return isbn; }
    public String getPublisher() { return publisher; }
    public int getYear() { return year; }
    public int getTotalQuantity() { return totalQuantity; }
    public int getAvailableCopies() { return availableCopies; }
    public int getIssuedCopies() { return totalQuantity - availableCopies; }

    public void setTitle(String title) {
        this.title = requireText(title, "Title");
    }

    public void setAuthor(String author) {
        this.author = requireText(author, "Author");
    }

    public void setCategory(String category) {
        this.category = requireText(category, "Category");
    }

    public void setIsbn(String isbn) {
        this.isbn = requireText(isbn, "ISBN");
    }

    public void setPublisher(String publisher) {
        this.publisher = requireText(publisher, "Publisher");
    }

    public void setYear(int year) {
        validateYear(year);
        this.year = year;
    }

    public void setQuantities(int totalQuantity, int availableCopies) {
        validateQuantities(totalQuantity, availableCopies);
        this.totalQuantity = totalQuantity;
        this.availableCopies = availableCopies;
    }

    public void checkoutCopy() {
        if (availableCopies <= 0) {
            throw new IllegalStateException("No available copies for " + title);
        }
        availableCopies--;
    }

    public void returnCopy() {
        if (availableCopies >= totalQuantity) {
            throw new IllegalStateException("All copies are already marked available");
        }
        availableCopies++;
    }

    public boolean matches(String searchText) {
        if (searchText == null || searchText.isBlank()) {
            return true;
        }

        String needle = searchText.toLowerCase();
        return contains(bookId, needle)
                || contains(title, needle)
                || contains(author, needle)
                || contains(category, needle)
                || contains(isbn, needle)
                || contains(publisher, needle)
                || String.valueOf(year).contains(needle);
    }

    private static boolean contains(String value, String needle) {
        return value != null && value.toLowerCase().contains(needle);
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        return value.trim();
    }

    private static void validateYear(int year) {
        int nextYear = LocalDate.now().getYear() + 1;
        if (year < 1000 || year > nextYear) {
            throw new IllegalArgumentException("Year must be between 1000 and " + nextYear);
        }
    }

    private static void validateQuantities(int totalQuantity, int availableCopies) {
        if (totalQuantity < 0) {
            throw new IllegalArgumentException("Total quantity cannot be negative");
        }
        if (availableCopies < 0) {
            throw new IllegalArgumentException("Available copies cannot be negative");
        }
        if (availableCopies > totalQuantity) {
            throw new IllegalArgumentException("Available copies cannot exceed total quantity");
        }
    }
}
