public class Librarian extends Account {

    public Librarian(String id, String password, Person person) {
        super(id, password, person);
    }

    public boolean addBookItem(Object bookItem) {

        System.out.println("[Librarian] addBookItem called for: " + bookItem);
        return true;
    }

    public boolean blockMember(Member member) {
        if (member == null) return false;
        member.setStatus(AccountStatus.BLACKLISTED);
        System.out.println("[Librarian] Blocked member: " + member.getId());
        return true;
    }

    public boolean unblockMember(Member member) {
        if (member == null) return false;
        member.setStatus(AccountStatus.ACTIVE);
        System.out.println("[Librarian] Unblocked member: " + member.getId());
        return true;
    }

    @Override
    public String toString() {
        return "Librarian{id='" + getId() + "', name='" + getPerson().getName()
                + "', status=" + getStatus() + "}";
    }
}
