package andoksfooddeliverysystem;
import java.sql.CallableStatement;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import java.sql.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Popup;
import javafx.stage.PopupWindow;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.animation.*;
import javafx.scene.shape.Circle;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.BlurType;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class CustomerDashboard extends Application {
    private int userID;
    private BorderPane mainLayout;
    private VBox sideBar;
    private GridPane menuGrid;
    private Label cartCountLabel;
    private Stage primaryStage;
    
    // Andok's color scheme
    private final String ANDOKS_RED = "#D61A0C";
    private final String ANDOKS_YELLOW = "#FFD700";
    private final String ANDOKS_WHITE = "#FFFFFF";
    private final String ANDOKS_DARK_RED = "#B01508";
    private final String ANDOKS_LIGHT_YELLOW = "#FFF0B3";
    private int currentCategoryId = 2; // default or initial
    private TextField searchField = new TextField();
    
    // Constructor to receive userID
    public CustomerDashboard(int userID) {
        this.userID = userID;
        System.out.println("✅ CustomerDashboard opened with User ID: " + userID);
    }
    
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        
        // Check if the store is open
        if (!isStoreOpen()) {
            showStoreClosedWindow();
            return;  // Skip the rest of the dashboard setup if store is closed
        }
        
        mainLayout = new BorderPane();
        
        // Fetch and display customer details based on userID
        fetchCustomerData(userID);
        CartSession.setCartListener(count -> updateCartCount(count));
        updateCartCount(CartSession.getCartItemCount()); // Initial count
        
        // 🔝 Top Bar with Andok's branding
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);
        
        // 📂 Side Category Tabs (Vertical) with images
        sideBar = new VBox(15);
        sideBar.setPadding(new Insets(20));
        sideBar.setStyle("-fx-background-color: #F8F8F8; -fx-border-color: #E0E0E0; -fx-border-width: 0 1 0 0;");
        sideBar.setPrefWidth(220);
        
        // Add header for categories
        Label categoriesHeader = new Label("CATEGORIES");
        categoriesHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_RED + "; -fx-padding: 0 0 10 0;");
        sideBar.getChildren().add(categoriesHeader);
        
        loadCategories(); // ✅ Load categories from database with images
        
        mainLayout.setLeft(sideBar);
        
        // Create container for main content area
        VBox contentContainer = new VBox(15);
        contentContainer.setPadding(new Insets(20));
        contentContainer.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");
        
        // Add a welcome header
        HBox welcomeHeader = new HBox(10);
        welcomeHeader.setAlignment(Pos.CENTER_LEFT);
        Label welcomeLabel = new Label("Welcome to Andok's Menu");
        welcomeLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_RED + ";");
        welcomeHeader.getChildren().add(welcomeLabel);
       

        // 📦 Menu Grid with enhanced styling
        menuGrid = new GridPane();
        menuGrid.setPadding(new Insets(10));
        menuGrid.setHgap(25);
        menuGrid.setVgap(25);
        loadCategoryItems(currentCategoryId, ""); // empty search

        
        contentContainer.getChildren().addAll(welcomeHeader, menuGrid);
        
        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + ANDOKS_WHITE + "; -fx-background-color: " + ANDOKS_WHITE + ";");
        
        mainLayout.setCenter(scrollPane);
        
        // 🎭 Scene Setup
        Scene scene = new Scene(mainLayout, 1450, 700);
        
        // Add CSS for global styling
        scene.getStylesheets().add(getClass().getResource("/styles/customerDashboard.css").toExternalForm());
        
        primaryStage.setScene(scene);
        primaryStage.setTitle("Andok's - Customer Dashboard");
        primaryStage.getIcons().add(new Image("file:src/icons/miniLogo.png"));

        primaryStage.show();
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10, 20, 10, 20));
        topBar.setSpacing(15);
        topBar.setStyle("-fx-background-color: " + ANDOKS_RED + ";");
        
        // Logo
     ImageView logo = new ImageView(
        new Image(Main.class.getResourceAsStream("/icons/andoksLogo.png"))
    );

        
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        
        // Restaurant Name
        Label restaurantName = new Label("ANDOK'S");
        restaurantName.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_YELLOW + ";");
        
        // Search Box with enhanced styling
        HBox searchBox = new HBox();
        searchBox.setAlignment(Pos.CENTER);
        searchBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 20px; -fx-padding: 2px 10px;");
        
      
        searchField.setPromptText("Search menu items...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-prompt-text-fill: rgba(255,255,255,0.7);");
        
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            loadCategoryItems(currentCategoryId, newValue);
        });

        FontIcon searchIcon = new FontIcon("fas-search");
        searchIcon.setIconColor(Color.WHITE);
        
        searchBox.getChildren().addAll(searchIcon, searchField);
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Notification Button with counter
        Button notifBtn = new Button();
        notifBtn.setGraphic(new FontIcon("fas-bell"));  // Using FontAwesome icon
        notifBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ANDOKS_WHITE + "; -fx-padding: 5px;");
        notifBtn.setCursor(Cursor.HAND);
        notifBtn.setOnAction(e -> showNotification());

        // Notification count badge
        Label notifCountLabel = new Label();
        notifCountLabel.setStyle(
            "-fx-background-color: " + ANDOKS_YELLOW + ";" +
            "-fx-text-fill: " + ANDOKS_RED + ";" +
            "-fx-font-size: 10px;" +
            "-fx-padding: 1px 4px;" +
            "-fx-background-radius: 10;" +
            "-fx-min-width: 16px;" +
            "-fx-alignment: center;" +
            "-fx-font-weight: bold;"
        );
        notifCountLabel.setVisible(false); // Hide if no unread notifications

        // Update notification count
        int customerId = getCustomerIdFromUserId(userID);
        int unreadCount = getUnreadNotificationCount(customerId);

        if (unreadCount > 0) {
            notifCountLabel.setText(String.valueOf(unreadCount));
            notifCountLabel.setVisible(true);  // Show if there's a count > 0
        } else {
            notifCountLabel.setVisible(false); // Hide if no unread notifications
        }

        // Stack notification button and badge
        StackPane notifButtonPane = new StackPane();
        notifButtonPane.getChildren().addAll(notifBtn, notifCountLabel);
        StackPane.setAlignment(notifCountLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(notifCountLabel, new Insets(-2, -2, 0, 0));  // Position adjustment
        
        // Cart Button with counter
        Button cartBtn = new Button();
        cartBtn.setGraphic(new FontIcon("fas-shopping-cart"));  // Using FontAwesome icon
        cartBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ANDOKS_WHITE + "; -fx-padding: 5px;");
        cartBtn.setCursor(Cursor.HAND);
        cartBtn.setOnAction(e -> showCart());

        // Cart count indicator
        cartCountLabel = new Label("0");
        cartCountLabel.setStyle(
            "-fx-background-color: " + ANDOKS_YELLOW + ";" +
            "-fx-text-fill: " + ANDOKS_RED + ";" +
            "-fx-font-size: 10px;" +
            "-fx-padding: 1px 4px;" +
            "-fx-background-radius: 10;" +
            "-fx-min-width: 16px;" +
            "-fx-alignment: center;" +
            "-fx-font-weight: bold;"
        );
        
        // Position the cart count indicator
        StackPane cartButtonPane = new StackPane();
        cartButtonPane.getChildren().addAll(cartBtn, cartCountLabel);
        StackPane.setAlignment(cartCountLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(cartCountLabel, new Insets(-2, -2, 0, 0));  // Position adjustment
        
        // Profile Button with more info
        HBox profileBox = new HBox(10);
        profileBox.setAlignment(Pos.CENTER);
        profileBox.setPadding(new Insets(3, 8, 3, 8));
        profileBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 50px;");
        
        // Profile picture in a circle
        Circle profilePic = new Circle(15);
      profilePic.setFill(new ImagePattern(
            new Image(Main.class.getResourceAsStream("/icons/default.png"))
        ));

        profilePic.setStroke(Color.valueOf(ANDOKS_YELLOW));
        profilePic.setStrokeWidth(1.5);
        
        // Add username label
        Label username = new Label("Customer");
        username.setStyle("-fx-text-fill: " + ANDOKS_WHITE + "; -fx-font-size: 14px;");
        
        profileBox.getChildren().addAll(profilePic, username);
        profileBox.setCursor(Cursor.HAND);
        profileBox.setOnMouseClicked(e -> {
            Customer customer = CustomerDAO.getCustomerByUserId(userID);
            if (customer != null) {
                CustomerProfile profile = new CustomerProfile();
                profile.show(primaryStage, customer, userID);
            } else {
                System.out.println("⚠️ No customer found for user ID " + userID);
            }
        });
        
        // Hover effects for interactive elements
        notifButtonPane.setOnMouseEntered(e -> 
            notifBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 50%; -fx-padding: 5px;")
        );
        notifButtonPane.setOnMouseExited(e -> 
            notifBtn.setStyle("-fx-background-color: transparent; -fx-padding: 5px;")
        );
        
        cartButtonPane.setOnMouseEntered(e -> 
            cartBtn.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 50%; -fx-padding: 5px;")
        );
        cartButtonPane.setOnMouseExited(e -> 
            cartBtn.setStyle("-fx-background-color: transparent; -fx-padding: 5px;")
        );
        
        profileBox.setOnMouseEntered(e -> 
            profileBox.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 50px; -fx-padding: 3px 8px;")
        );
        profileBox.setOnMouseExited(e -> 
            profileBox.setStyle("-fx-background-color: rgba(255,255,255,0.1); -fx-background-radius: 50px; -fx-padding: 3px 8px;")
        );
        
        topBar.getChildren().addAll(logo, restaurantName, searchBox, spacer, notifButtonPane, cartButtonPane, profileBox);
        return topBar;
    }
 
     private VBox createMenuItemBox(String itemName, String imagePath, double price) {
        VBox itemBox = new VBox(10);
        itemBox.setPadding(new Insets(15));
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setStyle(
            "-fx-background-color: #f8f8f8;" + 
            "-fx-border-radius: 10px;" + 
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0);"
        );
        itemBox.setPrefWidth(230);
        itemBox.setPrefHeight(280);
        
        // Create an image container with a mask
        StackPane imageContainer = new StackPane();
        Rectangle imageClip = new Rectangle(150, 120);
        imageClip.setArcWidth(20);
        imageClip.setArcHeight(20);
        
        ImageView imageView = new ImageView(new Image("file:" + imagePath));
        imageView.setFitWidth(150);
        imageView.setFitHeight(120);
        imageView.setClip(imageClip);
        
        // Add a subtle border
        Rectangle imageBorder = new Rectangle(150, 120);
        imageBorder.setArcWidth(20);
        imageBorder.setArcHeight(20);
        imageBorder.setFill(Color.TRANSPARENT);
        imageBorder.setStroke(Color.valueOf("#E0E0E0"));
        imageBorder.setStrokeWidth(1);
        
        imageContainer.getChildren().addAll(imageView, imageBorder);
        
        // Item name with enhanced styling
        Label nameLabel = new Label(itemName);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_RED + ";");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        nameLabel.setMaxWidth(200);
        

        HBox priceBox = new HBox();
        priceBox.setAlignment(Pos.CENTER);
        
        Label priceLabel = new Label("₱" + String.format("%.2f", price));
        priceLabel.setStyle(
            "-fx-font-size: 16px;" + 
            "-fx-font-weight: bold;" + 
            "-fx-background-color: " + ANDOKS_YELLOW + ";" +
            "-fx-text-fill: " + ANDOKS_RED + ";" +
            "-fx-padding: 3px 15px;" +
            "-fx-background-radius: 15px;"
        );
        
        priceBox.getChildren().add(priceLabel);
  
        // Item hover effects with more sophisticated animation
        itemBox.setOnMouseEntered(e -> {
            itemBox.setStyle(
                "-fx-background-color: white;" + 
                "-fx-border-radius: 10px;" + 
                "-fx-background-radius: 10px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 8, 0, 0, 0);"
            );
            
            // Subtle scale effect
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), itemBox);
            scaleTransition.setToX(1.03);
            scaleTransition.setToY(1.03);
            scaleTransition.play();
            
            // Add a subtle glow to the image
            DropShadow glow = new DropShadow();
            glow.setColor(Color.valueOf(ANDOKS_YELLOW));
            glow.setRadius(20);
            glow.setSpread(0.15);
            imageView.setEffect(glow);
        });
        
        itemBox.setOnMouseExited(e -> {
            itemBox.setStyle(
                "-fx-background-color: #f8f8f8;" + 
                "-fx-border-radius: 10px;" + 
                "-fx-background-radius: 10px;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 0);"
            );
            
            // Return to original scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), itemBox);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            
            // Remove glow effect
            imageView.setEffect(null);
        });
        
        
        itemBox.getChildren().addAll(imageContainer, nameLabel,  priceBox);
           // ⏩ Open details window on click (you might need to pass the price here too)
        itemBox.setOnMouseClicked(e -> MenuDetails.showItemDetails(itemName));

      
       
        return itemBox;
    }
     
    private void showNotification() {
    // Keep the existing customer ID logic
    int customerId = getCustomerIdFromUserId(userID);
    System.out.println("Fetching notifications from " + customerId);
    
    if (customerId == -1) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Unable to fetch notifications");
        alert.setContentText("No customer account found for this user.");
        styleAlert(alert);
        alert.showAndWait();
        return;
    }
    
    List<Notification> notifications = fetchNotifications(customerId);
    
    if (notifications.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifications");
        alert.setHeaderText("No New Notifications");
        alert.setContentText("You don't have any new notifications at this time.");
        styleAlert(alert);
        alert.showAndWait();
        return;
    }
    
    // Constants for styling
    final String PRIMARY_COLOR = "#FF5252"; // Red
    final String SECONDARY_COLOR = "#FFD740"; // Yellow
    final String BACKGROUND_COLOR = "#FFFFFF"; // White
    final String LIGHT_GRAY = "#F5F5F5";
    final String TEXT_COLOR = "#333333";
    
    // Create a dialog
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("🔔 Notifications");
    
    // Main container for notifications
    VBox mainContainer = new VBox(10);
    mainContainer.setPadding(new Insets(15));
    mainContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
    
    // Header section
    Label headerLabel = new Label("Notifications");
    headerLabel.setStyle(
        "-fx-font-size: 18px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + TEXT_COLOR + ";"
    );
    
    // Notification counter badge
    Label countLabel = new Label(notifications.size() + " new");
    countLabel.setStyle(
        "-fx-background-color: " + PRIMARY_COLOR + ";" +
        "-fx-text-fill: white;" +
        "-fx-font-size: 12px;" +
        "-fx-padding: 2 8;" +
        "-fx-background-radius: 10px;"
    );
    
    HBox headerBox = new HBox(10, headerLabel, countLabel);
    headerBox.setAlignment(Pos.CENTER_LEFT);
    
    // Separator
    Separator separator = new Separator();
    
    // Notifications list
    VBox notificationList = new VBox(10);
    
    for (int i = 0; i < notifications.size(); i++) {
        Notification notif = notifications.get(i);
        
        // Create notification card
        HBox notificationCard = createNotificationCard(notif, i, PRIMARY_COLOR, SECONDARY_COLOR, TEXT_COLOR);
        
        // Add click handler to show full message
        notificationCard.setOnMouseClicked(e -> {
            showFullNotification(notif);
            markAsRead(notif.notificationId);
            
            // Visual feedback - change background to indicate "read"
            notificationCard.setStyle(
                "-fx-background-color: #F8F8F8;" +
                "-fx-background-radius: 8px;" +
                "-fx-border-radius: 8px;" +
                "-fx-border-color: #EEEEEE;" +
                "-fx-border-width: 1px;" +
                "-fx-padding: 12px;" +
                "-fx-opacity: 0.7;"
            );
        });
        
        notificationList.getChildren().add(notificationCard);
    }
    
    // "Mark All Read" button
    Button markAllReadBtn = new Button("Mark All Read");
    markAllReadBtn.setStyle(
        "-fx-background-color: " + SECONDARY_COLOR + ";" +
        "-fx-text-fill: " + TEXT_COLOR + ";" +
        "-fx-font-weight: bold;" +
        "-fx-font-size: 12px;" +
        "-fx-padding: 8 15;" +
        "-fx-background-radius: 5px;"
    );
    markAllReadBtn.setOnAction(e -> {
        for (Notification notif : notifications) {
            markAsRead(notif.notificationId);
        }
        dialog.close();
    });
    
    HBox actionBox = new HBox(markAllReadBtn);
    actionBox.setAlignment(Pos.CENTER_RIGHT);
    actionBox.setPadding(new Insets(10, 0, 0, 0));
    
    // Scrollable container for notifications
    ScrollPane scrollPane = new ScrollPane(notificationList);
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle(
        "-fx-background: transparent;" +
        "-fx-background-color: transparent;" +
        "-fx-padding: 5px;" +
        "-fx-border-color: transparent;"
    );
    scrollPane.setPrefHeight(Math.min(notifications.size() * 80, 400));
    scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    
    // Add all components to main container
    mainContainer.getChildren().addAll(
        headerBox, 
        separator,
        scrollPane,
        actionBox
    );
    
    // Configure dialog
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.setContent(mainContainer);
    dialogPane.getButtonTypes().add(ButtonType.CLOSE);
    dialogPane.setPrefWidth(400);
    
    // Style the dialog
    dialogPane.setStyle(
        "-fx-background-color: " + BACKGROUND_COLOR + ";" +
        "-fx-border-radius: 5px;" +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 4);"
    );
    
    dialog.showAndWait();
}

