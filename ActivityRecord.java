import java.time.LocalDateTime;

public class ActivityRecord {
    private final LocalDateTime timestamp;
    private final String description;

    public ActivityRecord(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Activity description cannot be empty");
        }
        this.timestamp = LocalDateTime.now();
        this.description = description.trim();
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getDescription() {
        return description;
    }
}
