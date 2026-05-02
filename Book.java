import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Book {

    private final String isbn;
    private final String title;
    private final String subject;
    private final String publisher;
    private final String language;
    private final int numberOfPages;
    private final List<Author> authors;

    public Book(String isbn, String title, String subject,
                String publisher, String language,
                int numberOfPages, List<Author> authors) {
        Objects.requireNonNull(isbn, "ISBN cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(subject, "Subject cannot be null");
        Objects.requireNonNull(publisher, "Publisher cannot be null");
        Objects.requireNonNull(language, "Language cannot be null");
        Objects.requireNonNull(authors, "Authors list cannot be null");
        if (isbn.isBlank()) throw new IllegalArgumentException("ISBN cannot be empty");
        if (title.isBlank()) throw new IllegalArgumentException("Title cannot be empty");
        if (numberOfPages <= 0) throw new IllegalArgumentException("Number of pages must be positive");
        if (authors.isEmpty()) throw new IllegalArgumentException("Book must have at least one author");

        this.isbn = isbn;
        this.title = title;
        this.subject = subject;
        this.publisher = publisher;
        this.language = language;
        this.numberOfPages = numberOfPages;
        this.authors = Collections.unmodifiableList(new ArrayList<>(authors));
    }

    public String getISBN() { return isbn; }

    public String getTitle() { return title; }

    public String getSubject() { return subject; }

    public String getPublisher() { return publisher; }

    public String getLanguage() { return language; }

    public int getNumberOfPages() { return numberOfPages; }

    public List<Author> getAuthors() { return authors; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Book)) return false;
        Book other = (Book) o;
        return isbn.equalsIgnoreCase(other.isbn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isbn.toLowerCase());
    }

    @Override
    public String toString() {
        return "Book{isbn='" + isbn + "', title='" + title +
               "', subject='" + subject + "', authors=" + authors + "}";
    }
}