private HBox createNotificationCard(Notification notification, int index, String primaryColor, String secondaryColor, String textColor) {
    // Handle timestamp - adjust field name as needed
    String timeText;
    try {
        timeText = notification.getTimestamp() != null ?
                notification.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) :
                "Just now";
    } catch (Exception e) {
        timeText = "Just now";
    }
    Label timeLabel = new Label(timeText);
    timeLabel.setStyle(
        "-fx-font-size: 11px;" +
        "-fx-text-fill: #888888;"
    );
    
    // Message preview
    String snippet = notification.message.length() > 60
            ? notification.message.substring(0, 60) + "..."
            : notification.message;
    
    Label messageLabel = new Label(snippet);
    messageLabel.setStyle(
        "-fx-font-size: 13px;" +
        "-fx-text-fill: " + textColor + ";" +
        "-fx-wrap-text: true;"
    );
    messageLabel.setWrapText(true);
    messageLabel.setMaxWidth(Double.MAX_VALUE);
    
    // "Read more" hint
    Label readMoreLabel = new Label("Tap to read more");
    readMoreLabel.setStyle(
        "-fx-font-size: 11px;" +
        "-fx-text-fill: " + primaryColor + ";" +
        "-fx-font-style: italic;"
    );
    
    // Left indicator bar for visual interest (alternating colors)
    Rectangle leftBar = new Rectangle(4, 60);
    leftBar.setFill(Color.web(index % 2 == 0 ? primaryColor : secondaryColor));
    leftBar.setArcWidth(2);
    leftBar.setArcHeight(2);
    
    // Create text content
    VBox textContent = new VBox(2, timeLabel, messageLabel);
    if (notification.message.length() > 60) {
        textContent.getChildren().add(readMoreLabel);
    }
    textContent.setPadding(new Insets(0, 0, 0, 10));
    HBox.setHgrow(textContent, Priority.ALWAYS);
    
    // Circle indicator for unread (assuming all notifications are unread when fetched)
    Circle unreadIndicator = new Circle(4);
    unreadIndicator.setFill(Color.web(primaryColor));
    
    // Combine elements
    HBox card = new HBox(leftBar, textContent, unreadIndicator);
    card.setAlignment(Pos.CENTER_LEFT);
    card.setPadding(new Insets(12));
    card.setStyle(
        "-fx-background-color: #FFFFFF;" +
        "-fx-background-radius: 8px;" +
        "-fx-border-radius: 8px;" +
        "-fx-border-color: #EEEEEE;" +
        "-fx-border-width: 1px;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);"
    );
    
    // Hover effect
    card.setOnMouseEntered(e -> {
        card.setStyle(
            "-fx-background-color: #FAFAFA;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-color: " + secondaryColor + ";" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);"
        );
    });
    
    card.setOnMouseExited(e -> {
        card.setStyle(
            "-fx-background-color: #FFFFFF;" +
            "-fx-background-radius: 8px;" +
            "-fx-border-radius: 8px;" +
            "-fx-border-color: #EEEEEE;" +
            "-fx-border-width: 1px;" +
            "-fx-padding: 12px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);"
        );
    });
    
    return card;
}

