package andoksfooddeliverysystem;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;

import javafx.util.Duration;

public class AdminDashboard extends Application {
    private VBox sidebar;
    private BorderPane mainLayout;
    private boolean sidebarVisible = true;
    private VBox mainContent;


    @Override
    public void start(Stage primaryStage) {
        mainLayout = new BorderPane();
        mainContent = new VBox();
        mainContent.setPadding(new Insets(20));
        mainLayout.setCenter(mainContent); // ✅ Add mainContent to the center


        // Sidebar
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #333; -fx-pref-width: 200px;");

        Button menuButton = new Button("Menu");
        sidebar.getChildren().add(menuButton);
        menuButton.setOnAction(e -> showMenuManagement());
        
        Button riderButton = new Button("Register Riders");
        sidebar.getChildren().add(riderButton);
        riderButton.setOnAction(e -> showRiderManagement()); 

        
        // Toggle button (placed in the main layout, not the sidebar)
        Button toggleSidebar = new Button("☰");
        toggleSidebar.setOnAction(e -> toggleSidebar());
        mainLayout.setTop(toggleSidebar);

        mainLayout.setLeft(sidebar);

        Scene scene = new Scene(mainLayout);
        primaryStage.setWidth(1500);
        primaryStage.setHeight(800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Admin Dashboard");
        primaryStage.show();
    }
    
private void showRiderManagement() {
    mainContent.getChildren().clear();
    System.out.println("Switching to Rider Management");

    RiderManagement riderManagement = new RiderManagement();
    Node riderUI = riderManagement.getRoot();

    if (riderUI == null) {
        System.out.println("❌ Rider UI is null!");  // Debugging
    } else {
        System.out.println("✅ Adding Rider UI to mainContent");
        mainContent.getChildren().add(riderUI);
    }
}



    private void toggleSidebar() {
        TranslateTransition transition = new TranslateTransition(Duration.millis(300), sidebar);
        if (sidebarVisible) {
            transition.setToX(-200);
            mainLayout.setLeft(null);
        } else {
            transition.setToX(0);
            mainLayout.setLeft(sidebar);
        }
        transition.play();
        sidebarVisible = !sidebarVisible;
    }
    
    private void loadCategories(ComboBox<String> comboBox) {
    String query = "SELECT category_name FROM categories"; // Adjust table name as needed

    try (Connection conn = Database.connect();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(query)) {

        while (rs.next()) {
            comboBox.getItems().add(rs.getString("category_name"));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    
    private int getCategoryId(String categoryName) {
        String query = "SELECT category_id FROM categories WHERE category_name = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("category_id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1; // Return -1 if category not found
    }
    private String getCategoryName(int categoryId) {
    String query = "SELECT category_name FROM categories WHERE category_id = ?";
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getString("category_name");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    return null; // Return null if not found
}


    
      private void showMenuManagement() {
           
            HBox mainPane = new HBox(20); // Spacing between form & table
            mainPane.setPadding(new Insets(20));
            
            mainContent.getChildren().clear(); 
    
    // ====== FORM FOR ADDING MENU ITEMS ======
    VBox formPane = new VBox(10);
    formPane.setPadding(new Insets(10));
    formPane.setPrefWidth(600); // Set a wider form size

    Label titleLabel = new Label("Add Menu Item");
    TextField nameField = new TextField();
    nameField.setPromptText("Item Name");
    TextField priceField = new TextField();
    priceField.setPromptText("Price");
    TextField stockField = new TextField();
    stockField.setPromptText("Stock");
    ComboBox<String> categoryComboBox = new ComboBox<>();
    categoryComboBox.setPromptText("Select Category");
    loadCategories(categoryComboBox); // Call method to populate categories

    TextArea descriptionField = new TextArea();
    descriptionField.setPromptText("Description");
    descriptionField.setPrefHeight(100); // Adjust height for better visibility

    // Image Upload
    ImageView imageView = new ImageView();
    imageView.setFitHeight(120);
    imageView.setFitWidth(120);
    Button uploadButton = new Button("Upload Image");
    uploadButton.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
    });

    // Save Button
    Button saveButton = new Button("Save Item");
        saveButton.setOnAction(e -> {
            
            String selectedCategory = categoryComboBox.getValue();
            if (selectedCategory == null) {
                System.out.println("Please select a category!");
                return; // Prevent saving without category
            }

             String itemName = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String description = descriptionField.getText();

            // Get category_id from category_name
            int categoryId = getCategoryId(selectedCategory); // Call method to fetch category_id
            if (categoryId == -1) {
                System.out.println("Category not found!");
                return;
            }
    // Handle Image
String imagePath = null;
if (imageView.getImage() != null) {
    try {
        File destFolder = new File("src/menu");
        if (!destFolder.exists()) {
            destFolder.mkdirs(); // Create menu folder if it doesn't exist
        }

        // Get the file path safely
        File file = new File(Paths.get(URI.create(imageView.getImage().getUrl())).toFile().getAbsolutePath());

        // Destination file
        File destFile = new File(destFolder, file.getName());

        // Copy image if it exists
        if (file.exists()) {
            Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            imagePath = "src/menu/" + file.getName(); // Save relative path
        } else {
            System.out.println("❌ Image file not found: " + file.getAbsolutePath());
        }

    } catch (Exception ex) {
        ex.printStackTrace();
    }
}


        // Save to Database
        try (Connection conn = Database.connect()) {
            // Check if the item exists based on ID
            String checkSql = "SELECT item_id FROM menu_items WHERE name = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, itemName);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {  // If an existing item is found
                int existingId = rs.getInt("item_id");  // Get the item's ID

                // Show confirmation alert
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Item Exists");
                alert.setHeaderText("This item already exists.");
                alert.setContentText("Do you want to update the existing item or insert a new one?");

                ButtonType updateButton = new ButtonType("Update");
                ButtonType insertNewButton = new ButtonType("Insert New");
                ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(updateButton, insertNewButton, cancelButton);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent()) {
                    if (result.get() == updateButton) {
                        // User chose to update existing item using ID
                        String updateSql = "UPDATE menu_items SET price = ?, stock = ?, category_id = ?, description = ?, image_path = ? WHERE item_id = ?";
                        PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                        updateStmt.setDouble(1, price);
                        updateStmt.setInt(2, stock);
                        updateStmt.setInt(3, categoryId);
                        updateStmt.setString(4, description);
                        updateStmt.setString(5, imagePath);
                        updateStmt.setInt(6, existingId);  // Use ID instead of name

                        updateStmt.executeUpdate();
                        System.out.println("Menu item updated!");
                    } else if (result.get() == insertNewButton) {
                        // Insert a new item (ensuring name uniqueness)
                        String insertSql = "INSERT INTO menu_items (name, price, stock, category_id, description, image_path) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                        insertStmt.setString(1, itemName + " (New)");
                        insertStmt.setDouble(2, price);
                        insertStmt.setInt(3, stock);
                        insertStmt.setInt(4, categoryId);
                        insertStmt.setString(5, description);
                        insertStmt.setString(6, imagePath);

                        insertStmt.executeUpdate();
                        System.out.println("New menu item added!");
                    }
                }
            } else {
                // If item doesn't exist, insert a new record
                String insertSql = "INSERT INTO menu_items (name, price, stock, category_id, description, image_path) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, itemName);
                insertStmt.setDouble(2, price);
                insertStmt.setInt(3, stock);
                insertStmt.setInt(4, categoryId);
                insertStmt.setString(5, description);
                insertStmt.setString(6, imagePath);

                insertStmt.executeUpdate();
                System.out.println("New menu item added!");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        });

    // Add components to the form layout
    formPane.getChildren().addAll(titleLabel, nameField, priceField, stockField, categoryComboBox, descriptionField, uploadButton, imageView, saveButton);

    
    // ====== TABLEVIEW FOR DISPLAYING MENU ITEMS ======
    TableView<FoodItem> tableView = new TableView<>();
    tableView.setPrefWidth(500); // Give it enough space

    TableColumn<FoodItem, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameColumn.setPrefWidth(100); // Adjust width

    TableColumn<FoodItem, Double> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    priceColumn.setPrefWidth(100);

    TableColumn<FoodItem, Double> stockColumn = new TableColumn<>("Stock");
    stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));
    stockColumn.setPrefWidth(100);
    
    TableColumn<FoodItem, String> categoryColumn = new TableColumn<>("Category");
    categoryColumn.setCellValueFactory(cellData -> {
     int categoryId = cellData.getValue().getCategoryId();
     String categoryName = getCategoryName(categoryId); // Convert ID to name
     return new SimpleStringProperty(categoryName);
 });


    categoryColumn.setPrefWidth(100);
    
    TableColumn<FoodItem, String> descriptionColumn = new TableColumn<>("Description");
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    descriptionColumn.setPrefWidth(100);
    
    TableColumn<FoodItem, String> imageColumn = new TableColumn<>("Image Path");
    imageColumn.setCellValueFactory(new PropertyValueFactory<>("imagePath"));
    imageColumn.setPrefWidth(100);

    tableView.getColumns().addAll(nameColumn, priceColumn, stockColumn, categoryColumn, descriptionColumn, imageColumn);
    
   ObservableList<FoodItem> menuItems = FXCollections.observableArrayList();

    try (Connection conn = Database.connect()) {
        String sql = "SELECT name, price, stock, category_id, description, image_path FROM menu_items";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String name = rs.getString("name");
            double price = rs.getDouble("price");
            int stock = rs.getInt("stock");
            int category = rs.getInt("category_id");
            String description = rs.getString("description");
            String imagePath = rs.getString("image_path");

            // Add to TableView
            menuItems.add(new FoodItem(name, price, stock, category, description, imagePath));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }


    tableView.setItems(menuItems);

    // Make table stretchable
    HBox.setHgrow(tableView, Priority.ALWAYS);
    HBox.setHgrow(formPane, Priority.ALWAYS);

    // Update selection event
   tableView.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !tableView.getSelectionModel().isEmpty()) {
            FoodItem selectedItem = tableView.getSelectionModel().getSelectedItem();
            nameField.setText(selectedItem.getName());
            priceField.setText(String.valueOf(selectedItem.getPrice()));
            stockField.setText(String.valueOf(selectedItem.getStock()));

            // Convert category ID to category name before setting ComboBox value
            String categoryName = getCategoryName(selectedItem.getCategoryId());
            categoryComboBox.setValue(categoryName);

            descriptionField.setText(selectedItem.getDescription());

            File menuFolder = new File("C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/menu/");
            if (menuFolder.exists() && menuFolder.isDirectory()) {
                String[] files = menuFolder.list();
                System.out.println("Files in menu/:");
                for (String f : files) {
                    System.out.println(f);
                }
            } else {
                System.out.println("❌ menu/ folder not found!");
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
         FoodItem selectedItem = tableView.getSelectionModel().getSelectedItem();
         if (selectedItem == null) {
             System.out.println("❌ No item selected for deletion.");
             return;
         }

    // Confirm before deleting
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to delete this item?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Deletion");
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            try (Connection conn = Database.connect()) {
                String sql = "DELETE FROM menu_items WHERE name = ?";
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
                menuItems.remove(selectedItem);
                System.out.println("✅ Menu item deleted successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    });

    // Add to form layout
    formPane.getChildren().add(deleteButton);

    // Add formPane and tableView to mainPane
    mainPane.getChildren().addAll(formPane, tableView);
    mainContent.getChildren().add(mainPane);  // ✅ Add menu UI inside `mainContent`
}
                

    
    public class FoodItem {
    private String name;
    private double price;
    private int stock;
    private int categoryId;
    private String description;
    private String imagePath; // NEW!

    public FoodItem(String name, double price, int stock, int categoryId, String description, String imagePath) {
        this.name = name;
        this.price = price;
        this.stock = stock;
         this.categoryId = categoryId;
        this.description = description;
        this.imagePath = imagePath;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
     public int getCategoryId() {  // Change from getCategory() to getCategoryId()
        return categoryId;
    }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
}




    public static void main(String[] args) {
        launch(args);
    }
}
