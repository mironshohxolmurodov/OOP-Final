import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Library {
    private final String name;
    private final Catalog catalog;
    private final List<Member> members;
    private final List<Librarian> librarians;
    private final List<BookLending> lendings;

    public Library(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Library name cannot be empty");
        }
        this.name = name;
        this.catalog = new Catalog();
        this.members = new ArrayList<>();
        this.librarians = new ArrayList<>();
        this.lendings = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public Catalog getCatalog() {
        return catalog;
    }

    public List<Member> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public List<Librarian> getLibrarians() {
        return Collections.unmodifiableList(librarians);
    }

    public List<BookLending> getLendings() {
        return Collections.unmodifiableList(lendings);
    }

    public boolean addBookItem(BookItem bookItem) {
        return catalog.addBookItem(bookItem);
    }

    public boolean registerMember(Member member) {
        if (member == null || findMember(member.getId()) != null) {
            return false;
        }
        members.add(member);
        return true;
    }

    public boolean registerLibrarian(Librarian librarian) {
        if (librarian == null || findLibrarian(librarian.getId()) != null) {
            return false;
        }
        librarians.add(librarian);
        return true;
    }

    public Member findMember(String memberId) {
        if (memberId == null) {
            return null;
        }
        for (Member member : members) {
            if (member.getId().equalsIgnoreCase(memberId.trim())) {
                return member;
            }
        }
        return null;
    }

    public Librarian findLibrarian(String librarianId) {
        if (librarianId == null) {
            return null;
        }
        for (Librarian librarian : librarians) {
            if (librarian.getId().equalsIgnoreCase(librarianId.trim())) {
                return librarian;
            }
        }
        return null;
    }

    public boolean checkoutBook(String memberId, String barcode) {
        Member member = findMember(memberId);
        BookItem bookItem = catalog.findByBarcode(barcode);
        if (member == null || bookItem == null) {
            return false;
        }
        boolean checkedOut = CheckoutService.checkoutBook(member, bookItem);
        if (checkedOut) {
            lendings.add(new BookLending(member, bookItem));
        }
        return checkedOut;
    }

    public boolean returnBook(String barcode) {
        BookLending lending = findActiveLending(barcode);
        if (lending == null) {
            return false;
        }
        ReturnService.returnBook(lending);
        return true;
    }

    public List<BookItem> searchByTitle(String title) {
        return catalog.searchByTitle(title);
    }

    public List<BookItem> searchByAuthor(String authorName) {
        return catalog.searchByAuthor(authorName);
    }

    public List<BookItem> searchBySubject(String subject) {
        return catalog.searchBySubject(subject);
    }

    public List<BookItem> searchByPublicationDate(LocalDate publicationDate) {
        return catalog.searchByPublicationDate(publicationDate);
    }

    public boolean renewBook(String barcode) {
        BookLending lending = findActiveLending(barcode);
        if (lending == null) {
            return false;
        }
        if (ReservationService.isReservedByAnotherMember(lending.getBook(), lending.getMember())) {
            return false;
        }
        return lending.renew();
    }

    private BookLending findActiveLending(String barcode) {
        if (barcode == null) {
            return null;
        }
        for (BookLending lending : lendings) {
            if (lending.getBook().getBarcode().equalsIgnoreCase(barcode.trim())
                    && lending.getBook().getStatus() == BookStatus.LOANED) {
                return lending;
            }
        }
        return null;
    }
}
