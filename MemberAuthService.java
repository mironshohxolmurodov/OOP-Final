public class MemberAuthService {
    private final LibraryDatabase database;
    private StudentRecord currentMember;

    public MemberAuthService(LibraryDatabase database) {
        this.database = database;
    }

    public boolean login(String userId, String password) {
        StudentRecord member = database.getStudent(userId);
        if (member == null || !member.verifyPassword(password)) {
            return false;
        }
        currentMember = member;
        database.recordActivity("Member " + currentMember.getUserId() + " logged in");
        return true;
    }

    public void logout() {
        if (currentMember != null) {
            database.recordActivity("Member " + currentMember.getUserId() + " logged out");
        }
        currentMember = null;
    }

    public StudentRecord getCurrentMember() {
        return currentMember;
    }

    public boolean isAuthenticated() {
        return currentMember != null;
    }
}
