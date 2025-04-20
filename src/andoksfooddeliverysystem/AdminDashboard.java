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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import javafx.util.Duration;
import javax.mail.MessagingException;



public class AdminDashboard extends Application {
    private int userID;
    private VBox sidebar;
    private BorderPane mainLayout;
    private boolean sidebarVisible = true;
    private VBox mainContent;
    ListView<String> variationList;
    
    // Define brand colors
    private static final String PRIMARY_RED = "#C81D24";     // Andok's red
    private static final String ACCENT_YELLOW = "#FFBA00";   // Vibrant yellow
    private static final String WHITE = "#FFFFFF";           // Pure white
    private static final String LIGHT_GRAY = "#F5F5F5";      // For hover states
    private static final String DARK_TEXT = "#2B2B2B";       // For text
    private static final String SIDEBAR_BG = "#1A1A1A";      // Dark sidebar background
    
    public AdminDashboard(int userID) {
        this.userID = userID;
        System.out.println("✅ AdminDashboard opened with User ID: " + userID); // Debugging
    }
    
    @Override
    public void start(Stage primaryStage) throws MessagingException {
        mainLayout = new BorderPane();
        
        // Create main content area
        mainContent = new VBox();
        mainContent.setPadding(new Insets(20));
        mainContent.setStyle("-fx-background-color: " + WHITE + ";");
        mainLayout.setCenter(mainContent);
        
        // Create sidebar with modern styling
        createSidebar();
        mainLayout.setLeft(sidebar);
        
        // Top bar with toggle button and title
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);
        
        // Set up the scene
        Scene scene = new Scene(mainLayout);
        
        // Add custom font if available
        try {
            scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap");
            mainLayout.setStyle("-fx-font-family: 'Poppins', sans-serif;");
        } catch (Exception e) {
            // Fallback to system fonts if web font fails
            mainLayout.setStyle("-fx-font-family: 'Segoe UI', 'Arial', sans-serif;");
        }
        
        primaryStage.setWidth(1500);
        primaryStage.setHeight(800);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Andok's Admin Dashboard");
        primaryStage.show();
        
