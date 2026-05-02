public class CheckoutService {

    public static boolean checkoutBook(Member member, BookItem book) {

        if (!book.isAvailable()) {
            System.out.println("Book is not available");
            return false;
        }

        if (!member.canCheckoutBook()) {
            System.out.println("Checkout limit reached");
            return false;
        }

        if (ReservationService.isReservedByAnotherMember(book, member)) {
            System.out.println("Book is reserved by another member");
            return false;
        }

        BookLending lending = new BookLending(member, book);

        book.setStatus(BookStatus.LOANED);

        member.incrementCheckedoutBookCount();

        if (book.getReservation() != null) {
            book.getReservation().completeReservation();
        }

        System.out.println("Checkout successful. Due date: " + lending.getDueDate());

        return true;
    }
}
