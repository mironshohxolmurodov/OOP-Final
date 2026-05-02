import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Catalog implements Search {
    private final Map<String, BookItem> bookItems;

    public Catalog() {
        this.bookItems = new LinkedHashMap<>();
    }

    public boolean addBookItem(BookItem bookItem) {
        if (bookItem == null || containsBookItem(bookItem.getBarcode())) {
            return false;
        }
        bookItems.put(normalize(bookItem.getBarcode()), bookItem);
        return true;
    }

    public boolean removeBookItem(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            return false;
        }
        return bookItems.remove(normalize(barcode)) != null;
    }

    public BookItem findByBarcode(String barcode) {
        if (barcode == null || barcode.isBlank()) {
            return null;
        }
        return bookItems.get(normalize(barcode));
    }

    public boolean containsBookItem(String barcode) {
        return findByBarcode(barcode) != null;
    }

    public List<BookItem> getAllBookItems() {
        return Collections.unmodifiableList(new ArrayList<>(bookItems.values()));
    }

    public List<BookItem> getAvailableBookItems() {
        List<BookItem> results = new ArrayList<>();
        for (BookItem bookItem : bookItems.values()) {
            if (bookItem.isAvailable()) {
                results.add(bookItem);
            }
        }
        return results;
    }

    public int getTotalItems() {
        return bookItems.size();
    }

    public int getAvailableItemsCount() {
        return getAvailableBookItems().size();
    }

    @Override
    public List<BookItem> searchByTitle(String title) {
        return search(bookItem -> contains(bookItem.getTitle(), title));
    }

    @Override
    public List<BookItem> searchByAuthor(String authorName) {
        return search(bookItem -> {
            for (Author author : bookItem.getAuthors()) {
                if (contains(author.getName(), authorName)) {
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public List<BookItem> searchBySubject(String subject) {
        return search(bookItem -> contains(bookItem.getSubject(), subject));
    }

    @Override
    public List<BookItem> searchByPublicationDate(LocalDate publicationDate) {
        if (publicationDate == null) {
            return new ArrayList<>();
        }
        return search(bookItem -> publicationDate.equals(bookItem.getPublicationDate()));
    }

    private List<BookItem> search(BookItemMatcher matcher) {
        List<BookItem> results = new ArrayList<>();
        for (BookItem bookItem : bookItems.values()) {
            if (matcher.matches(bookItem)) {
                results.add(bookItem);
            }
        }
        return results;
    }

    private boolean contains(String value, String searchText) {
        if (value == null || searchText == null || searchText.isBlank()) {
            return false;
        }
        return value.toLowerCase().contains(searchText.trim().toLowerCase());
    }

    private String normalize(String value) {
        return value.trim().toUpperCase();
    }

    private interface BookItemMatcher {
        boolean matches(BookItem bookItem);
    }
}
