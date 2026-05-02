import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Student5Demo {
    public static void main(String[] args) {
        Library library = new Library("City Central Library");

        Address address = new Address("12 Main Street", "Tashkent", "Tashkent", "100000", "Uzbekistan");
        Person librarianPerson = new Person("Student 5 Librarian", address, "librarian@example.com", "998901112233");
        Person memberPerson = new Person("Ali Karimov", address, "ali@example.com", "998901234567");

        Librarian librarian = new Librarian("LIB-5", "Library@123", librarianPerson);
        Member member = new Member("MEM-5", "Member@123", memberPerson, new Date());

        library.registerLibrarian(librarian);
        library.registerMember(member);

        Author robertMartin = new Author("Robert C. Martin", "Software engineering author");
        Author joshuaBloch = new Author("Joshua Bloch", "Java author");
        Author silberschatz = new Author("Abraham Silberschatz", "Database author");

        Rack programmingRack = new Rack(1, "Programming Section");
        Rack databaseRack = new Rack(2, "Database Section");

        library.addBookItem(new BookItem(
                "BC-1001", false, 45.0, BookFormat.PAPERBACK,
                LocalDate.of(2008, 8, 1), LocalDate.now(), programmingRack,
                "9780132350884", "Clean Code", "Programming",
                "Prentice Hall", "English", 464, Arrays.asList(robertMartin)
        ));

        library.addBookItem(new BookItem(
                "BC-1002", false, 55.0, BookFormat.HARDCOVER,
                LocalDate.of(2018, 1, 6), LocalDate.now(), programmingRack,
                "9780134685991", "Effective Java", "Programming",
                "Addison-Wesley", "English", 416, Arrays.asList(joshuaBloch)
        ));

        library.addBookItem(new BookItem(
                "BC-1003", false, 60.0, BookFormat.HARDCOVER,
                LocalDate.of(2010, 1, 1), LocalDate.now(), databaseRack,
                "9780073523323", "Database System Concepts", "Databases",
                "McGraw-Hill", "English", 1376, Arrays.asList(silberschatz)
        ));

        System.out.println("Student 5 Search, Catalog and Integration Demo");
        System.out.println("Library: " + library.getName());
        System.out.println("Total catalog items: " + library.getCatalog().getTotalItems());
        System.out.println("Available catalog items: " + library.getCatalog().getAvailableItemsCount());

        printResults("Search by title: Java", library.searchByTitle("Java"));
        printResults("Search by author: Martin", library.searchByAuthor("Martin"));
        printResults("Search by subject: Programming", library.searchBySubject("Programming"));
        printResults("Search by publication date: 2010-01-01", library.searchByPublicationDate(LocalDate.of(2010, 1, 1)));

        System.out.println();
        System.out.println("Checkout BC-1002 to MEM-5");
        System.out.println("Checkout result: " + library.checkoutBook("MEM-5", "BC-1002"));
        System.out.println("BC-1002 status after checkout: " + library.getCatalog().findByBarcode("BC-1002").getStatus());

        System.out.println();
        System.out.println("Renew BC-1002");
        System.out.println("Renew result: " + library.renewBook("BC-1002"));

        System.out.println();
        System.out.println("Return BC-1002");
        System.out.println("Return result: " + library.returnBook("BC-1002"));
        System.out.println("BC-1002 status after return: " + library.getCatalog().findByBarcode("BC-1002").getStatus());
    }

    private static void printResults(String heading, List<BookItem> results) {
        System.out.println();
        System.out.println(heading);
        if (results.isEmpty()) {
            System.out.println("No books found.");
            return;
        }
        for (BookItem bookItem : results) {
            System.out.println(bookItem.getBarcode() + " | " + bookItem.getTitle()
                    + " | " + bookItem.getSubject() + " | " + bookItem.getStatus());
        }
    }
}