private void showFullNotification(Notification notification) {
    // Modern color palette
    final String PRIMARY_COLOR = "#E57373";  // Softer red
    final String SECONDARY_COLOR = "#F5F5F5"; // Light gray
    final String BACKGROUND_COLOR = "#FFFFFF"; // White
    final String TEXT_COLOR = "#424242";      // Dark gray (better readability)
    final String ACCENT_COLOR = "#64B5F6";    // Blue for contrast
    
    // Create alert
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Notification Details");
    alert.setHeaderText("New Message from Andok's");
    
    // Custom content with better typography
    VBox content = new VBox(10);
    content.setPadding(new Insets(15));
    
    // Notification message with improved styling
    Label messageLabel = new Label(notification.getMessage());
    messageLabel.setStyle(
        "-fx-text-fill: " + TEXT_COLOR + ";" +
        "-fx-font-size: 14px;" +
        "-fx-font-family: 'Segoe UI', Roboto, sans-serif;" +
        "-fx-wrap-text: true;" +
        "-fx-max-width: 400px;"
    );
    
    // Timestamp (if available)
    Label timeLabel = new Label();
    try {
        if (notification.getTimestamp() != null) {
            timeLabel.setText("Received: " + notification.getTimestamp()
                .format(DateTimeFormatter.ofPattern("MMM dd, yyyy • hh:mm a")));
        }
    } catch (Exception e) {
        timeLabel.setText("Received just now");
    }
    timeLabel.setStyle(
        "-fx-text-fill: #757575;" +
        "-fx-font-size: 12px;" +
        "-fx-font-style: italic;"
    );
    
    content.getChildren().addAll(messageLabel, timeLabel);
    alert.getDialogPane().setContent(content);
    
    // Style the dialog
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle(
        "-fx-background-color: " + BACKGROUND_COLOR + ";" +
        "-fx-font-family: 'Segoe UI', Roboto, sans-serif;" +
        "-fx-border-color: " + SECONDARY_COLOR + ";" +
        "-fx-border-width: 1px;" +
        "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
    );
    
    // Header styling
    dialogPane.lookupAll(".header-panel").forEach(node -> 
        node.setStyle(
            "-fx-background-color: linear-gradient(to right, " + PRIMARY_COLOR + ", #EF9A9A);" +
            "-fx-padding: 15px;"
        )
    );
    
    // Header text styling
    dialogPane.lookupAll(".header-panel .label").forEach(node -> 
        node.setStyle(
            "-fx-text-fill: white;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: 700;" +  // Semi-bold
            "-fx-alignment: center-left;"
        )
    );
    
    // Button styling
    dialogPane.lookupAll(".button").forEach(node -> 
        node.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 4px;" +
            "-fx-padding: 8px 16px;"
        )
    );
    
    // Make window slightly larger
    alert.getDialogPane().setMinSize(400, 200);
    
    alert.showAndWait();
}

