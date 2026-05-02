public class DashboardStats {
    private final int totalBooks;
    private final int totalUsers;
    private final int issuedBooks;
    private final int returnedBooks;
    private final int overdueBooks;

    public DashboardStats(int totalBooks, int totalUsers, int issuedBooks,
                          int returnedBooks, int overdueBooks) {
        this.totalBooks = totalBooks;
        this.totalUsers = totalUsers;
        this.issuedBooks = issuedBooks;
        this.returnedBooks = returnedBooks;
        this.overdueBooks = overdueBooks;
    }

    public int getTotalBooks() { return totalBooks; }
    public int getTotalUsers() { return totalUsers; }
    public int getIssuedBooks() { return issuedBooks; }
    public int getReturnedBooks() { return returnedBooks; }
    public int getOverdueBooks() { return overdueBooks; }
}
