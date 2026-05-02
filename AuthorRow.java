public class AuthorRow {
    private int authorID;
    private String firstName;
    private String lastName;

    public AuthorRow(int authorID, String firstName, String lastName) {
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public int getAuthorID() {
        return authorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
