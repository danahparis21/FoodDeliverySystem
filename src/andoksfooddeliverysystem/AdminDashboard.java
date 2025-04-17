package andoksfooddeliverysystem;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
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
import java.util.ArrayList;
import java.util.List;
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
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ListView;

import javafx.util.Duration;

public class AdminDashboard extends Application {
    private int userID;
    private VBox sidebar;
    private BorderPane mainLayout;
    private boolean sidebarVisible = true;
    private VBox mainContent;
    ListView<String> variationList;


    public AdminDashboard(int userID) {
        this.userID = userID;
        System.out.println("✅ AdminDashboard opened with User ID: " + userID); // Debugging
    }
    
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

        Button orderButton = new Button("Orders");
        sidebar.getChildren().add(orderButton);
        orderButton.setOnAction(e -> showOrders()); 
        
         Button auditLogsButton = new Button("Audit Logs");
        sidebar.getChildren().add(auditLogsButton);
        auditLogsButton.setOnAction(e -> showLogs()); 
        
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

    RiderManagement riderManagement = new RiderManagement(userID);
  
    Node riderUI = riderManagement.getRoot();

    if (riderUI == null) {
        System.out.println("❌ Rider UI is null!");  // Debugging
    } else {
        System.out.println("✅ Adding Rider UI to mainContent");
        mainContent.getChildren().add(riderUI);
    }
}

    private void showOrders() {
        mainContent.getChildren().clear();
        System.out.println("Switching to orders");

        ShowOrders showOrders = new ShowOrders(userID);
        Node riderUI = showOrders.getRoot();

        if (riderUI == null) {
            System.out.println("❌ Order UI is null!");  // Debugging
        } else {
            System.out.println("✅ Adding Order UI to mainContent");
            mainContent.getChildren().add(riderUI);
        }
    }
    
     private void showLogs() {
        mainContent.getChildren().clear();
        System.out.println("Switching to Audit Logs");

        AuditLogs showLogs = new AuditLogs(userID);
        Node riderUI = showLogs.getRoot();

        if (riderUI == null) {
            System.out.println("❌ Order UI is null!");  // Debugging
        } else {
            System.out.println("✅ Adding Order UI to mainContent");
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
    
    ComboBox<String> availabilityCombo = new ComboBox<>();
    availabilityCombo.getItems().addAll("Available", "Not Available");
    availabilityCombo.setValue("Available"); // default

    
    ComboBox<String> categoryComboBox = new ComboBox<>();
    categoryComboBox.setPromptText("Select Category");
    loadCategories(categoryComboBox); // Call method to populate categories

    TextArea descriptionField = new TextArea();
    descriptionField.setPromptText("Description");
    descriptionField.setPrefHeight(100); // Adjust height for better visibility
    
    variationList = new ListView<>();
    ObservableList<String> variationItems = FXCollections.observableArrayList();
    variationList.setItems(variationItems);
    TextField variationField = new TextField();
    variationField.setPromptText("Variation Name");

    TextField variationPriceField = new TextField();
    variationPriceField.setPromptText("Price Adjustment");

    Button addVariationButton = new Button("Add Variation");
    addVariationButton.setOnAction(e -> {
        String variationName = variationField.getText();
        String variationPrice = variationPriceField.getText();

        if (!variationName.isEmpty() && !variationPrice.isEmpty()) {
            variationItems.add(variationName + " (₱" + variationPrice + ")");
            variationField.clear();
            variationPriceField.clear();
        }
    });
    
    VBox variationPane = new VBox(10, new Label("Variations"), variationField, variationPriceField, addVariationButton, variationList);
    variationPane.setPadding(new Insets(10));
    variationPane.setPrefHeight(500);



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
            String availability = availabilityCombo.getValue(); // changed
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
    // Update existing menu item
            String updateSql = "UPDATE menu_items SET price = ?, availability = ?, category_id = ?, description = ?, image_path = ?, last_modified_by = ? WHERE item_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setDouble(1, price);
            updateStmt.setString(2, availability);
            updateStmt.setInt(3, categoryId);
            updateStmt.setString(4, description);
            updateStmt.setString(5, imagePath);
            updateStmt.setInt(6, userID);  
            updateStmt.setInt(7, existingId);   
   
            updateStmt.executeUpdate();
            System.out.println("Menu item updated!");

            // ✅ 1. Delete old variations
            String deleteVariationsSQL = "DELETE FROM menu_variations WHERE item_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteVariationsSQL);
            deleteStmt.setInt(1, existingId);
            deleteStmt.executeUpdate();
            System.out.println("Old variations deleted.");

            // ✅ 2. Insert new variations
            ObservableList<String> variations = variationList.getItems();
            String insertVariationSQL = "INSERT INTO menu_variations (item_id, variation_name, variation_price, last_modified_by) VALUES (?, ?, ?, ?)";
            PreparedStatement variationStmt = conn.prepareStatement(insertVariationSQL);

            for (String variation : variations) {
                int priceStart = variation.indexOf("₱"); 
                if (priceStart != -1) {
                    String variationName = variation.substring(0, priceStart).trim().replaceAll("\\($", "");

                    String priceString = variation.substring(priceStart + 1, variation.length() - 1);

                    variationStmt.setInt(1, existingId);
                    variationStmt.setString(2, variationName);
                    variationStmt.setBigDecimal(3, new BigDecimal(priceString));
                     variationStmt.setInt(4, userID);  
                    variationStmt.executeUpdate();
                }
            }
            System.out.println("New variations updated!");
        }
           else if (result.get() == insertNewButton) {
                        // Insert a new item (ensuring name uniqueness)
                        String insertSql = "INSERT INTO menu_items (name, price, availability, category_id, description, image_path, last_modified_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
                        PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);

                        insertStmt.setString(1, itemName + " (New)");
                        insertStmt.setDouble(2, price);
                        insertStmt.setString(3, availability);
                        insertStmt.setInt(4, categoryId);
                        insertStmt.setString(5, description);
                        insertStmt.setString(6, imagePath);
                        insertStmt.setInt(7, userID);

                        insertStmt.executeUpdate();
                        
                         ResultSet resultset = insertStmt.getGeneratedKeys();
                        int itemId = -1;
                        if (resultset.next()) {
                            itemId = resultset.getInt(1); // Get the generated item_id
                        }
                        
                         ObservableList<String> variations = variationList.getItems(); // Get all variations

                        String insertVariationSQL = "INSERT INTO menu_variations (item_id, variation_name, variation_price, last_modified_by) VALUES (?, ?, ?, ?)";
                        PreparedStatement variationStmt = conn.prepareStatement(insertVariationSQL);

                        for (String variation : variations) {
                            // Extract variation name & price from format: "Cut (₱50.00)"
                            int priceStart = variation.indexOf("₱"); 
                            if (priceStart != -1) {
                                String variationName = variation.substring(0, priceStart).trim().replaceAll("\\($", "");

                                String priceString = variation.substring(priceStart + 1, variation.length() - 1); // Remove ₱ and )

                                variationStmt.setInt(1, itemId); // Link variation to menu item
                                variationStmt.setString(2, variationName); // Extracted variation name
                                variationStmt.setBigDecimal(3, new BigDecimal(priceString)); // Convert price to BigDecimal
                                variationStmt.setInt(4, userID);
                                variationStmt.executeUpdate();
                            }
                        }
                        System.out.println("Variations added successfully!");
                        System.out.println("New menu item added!");
                    }
                }
            } else {
                // If item doesn't exist, insert a new record
                String insertSql = "INSERT INTO menu_items (name, price, availability, category_id, description, image_path, last_modified_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS); // ✅ FIXED

                insertStmt.setString(1, itemName);
                insertStmt.setDouble(2, price);
                insertStmt.setString(3, availability);
                insertStmt.setInt(4, categoryId);
                insertStmt.setString(5, description);
                insertStmt.setString(6, imagePath);
                insertStmt.setInt(7, userID);

                insertStmt.executeUpdate();
                
                ResultSet resultset = insertStmt.getGeneratedKeys();
                int itemId = -1;
                if (resultset.next()) {
                    itemId = resultset.getInt(1); // Get the generated item_id
                }
                
                ObservableList<String> variations = variationList.getItems(); // Get all variations

                String insertVariationSQL = "INSERT INTO menu_variations (item_id, variation_name, variation_price, last_modified_by) VALUES (?, ?, ?,?)";
                PreparedStatement variationStmt = conn.prepareStatement(insertVariationSQL);

                for (String variation : variations) {
                    // Extract variation name & price from format: "Cut (₱50.00)"
                    int priceStart = variation.indexOf("₱"); 
                    if (priceStart != -1) {
                        String variationName = variation.substring(0, priceStart).trim().replaceAll("\\($", "");

                        String priceString = variation.substring(priceStart + 1, variation.length() - 1); // Remove ₱ and )

                        variationStmt.setInt(1, itemId); // Link variation to menu item
                        variationStmt.setString(2, variationName); // Extracted variation name
                        variationStmt.setBigDecimal(3, new BigDecimal(priceString)); // Convert price to BigDecimal
                        variationStmt.setInt(4, userID); // Convert price to BigDecimal
                        variationStmt.executeUpdate();
                    }
                }
                System.out.println("Variations added successfully!");
                // ✅ Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Menu Item Saved");
            alert.setHeaderText(null);
            alert.setContentText("The menu item and its variations have been saved successfully!");
            alert.showAndWait();

                
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        });

    // Add components to the form layout
    formPane.getChildren().addAll(titleLabel, nameField, priceField, availabilityCombo , categoryComboBox, descriptionField, variationPane,uploadButton,  imageView, saveButton);

    //===
    TextField searchField = new TextField();
    searchField.setPromptText("Search...");

    ComboBox<String> categoryFilter = new ComboBox<>();
    categoryFilter.getItems().add("All Categories");
    categoryFilter.getItems().addAll(getAllCategoryNamesFromDB());
    categoryFilter.setValue("All Categories");

    Button sortAZ = new Button("Sort A-Z");
    Button sortZA = new Button("Sort Z-A");

    HBox topBar = new HBox(10, searchField, categoryFilter, sortAZ, sortZA);
    topBar.setPadding(new Insets(10));

    
    // ====== TABLEVIEW FOR DISPLAYING MENU ITEMS ======
    TableView<FoodItem> tableView = new TableView<>();
    tableView.setPrefWidth(500); // Give it enough space

    TableColumn<FoodItem, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameColumn.setPrefWidth(100); // Adjust width

    TableColumn<FoodItem, Double> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    priceColumn.setPrefWidth(100);

    TableColumn<FoodItem, Double> availabilityColumn = new TableColumn<>("Availability");
    availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
    availabilityColumn.setPrefWidth(100);
    
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

    tableView.getColumns().addAll(nameColumn, priceColumn, availabilityColumn, categoryColumn, descriptionColumn, imageColumn);
    
   ObservableList<FoodItem> menuItems = FXCollections.observableArrayList();
         
    FilteredList<FoodItem> filteredData = new FilteredList<>(menuItems, p -> true);
    SortedList<FoodItem> sortedData = new SortedList<>(filteredData);
    sortedData.comparatorProperty().bind(tableView.comparatorProperty());
    tableView.setItems(sortedData);

    

    try (Connection conn = Database.connect()) {
        String sql = "SELECT item_id, name, price, availability, category_id, description, image_path FROM menu_items";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("item_id");
            String name = rs.getString("name");
            double price = rs.getDouble("price");
            String availability = rs.getString("availability"); // fixed
            int category = rs.getInt("category_id");
            String description = rs.getString("description");
            String imagePath = rs.getString("image_path");

            // Add to TableView
            menuItems.add(new FoodItem(id, name, price, availability, category, description, imagePath));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
        applyFilters(filteredData, searchField, categoryFilter);
    });

    categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
        applyFilters(filteredData, searchField, categoryFilter);
    });

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



    // Make table stretchable
    HBox.setHgrow(tableView, Priority.ALWAYS);
    HBox.setHgrow(formPane, Priority.ALWAYS);

    // Update selection event
   tableView.setOnMouseClicked(event -> {
        if (event.getClickCount() == 2 && !tableView.getSelectionModel().isEmpty()) {
            FoodItem selectedItem = tableView.getSelectionModel().getSelectedItem();
            nameField.setText(selectedItem.getName());
            priceField.setText(String.valueOf(selectedItem.getPrice()));
             availabilityCombo.setValue(selectedItem.getAvailability());

            // Convert category ID to category name before setting ComboBox value
            String categoryName = getCategoryName(selectedItem.getCategoryId());
            categoryComboBox.setValue(categoryName);

            descriptionField.setText(selectedItem.getDescription());
            // ✅ Fetch variations from database and update variationList
             fetchAndDisplayVariations(selectedItem.getId());
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
                // Delete variations first
                String deleteVariationsSQL = "DELETE FROM menu_variations WHERE item_id = (SELECT item_id FROM menu_items WHERE name = ?)";
                PreparedStatement deleteVariationsStmt = conn.prepareStatement(deleteVariationsSQL);
                deleteVariationsStmt.setString(1, selectedItem.getName());
                deleteVariationsStmt.executeUpdate();

                // Delete the menu item itself
                String deleteItemSQL = "DELETE FROM menu_items WHERE name = ?";
                PreparedStatement deleteItemStmt = conn.prepareStatement(deleteItemSQL);
                deleteItemStmt.setString(1, selectedItem.getName());
                deleteItemStmt.executeUpdate();

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
                System.out.println("✅ Menu item and its variations deleted successfully!");

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

    });

    // Add to form layout
    formPane.getChildren().add(deleteButton);
   
     VBox searchTableView = new VBox(10); // VBox to stack search bar and table with spacing
    searchTableView.getChildren().addAll(topBar, tableView);

    // Add formPane and tableView to mainPane
    mainPane.getChildren().addAll(formPane, searchTableView);
    mainContent.getChildren().add(mainPane);  // ✅ Add menu UI inside `mainContent`
}
    private void applyFilters(FilteredList<FoodItem> filteredData, TextField searchField, ComboBox<String> categoryFilter) {
    String search = searchField.getText().toLowerCase();
    String selectedCategory = categoryFilter.getValue();

    filteredData.setPredicate(item -> {
        boolean matchesSearch = item.getName().toLowerCase().contains(search)
            || String.valueOf(item.getPrice()).contains(search)
            || item.getAvailability().toLowerCase().contains(search)
            || getCategoryName(item.getCategoryId()).toLowerCase().contains(search)
            || (item.getDescription() != null && item.getDescription().toLowerCase().contains(search));

        boolean matchesCategory = selectedCategory.equals("All Categories") ||
            getCategoryName(item.getCategoryId()).equals(selectedCategory);

        return matchesSearch && matchesCategory;
    });
}
    
    public List<String> getAllCategoryNamesFromDB() {
        List<String> categoryNames = new ArrayList<>();

        try (Connection conn = Database.connect()) {
            String sql = "SELECT category_name FROM categories"; // adjust table name if needed
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                categoryNames.add(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return categoryNames;
    }


    private void fetchAndDisplayVariations(int itemId) {
      variationList.getItems().clear(); // Clear old variations first

      try (Connection conn = Database.connect()) {
          String sql = "SELECT variation_name, variation_price FROM menu_variations WHERE item_id = ?";
          PreparedStatement stmt = conn.prepareStatement(sql);
          stmt.setInt(1, itemId);
          ResultSet rs = stmt.executeQuery();

          while (rs.next()) {
              String variationName = rs.getString("variation_name");
              BigDecimal variationPrice = rs.getBigDecimal("variation_price");

              // Format variation as: "Cut (₱50.00)"
              String formattedVariation = variationName + " (₱" + variationPrice + ")";
              variationList.getItems().add(formattedVariation);
          }
      } catch (SQLException ex) {
          ex.printStackTrace();
      }
  }



    public static void main(String[] args) {
        launch(args);
    }
}
