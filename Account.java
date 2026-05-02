public abstract class Account {

    private String        id;
    private String        password;
    private AccountStatus status;
    private Person        person;

    public Account(String id, String password, Person person) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Account id cannot be empty");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (person == null) {
            throw new IllegalArgumentException("Person cannot be null");
        }
        this.id       = id;
        this.password = PasswordUtil.isHashed(password) ? password : PasswordUtil.hashPassword(password);
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
        this.password = PasswordUtil.hashPassword(newPassword);
        return true;
    }

    public boolean verifyPassword(String password) {
        return PasswordUtil.verifyPassword(password, this.password);
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