private void styleAlert(Alert alert) {
    // Constants for styling
    final String PRIMARY_COLOR = "#FF5252"; // Red
    final String BACKGROUND_COLOR = "#FFFFFF"; // White
    
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle(
        "-fx-background-color: " + BACKGROUND_COLOR + ";" +
        "-fx-font-family: 'Segoe UI', Arial, sans-serif;"
    );
    
    // Style the header panel
    dialogPane.lookupAll(".header-panel").forEach(node -> {
        node.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";");
    });
    
    // Style the header text
    dialogPane.lookupAll(".header-panel .label").forEach(node -> {
        node.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
    });
}

    private void createCategoryButton(int categoryId, String categoryName, String imagePath) {
        HBox categoryBox = new HBox(10);
        categoryBox.setAlignment(Pos.CENTER_LEFT);
        categoryBox.setPadding(new Insets(10));
        categoryBox.setPrefWidth(Double.MAX_VALUE);
        categoryBox.setStyle(
            "-fx-background-color: " + ANDOKS_WHITE + ";" + 
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;"
        );
        
        // Create circular background for category icon
        StackPane iconContainer = new StackPane();
        Circle iconBackground = new Circle(20);
        iconBackground.setFill(Color.valueOf(ANDOKS_LIGHT_YELLOW));
        
        // Add category image
        ImageView categoryImage = new ImageView(new Image("file:" + imagePath));
        categoryImage.setFitHeight(24);
        categoryImage.setFitWidth(24);
        
        iconContainer.getChildren().addAll(iconBackground, categoryImage);
        
        Label nameLabel = new Label(categoryName);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
        nameLabel.setWrapText(true);
        
        categoryBox.getChildren().addAll(iconContainer, nameLabel);
        
        // Hover effect
        categoryBox.setOnMouseEntered(e -> {
            categoryBox.setStyle(
                "-fx-background-color: " + ANDOKS_YELLOW + ";" + 
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 4, 0, 0, 0);"
            );
            iconBackground.setFill(Color.valueOf(ANDOKS_WHITE));
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_RED + ";");
            
            // Small bounce effect
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), categoryBox);
            scaleTransition.setToX(1.03);
            scaleTransition.setToY(1.03);
            scaleTransition.play();
        });
        
        categoryBox.setOnMouseExited(e -> {
            categoryBox.setStyle(
                "-fx-background-color: " + ANDOKS_WHITE + ";" + 
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;" +
                "-fx-cursor: hand;"
            );
            iconBackground.setFill(Color.valueOf(ANDOKS_LIGHT_YELLOW));
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
            
            // Return to original scale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), categoryBox);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
        
        // Click effect
        categoryBox.setOnMouseClicked(e -> {
            highlightSelectedCategory(categoryBox);
            loadCategoryItems(currentCategoryId, ""); // empty search

        });
        
        sideBar.getChildren().add(categoryBox);
        
        // Set first category as active initially
        if (sideBar.getChildren().size() == 2) { // Account for header label at index 0
            highlightSelectedCategory(categoryBox);
        }
    }
    
    private void highlightSelectedCategory(HBox selectedCategory) {
        // Reset all categories
        for (int i = 1; i < sideBar.getChildren().size(); i++) { // Start from 1 to skip header
            Node node = sideBar.getChildren().get(i);
            if (node instanceof HBox) {
                HBox categoryBox = (HBox) node;
                categoryBox.setStyle(
                    "-fx-background-color: " + ANDOKS_WHITE + ";" + 
                    "-fx-border-radius: 8px;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-cursor: hand;"
                );
                
                // Reset label style
                for (Node childNode : categoryBox.getChildren()) {
                    if (childNode instanceof Label) {
                        ((Label) childNode).setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333333;");
                    }
                    
                    // Reset circle color
                    if (childNode instanceof StackPane) {
                        for (Node stackNode : ((StackPane) childNode).getChildren()) {
                            if (stackNode instanceof Circle) {
                                ((Circle) stackNode).setFill(Color.valueOf(ANDOKS_LIGHT_YELLOW));
                            }
                        }
                    }
                }
            }
        }
        
        // Highlight selected category
        selectedCategory.setStyle(
            "-fx-background-color: " + ANDOKS_RED + ";" + 
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-cursor: hand;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 4, 0, 0, 0);"
        );
        
        // Update label and circle in the selected category
        for (Node childNode : selectedCategory.getChildren()) {
            if (childNode instanceof Label) {
                ((Label) childNode).setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_WHITE + ";");
            }
            
            // Update circle color
            if (childNode instanceof StackPane) {
                for (Node stackNode : ((StackPane) childNode).getChildren()) {
                    if (stackNode instanceof Circle) {
                        ((Circle) stackNode).setFill(Color.valueOf(ANDOKS_YELLOW));
                    }
                }
            }
        }
    }
  

    
   private boolean isStoreOpen() {
    String query = "{CALL GetStoreStatus()}"; // Stored procedure call

    try (Connection conn = Database.connect();
         CallableStatement stmt = conn.prepareCall(query);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            String status = rs.getString("store_status");
            System.out.println("✅ Store status checked: " + status);
            return status.equalsIgnoreCase("Open");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false; // Default to false if something goes wrong
}



    private void showStoreClosedWindow() {
        Stage closedStage = new Stage();
        closedStage.setTitle("Andok's - Store Closed");
        closedStage.initStyle(StageStyle.UNDECORATED); // Remove default window decorations

        // Main container
        BorderPane mainLayout = new BorderPane();

        // Top header with store branding
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #E60000; -fx-padding: 15px;");
        header.setPrefHeight(70);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setOpacity(0); // Start with 0 opacity for animation

        // Store logo - replace path with your actual logo or use text
        Label logoText = new Label("ANDOK'S");
        logoText.setStyle("-fx-font-family: 'Arial Black'; -fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        // Add close button
        Button closeButton = new Button("✕");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");
        closeButton.setOnAction(e -> {
        // Create closing animation
        ScaleTransition scaleOut = new ScaleTransition(Duration.millis(200), mainLayout);
        scaleOut.setFromX(1.0);
        scaleOut.setFromY(1.0);
        scaleOut.setToX(0.8);
        scaleOut.setToY(0.8);
        
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), mainLayout);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        ParallelTransition closeTransition = new ParallelTransition(scaleOut, fadeOut);
        closeTransition.setOnFinished(event -> closedStage.close());
        closeTransition.play();
    });
    
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    header.getChildren().addAll(logoText, spacer, closeButton);
    
    // Center content area
    VBox contentArea = new VBox(20);
    contentArea.setAlignment(Pos.CENTER);
    contentArea.setPadding(new Insets(30, 40, 30, 40));
    contentArea.setStyle("-fx-background-color: white;");
    contentArea.setOpacity(0); // Start with 0 opacity for animation
    
    // Store closed icon
    StackPane iconContainer = new StackPane();
    Circle circle = new Circle(0); // Start with 0 radius for animation
    circle.setFill(Color.web("#FFCC00")); // Yellow
    
    FontIcon storeIcon = new FontIcon(); // Requires FontAwesomeFX library
    storeIcon.setIconLiteral("fas-store-slash");
    storeIcon.setIconSize(50);
    storeIcon.setIconColor(Color.web("#941c1e")); // Red
    storeIcon.setOpacity(0); // Start with 0 opacity for animation
    
    iconContainer.getChildren().addAll(circle, storeIcon);
    
    // Alternative without FontAwesomeFX
    /*
    Text storeIcon = new Text("🚫");
    storeIcon.setFont(Font.font("Arial", FontWeight.BOLD, 50));
    storeIcon.setFill(Color.web("#FF0000"));
    storeIcon.setOpacity(0); // Start with 0 opacity for animation
    iconContainer.getChildren().addAll(circle, storeIcon);
    */
    
    // Message text
    Label message = new Label("STORE IS CURRENTLY CLOSED");
    message.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #FF0000;");
    message.setOpacity(0); // Start with 0 opacity for animation
    
    // Note text
    Label note = new Label("We will notify you once the store is open again.\nPlease check back later!");
    note.setStyle("-fx-font-family: 'Arial'; -fx-font-size: 14px; -fx-text-fill: #333333; -fx-text-alignment: center;");
    note.setWrapText(true);
    note.setTextAlignment(TextAlignment.CENTER);
    note.setOpacity(0); // Start with 0 opacity for animation
    
    // Button to check status
    Button checkStatusButton = new Button("OKAY");
    checkStatusButton.setPrefWidth(200);
    checkStatusButton.setPrefHeight(40);
    checkStatusButton.setStyle("-fx-background-color: #941c1e; -fx-text-fill: white; -fx-font-weight: bold; " +
                             "-fx-font-size: 14px; -fx-background-radius: 20px;");
    checkStatusButton.setOpacity(0); // Start with 0 opacity for animation
    
    // Button hover animation
    checkStatusButton.setOnMouseEntered(e -> {
        ScaleTransition buttonGrow = new ScaleTransition(Duration.millis(100), checkStatusButton);
        buttonGrow.setToX(1.05);
        buttonGrow.setToY(1.05);
        buttonGrow.play();
        
        checkStatusButton.setStyle("-fx-background-color: #E60000; -fx-text-fill: white; -fx-font-weight: bold; " +
                                 "-fx-font-size: 14px; -fx-background-radius: 20px;");
    });
    
    checkStatusButton.setOnMouseExited(e -> {
        ScaleTransition buttonShrink = new ScaleTransition(Duration.millis(100), checkStatusButton);
        buttonShrink.setToX(1.0);
        buttonShrink.setToY(1.0);
        buttonShrink.play();
        
        checkStatusButton.setStyle("-fx-background-color: #941c1e; -fx-text-fill: white; -fx-font-weight: bold; " +
                                 "-fx-font-size: 14px; -fx-background-radius: 20px;");
    });
    
        checkStatusButton.setOnAction(e -> {
         // Create a subtle button press effect
         ScaleTransition buttonPress = new ScaleTransition(Duration.millis(100), checkStatusButton);
         buttonPress.setToX(0.95);
         buttonPress.setToY(0.95);

         ScaleTransition buttonRelease = new ScaleTransition(Duration.millis(100), checkStatusButton);
         buttonRelease.setToX(1.0);
         buttonRelease.setToY(1.0);
         buttonRelease.setDelay(Duration.millis(100));

         // Fade out the window
         FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mainLayout);
         fadeOut.setFromValue(1.0);
         fadeOut.setToValue(0.0);
         fadeOut.setDelay(Duration.millis(200));

         // Combine animations in sequence
         SequentialTransition checkTransition = new SequentialTransition(
             buttonPress, 
             buttonRelease, 
             fadeOut
         );

         checkTransition.setOnFinished(event -> {
             closedStage.close();
             // Add your logic to check store status again
         });

         checkTransition.play();
     });
    // Add elements to content area
    contentArea.getChildren().addAll(iconContainer, message, note, checkStatusButton);
    
    // Bottom strip with yellow accent
    HBox footer = new HBox();
    footer.setPrefHeight(15);
    footer.setStyle("-fx-background-color: #FFCC00;"); // Yellow
    footer.setOpacity(0); // Start with 0 opacity for animation
    
    // Add all sections to main layout
    mainLayout.setTop(header);
    mainLayout.setCenter(contentArea);
    mainLayout.setBottom(footer);
    
    // Add drop shadow effect to the window
    mainLayout.setEffect(new DropShadow(BlurType.GAUSSIAN, Color.rgb(0, 0, 0, 0.3), 10, 0.5, 0.0, 0.0));
    
    // Create scene with initial scale for animation
    Scene scene = new Scene(mainLayout, 450, 500);
    
    // Add CSS for smooth transitions
    scene.getRoot().setStyle("-fx-background-color: transparent;");
    mainLayout.setScaleX(0.8);
    mainLayout.setScaleY(0.8);
    
    // Add dragging capability since we're using undecorated window
    final Delta dragDelta = new Delta();
    scene.setOnMousePressed(mouseEvent -> {
        dragDelta.x = closedStage.getX() - mouseEvent.getScreenX();
        dragDelta.y = closedStage.getY() - mouseEvent.getScreenY();
    });
    scene.setOnMouseDragged(mouseEvent -> {
        closedStage.setX(mouseEvent.getScreenX() + dragDelta.x);
        closedStage.setY(mouseEvent.getScreenY() + dragDelta.y);
    });
    
    // Apply scene to stage
    closedStage.setScene(scene);
    closedStage.show();
    
    // Create entrance animations
    
    // 1. Whole window animation
    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), mainLayout);
    scaleIn.setFromX(0.8);
    scaleIn.setFromY(0.8);
    scaleIn.setToX(1.0);
    scaleIn.setToY(1.0);
    scaleIn.setInterpolator(Interpolator.EASE_OUT);
    
    // 2. Header fade in
    FadeTransition headerFade = new FadeTransition(Duration.millis(400), header);
    headerFade.setFromValue(0.0);
    headerFade.setToValue(1.0);
    headerFade.setDelay(Duration.millis(200));
    
    // 3. Content fade in
    FadeTransition contentFade = new FadeTransition(Duration.millis(400), contentArea);
    contentFade.setFromValue(0.0);
    contentFade.setToValue(1.0);
    contentFade.setDelay(Duration.millis(300));
    
    // 4. Footer fade in
    FadeTransition footerFade = new FadeTransition(Duration.millis(400), footer);
    footerFade.setFromValue(0.0);
    footerFade.setToValue(1.0);
    footerFade.setDelay(Duration.millis(200));
    
    // 5. Circle animation
    Timeline circleGrow = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(circle.radiusProperty(), 0)),
        new KeyFrame(Duration.millis(600), new KeyValue(circle.radiusProperty(), 50, Interpolator.EASE_OUT))
    );
    circleGrow.setDelay(Duration.millis(400));
    
    // 6. Icon fade in
    FadeTransition iconFade = new FadeTransition(Duration.millis(400), storeIcon);
    iconFade.setFromValue(0.0);
    iconFade.setToValue(1.0);
    iconFade.setDelay(Duration.millis(700));
    
    // 7. Message fade in with slight bounce
    TranslateTransition messageMove = new TranslateTransition(Duration.millis(500), message);
    messageMove.setFromY(20);
    messageMove.setToY(0);
    messageMove.setInterpolator(Interpolator.EASE_OUT);
    messageMove.setDelay(Duration.millis(800));
    
    FadeTransition messageFade = new FadeTransition(Duration.millis(500), message);
    messageFade.setFromValue(0.0);
    messageFade.setToValue(1.0);
    messageFade.setDelay(Duration.millis(800));
    
    // 8. Note fade in
    TranslateTransition noteMove = new TranslateTransition(Duration.millis(500), note);
    noteMove.setFromY(20);
    noteMove.setToY(0);
    noteMove.setInterpolator(Interpolator.EASE_OUT);
    noteMove.setDelay(Duration.millis(900));
    
    FadeTransition noteFade = new FadeTransition(Duration.millis(500), note);
    noteFade.setFromValue(0.0);
    noteFade.setToValue(1.0);
    noteFade.setDelay(Duration.millis(900));
    
    // 9. Button animation with bounce
    TranslateTransition buttonMove = new TranslateTransition(Duration.millis(600), checkStatusButton);
    buttonMove.setFromY(30);
    buttonMove.setToY(0);
    buttonMove.setInterpolator(Interpolator.SPLINE(0.215, 0.610, 0.355, 1.000));
    buttonMove.setDelay(Duration.millis(1000));
    
    FadeTransition buttonFade = new FadeTransition(Duration.millis(600), checkStatusButton);
    buttonFade.setFromValue(0.0);
    buttonFade.setToValue(1.0);
    buttonFade.setDelay(Duration.millis(1000));
    
    // Pulse animation for the button to draw attention
    Timeline buttonPulse = new Timeline(
        new KeyFrame(Duration.ZERO, new KeyValue(checkStatusButton.scaleXProperty(), 1.0)),
        new KeyFrame(Duration.ZERO, new KeyValue(checkStatusButton.scaleYProperty(), 1.0)),
        new KeyFrame(Duration.millis(600), new KeyValue(checkStatusButton.scaleXProperty(), 1.07)),
        new KeyFrame(Duration.millis(600), new KeyValue(checkStatusButton.scaleYProperty(), 1.07)),
        new KeyFrame(Duration.millis(1200), new KeyValue(checkStatusButton.scaleXProperty(), 1.0)),
        new KeyFrame(Duration.millis(1200), new KeyValue(checkStatusButton.scaleYProperty(), 1.0))
    );
    buttonPulse.setDelay(Duration.millis(1500));
    buttonPulse.setCycleCount(2);
    
    // Play all animations together
    ParallelTransition parallelTransition = new ParallelTransition(
        scaleIn, headerFade, contentFade, footerFade, circleGrow, iconFade,
        messageMove, messageFade, noteMove, noteFade, buttonMove, buttonFade, buttonPulse
    );
    parallelTransition.play();
}

