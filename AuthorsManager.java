import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AuthorsManager extends Application {
    private TableView<AuthorRow> tableView = new TableView<>();
    private TextField firstNameField = new TextField();
    private TextField lastNameField = new TextField();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        TableColumn<AuthorRow, Integer> idColumn = new TableColumn<>("AuthorID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("authorID"));

        TableColumn<AuthorRow, String> firstNameColumn = new TableColumn<>("FirstName");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<AuthorRow, String> lastNameColumn = new TableColumn<>("LastName");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        tableView.getColumns().addAll(idColumn, firstNameColumn, lastNameColumn);

        firstNameField.setPromptText("First Name");
        lastNameField.setPromptText("Last Name");

        Button addButton = new Button("Add");
        Button updateButton = new Button("Update");
        Button deleteButton = new Button("Delete");

        addButton.setOnAction(event -> addAuthor());
        updateButton.setOnAction(event -> updateAuthor());
        deleteButton.setOnAction(event -> deleteAuthor());

        tableView.setOnMouseClicked(event -> {
            AuthorRow selected = tableView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                firstNameField.setText(selected.getFirstName());
                lastNameField.setText(selected.getLastName());
            }
        });

        HBox form = new HBox(10, firstNameField, lastNameField, addButton, updateButton, deleteButton);
        VBox root = new VBox(10, tableView, form);
        root.setPadding(new Insets(15));

        loadAuthors();

        stage.setTitle("Authors Manager");
        stage.setScene(new Scene(root, 550, 400));
        stage.show();
    }

    private void loadAuthors() {
        List<AuthorRow> list = new ArrayList<>();
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Authors");
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                list.add(new AuthorRow(
                        resultSet.getInt("AuthorID"),
                        resultSet.getString("FirstName"),
                        resultSet.getString("LastName")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
        tableView.getItems().setAll(list);
    }

    private void addAuthor() {
        String sql = "INSERT INTO Authors (FirstName, LastName) VALUES (?, ?)";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, firstNameField.getText());
            preparedStatement.setString(2, lastNameField.getText());
            preparedStatement.executeUpdate();
            firstNameField.clear();
            lastNameField.clear();
            loadAuthors();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void updateAuthor() {
        AuthorRow selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String sql = "UPDATE Authors SET FirstName=?, LastName=? WHERE AuthorID=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, firstNameField.getText());
            preparedStatement.setString(2, lastNameField.getText());
            preparedStatement.setInt(3, selected.getAuthorID());
            preparedStatement.executeUpdate();
            loadAuthors();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void deleteAuthor() {
        AuthorRow selected = tableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        String sql = "DELETE FROM Authors WHERE AuthorID=?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setInt(1, selected.getAuthorID());
            preparedStatement.executeUpdate();
            firstNameField.clear();
            lastNameField.clear();
            loadAuthors();
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }
}
