
package andoksfooddeliverysystem;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Comparator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


import java.util.Optional;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javax.swing.JOptionPane;

public class RiderManagement {
    private VBox root;
    private int adminId; // the logged-in admin's ID

    // Color palette constants
    private static final String PRIMARY_RED = "#D50000";
    private static final String SECONDARY_YELLOW = "#FFC107";
    private static final String ACCENT_YELLOW = "#FFD54F";
    private static final String BACKGROUND_WHITE = "#FFFFFF";
    private static final String LIGHT_GRAY = "#F5F5F5";
    private static final String TEXT_DARK = "#212121";  
    private static final String neutralWhite = "#FFFFFF";
    private static final String mediumGray = "#E0E0E0";
    private static TextField nameField = new TextField();
    private static TextField contactField = new TextField();
    private static ImageView riderImageView = new ImageView();
    private static Button deleteButton = new Button("Delete");
    private static TableView<RidersList> tableView = new TableView<>();
    private static ObservableList<RidersList> riderData = FXCollections.observableArrayList();

    public RiderManagement(int adminId) {
        this.adminId = adminId;
        
        // Main container
        root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setPrefSize(1000, 700);
        root.setStyle("-fx-background-color: " + BACKGROUND_WHITE + ";");
         String primaryRed = "#D50000";
        String secondaryYellow = "#FFD600";
        String neutralWhite = "#FFFFFF";
        String darkText = "#212121";
        String lightGray = "#F5F5F5";
        String mediumGray = "#E0E0E0";
//        // Header section
//        Label headerLabel = new Label("Rider Management");
//        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_RED + ";");
//        
        // Header with title
        HBox header = new HBox();
        header.setStyle("-fx-background-color: " + primaryRed + "; -fx-padding: 15px; -fx-alignment: center-left;");
        Label headerLabel = new Label("Rider Management");
        headerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + neutralWhite + ";");
        header.getChildren().add(headerLabel);
        
        // Main content area
        HBox mainContent = new HBox(30);
        mainContent.setPrefHeight(650);
        
        // Left section: Form pane with card-like appearance
        VBox formPane = createFormPane();
        
        // Right section: Table and search controls
        VBox tableSection = createTableSection();
        
        // Add components to main layout
        mainContent.getChildren().addAll(formPane, tableSection);
        HBox.setHgrow(tableSection, Priority.ALWAYS);
        
