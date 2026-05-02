import java.time.LocalDateTime;

public class NotificationRecord {
    private final int notificationId;
    private final String userId;
    private final String channel;
    private final String content;
    private final LocalDateTime createdAt;

    public NotificationRecord(int notificationId, String userId, String channel, String content) {
        if (userId == null || userId.isBlank()) {
            throw new IllegalArgumentException("User ID cannot be empty");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("Channel cannot be empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
        this.notificationId = notificationId;
        this.userId = userId.trim();
        this.channel = channel.trim();
        this.content = content.trim();
        this.createdAt = LocalDateTime.now();
    }

    public int getNotificationId() { return notificationId; }
    public String getUserId() { return userId; }
    public String getChannel() { return channel; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
