public class EmailNotification extends Notification {
    private String email;

    public EmailNotification(int notificationId, String content, String email) {
        super(notificationId, content);
        this.email = email;
    }

    public boolean sendNotification() {
        if (email == null || email.isEmpty()) {
            return false;
        }
        System.out.println("Email sent to " + email + ": " + getContent());
        return true;
    }

    public String getEmail() {
        return email;
    }
}
