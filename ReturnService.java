public class ReturnService {

    public static void returnBook(BookLending lending) {

        lending.returnBook();

        BookItem book = lending.getBook();
        Member member = lending.getMember();

        member.decrementCheckedoutBookCount();

        if (lending.isOverdue()) {
            System.out.println("Book returned late. Fine should be applied.");
        }

        BookReservation reservation = book.getReservation();

        if (reservation != null && reservation.isActive()) {
            book.setStatus(BookStatus.RESERVED);
            System.out.println("Book reserved. Notify member.");
        } else {
            book.setStatus(BookStatus.AVAILABLE);
        }

        System.out.println("Return completed.");
    }
}