        deleteButton.setOnAction(event -> {
         RiderManagement.RidersList selectedItem = tableView.getSelectionModel().getSelectedItem();
         if (selectedItem == null) {
             System.out.println("❌ No item selected for deletion.");
             return;
         }

    // Confirm before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this rider?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Deletion");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try (Connection conn = Database.connect()) {
                String sql = "DELETE FROM riders WHERE name = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, selectedItem.getName());
                stmt.executeUpdate();

                // Remove image file (optional)
                if (selectedItem.getImagePath() != null) {
                    File imageFile = new File(selectedItem.getImagePath());
                    if (imageFile.exists()) {
                        imageFile.delete();
                        System.out.println("🗑️ Image deleted: " + selectedItem.getImagePath());
                    }
                }

                // Remove item from the table
                riderData.remove(selectedItem);
                System.out.println("✅ Rider deleted successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    });
        
        root.getChildren().addAll(header, mainContent);
    }
    
    private VBox createFormPane() {
        // Card-like form pane
        VBox formPane = new VBox(15);
        formPane.setPadding(new Insets(25));
        formPane.setMinWidth(350);
        formPane.setMaxWidth(350);
        formPane.setStyle("-fx-background-color: " + LIGHT_GRAY + "; " +
                         "-fx-background-radius: 8px; " +
                         "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        // Form title
        Label formTitle = new Label("Rider Information");
        formTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        // Name field
        Label nameLabel = new Label("Rider Name");
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
      
        nameField.setPromptText("Enter rider's full name");
        nameField.setPrefHeight(35);
        nameField.setStyle("-fx-background-radius: 4px;");
        
        // Contact field
        Label contactLabel = new Label("Contact Number");
        contactLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
      
        contactField.setPromptText("Enter contact number");
        contactField.setPrefHeight(35);
        contactField.setStyle("-fx-background-radius: 4px;");
        
        // Profile picture
        Label imageLabel = new Label("Profile Picture");
        imageLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        StackPane imageContainer = new StackPane();
        imageContainer.setStyle("-fx-background-color: white; " +
                              "-fx-border-color: #DDDDDD; " +
                              "-fx-border-radius: 4px;");
        imageContainer.setMinHeight(180);
        imageContainer.setMaxHeight(180);
        
    

        riderImageView.setFitHeight(170);
        riderImageView.setFitWidth(170);
        riderImageView.setPreserveRatio(true);
        
        imageContainer.getChildren().add(riderImageView);
        
        // Upload button
        Button uploadButton = new Button("Upload Image");
        uploadButton.setPrefHeight(35);
        uploadButton.setPrefWidth(320);
        uploadButton.setStyle("-fx-background-color: " + SECONDARY_YELLOW + "; " +
                            "-fx-text-fill: " + TEXT_DARK + "; " +
                            "-fx-font-weight: bold; " +
                            "-fx-background-radius: 4px;");
        
        FileChooser fileChooser = new FileChooser();
        uploadButton.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                Image image = new Image(file.toURI().toString());
                riderImageView.setImage(image);
            }
        });
        
        // Action buttons
        HBox actionButtons = new HBox(10);
        
        Button saveButton = new Button("Save Rider");
        saveButton.setPrefHeight(40);
        saveButton.setPrefWidth(200);
        saveButton.setStyle("-fx-background-color: " + PRIMARY_RED + "; " +
                          "-fx-text-fill: white; " +
                          "-fx-font-weight: bold; " +
                          "-fx-background-radius: 4px;");
        
      
        deleteButton.setPrefHeight(40);
        deleteButton.setPrefWidth(110);
        deleteButton.setStyle("-fx-background-color: transparent; " +
                            "-fx-border-color: " + PRIMARY_RED + "; " +
                            "-fx-text-fill: " + PRIMARY_RED + "; " +
                            "-fx-border-radius: 4px; " +
                            "-fx-font-weight: bold;");
        
        actionButtons.getChildren().addAll(saveButton, deleteButton);
        
        // Implement save button logic (from original code)
        saveButton.setOnAction(e -> {
            String name = nameField.getText();
            String contact = contactField.getText();
            String imagePath = null;

            // Handle Image
            if (riderImageView.getImage() != null) {
                try {
                    File destFolder = new File("src/rider");
                    if (!destFolder.exists()) {
                        destFolder.mkdirs();
                    }
                    File file = new File(Paths.get(URI.create(riderImageView.getImage().getUrl())).toFile().getAbsolutePath());
                    File destFile = new File(destFolder, file.getName());

                    if (file.exists()) {
                        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imagePath = "src/rider/" + file.getName();
                    } else {
                        System.out.println("❌ Image file not found: " + file.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // Database Operations
            try (Connection conn = Database.connect()) {
                conn.setAutoCommit(false); // Start transaction

                // Check if the rider already exists
                String checkSql = "SELECT rider_id FROM riders WHERE name = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setString(1, name);
                    try (ResultSet rs = checkStmt.executeQuery()) {
                        if (rs.next()) {
                            int existingId = rs.getInt("rider_id");

                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                            alert.setTitle("Rider Exists");
                            alert.setHeaderText("This rider already exists.");
                            alert.setContentText("Do you want to update the existing rider or insert a new one?");

                            ButtonType updateButton = new ButtonType("Update");
                            ButtonType insertNewButton = new ButtonType("Insert New");
                            ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
                            alert.getButtonTypes().setAll(updateButton, insertNewButton, cancelButton);
                            Optional<ButtonType> result = alert.showAndWait();

                            if (result.isPresent()) {
                                if (result.get() == updateButton) {
                                    // Update existing rider
                                    String updateSql = "UPDATE riders SET name = ?, contact_number = ?, imagePath = ?, last_modified_by = ? WHERE rider_id = ?";
                                    try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                                        updateStmt.setString(1, name);
                                        updateStmt.setString(2, contact);
                                        updateStmt.setString(3, imagePath);
                                        updateStmt.setInt(4, adminId); // last_modified_by
                                        updateStmt.setInt(5, existingId); // rider_id
                                        
                                        // Execute the update
                                        int rowsUpdated = updateStmt.executeUpdate();
                                        if (rowsUpdated > 0) {
                                            showSuccessNotification("Rider details updated successfully!");
                                        } else {
                                            JOptionPane.showMessageDialog(null, "No updates were made. Please verify the input.", "No Changes", JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    }
                                } else if (result.get() == insertNewButton) {
                                    // Insert a new rider entry
                                    insertNewRider(conn, name + " (New)", contact, imagePath);
                                }
                            }
                        } else {
                            // Insert new Rider and User
                            insertNewRider(conn, name, contact, imagePath);
                        }
                    }
                }

                // Commit the transaction after operations are done
                conn.commit();
                
                // Clear fields after successful save
                nameField.clear();
                contactField.clear();
                riderImageView.setImage(null);
                
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error processing rider data!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        
        
        // Add all components to form pane
        formPane.getChildren().addAll(
            formTitle, 
            nameLabel, nameField, 
            contactLabel, contactField, 
            imageLabel, imageContainer, 
            uploadButton, 
            new Separator(), 
            actionButtons
        );
        
        return formPane;
    }
      private void showSuccessNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private VBox createTableSection() {
        VBox tableSection = new VBox(15);
        tableSection.setPrefWidth(600);
        
        // Search and filter controls
        HBox searchBar = new HBox(15);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        searchBar.setPadding(new Insets(0, 0, 10, 0));
        
        TextField searchField = new TextField();
        searchField.setPromptText("Search riders...");
        searchField.setPrefHeight(35);
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-radius: 20px; -fx-padding: 5px 15px;");
        HBox.setHgrow(searchField, Priority.ALWAYS);
        
        ComboBox<String> categoryFilter = new ComboBox<>();
        categoryFilter.getItems().addAll("All", "Available", "Delivering", "Online", "Offline");
        categoryFilter.setValue("All");
        categoryFilter.setPrefHeight(35);
        categoryFilter.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    
        
        Button sortAZ = new Button("A-Z");
        sortAZ.setPrefHeight(35);
        sortAZ.setStyle("-fx-background-color: " + ACCENT_YELLOW + "; " +
                      "-fx-text-fill: " + TEXT_DARK + "; " +
                      "-fx-background-radius: 4px;");
        
        Button sortZA = new Button("Z-A");
        sortZA.setPrefHeight(35);
        sortZA.setStyle("-fx-background-color: " + ACCENT_YELLOW + "; " +
                      "-fx-text-fill: " + TEXT_DARK + "; " +
                      "-fx-background-radius: 4px;");
        
        searchBar.getChildren().addAll(searchField, categoryFilter, sortAZ, sortZA);
        
        // Table view creation
     
        tableView.setPrefWidth(600);
        tableView.setStyle("-fx-background-radius: 4px; " +
                         "-fx-border-color: #DDDDDD; " +
                         "-fx-border-radius: 4px;");
        
        // Define table columns
        TableColumn<RidersList, String> nameColumn = new TableColumn<>("Rider Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(120);
        
        
        TableColumn<RidersList, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        contactColumn.setPrefWidth(120);
        
        TableColumn<RidersList, String> imageColumn = new TableColumn<>("Image");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setPrefWidth(100);
        
        TableColumn<RidersList, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setPrefWidth(100);
        
        TableColumn<RidersList, String> onlineStatusColumn = new TableColumn<>("Online Status");
        onlineStatusColumn.setCellValueFactory(new PropertyValueFactory<>("onlineStatus"));
        onlineStatusColumn.setPrefWidth(120);
        
     
   TableColumn<RidersList, Void> actionsColumn = new TableColumn<>("Actions");
    actionsColumn.setPrefWidth(100);

    actionsColumn.setCellFactory(col -> new TableCell<RidersList, Void>() {
        private final Button editButton = new Button("Edit");

        {
            editButton.setStyle("-fx-background-color: " + SECONDARY_YELLOW + "; -fx-text-fill: " + TEXT_DARK + "; -fx-font-size: 11px;");
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                editButton.setOnAction(event -> {
                    RidersList rider = getTableView().getItems().get(getIndex());

                    tableView.getSelectionModel().select(rider); // 🔥 Mark it as selected

                    // Example: Populate fields from rider data
                    nameField.setText(rider.getName());
                    contactField.setText(rider.getContact());
                    
                  

                    // Load image
                    if (rider.getImagePath() != null && !rider.getImagePath().isEmpty()) {
                        File file = new File(rider.getImagePath());
                        if (file.exists()) {
                            riderImageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            riderImageView.setImage(null);
                        }
                    } else {
                        riderImageView.setImage(null);
                    }
                });

                setGraphic(editButton);
            }
        }
    });

        
        // Apply custom cell factory for status columns to add color indicators
        statusColumn.setCellFactory(column -> new TableCell<RidersList, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    if (item.equalsIgnoreCase("delivering")) {
                        setStyle("-fx-text-fill: " + PRIMARY_RED + "; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("available")) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + TEXT_DARK + ";");
                    }
                }
            }
        });
        
        onlineStatusColumn.setCellFactory(column -> new TableCell<RidersList, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    if (item.equalsIgnoreCase("online")) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("offline")) {
                        setStyle("-fx-text-fill: #9E9E9E; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + TEXT_DARK + ";");
                    }
                }
            }
        });
        
        tableView.getColumns().clear(); // 💥 clear to prevent duplication!

        tableView.setItems(riderData);  // Assuming riderData is a list of your rows

        tableView.getColumns().addAll(nameColumn, contactColumn, imageColumn, statusColumn, onlineStatusColumn, actionsColumn);

        // Data setup
      
        FilteredList<RidersList> filteredData = new FilteredList<>(riderData, p -> true);
        
        // Update filter logic
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(filteredData, newValue, categoryFilter.getValue());
        });
        
        categoryFilter.setOnAction(e -> {
            updateFilter(filteredData, searchField.getText(), categoryFilter.getValue());
        });
        
        // Set up sorted data
        SortedList<RidersList> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);
        
