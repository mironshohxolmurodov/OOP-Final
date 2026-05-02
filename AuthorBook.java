public class AuthorBook {
    private String firstName;
    private String lastName;
    private String isbn;
    private String title;

    public AuthorBook(String firstName, String lastName, String isbn, String title) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.isbn = isbn;
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }
}
