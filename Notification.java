import java.util.Date;

public abstract class Notification {
    private int notificationId;
    private Date createdOn;
    private String content;

    public Notification(int notificationId, String content) {
        this.notificationId = notificationId;
        this.createdOn = new Date();
        this.content = content;
    }

    public abstract boolean sendNotification();

    public int getNotificationId() {
        return notificationId;
    }

    public Date getCreatedOn() {
        return createdOn;
    }

    public String getContent() {
        return content;
    }
}
