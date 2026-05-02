public class PostalNotification extends Notification {
    private Address address;

    public PostalNotification(int notificationId, String content, Address address) {
        super(notificationId, content);
        this.address = address;
    }

    public boolean sendNotification() {
        if (address == null) {
            return false;
        }
        System.out.println("Postal mail sent to " + address + ": " + getContent());
        return true;
    }

    public Address getAddress() {
        return address;
    }
}
