import java.time.LocalDate;
import java.util.List;

public interface Search {
    List<BookItem> searchByTitle(String title);

    List<BookItem> searchByAuthor(String authorName);

    List<BookItem> searchBySubject(String subject);

    List<BookItem> searchByPublicationDate(LocalDate publicationDate);
}