// Helper class for window dragging
private static class Delta {
    double x, y;
}



   
    
        public void updateCartCount(int count) {
        Platform.runLater(() -> {
            cartCountLabel.setText(String.valueOf(count));
            cartCountLabel.setVisible(count > 0);
        });
}
        
 
    
        
private int getUnreadNotificationCount(int customerId) {
    String query = "{CALL GetUnreadNotificationCount(?, ?)}"; // Call the stored procedure
    int unreadCount = 0;

    try (Connection conn = Database.connect();
         CallableStatement stmt = conn.prepareCall(query)) {
        
        // Set the input parameter (customerId)
        stmt.setInt(1, customerId);

        // Register the output parameter for unread count
        stmt.registerOutParameter(2, Types.INTEGER);

        // Execute the stored procedure
        stmt.execute();

        // Retrieve the output value (unread count)
        unreadCount = stmt.getInt(2);
        System.out.println("Unread Notifications Count: " + unreadCount); // Debugging line

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return unreadCount;
}

    private void loadCategories() {
        sideBar.getChildren().clear(); // Clear previous items
        String query = "{CALL GetCategories()}"; // Stored procedure call

        try (Connection conn = Database.connect();
             CallableStatement stmt = conn.prepareCall(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String category = rs.getString("category_name");
                Button categoryBtn = new Button(category);
                categoryBtn.setMaxWidth(Double.MAX_VALUE);
                int categoryID = rs.getInt("category_id");
                categoryBtn.setOnAction(e -> {
                currentCategoryId = categoryID;
                loadCategoryItems(currentCategoryId, searchField.getText()); // with current search
            });
            sideBar.getChildren().add(categoryBtn);
                        }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     
    private void fetchCustomerData(int userID) {
    String query = "{CALL GetCustomerDataByUserId(?)}"; // Stored procedure call

    try (Connection conn = Database.connect();
         CallableStatement stmt = conn.prepareCall(query)) {

        // Set input parameter (userID)
        stmt.setInt(1, userID);

        // Execute the stored procedure
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            String customerName = rs.getString("name");
            System.out.println("Welcome, " + customerName + "!");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}


private void loadCategoryItems(int categoryId, String searchTerm) {
    menuGrid.getChildren().clear();
    System.out.println("Loading items for category ID: " + categoryId + " with search term: " + searchTerm);

    String query = "SELECT * FROM menu_items WHERE category_id = ? AND availability = 'Available'";
    if (searchTerm != null && !searchTerm.trim().isEmpty()) {
        query += " AND LOWER(name) LIKE ?";
    }

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, categoryId);
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            stmt.setString(2, "%" + searchTerm.toLowerCase() + "%");
        }

        ResultSet rs = stmt.executeQuery();
        int row = 0, col = 0;
        int maxColumns = 5;
        while (rs.next()) {
            String itemName = rs.getString("name");
            String imagePath = rs.getString("image_path");
            double price = rs.getDouble("price");

            VBox itemBox = createMenuItemBox(itemName, imagePath, price);
            menuGrid.add(itemBox, col, row);

            col++;
            if (col > maxColumns) {
                col = 0;
                row++;
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}

        private int getCustomerIdFromUserId(int userId) {
            String query = "{CALL GetCustomerIdFromUserId(?, ?)}"; // Stored procedure call
            int customerId = -1;

            try (Connection conn = Database.connect();
                 CallableStatement stmt = conn.prepareCall(query)) {

                // Set input parameter (userId)
                stmt.setInt(1, userId);

                // Register output parameter (customerId)
                stmt.registerOutParameter(2, Types.INTEGER);

                // Execute the stored procedure
                stmt.execute();

                // Get the result (customerId)
                customerId = stmt.getInt(2);

                System.out.println("Customer ID: " + customerId); // Debugging line

            } catch (SQLException ex) {
                System.err.println("❌ Error fetching customer ID: " + ex.getMessage());
            }

            return customerId;
        }




 
 private List<Notification> fetchNotifications(int customerId) {
    List<Notification> list = new ArrayList<>();
    String sql = "SELECT notification_id, message FROM notifications WHERE customer_id = ? ORDER BY notification_id DESC";
    System.out.println("Fetching notifications from " + customerId);
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, customerId);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            int id = rs.getInt("notification_id");
            String msg = rs.getString("message");
            list.add(new Notification(id, msg));
        }

    } catch (SQLException ex) {
        System.err.println("❌ Error fetching notifications: " + ex.getMessage());
    }
    return list;
}
private void markAsRead(int notificationId) {
    String sql = "UPDATE notifications SET is_read = 1 WHERE notification_id = ?";
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, notificationId);
        stmt.executeUpdate();

    } catch (SQLException ex) {
        System.err.println("❌ Error marking notification as read: " + ex.getMessage());
    }
}


  
       private void showCart() {
    if (CartSession.getCartItems().isEmpty()) {
            showEmptyCartMessage();
        } else {
            ShowCart.displayCart(userID);
        }
    }
       
       private void showEmptyCartMessage() {
            EmptyCart.showEmptyCartMessage();

        }

  
    public static void main(String[] args) {
        launch(args);
    }
}
