import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class AdminFxApp extends Application {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private LibraryDatabase database;
    private AdminAuthService authService;
    private MemberAuthService memberAuthService;
    private LibraryService libraryService;
    private Stage primaryStage;
    private BorderPane shell;
    private StackPane contentPane;
    private Label titleLabel;
    private Label statusLabel;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        database = LibraryDatabase.withSampleData();
        authService = new AdminAuthService(database);
        memberAuthService = new MemberAuthService(database);
        libraryService = new LibraryService(database, authService);
        primaryStage = stage;
        primaryStage.setTitle("NewUU Lib");
        showLoginScene();
        primaryStage.show();
    }

    private void showLoginScene() {
        Node logo = createLogoMark(112);

        Label title = new Label("NewUU Lib");
        title.setStyle("-fx-font-size: 26px; -fx-font-weight: bold; -fx-text-fill: #111827;");

        Label subtitle = new Label("Choose Admin or Member login");
        subtitle.setStyle("-fx-font-size: 15px; -fx-text-fill: #4b5563;");

        ToggleGroup loginMode = new ToggleGroup();
        RadioButton adminMode = new RadioButton("Admin");
        adminMode.setToggleGroup(loginMode);
        adminMode.setSelected(true);
        RadioButton memberMode = new RadioButton("Member");
        memberMode.setToggleGroup(loginMode);
        HBox modeBox = new HBox(16, adminMode, memberMode);
        modeBox.setAlignment(Pos.CENTER);

        TextField adminIdField = new TextField("admin");
        adminIdField.setPromptText("Admin ID / Student ID");
        adminIdField.setPrefWidth(300);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(220);
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.setPromptText("Password");
        visiblePasswordField.setPrefWidth(220);
        visiblePasswordField.setManaged(false);
        visiblePasswordField.setVisible(false);
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());
        CheckBox showPassword = new CheckBox("Show");
        showPassword.setOnAction(event -> {
            boolean show = showPassword.isSelected();
            visiblePasswordField.setManaged(show);
            visiblePasswordField.setVisible(show);
            passwordField.setManaged(!show);
            passwordField.setVisible(!show);
        });
        HBox passwordBox = new HBox(8, passwordField, visiblePasswordField, showPassword);
        passwordBox.setAlignment(Pos.CENTER);

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #b91c1c;");

        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginButton.setPrefWidth(300);
        loginButton.setStyle(primaryButtonStyle());

        loginButton.setOnAction(event -> {
            String adminId = adminIdField.getText();
            String password = showPassword.isSelected() ? visiblePasswordField.getText() : passwordField.getText();
            boolean adminSelected = adminMode.isSelected();
            if (adminSelected && authService.login(adminId, password)) {
                showAdminScene();
            } else if (!adminSelected && memberAuthService.login(adminId, password)) {
                showMemberScene();
            } else {
                errorLabel.setText("Invalid login.");
                passwordField.clear();
                visiblePasswordField.clear();
            }
        });

        VBox form = new VBox(12, logo, title, subtitle, new Separator(), modeBox, adminIdField, passwordBox, loginButton, errorLabel);
        form.setAlignment(Pos.CENTER);
        form.setPadding(new Insets(34));
        form.setMaxWidth(390);
        form.setStyle(cardStyle());

        StackPane root = new StackPane(form);
        root.setPadding(new Insets(32));
        root.setStyle("-fx-background-color: linear-gradient(to bottom right, #f8fafc, #eef2f7);");

        primaryStage.setScene(new Scene(root, 980, 640));
    }

    private void showAdminScene() {
        shell = new BorderPane();
        contentPane = new StackPane();
        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #047857;");

        shell.setTop(createTopBar());
        shell.setLeft(createSidebar());
        shell.setCenter(contentPane);
        shell.setStyle("-fx-background-color: #f6f7f9;");

        Scene scene = new Scene(shell, 1220, 760);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(1040);
        primaryStage.setMinHeight(680);
        libraryService.processOverdueNotifications();
        showDashboardPage();
    }

    private void showMemberScene() {
        shell = new BorderPane();
        contentPane = new StackPane();
        titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        statusLabel = new Label();
        statusLabel.setStyle("-fx-text-fill: #047857;");

        shell.setTop(createMemberTopBar());
        shell.setLeft(createMemberSidebar());
        shell.setCenter(contentPane);
        shell.setStyle("-fx-background-color: #f6f7f9;");

        Scene scene = new Scene(shell, 1180, 740);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(980);
        primaryStage.setMinHeight(660);
        showMemberSearchPage();
    }

    private Node createTopBar() {
        Label adminLabel = new Label("Logged in: " + authService.getCurrentAdmin().getPerson().getName());
        adminLabel.setStyle("-fx-text-fill: #374151;");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            authService.logout();
            showLoginScene();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox titleBox = new VBox(3, titleLabel, statusLabel);
        HBox topBar = new HBox(16, titleBox, spacer, adminLabel, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 24, 14, 24));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #e5e7eb transparent;");
        return topBar;
    }

    private Node createMemberTopBar() {
        Label memberLabel = new Label("Logged in: " + memberAuthService.getCurrentMember().getName());
        memberLabel.setStyle("-fx-text-fill: #374151;");

        Button logoutButton = new Button("Logout");
        logoutButton.setOnAction(event -> {
            memberAuthService.logout();
            showLoginScene();
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        VBox titleBox = new VBox(3, titleLabel, statusLabel);
        HBox topBar = new HBox(16, titleBox, spacer, memberLabel, logoutButton);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(18, 24, 14, 24));
        topBar.setStyle("-fx-background-color: white; -fx-border-color: transparent transparent #e5e7eb transparent;");
        return topBar;
    }

    private Node createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(22, 16, 22, 16));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #24313d;");

        Label brand = new Label("NewUU Lib");
        brand.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label panel = new Label("Admin Panel");
        panel.setStyle("-fx-font-size: 12px; -fx-text-fill: #cbd5e1;");
        VBox brandText = new VBox(2, brand, panel);
        HBox brandBox = new HBox(10, createLogoMark(42), brandText);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Button dashboard = navButton("Dashboard");
        dashboard.setOnAction(event -> showDashboardPage());
        Button books = navButton("Books");
        books.setOnAction(event -> showBooksPage());
        Button users = navButton("Users");
        users.setOnAction(event -> showUsersPage());
        Button issue = navButton("Issue Book");
        issue.setOnAction(event -> showIssuePage());
        Button returns = navButton("Return Book");
        returns.setOnAction(event -> showReturnPage());
        Button reports = navButton("Reports");
        reports.setOnAction(event -> showReportsPage());
        Button notifications = navButton("Notifications");
        notifications.setOnAction(event -> showAdminNotificationsPage());

        sidebar.getChildren().addAll(brandBox, new Separator(), dashboard, books, users, issue, returns, reports, notifications);
        return sidebar;
    }

    private Node createMemberSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(22, 16, 22, 16));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #1f4f46;");

        Label brand = new Label("NewUU Lib");
        brand.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label panel = new Label("Member Panel");
        panel.setStyle("-fx-font-size: 12px; -fx-text-fill: #ccfbf1;");
        VBox brandText = new VBox(2, brand, panel);
        HBox brandBox = new HBox(10, createLogoMark(42), brandText);
        brandBox.setAlignment(Pos.CENTER_LEFT);

        Button search = navButton("Search Catalog");
        search.setOnAction(event -> showMemberSearchPage());
        Button checkout = navButton("Check Out");
        checkout.setOnAction(event -> showMemberCheckoutPage());
        Button reserve = navButton("Reserve");
        reserve.setOnAction(event -> showMemberReservePage());
        Button renew = navButton("Renew");
        renew.setOnAction(event -> showMemberRenewPage());
        Button returns = navButton("Return");
        returns.setOnAction(event -> showMemberReturnPage());
        Button notifications = navButton("Notifications");
        notifications.setOnAction(event -> showMemberNotificationsPage());

        sidebar.getChildren().addAll(brandBox, new Separator(), search, checkout, reserve, renew, returns, notifications);
        return sidebar;
    }

    private Node createLogoMark(double size) {
        StackPane logo = new StackPane();
        logo.setMinSize(size, size);
        logo.setPrefSize(size, size);
        logo.setMaxSize(size, size);

        Rectangle background = new Rectangle(size, size);
        background.setArcWidth(size * 0.14);
        background.setArcHeight(size * 0.14);
        background.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#00195a")),
                new Stop(1, Color.web("#020817"))));

        Polygon mark = new Polygon(
                size * 0.32, size * 0.76,
                size * 0.32, size * 0.31,
                size * 0.47, size * 0.16,
                size * 0.53, size * 0.16,
                size * 0.53, size * 0.54,
                size * 0.43, size * 0.64,
                size * 0.43, size * 0.72,
                size * 0.63, size * 0.72,
                size * 0.78, size * 0.58,
                size * 0.86, size * 0.58,
                size * 0.70, size * 0.76
        );
        mark.setFill(new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#53a8ff")),
                new Stop(1, Color.web("#1e63d6"))));

        logo.getChildren().addAll(background, mark);
        return logo;
    }

    private Button navButton(String text) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setAlignment(Pos.CENTER_LEFT);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #f9fafb; -fx-font-size: 14px; -fx-padding: 10 12;");
        button.setOnMouseEntered(event -> button.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 10 12;"));
        button.setOnMouseExited(event -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: #f9fafb; -fx-font-size: 14px; -fx-padding: 10 12;"));
        return button;
    }

    private void showDashboardPage() {
        setPageTitle("Dashboard");
        DashboardStats stats = libraryService.getDashboardStats();

        GridPane cards = new GridPane();
        cards.setHgap(14);
        cards.setVgap(14);
        cards.add(dashboardCard("Total Books", stats.getTotalBooks()), 0, 0);
        cards.add(dashboardCard("Total Users", stats.getTotalUsers()), 1, 0);
        cards.add(dashboardCard("Issued Books", stats.getIssuedBooks()), 2, 0);
        cards.add(dashboardCard("Returned Books", stats.getReturnedBooks()), 3, 0);
        cards.add(dashboardCard("Overdue Books", stats.getOverdueBooks()), 4, 0);

        TableView<ActivityRecord> activitiesTable = new TableView<>();
        activitiesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        TableColumn<ActivityRecord, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDateTime(data.getValue().getTimestamp())));
        timeColumn.setPrefWidth(160);
        TableColumn<ActivityRecord, String> activityColumn = new TableColumn<>("Activity");
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        activitiesTable.getColumns().add(timeColumn);
        activitiesTable.getColumns().add(activityColumn);
        activitiesTable.setItems(FXCollections.observableArrayList(libraryService.getRecentActivities(12)));

        VBox body = new VBox(18, cards, sectionTitle("Recent Activities"), activitiesTable);
        VBox.setVgrow(activitiesTable, Priority.ALWAYS);
        showContent(body);
    }

    private Node dashboardCard(String label, int value) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-text-fill: #4b5563; -fx-font-size: 13px;");
        Label valueNode = new Label(String.valueOf(value));
        valueNode.setStyle("-fx-text-fill: #111827; -fx-font-size: 28px; -fx-font-weight: bold;");
        VBox card = new VBox(8, labelNode, valueNode);
        card.setPadding(new Insets(18));
        card.setMinWidth(170);
        card.setStyle(cardStyle());
        return card;
    }

    private void showBooksPage() {
        setPageTitle("Book Management");
        TableView<BookRecord> table = createBooksTable();
        TextField search = new TextField();
        search.setPromptText("Search title, author, category, ISBN, publisher, year, or ID");
        search.textProperty().addListener((obs, oldValue, newValue) ->
                table.setItems(FXCollections.observableArrayList(libraryService.searchBooks(newValue))));

        Button addButton = new Button("Add Book");
        addButton.setStyle(primaryButtonStyle());
        addButton.setOnAction(event -> openBookDialog(null, table));

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            BookRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a book to edit.");
                return;
            }
            openBookDialog(selected, table);
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> deleteSelectedBook(table));

        table.setItems(FXCollections.observableArrayList(libraryService.listBooks()));
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                openBookDialog(table.getSelectionModel().getSelectedItem(), table);
            }
        });

        HBox toolbar = new HBox(10, search, addButton, editButton, deleteButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(search, Priority.ALWAYS);

        VBox body = new VBox(12, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private TableView<BookRecord> createBooksTable() {
        TableView<BookRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(propertyColumn("ID", "bookId", 80));
        table.getColumns().add(propertyColumn("Title", "title", 210));
        table.getColumns().add(propertyColumn("Author", "author", 170));
        table.getColumns().add(propertyColumn("Category", "category", 140));
        table.getColumns().add(propertyColumn("ISBN", "isbn", 140));
        table.getColumns().add(propertyColumn("Publisher", "publisher", 150));
        table.getColumns().add(propertyColumn("Year", "year", 70));
        table.getColumns().add(propertyColumn("Total", "totalQuantity", 70));
        table.getColumns().add(propertyColumn("Available", "availableCopies", 85));
        return table;
    }

    private void openBookDialog(BookRecord existing, TableView<BookRecord> table) {
        Dialog<BookFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add Book" : "Edit Book");
        dialog.setHeaderText(existing == null ? "Add a new library book" : "Edit " + existing.getTitle());

        TextField id = new TextField(existing == null ? "" : existing.getBookId());
        id.setDisable(existing != null);
        TextField title = new TextField(existing == null ? "" : existing.getTitle());
        TextField author = new TextField(existing == null ? "" : existing.getAuthor());
        TextField category = new TextField(existing == null ? "" : existing.getCategory());
        TextField isbn = new TextField(existing == null ? "" : existing.getIsbn());
        TextField publisher = new TextField(existing == null ? "" : existing.getPublisher());
        TextField year = new TextField(existing == null ? "" : String.valueOf(existing.getYear()));
        TextField quantity = new TextField(existing == null ? "" : String.valueOf(existing.getTotalQuantity()));

        GridPane form = formGrid();
        addFormRow(form, 0, "Book ID", id);
        addFormRow(form, 1, "Title", title);
        addFormRow(form, 2, "Author", author);
        addFormRow(form, 3, "Category", category);
        addFormRow(form, 4, "ISBN", isbn);
        addFormRow(form, 5, "Publisher", publisher);
        addFormRow(form, 6, "Year", year);
        addFormRow(form, 7, "Quantity", quantity);
        dialog.getDialogPane().setContent(form);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == saveType) {
                return new BookFormData(id.getText(), title.getText(), author.getText(), category.getText(),
                        isbn.getText(), publisher.getText(), year.getText(), quantity.getText());
            }
            return null;
        });

        Optional<BookFormData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            try {
                if (existing == null) {
                    libraryService.addBook(data.id, data.title, data.author, data.category, data.isbn,
                            data.publisher, data.yearAsInt(), data.quantityAsInt());
                    showStatus("Book added successfully.");
                } else {
                    libraryService.editBook(existing.getBookId(), data.title, data.author, data.category,
                            data.isbn, data.publisher, data.yearAsInt(), data.quantityAsInt());
                    showStatus("Book updated successfully.");
                }
                table.setItems(FXCollections.observableArrayList(libraryService.listBooks()));
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void deleteSelectedBook(TableView<BookRecord> table) {
        BookRecord selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a book to delete.");
            return;
        }
        if (!confirm("Delete book", "Delete " + selected.getTitle() + "?")) {
            return;
        }
        try {
            libraryService.deleteBook(selected.getBookId());
            table.setItems(FXCollections.observableArrayList(libraryService.listBooks()));
            showStatus("Book deleted.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void showUsersPage() {
        setPageTitle("User / Student Management");
        TableView<StudentRecord> table = createStudentsTable();
        TextField search = new TextField();
        search.setPromptText("Search name, ID, email, department, or phone");
        search.textProperty().addListener((obs, oldValue, newValue) ->
                table.setItems(FXCollections.observableArrayList(libraryService.searchStudents(newValue))));

        Button addButton = new Button("Add User");
        addButton.setStyle(primaryButtonStyle());
        addButton.setOnAction(event -> openStudentDialog(null, table));

        Button editButton = new Button("Edit");
        editButton.setOnAction(event -> {
            StudentRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a user to edit.");
                return;
            }
            openStudentDialog(selected, table);
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> deleteSelectedStudent(table));

        table.setItems(FXCollections.observableArrayList(libraryService.listStudents()));
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && table.getSelectionModel().getSelectedItem() != null) {
                openStudentDialog(table.getSelectionModel().getSelectedItem(), table);
            }
        });

        HBox toolbar = new HBox(10, search, addButton, editButton, deleteButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(search, Priority.ALWAYS);

        VBox body = new VBox(12, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private TableView<StudentRecord> createStudentsTable() {
        TableView<StudentRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(propertyColumn("ID", "userId", 90));
        table.getColumns().add(propertyColumn("Name", "name", 190));
        table.getColumns().add(propertyColumn("Email", "email", 230));
        table.getColumns().add(propertyColumn("Department", "department", 190));
        table.getColumns().add(propertyColumn("Phone", "phone", 130));
        table.getColumns().add(propertyColumn("Password", "passwordStatus", 95));
        return table;
    }

    private void openStudentDialog(StudentRecord existing, TableView<StudentRecord> table) {
        Dialog<StudentFormData> dialog = new Dialog<>();
        dialog.setTitle(existing == null ? "Add User" : "Edit User");
        dialog.setHeaderText(existing == null ? "Add a new user/student" : "Edit " + existing.getName());

        TextField id = new TextField(existing == null ? "" : existing.getUserId());
        id.setDisable(existing != null);
        TextField name = new TextField(existing == null ? "" : existing.getName());
        TextField email = new TextField(existing == null ? "" : existing.getEmail());
        TextField department = new TextField(existing == null ? "" : existing.getDepartment());
        TextField phone = new TextField(existing == null ? "" : existing.getPhone());
        TextField password = new TextField(existing == null ? "" : StudentRecord.defaultPasswordFor(existing.getUserId(), existing.getName()));

        GridPane form = formGrid();
        addFormRow(form, 0, "User ID", id);
        addFormRow(form, 1, "Name", name);
        addFormRow(form, 2, "Email", email);
        addFormRow(form, 3, "Department", department);
        addFormRow(form, 4, "Phone", phone);
        addFormRow(form, 5, "Password", password);
        dialog.getDialogPane().setContent(form);

        ButtonType saveType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);
        dialog.setResultConverter(button -> {
            if (button == saveType) {
                return new StudentFormData(id.getText(), name.getText(), email.getText(), department.getText(), phone.getText(), password.getText());
            }
            return null;
        });

        Optional<StudentFormData> result = dialog.showAndWait();
        result.ifPresent(data -> {
            try {
                if (existing == null) {
                    libraryService.addStudent(data.id, data.name, data.email, data.department, data.phone, data.password);
                    showStatus("User added successfully.");
                } else {
                    libraryService.editStudent(existing.getUserId(), data.name, data.email, data.department, data.phone, data.password);
                    showStatus("User updated successfully.");
                }
                table.setItems(FXCollections.observableArrayList(libraryService.listStudents()));
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
    }

    private void deleteSelectedStudent(TableView<StudentRecord> table) {
        StudentRecord selected = table.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select a user to delete.");
            return;
        }
        if (!confirm("Delete user", "Delete " + selected.getName() + "?")) {
            return;
        }
        try {
            libraryService.deleteStudent(selected.getUserId());
            table.setItems(FXCollections.observableArrayList(libraryService.listStudents()));
            showStatus("User deleted.");
        } catch (RuntimeException ex) {
            showError(ex.getMessage());
        }
    }

    private void showIssuePage() {
        setPageTitle("Issue Book");
        ComboBox<BookRecord> bookBox = new ComboBox<>();
        bookBox.setConverter(bookConverter());
        bookBox.setMaxWidth(Double.MAX_VALUE);
        bookBox.setItems(FXCollections.observableArrayList(libraryService.listBooks()));

        ComboBox<StudentRecord> studentBox = new ComboBox<>();
        studentBox.setConverter(studentConverter());
        studentBox.setMaxWidth(Double.MAX_VALUE);
        studentBox.setItems(FXCollections.observableArrayList(libraryService.listStudents()));

        DatePicker dueDate = new DatePicker(LocalDate.now().plusDays(14));
        dueDate.setMaxWidth(Double.MAX_VALUE);

        Button issueButton = new Button("Issue Book");
        issueButton.setStyle(primaryButtonStyle());
        issueButton.setOnAction(event -> {
            BookRecord book = bookBox.getValue();
            StudentRecord student = studentBox.getValue();
            if (book == null || student == null) {
                showError("Choose both a book and a user.");
                return;
            }
            try {
                LoanRecord loan = libraryService.issueBook(book.getBookId(), student.getUserId(), dueDate.getValue());
                showInfo("Book issued", "Loan ID: " + loan.getLoanId() + "\nDue date: " + loan.getDueDate());
                showIssuePage();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });

        GridPane form = formGrid();
        addFormRow(form, 0, "Book", bookBox);
        addFormRow(form, 1, "User", studentBox);
        addFormRow(form, 2, "Due Date", dueDate);
        form.add(issueButton, 1, 3);

        TableView<BookRecord> availability = createBooksTable();
        availability.setItems(FXCollections.observableArrayList(libraryService.listBooks()));

        VBox body = new VBox(18, wrapCard(form), sectionTitle("Book Availability"), availability);
        VBox.setVgrow(availability, Priority.ALWAYS);
        showContent(body);
    }

    private void showReturnPage() {
        setPageTitle("Return Book");
        TableView<LoanRecord> table = createLoansTable();
        table.setItems(FXCollections.observableArrayList(libraryService.getIssuedLoans()));

        DatePicker returnDate = new DatePicker(LocalDate.now());
        Button returnButton = new Button("Return Selected Book");
        returnButton.setStyle(primaryButtonStyle());
        returnButton.setOnAction(event -> {
            LoanRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select an issued book to return.");
                return;
            }
            try {
                LoanRecord returned = libraryService.returnBook(selected.getLoanId(), returnDate.getValue());
                showInfo("Return complete", "Fine amount: $" + String.format("%.2f", returned.getFineAmount()));
                showReturnPage();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });

        HBox toolbar = new HBox(10, new Label("Return Date:"), returnDate, returnButton);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        VBox body = new VBox(12, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showReportsPage() {
        setPageTitle("Reports / Records");
        TabPane tabs = new TabPane();
        tabs.getTabs().add(tab("Issued Books", loansPane(libraryService.getIssuedLoans())));
        tabs.getTabs().add(tab("Returned Books", loansPane(libraryService.getReturnedLoans())));
        tabs.getTabs().add(tab("Overdue Books", loansPane(libraryService.getOverdueLoans())));
        tabs.getTabs().add(tab("Book Availability", booksPane(libraryService.listBooks())));
        tabs.getTabs().add(tab("Borrowing History", historyPane()));
        showContent(tabs);
    }

    private void showAdminNotificationsPage() {
        setPageTitle("System Notifications");
        TableView<NotificationRecord> table = createNotificationsTable();
        table.setItems(FXCollections.observableArrayList(libraryService.getAllNotifications()));
        Button process = new Button("Process Overdue Notifications");
        process.setStyle(primaryButtonStyle());
        process.setOnAction(event -> {
            libraryService.processOverdueNotifications();
            table.setItems(FXCollections.observableArrayList(libraryService.getAllNotifications()));
            showStatus("Notifications updated.");
        });
        VBox body = new VBox(12, process, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showMemberNotificationsPage() {
        setPageTitle("My Notifications");
        TableView<NotificationRecord> table = createNotificationsTable();
        table.setItems(FXCollections.observableArrayList(
                libraryService.getMemberNotifications(memberAuthService.getCurrentMember().getUserId())));
        VBox body = new VBox(12, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showMemberSearchPage() {
        setPageTitle("Search Catalog");
        TableView<BookRecord> table = createBooksTable();
        TextField search = new TextField();
        search.setPromptText("Search title, author, category, ISBN, publisher, year, or ID");
        table.setItems(FXCollections.observableArrayList(libraryService.listBooksForMember()));
        search.textProperty().addListener((obs, oldValue, newValue) ->
                table.setItems(FXCollections.observableArrayList(libraryService.searchBooksForMember(newValue))));
        VBox body = new VBox(12, search, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showMemberCheckoutPage() {
        setPageTitle("Check-out Book");
        TableView<BookRecord> table = createBooksTable();
        table.setItems(FXCollections.observableArrayList(libraryService.listBooksForMember()));
        Button checkout = new Button("Check Out Selected");
        checkout.setStyle(primaryButtonStyle());
        checkout.setOnAction(event -> {
            BookRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a book first.");
                return;
            }
            try {
                LoanRecord loan = libraryService.checkoutBookForMember(memberAuthService.getCurrentMember().getUserId(), selected.getBookId());
                showInfo("Checked out", "Loan ID: " + loan.getLoanId() + "\nDue date: " + loan.getDueDate());
                showMemberCheckoutPage();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
        VBox body = new VBox(12, checkout, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showMemberReservePage() {
        setPageTitle("Reserve Book");
        TableView<BookRecord> table = createBooksTable();
        table.setItems(FXCollections.observableArrayList(libraryService.listBooksForMember()));
        Button reserve = new Button("Reserve Selected");
        reserve.setStyle(primaryButtonStyle());
        reserve.setOnAction(event -> {
            BookRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a book first.");
                return;
            }
            try {
                ReservationRecord reservation = libraryService.reserveBookForMember(memberAuthService.getCurrentMember().getUserId(), selected.getBookId());
                showInfo("Reserved", "Reservation ID: " + reservation.getReservationId());
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
        VBox body = new VBox(12, reserve, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showMemberRenewPage() {
        setPageTitle("Renew Book");
        TableView<LoanRecord> table = createLoansTable();
        table.setItems(FXCollections.observableArrayList(libraryService.getMemberActiveLoans(memberAuthService.getCurrentMember().getUserId())));
        Button renew = new Button("Renew Selected");
        renew.setStyle(primaryButtonStyle());
        renew.setOnAction(event -> {
            LoanRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a loan first.");
                return;
            }
            try {
                LoanRecord renewed = libraryService.renewBookForMember(memberAuthService.getCurrentMember().getUserId(), selected.getLoanId());
                showInfo("Renewed", "New due date: " + renewed.getDueDate());
                showMemberRenewPage();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
        VBox body = new VBox(12, renew, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private void showMemberReturnPage() {
        setPageTitle("Return Book");
        TableView<LoanRecord> table = createLoansTable();
        table.setItems(FXCollections.observableArrayList(libraryService.getMemberActiveLoans(memberAuthService.getCurrentMember().getUserId())));
        Button returnButton = new Button("Return Selected");
        returnButton.setStyle(primaryButtonStyle());
        returnButton.setOnAction(event -> {
            LoanRecord selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showError("Select a loan first.");
                return;
            }
            try {
                LoanRecord returned = libraryService.returnBookForMember(memberAuthService.getCurrentMember().getUserId(), selected.getLoanId());
                showInfo("Returned", "Fine: $" + String.format("%.2f", returned.getFineAmount()));
                showMemberReturnPage();
            } catch (RuntimeException ex) {
                showError(ex.getMessage());
            }
        });
        VBox body = new VBox(12, returnButton, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        showContent(body);
    }

    private Node historyPane() {
        ComboBox<StudentRecord> studentBox = new ComboBox<>();
        studentBox.setConverter(studentConverter());
        studentBox.setItems(FXCollections.observableArrayList(libraryService.listStudents()));
        studentBox.setPrefWidth(360);

        TableView<LoanRecord> table = createLoansTable();
        studentBox.valueProperty().addListener((obs, oldValue, selected) -> {
            if (selected != null) {
                table.setItems(FXCollections.observableArrayList(libraryService.getBorrowingHistory(selected.getUserId())));
            }
        });
        if (!studentBox.getItems().isEmpty()) {
            studentBox.getSelectionModel().selectFirst();
        }

        HBox toolbar = new HBox(10, new Label("User:"), studentBox);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        VBox box = new VBox(12, toolbar, table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setPadding(new Insets(14));
        return box;
    }

    private Node loansPane(List<LoanRecord> loans) {
        TableView<LoanRecord> table = createLoansTable();
        table.setItems(FXCollections.observableArrayList(loans));
        VBox box = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setPadding(new Insets(14));
        return box;
    }

    private Node booksPane(List<BookRecord> books) {
        TableView<BookRecord> table = createBooksTable();
        table.setItems(FXCollections.observableArrayList(books));
        VBox box = new VBox(table);
        VBox.setVgrow(table, Priority.ALWAYS);
        box.setPadding(new Insets(14));
        return box;
    }

    private TableView<LoanRecord> createLoansTable() {
        TableView<LoanRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);

        TableColumn<LoanRecord, String> titleColumn = new TableColumn<>("Book Title");
        titleColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(libraryService.getBookTitle(data.getValue().getBookId())));
        titleColumn.setPrefWidth(210);

        TableColumn<LoanRecord, String> userColumn = new TableColumn<>("User Name");
        userColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(libraryService.getStudentName(data.getValue().getUserId())));
        userColumn.setPrefWidth(170);

        TableColumn<LoanRecord, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(data -> {
            LoanRecord loan = data.getValue();
            if (loan.getStatus() == LoanStatus.ISSUED && loan.isOverdue(LocalDate.now())) {
                return new ReadOnlyStringWrapper("OVERDUE");
            }
            return new ReadOnlyStringWrapper(loan.getStatus().toString());
        });

        TableColumn<LoanRecord, Double> fineColumn = new TableColumn<>("Fine");
        fineColumn.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue().getFineAmount()));

        table.getColumns().add(propertyColumn("Loan ID", "loanId", 90));
        table.getColumns().add(propertyColumn("Book ID", "bookId", 75));
        table.getColumns().add(titleColumn);
        table.getColumns().add(propertyColumn("User ID", "userId", 75));
        table.getColumns().add(userColumn);
        table.getColumns().add(propertyColumn("Issue Date", "issueDate", 105));
        table.getColumns().add(propertyColumn("Due Date", "dueDate", 105));
        table.getColumns().add(propertyColumn("Return Date", "returnDate", 105));
        table.getColumns().add(statusColumn);
        table.getColumns().add(fineColumn);
        return table;
    }

    private TableView<NotificationRecord> createNotificationsTable() {
        TableView<NotificationRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.getColumns().add(propertyColumn("ID", "notificationId", 70));
        table.getColumns().add(propertyColumn("User ID", "userId", 90));
        table.getColumns().add(propertyColumn("Channel", "channel", 90));
        TableColumn<NotificationRecord, String> timeColumn = new TableColumn<>("Created");
        timeColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(formatDateTime(data.getValue().getCreatedAt())));
        timeColumn.setPrefWidth(160);
        TableColumn<NotificationRecord, String> contentColumn = new TableColumn<>("Content");
        contentColumn.setCellValueFactory(new PropertyValueFactory<>("content"));
        contentColumn.setPrefWidth(500);
        table.getColumns().add(timeColumn);
        table.getColumns().add(contentColumn);
        return table;
    }

    private Tab tab(String title, Node content) {
        Tab tab = new Tab(title, content);
        tab.setClosable(false);
        return tab;
    }

    private <S, T> TableColumn<S, T> propertyColumn(String title, String property, int width) {
        TableColumn<S, T> column = new TableColumn<>(title);
        column.setCellValueFactory(new PropertyValueFactory<>(property));
        column.setPrefWidth(width);
        return column;
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setPadding(new Insets(8));
        return grid;
    }

    private void addFormRow(GridPane grid, int row, String label, Node input) {
        Label labelNode = new Label(label);
        labelNode.setStyle("-fx-font-weight: bold; -fx-text-fill: #374151;");
        grid.add(labelNode, 0, row);
        grid.add(input, 1, row);
        GridPane.setHgrow(input, Priority.ALWAYS);
        if (input instanceof Region) {
            ((Region) input).setPrefWidth(360);
        }
    }

    private StringConverter<BookRecord> bookConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(BookRecord book) {
                if (book == null) {
                    return "";
                }
                return book.getBookId() + " - " + book.getTitle() + " (" + book.getAvailableCopies() + " available)";
            }

            @Override
            public BookRecord fromString(String text) {
                return null;
            }
        };
    }

    private StringConverter<StudentRecord> studentConverter() {
        return new StringConverter<>() {
            @Override
            public String toString(StudentRecord student) {
                if (student == null) {
                    return "";
                }
                return student.getUserId() + " - " + student.getName() + " (" + student.getDepartment() + ")";
            }

            @Override
            public StudentRecord fromString(String text) {
                return null;
            }
        };
    }

    private Node sectionTitle(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #111827;");
        return label;
    }

    private Node wrapCard(Node content) {
        VBox card = new VBox(content);
        card.setPadding(new Insets(18));
        card.setStyle(cardStyle());
        return card;
    }

    private void showContent(Node content) {
        BorderPane page = new BorderPane(content);
        page.setPadding(new Insets(22));
        contentPane.getChildren().setAll(page);
    }

    private void setPageTitle(String title) {
        titleLabel.setText(title);
        statusLabel.setText("");
    }

    private void showStatus(String message) {
        statusLabel.setText(message);
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Action could not be completed");
        alert.setContentText(message == null || message.isBlank() ? "Unknown error." : message);
        alert.showAndWait();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DATE_TIME_FORMAT);
    }

    private String cardStyle() {
        return "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 8; "
                + "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(17,24,39,0.08), 14, 0, 0, 3);";
    }

    private String primaryButtonStyle() {
        return "-fx-background-color: #0f766e; -fx-text-fill: white; -fx-font-weight: bold; "
                + "-fx-background-radius: 6; -fx-padding: 8 14;";
    }

    private static class BookFormData {
        private final String id;
        private final String title;
        private final String author;
        private final String category;
        private final String isbn;
        private final String publisher;
        private final String year;
        private final String quantity;

        private BookFormData(String id, String title, String author, String category,
                             String isbn, String publisher, String year, String quantity) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.category = category;
            this.isbn = isbn;
            this.publisher = publisher;
            this.year = year;
            this.quantity = quantity;
        }

        private int yearAsInt() {
            return Integer.parseInt(year.trim());
        }

        private int quantityAsInt() {
            return Integer.parseInt(quantity.trim());
        }
    }

    private static class StudentFormData {
        private final String id;
        private final String name;
        private final String email;
        private final String department;
        private final String phone;
        private final String password;

        private StudentFormData(String id, String name, String email, String department, String phone, String password) {
            this.id = id;
            this.name = name;
            this.email = email;
            this.department = department;
            this.phone = phone;
            this.password = password;
        }
    }
}
