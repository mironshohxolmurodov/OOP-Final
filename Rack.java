import java.util.Objects;

public class Rack {

    private final int number;
    private final String locationIdentifier;

    public Rack(int number, String locationIdentifier) {
        Objects.requireNonNull(locationIdentifier, "Location identifier cannot be null");
        if (number <= 0) throw new IllegalArgumentException("Rack number must be positive");
        if (locationIdentifier.isBlank()) throw new IllegalArgumentException("Location identifier cannot be empty");
        this.number = number;
        this.locationIdentifier = locationIdentifier;
    }

    public int getNumber() {
        return number;
    }

    public String getLocationIdentifier() {
        return locationIdentifier;
    }

    public String getFullLocation() {
        return "Rack " + number + " - " + locationIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Rack)) return false;
        Rack other = (Rack) o;
        return number == other.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "Rack{number=" + number + ", location='" + locationIdentifier + "'}";
    }
}