        // Show admin dashboard immediately on load
        showMainDashboard();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(15, 25, 15, 15));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(15);
        topBar.setStyle("-fx-background-color: " + WHITE + ";" +
                      "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        // Toggle sidebar button with hamburger icon
        Button toggleSidebar = new Button("☰");
        toggleSidebar.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        );
        
        // Hover effect
        toggleSidebar.setOnMouseEntered(e -> toggleSidebar.setStyle(
            "-fx-background-color: " + LIGHT_GRAY + ";" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;" +
            "-fx-background-radius: 5;"
        ));
        
        toggleSidebar.setOnMouseExited(e -> toggleSidebar.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + PRIMARY_RED + ";" +
            "-fx-font-size: 16px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 5 10;"
        ));
        
        toggleSidebar.setOnAction(e -> toggleSidebar());
        
        // Dashboard title
        Label dashboardTitle = new Label("ANDOK'S ADMIN DASHBOARD");
        dashboardTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_RED + ";"
        );
        
        // User info section on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Admin ID: " + userID);
        userLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        topBar.getChildren().addAll(toggleSidebar, dashboardTitle, spacer, userLabel);
        
        return topBar;
    }
    
    private void createSidebar() {
        // Create sidebar container
        sidebar = new VBox(0);
        sidebar.setPrefWidth(250);
        sidebar.setStyle(
            "-fx-background-color: " + SIDEBAR_BG + ";" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 0);"
        );
        
        // Logo/brand section at top
        HBox logoBox = new HBox();
        logoBox.setPadding(new Insets(25, 15, 25, 15));
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setStyle("-fx-background-color: " + PRIMARY_RED + ";");
        
        Label brandLabel = new Label("ANDOK'S");
        brandLabel.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + WHITE + ";"
        );
        
        logoBox.getChildren().add(brandLabel);
        
        // Navigation section
        VBox navItems = new VBox(0);
        
        // Add menu items
        String[] menuItems = {"Admin Dashboard", "Menu", "Register Riders", "Orders", "Audit Logs", "Order History"};
        String[] menuIcons = {"🏠", "🍗", "🛵", "📋", "📊", "⏱️"};
        
        for (int i = 0; i < menuItems.length; i++) {
            HBox navItem = createNavItem(menuItems[i], menuIcons[i], i);
            navItems.getChildren().add(navItem);
        }
        
        // Add spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Logout button at bottom
        HBox logoutBox = createNavItem("Log Out", "🚪", -1);
        logoutBox.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-border-color: " + PRIMARY_RED + ";" +
            "-fx-border-width: 1 0 0 0;" +
            "-fx-padding: 15 15 15 15;" +
            "-fx-cursor: hand;"
        );
        
        // Set logout action
        logoutBox.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Log Out Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to log out?");
            
            // Style dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + WHITE + ";");
            
            // Style buttons
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            
            okButton.setStyle(
                "-fx-background-color: " + PRIMARY_RED + ";" +
                "-fx-text-fill: " + WHITE + ";" 
            );
            
            cancelButton.setStyle(
                "-fx-background-color: #cccccc;" +
                "-fx-text-fill: " + DARK_TEXT + ";"
            );
            
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Stage stage = (Stage) sidebar.getScene().getWindow();
                stage.close();
                new Main().start(new Stage());
            }
        });
        
        // Add all components to sidebar
        sidebar.getChildren().addAll(logoBox, navItems, spacer, logoutBox);
    }
    
    private HBox createNavItem(String text, String icon, int index) {
        HBox navItem = new HBox(15);
        navItem.setPadding(new Insets(15));
        navItem.setAlignment(Pos.CENTER_LEFT);
        
        // Menu indicator bar (initially invisible for non-active items)
        Rectangle indicator = new Rectangle(5, 30);
        indicator.setFill(Color.web(ACCENT_YELLOW));
        indicator.setVisible(index == 0); // Only visible for first item initially
        
        // Icon label
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 16px;");
        
        // Menu text
        Label menuText = new Label(text);
        menuText.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: 500;" +
            "-fx-text-fill: " + WHITE + ";"
        );
        
        // Default styling
        navItem.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-cursor: hand;"
        );
        
        // Hover styling
        navItem.setOnMouseEntered(e -> {
            if (!indicator.isVisible()) {
                navItem.setStyle(
                    "-fx-background-color: rgba(255,255,255,0.1);" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        navItem.setOnMouseExited(e -> {
            if (!indicator.isVisible()) {
                navItem.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-cursor: hand;"
                );
            }
        });
        
        // Active styling (for first item by default)
        if (index == 0) {
            navItem.setStyle(
                "-fx-background-color: rgba(255,255,255,0.1);" +
                "-fx-cursor: hand;"
            );
        }
        
        // Set actions based on menu item
        switch (text) {
            case "Admin Dashboard":
                navItem.setOnMouseClicked(e -> {
                    try {
                        showMainDashboard();
                        setActiveNavItem(navItem, index);
                    } catch (MessagingException ex) {
                        Logger.getLogger(AdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
                break;
            case "Menu":
                navItem.setOnMouseClicked(e -> {
                    showMenuManagement();
                    setActiveNavItem(navItem, index);
                });
                break;
            case "Register Riders":
                navItem.setOnMouseClicked(e -> {
                    showRiderManagement();
                    setActiveNavItem(navItem, index);
                });
                break;
            case "Orders":
                navItem.setOnMouseClicked(e -> {
                    showOrders();
                    setActiveNavItem(navItem, index);
                });
                break;
            case "Audit Logs":
                navItem.setOnMouseClicked(e -> {
                    showLogs();
                    setActiveNavItem(navItem, index);
                });
                break;
            case "Order History":
                navItem.setOnMouseClicked(e -> {
                    showOrderHistory();
                    setActiveNavItem(navItem, index);
                });
                break;
        }
        
        navItem.getChildren().addAll(indicator, iconLabel, menuText);
        return navItem;
    }
    
    private void setActiveNavItem(HBox clickedItem, int index) {
        // Clear all active states
        for (Node node : ((VBox) sidebar.getChildren().get(1)).getChildren()) {
            if (node instanceof HBox) {
                HBox item = (HBox) node;
                item.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                
                // Find and hide indicator
                for (Node child : item.getChildren()) {
                    if (child instanceof Rectangle) {
                        ((Rectangle) child).setVisible(false);
                    }
                }
            }
        }
        
        // Set active state for clicked item
        clickedItem.setStyle(
            "-fx-background-color: rgba(255,255,255,0.1);" +
            "-fx-cursor: hand;"
        );
        
        // Show indicator for active item
        for (Node child : clickedItem.getChildren()) {
            if (child instanceof Rectangle) {
                ((Rectangle) child).setVisible(true);
            }
        }
    }
    
    private void toggleSidebar() {
        if (sidebarVisible) {
            // Hide sidebar with animation
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(200), sidebar);
            slideOut.setToX(-sidebar.getWidth());
            slideOut.setOnFinished(e -> {
                mainLayout.setLeft(null);
                sidebarVisible = false;
            });
            slideOut.play();
        } else {
            // Show sidebar with animation
            mainLayout.setLeft(sidebar);
            TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), sidebar);
            slideIn.setFromX(-sidebar.getWidth());
            slideIn.setToX(0);
            slideIn.play();
            sidebarVisible = true;
        }
    }
    
    private void showMainDashboard() throws MessagingException {
      mainContent.getChildren().clear();
      System.out.println("Switching to Admin Dashboard");

      ShowAdminDashboard showAdminDashboard = new ShowAdminDashboard(userID);
      Node adminUI = showAdminDashboard.getRoot();

      if (adminUI == null) {
          System.out.println("❌ Admin UI is null!");  // Debugging
      } else {
          System.out.println("✅ Adding Admin UI to mainContent");
          mainContent.getChildren().add(adminUI);
      }
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

 private void showOrderHistory() {
        mainContent.getChildren().clear();
        System.out.println("Switching to ORder History");

        OrderHistory showOrderHistory = new OrderHistory(userID);
        Node orderHistoryUI = showOrderHistory.getRoot();

        if (orderHistoryUI == null) {
            System.out.println("❌ Order History UI is null!");  // Debugging
        } else {
            System.out.println("✅ Adding Order History UI to mainContent");
            mainContent.getChildren().add(orderHistoryUI);
        }
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
