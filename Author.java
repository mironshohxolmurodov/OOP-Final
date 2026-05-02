import java.util.Objects;

public class Author {

    private final String name;
    private final String description;

    public Author(String name, String description) {
        Objects.requireNonNull(name, "Author name cannot be null");
        Objects.requireNonNull(description, "Author description cannot be null");
        if (name.isBlank()) throw new IllegalArgumentException("Author name cannot be empty");
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Author)) return false;
        Author other = (Author) o;
        return name.equalsIgnoreCase(other.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase());
    }

    @Override
    public String toString() {
        return "Author{name='" + name + "', description='" + description + "'}";
    }
}
