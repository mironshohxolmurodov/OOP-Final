import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class AuthorsBrowser {
    public static void main(String[] args) {
        System.out.println("All authors:");
        try (Connection connection = DatabaseUtil.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM Authors")) {

            while (resultSet.next()) {
                System.out.println("ID: " + resultSet.getInt("AuthorID") + " | "
                        + resultSet.getString("FirstName") + " "
                        + resultSet.getString("LastName"));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter last-name prefix to search: ");
        String prefix = scanner.nextLine();

        String sql = "SELECT * FROM Authors WHERE LastName LIKE ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, prefix + "%");
            ResultSet resultSet = preparedStatement.executeQuery();
            boolean found = false;

            while (resultSet.next()) {
                found = true;
                System.out.println("ID: " + resultSet.getInt("AuthorID") + " | "
                        + resultSet.getString("FirstName") + " "
                        + resultSet.getString("LastName"));
            }

            if (!found) {
                System.out.println("No results found.");
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
