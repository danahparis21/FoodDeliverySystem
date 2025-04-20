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
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

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
    // Base styling variables
    String primaryRed = "#D50000";
    String secondaryYellow = "#FFD600";
    String neutralWhite = "#FFFFFF";
    String darkText = "#212121";
    String lightGray = "#F5F5F5";
    String mediumGray = "#E0E0E0";
    
    // Main container with modern styling
    BorderPane mainContainer = new BorderPane();
    mainContainer.setStyle("-fx-background-color: " + neutralWhite + ";");
    
    // Header with title
    HBox header = new HBox();
    header.setStyle("-fx-background-color: " + primaryRed + "; -fx-padding: 15px; -fx-alignment: center-left;");
    Label headerLabel = new Label("Menu Management");
    headerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + neutralWhite + ";");
    header.getChildren().add(headerLabel);
    
    // Main content with split pane for form and table
    SplitPane contentSplit = new SplitPane();
    contentSplit.setDividerPositions(0.4);
    
    // ====== FORM FOR ADDING MENU ITEMS ======
    ScrollPane formScrollPane = new ScrollPane();
    formScrollPane.setFitToWidth(true);
    formScrollPane.setStyle("-fx-background-color: " + neutralWhite + ";");
    
    VBox formPane = new VBox(15);
    formPane.setPadding(new Insets(20));
    formPane.setStyle("-fx-background-color: " + neutralWhite + ";");
    
    // Section title with accent bar
    HBox titleBox = new HBox(10);
    titleBox.setAlignment(Pos.CENTER_LEFT);
    Rectangle accentBar = new Rectangle(5, 24, Color.web(secondaryYellow));
    Label titleLabel = new Label("Add Menu Item");
    titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + darkText + ";");
    titleBox.getChildren().addAll(accentBar, titleLabel);
    
    // Form fields with better styling
    VBox nameBox = createFormField("Item Name", false);
    TextField nameField = (TextField) nameBox.getChildren().get(1);
    
    VBox priceBox = createFormField("Price (₱)", false);
    TextField priceField = (TextField) priceBox.getChildren().get(1);
    
    VBox availabilityBox = createFormField("Availability", false);
    ComboBox<String> availabilityCombo = new ComboBox<>();
    availabilityCombo.getItems().addAll("Available", "Not Available");
    availabilityCombo.setValue("Available");
    availabilityCombo.setMaxWidth(Double.MAX_VALUE);
    availabilityCombo.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    availabilityBox.getChildren().set(1, availabilityCombo);
    
    VBox categoryBox = createFormField("Category", false);
    ComboBox<String> categoryComboBox = new ComboBox<>();
    categoryComboBox.setPromptText("Select Category");
    categoryComboBox.setMaxWidth(Double.MAX_VALUE);
    categoryComboBox.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    loadCategories(categoryComboBox); // Call method to populate categories
    categoryBox.getChildren().set(1, categoryComboBox);
    
    VBox descriptionBox = createFormField("Description", true);
    TextArea descriptionField = (TextArea) descriptionBox.getChildren().get(1);
    
    // Variations section with better styling
    VBox variationSection = new VBox(15);
    variationSection.setStyle("-fx-background-color: " + lightGray + "; -fx-padding: 15px; -fx-background-radius: 5px;");
    
    Label variationTitle = new Label("Variations");
    variationTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    
    HBox variationInputs = new HBox(10);
    TextField variationField = new TextField();
    variationField.setPromptText("Variation Name");
    variationField.setPrefWidth(150);
    variationField.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    
    TextField variationPriceField = new TextField();
    variationPriceField.setPromptText("Price Adjustment");
    variationPriceField.setPrefWidth(120);
    variationPriceField.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    
    Button addVariationButton = new Button("Add");
    addVariationButton.setStyle("-fx-background-color: " + secondaryYellow + "; -fx-text-fill: " + darkText + "; -fx-font-weight: bold; -fx-background-radius: 4px;");
    HBox.setHgrow(variationField, Priority.ALWAYS);
    variationInputs.getChildren().addAll(variationField, variationPriceField, addVariationButton);
    
    variationList = new ListView<>();
    variationList.setPrefHeight(150);
    variationList.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    ObservableList<String> variationItems = FXCollections.observableArrayList();
    variationList.setItems(variationItems);
    
    addVariationButton.setOnAction(e -> {
        String variationName = variationField.getText();
        String variationPrice = variationPriceField.getText();

        if (!variationName.isEmpty() && !variationPrice.isEmpty()) {
            variationItems.add(variationName + " (₱" + variationPrice + ")");
            variationField.clear();
            variationPriceField.clear();
        }
    });
    
    variationSection.getChildren().addAll(variationTitle, variationInputs, variationList);
    
    // Image upload section
    VBox imageSection = new VBox(15);
    imageSection.setStyle("-fx-padding: 15px 0;");
    
    Label imageLabel = new Label("Menu Item Image");
    imageLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    
    ImageView imageView = new ImageView();
    imageView.setFitHeight(150);
    imageView.setFitWidth(150);
    imageView.setPreserveRatio(true);
    
    // Style image placeholder
    Rectangle imagePlaceholder = new Rectangle(150, 150);
    imagePlaceholder.setArcWidth(10);
    imagePlaceholder.setArcHeight(10);
    imagePlaceholder.setFill(Color.web(lightGray));
    
    StackPane imageContainer = new StackPane();
    imageContainer.getChildren().add(imagePlaceholder);
    imageContainer.getChildren().add(imageView);
    
    Button uploadButton = new Button("Upload Image");
    uploadButton.setStyle("-fx-background-color: " + secondaryYellow + "; -fx-text-fill: " + darkText + "; -fx-font-weight: bold; -fx-background-radius: 4px;");
    uploadButton.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            imageView.setImage(new Image(file.toURI().toString()));
        }
    });
    
    HBox imageButtonsBox = new HBox(10);
    imageButtonsBox.setAlignment(Pos.CENTER);
    imageButtonsBox.getChildren().add(uploadButton);
    
    imageSection.getChildren().addAll(imageLabel, imageContainer, imageButtonsBox);
    imageSection.setAlignment(Pos.CENTER);
    
    // Action buttons
    HBox actionButtons = new HBox(15);
    actionButtons.setPadding(new Insets(20, 0, 10, 0));
    actionButtons.setAlignment(Pos.CENTER);
    
    Button saveButton = new Button("Save Item");
    saveButton.setPrefWidth(120);
    saveButton.setStyle("-fx-background-color: " + primaryRed + "; -fx-text-fill: " + neutralWhite + "; -fx-font-weight: bold; -fx-background-radius: 4px;");
    
    Button deleteButton = new Button("Delete");
    deleteButton.setPrefWidth(120);
    deleteButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + primaryRed + "; -fx-font-weight: bold; -fx-border-color: " + primaryRed + "; -fx-border-radius: 4px;");
    
    
    actionButtons.getChildren().addAll(saveButton, deleteButton);
    
    // Set up save action
    saveButton.setOnAction(e -> {
        String selectedCategory = categoryComboBox.getValue();
        if (selectedCategory == null) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Please select a category!");
            return;
        }

        if (nameField.getText().isEmpty() || priceField.getText().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Missing Information", "Name and price are required fields.");
            return;
        }

        try {
            String itemName = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String availability = availabilityCombo.getValue();
            String description = descriptionField.getText();

            // Get category_id from category_name
            int categoryId = getCategoryId(selectedCategory);
            if (categoryId == -1) {
                showAlert(Alert.AlertType.ERROR, "Error", "Category not found!");
                return;
            }
            
            // Handle image and database operations (keep the original logic)
            // Handle Image
            String imagePath = null;
            if (imageView.getImage() != null) {
                try {
                    File destFolder = new File("src/menu");
                    if (!destFolder.exists()) {
                        destFolder.mkdirs();
                    }

                    File file = new File(Paths.get(URI.create(imageView.getImage().getUrl())).toFile().getAbsolutePath());
                    File destFile = new File(destFolder, file.getName());

                    if (file.exists()) {
                        Files.copy(file.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        imagePath = "src/menu/" + file.getName();
                    } else {
                        System.out.println("❌ Image file not found: " + file.getAbsolutePath());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            // Database operations remain the same
            try (Connection conn = Database.connect()) {
                // Check if the item exists based on ID
                String checkSql = "SELECT item_id FROM menu_items WHERE name = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setString(1, itemName);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    int existingId = rs.getInt("item_id");

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
                            // Update existing menu item logic
                            // (Keep the original code)
                        } 
                        else if (result.get() == insertNewButton) {
                            // Insert new item logic
                            // (Keep the original code)
                        }
                    }
                } else {
                    // Insert new record logic
                    // (Keep the original code)
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Database Error", "Error saving menu item: " + ex.getMessage());
            }
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid price.");
        }
    });
  
    
    // Add components to the form layout
    formPane.getChildren().addAll(
        titleBox, 
        nameBox, 
        priceBox, 
        availabilityBox, 
        categoryBox, 
        descriptionBox, 
        variationSection, 
        imageSection, 
        actionButtons
    );
    
    formScrollPane.setContent(formPane);
    
    // ====== TABLEVIEW FOR DISPLAYING MENU ITEMS ======
    VBox tableContainer = new VBox(15);
    tableContainer.setPadding(new Insets(20));
    tableContainer.setStyle("-fx-background-color: " + lightGray + ";");
    
    // Modern search and filter section
    HBox searchFilterBar = new HBox(10);
    searchFilterBar.setPadding(new Insets(0, 0, 15, 0));
    searchFilterBar.setAlignment(Pos.CENTER_LEFT);
    
    TextField searchField = new TextField();
    searchField.setPromptText("Search menu items...");
    searchField.setPrefWidth(200);
    searchField.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    HBox.setHgrow(searchField, Priority.ALWAYS);
    
    ComboBox<String> categoryFilter = new ComboBox<>();
    categoryFilter.setPromptText("Category Filter");
    categoryFilter.getItems().add("All Categories");
    categoryFilter.getItems().addAll(getAllCategoryNamesFromDB());
    categoryFilter.setValue("All Categories");
    categoryFilter.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    
    HBox sortButtons = new HBox(5);
    Button sortAZ = new Button("A-Z");
    sortAZ.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    Button sortZA = new Button("Z-A");
    sortZA.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    sortButtons.getChildren().addAll(sortAZ, sortZA);
    
    searchFilterBar.getChildren().addAll(searchField, categoryFilter, sortButtons);
    
    // TableView with better styling
    TableView<FoodItem> tableView = new TableView<>();
    tableView.setStyle("-fx-background-color: " + neutralWhite + "; -fx-border-color: " + mediumGray + "; -fx-border-radius: 4px;");
    
    TableColumn<FoodItem, String> nameColumn = new TableColumn<>("Name");
    nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
    nameColumn.setPrefWidth(120);
    
    TableColumn<FoodItem, Double> priceColumn = new TableColumn<>("Price");
    priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
    priceColumn.setPrefWidth(80);
    priceColumn.setCellFactory(col -> new TableCell<FoodItem, Double>() {
        @Override
        protected void updateItem(Double price, boolean empty) {
            super.updateItem(price, empty);
            if (empty || price == null) {
                setText(null);
            } else {
                setText("₱" + String.format("%.2f", price));
            }
        }
    });
    
    TableColumn<FoodItem, String> availabilityColumn = new TableColumn<>("Status");
    availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));
    availabilityColumn.setPrefWidth(100);
    availabilityColumn.setCellFactory(col -> new TableCell<FoodItem, String>() {
        @Override
        protected void updateItem(String status, boolean empty) {
            super.updateItem(status, empty);
            if (empty || status == null) {
                setText(null);
                setGraphic(null);
            } else {
                HBox statusBox = new HBox(5);
                statusBox.setAlignment(Pos.CENTER_LEFT);
                
                Circle indicator = new Circle(5);
                indicator.setFill(status.equals("Available") ? Color.GREEN : Color.RED);
                
                Text text = new Text(status);
                
                statusBox.getChildren().addAll(indicator, text);
                setGraphic(statusBox);
            }
        }
    });
    
    TableColumn<FoodItem, String> categoryColumn = new TableColumn<>("Category");
    categoryColumn.setCellValueFactory(cellData -> {
        int categoryId = cellData.getValue().getCategoryId();
        String categoryName = getCategoryName(categoryId);
        return new SimpleStringProperty(categoryName);
    });
    categoryColumn.setPrefWidth(100);
    
    TableColumn<FoodItem, String> descriptionColumn = new TableColumn<>("Description");
    descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    descriptionColumn.setPrefWidth(150);
    
    TableColumn<FoodItem, Void> actionsColumn = new TableColumn<>("Actions");
    actionsColumn.setPrefWidth(100);
    actionsColumn.setCellFactory(col -> new TableCell<FoodItem, Void>() {
        private final Button editButton = new Button("Edit");
        {
            editButton.setStyle("-fx-background-color: " + secondaryYellow + "; -fx-text-fill: " + darkText + "; -fx-font-size: 11px;");
        }
        
        
        
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            } else {
                editButton.setOnAction(event -> {
                    FoodItem foodItem = getTableView().getItems().get(getIndex());
                    // Populate form with this item's data
                    nameField.setText(foodItem.getName());
                    priceField.setText(String.valueOf(foodItem.getPrice()));
                    availabilityCombo.setValue(foodItem.getAvailability());
                    categoryComboBox.setValue(getCategoryName(foodItem.getCategoryId()));
                    descriptionField.setText(foodItem.getDescription());
                    fetchAndDisplayVariations(foodItem.getId());
                    
                    // Load image
                    if (foodItem.getImagePath() != null && !foodItem.getImagePath().isEmpty()) {
                        File file = new File(foodItem.getImagePath());
                        if (file.exists()) {
                            imageView.setImage(new Image(file.toURI().toString()));
                        } else {
                            imageView.setImage(null);
                        }
                    } else {
                        imageView.setImage(null);
                    }
                });
                
                setGraphic(editButton);
            }
        }
    });
    
    tableView.getColumns().addAll(nameColumn, priceColumn, availabilityColumn, categoryColumn, descriptionColumn, actionsColumn);
    
    ObservableList<FoodItem> menuItems = FXCollections.observableArrayList();
    FilteredList<FoodItem> filteredData = new FilteredList<>(menuItems, p -> true);
    SortedList<FoodItem> sortedData = new SortedList<>(filteredData);
    sortedData.comparatorProperty().bind(tableView.comparatorProperty());
    tableView.setItems(sortedData);
    
    // Load data from database
    try (Connection conn = Database.connect()) {
        String sql = "SELECT item_id, name, price, availability, category_id, description, image_path FROM menu_items";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            int id = rs.getInt("item_id");
            String name = rs.getString("name");
            double price = rs.getDouble("price");
            String availability = rs.getString("availability");
            int category = rs.getInt("category_id");
            String description = rs.getString("description");
            String imagePath = rs.getString("image_path");
            
            menuItems.add(new FoodItem(id, name, price, availability, category, description, imagePath));
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
    
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


    
    // Apply filters
    searchField.textProperty().addListener((obs, oldVal, newVal) -> {
        applyFilters(filteredData, searchField, categoryFilter);
    });
    
    categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
        applyFilters(filteredData, searchField, categoryFilter);
    });
    
    // Add sort functionality
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
    
    // Add components to table container
    tableContainer.getChildren().addAll(searchFilterBar, tableView);
    VBox.setVgrow(tableView, Priority.ALWAYS);
    
    // Add both panes to the split pane
    contentSplit.getItems().addAll(formScrollPane, tableContainer);
    
    // Set up main container layout
    mainContainer.setTop(header);
    mainContainer.setCenter(contentSplit);
    
    mainContent.getChildren().clear();
    mainContent.getChildren().add(mainContainer);
}

// Helper method to create form fields with consistent styling
private VBox createFormField(String labelText, boolean isTextArea) {
    VBox fieldBox = new VBox(5);
    Label label = new Label(labelText);
    label.setStyle("-fx-font-weight: bold;");
    
    if (isTextArea) {
        TextArea field = new TextArea();
        field.setPrefHeight(100);
        field.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 4px;");
        fieldBox.getChildren().addAll(label, field);
    } else {
        TextField field = new TextField();
        field.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-radius: 4px;");
        fieldBox.getChildren().addAll(label, field);
    }
    
    return fieldBox;
}

// Helper method to show alerts
private void showAlert(Alert.AlertType alertType, String title, String content) {
    Alert alert = new Alert(alertType);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(content);
    alert.showAndWait();
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
