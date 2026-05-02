public class ReservationService {

    public static boolean isReservedByAnotherMember(BookItem book, Member currentMember) {

        if (book == null) return false;

        BookReservation reservation = book.getReservation();

        if (reservation == null) return false;

        return reservation.isActive() && reservation.getMember() != currentMember;
    }
}