        // Sort button actions
        sortAZ.setOnAction(e -> {
            tableView.getSortOrder().clear();
            nameColumn.setSortType(TableColumn.SortType.ASCENDING);
            tableView.getSortOrder().add(nameColumn);
        });
        
        sortZA.setOnAction(e -> {
            tableView.getSortOrder().clear();
            nameColumn.setSortType(TableColumn.SortType.DESCENDING);
            tableView.getSortOrder().add(nameColumn);
        });
        
        // Load data from database
        riderData.clear(); // 🚫 Prevents duplication
        loadRiderData(riderData);
        
         
        
        // Table selection event
        setupTableSelectionEvent(tableView);
        
        
        // Add components to table section
        tableSection.getChildren().addAll(searchBar, tableView);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        
        return tableSection;
    }
    
    private void setupTableSelectionEvent(TableView<RidersList> tableView) {
        tableView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !tableView.getSelectionModel().isEmpty()) {
                RidersList selectedItem = tableView.getSelectionModel().getSelectedItem();
                
                // Fill form fields with selected item data
                // (This would update the text fields in the form pane)
                // Note: Since we've restructured the class, we would need to 
                // have references to these controls as class fields to update them here
                
                // Load image preview
                if (selectedItem.getImagePath() != null && !selectedItem.getImagePath().isEmpty()) {
                    File file = new File(selectedItem.getImagePath());
                    
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        // Update the image view (needs to be a class field)
                    } else {
                        System.out.println("⚠ Image not found: " + file.getAbsolutePath());
                    }
                }
            }
        });
    }
    
    private void updateFilter(FilteredList<RiderManagement.RidersList> filteredData, String searchText, String category) {
    filteredData.setPredicate(rider -> {
        boolean matchesSearch = (searchText == null || searchText.isEmpty()) || 
            rider.getName().toLowerCase().contains(searchText.toLowerCase()) ||
            rider.getContact().toLowerCase().contains(searchText.toLowerCase()) ||
            rider.getStatus().toLowerCase().contains(searchText.toLowerCase()) ||
            rider.getOnlineStatus().toLowerCase().contains(searchText.toLowerCase());

        boolean matchesCategory = category.equalsIgnoreCase("all") ||
            rider.getStatus().toLowerCase().contains(category.toLowerCase()) ||
            rider.getOnlineStatus().toLowerCase().contains(category.toLowerCase());

        return matchesSearch && matchesCategory;
    });
}

    private void loadRiderData(ObservableList<RidersList> riderData) {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT name, contact_number, imagePath, status, online_status FROM riders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("name");
                String contact = rs.getString("contact_number");
                String imagePath = rs.getString("imagePath");
                String status = rs.getString("status");
                String onlineStatus = rs.getString("online_status");
                
                riderData.add(new RidersList(name, contact, imagePath, status, onlineStatus));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            showErrorNotification("Error loading rider data");
        }
    }
    
      private void showErrorNotification(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void insertNewRider(Connection conn, String name, String contact, String imagePath) throws SQLException {
        String userSql = "INSERT INTO Users (full_name, email, password, role, last_modified_by) VALUES (?, ?, ?, 'Rider', ?)";

        String riderSql = "INSERT INTO Riders (user_id, name, contact_number, imagePath, last_modified_by) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement riderStmt = conn.prepareStatement(riderSql)) {

            // Generate default username & password
            String defaultUsername = name.replaceAll("\\s+", "").toLowerCase(); // Trim spaces and make lowercase
            String defaultPassword = hashPassword("riderpassword"); // Default password

            // Insert into Users table
            userStmt.setString(1, name);
            userStmt.setString(2, defaultUsername + "@riders.com"); // Temporary email
            userStmt.setString(3, defaultPassword); // Store hashed password
            userStmt.setInt(4, adminId);
            int userRows = userStmt.executeUpdate();

            // Get generated user_id
            ResultSet generatedKeys = userStmt.getGeneratedKeys();
            int userId = -1;
            if (generatedKeys.next()) {
                userId = generatedKeys.getInt(1);
            }

            // Insert into Riders table
            if (userId != -1) {
                riderStmt.setInt(1, userId);
                riderStmt.setString(2, name);
                riderStmt.setString(3, contact);
                riderStmt.setString(4, imagePath);
                riderStmt.setInt(5, adminId); // 
                int riderRows = riderStmt.executeUpdate();

                if (riderRows > 0) {
                    JOptionPane.showMessageDialog(null, "New rider added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Error retrieving user ID!", "Error", JOptionPane.ERROR_MESSAGE);
            }

            conn.commit(); // Commit transaction
        } catch (SQLException e) {
            conn.rollback(); // Rollback on error
            throw e;
        }
    }
    
     private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }


      public class RidersList {
    private String name;
   
    private String contact;
    private String imagePath; // NEW!
    private String status;
    private String onlineStatus;
    

    public RidersList(String name, String contact, String imagePath, String status, String onlineStatus) {
        this.name = name;
        
        this.contact = contact;
        this.imagePath = imagePath;
        this.status = status;
        this.onlineStatus = onlineStatus;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getImagePath() { return imagePath; }
    
      public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOnlineStatus() {
        return onlineStatus;
    }

    public void setOnlineStatus(String onlineStatus) {
        this.onlineStatus = onlineStatus;
    }
}


    public VBox getRoot() {
        return root;
    }
}
