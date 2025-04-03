
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

import java.util.Optional;
import java.util.Random;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javax.swing.JOptionPane;

public class RiderManagement {
    private VBox root;

    public RiderManagement() {
        
    root = new VBox(20); // Adjusted spacing
    root.setPadding(new Insets(20));
    root.setPrefSize(600, 400); // Set preferred size
    
    // Define formPane (VBox)
    VBox formPane = new VBox(10);
    formPane.setPadding(new Insets(10));

    // Form Elements
    Label nameLabel = new Label("Name:");
    TextField nameField = new TextField();
    nameField.setPrefWidth(300); // Set text field width

    Label contactLabel = new Label("Contact Number:");
    TextField contactField = new TextField();
    contactField.setPrefWidth(300);

    Label imageLabel = new Label("Profile Picture:");
    ImageView imageView = new ImageView();
    imageView.setFitHeight(150); // Increased image size
    imageView.setFitWidth(150);

    Button uploadButton = new Button("Upload Image");
    FileChooser fileChooser = new FileChooser();
    uploadButton.setOnAction(e -> {
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            Image image = new Image(file.toURI().toString());
            imageView.setImage(image);
        }
    });

    Button saveButton = new Button("Save Rider");
   
     saveButton.setOnAction(e -> {
    String name = nameField.getText();
    String contact = contactField.getText();
    String imagePath = null;

    // Handle Image
    if (imageView.getImage() != null) {
        try {
            File destFolder = new File("src/rider");
            if (!destFolder.exists()) {
                destFolder.mkdirs();
            }
            File file = new File(Paths.get(URI.create(imageView.getImage().getUrl())).toFile().getAbsolutePath());
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
                        String updateSql = "UPDATE riders SET name = ?, contact_number = ?, imagePath = ? WHERE rider_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                            updateStmt.setString(1, name);
                            updateStmt.setString(2, contact);
                            updateStmt.setString(3, imagePath);
                            updateStmt.setInt(4, existingId);
                            updateStmt.executeUpdate();
                        }
                        JOptionPane.showMessageDialog(null, "Rider details updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
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
} catch (SQLException ex) {
    ex.printStackTrace();
    JOptionPane.showMessageDialog(null, "Error processing rider data!", "Error", JOptionPane.ERROR_MESSAGE);
}

});
   
     // ====== TABLEVIEW  ======
         TableView<RiderManagement.RidersList> tableView = new TableView<>();
        tableView.setPrefWidth(500); 

        TableColumn<RiderManagement.RidersList, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameColumn.setPrefWidth(100);

        TableColumn<RiderManagement.RidersList, String> contactColumn = new TableColumn<>("Contact");
        contactColumn.setCellValueFactory(new PropertyValueFactory<>("contact"));
        contactColumn.setPrefWidth(100);

        TableColumn<RiderManagement.RidersList, String> imageColumn = new TableColumn<>("Image Path");
        imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
        imageColumn.setPrefWidth(100);

        tableView.getColumns().addAll(nameColumn, contactColumn, imageColumn);

        // ✅ Corrected ObservableList
        ObservableList<RiderManagement.RidersList> riderData = FXCollections.observableArrayList();

        try (Connection conn = Database.connect()) {
            String sql = "SELECT name, contact_number, imagePath FROM riders"; // Ensure column names match DB
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String name = rs.getString("name");
                String contact = rs.getString("contact_number");
                String imagePath = rs.getString("imagePath"); 

                riderData.add(new RiderManagement.RidersList(name, contact, imagePath));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        tableView.setItems(riderData); // ✅ Set data to the table
        
        // Update selection event
   tableView.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !tableView.getSelectionModel().isEmpty()) {
             RiderManagement.RidersList selectedItem = tableView.getSelectionModel().getSelectedItem();
            nameField.setText(selectedItem.getName());
            contactField.setText(selectedItem.getContact());
            

            File menuFolder = new File("C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/rider/");
            if (menuFolder.exists() && menuFolder.isDirectory()) {
                String[] files = menuFolder.list();
                System.out.println("Files in rider/:");
                for (String f : files) {
                    System.out.println(f);
                }
            } else {
                System.out.println("❌ rider/ folder not found!");
            }

            // 🔹 Check if the image path is valid
        if (selectedItem.getImagePath() != null && !selectedItem.getImagePath().isEmpty()) {
            File file = new File(selectedItem.getImagePath()); // Use the direct path from DB
            
            // Debugging output
            System.out.println("Checking file at: " + file.getAbsolutePath());
            System.out.println("File exists? " + file.exists());

            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
            } else {
                System.out.println("⚠ Image not found: " + file.getAbsolutePath());
                imageView.setImage(null); // Clear image if not found
            }
        } else {
            System.out.println("⚠ No image path found for this item.");
            imageView.setImage(null);
        }
    }
});

        Button deleteButton = new Button("Delete");
     deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;"); // Red color for warning

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
                System.out.println("✅ Menu item deleted successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    });

    

        // ✅ Make table stretchable
        HBox.setHgrow(tableView, Priority.ALWAYS);
        HBox.setHgrow(formPane, Priority.ALWAYS);
        

           // Add components to formPane
        formPane.getChildren().addAll(nameLabel, nameField, contactLabel, contactField, imageLabel, imageView, uploadButton, saveButton, deleteButton);

    
         // ✅ Organize layout
    HBox mainPane = new HBox(20);
    mainPane.getChildren().addAll(formPane, tableView);
    root.getChildren().add(mainPane);
    root.setStyle("-fx-background-color: #f4f4f4;");
    }

    private void insertNewRider(Connection conn, String name, String contact, String imagePath) throws SQLException {
        String userSql = "INSERT INTO Users (full_name, email, password, role) VALUES (?, ?, ?, 'Rider')";
        String riderSql = "INSERT INTO Riders (user_id, name, contact_number, imagePath) VALUES (?, ?, ?, ?)";

        try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement riderStmt = conn.prepareStatement(riderSql)) {

            // Generate default username & password
            String defaultUsername = name.replaceAll("\\s+", "").toLowerCase(); // Trim spaces and make lowercase
            String defaultPassword = hashPassword("riderpassword"); // Default password

            // Insert into Users table
            userStmt.setString(1, name);
            userStmt.setString(2, defaultUsername + "@riders.com"); // Temporary email
            userStmt.setString(3, defaultPassword); // Store hashed password
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

    public RidersList(String name, String contact, String imagePath) {
        this.name = name;
        
        this.contact = contact;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public String getContact() { return contact; }
    public String getImagePath() { return imagePath; }
}


    public VBox getRoot() {
        return root;
    }
}
