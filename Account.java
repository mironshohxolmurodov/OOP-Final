public abstract class Account {

    private String        id;
    private String        password;
    private AccountStatus status;
    private Person        person;

    public Account(String id, String password, Person person) {
        this.id       = id;
        this.password = password;
        this.person   = person;
        this.status   = AccountStatus.ACTIVE;
    }

    public String        getId()      { return id; }
    public AccountStatus getStatus()  { return status; }
    public Person        getPerson()  { return person; }

    public boolean resetPassword(String newPassword) {
        if (newPassword == null || newPassword.isBlank()) {
            return false;
        }
        this.password = newPassword;
        return true;
    }

    public void setStatus(AccountStatus status) { this.status = status; }

    public boolean isActive() {
        return this.status == AccountStatus.ACTIVE;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{id='" + id + "', status=" + status
                + ", person=" + person + "}";
    }
}
