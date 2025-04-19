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
import org.kordamp.ikonli.javafx.FontIcon;

public class CustomerDashboard extends Application {
    private int userID;
    private BorderPane mainLayout;
    private VBox sideBar;
    private GridPane menuGrid;
    private Label cartCountLabel;
     private Stage primaryStage;
    // Constructor to receive userID
    public CustomerDashboard(int userID) {
        this.userID = userID;
        System.out.println("✅ CustomerDashboard opened with User ID: " + userID); // Debugging
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


        // 🔝 Top Bar (Search, Notifications, Profile)
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);

        // 📂 Side Category Tabs (Vertical)
        sideBar = new VBox(10);
        sideBar.setPadding(new Insets(10));
        sideBar.setStyle("-fx-background-color: #f4f4f4;");
         loadCategories(); // ✅ Load categories from database
         mainLayout.setLeft(sideBar);

        // 📦 Menu Grid
        menuGrid = new GridPane();
        menuGrid.setPadding(new Insets(20));
        menuGrid.setHgap(15);
        menuGrid.setVgap(15);
        loadCategoryItems(2);

        ScrollPane scrollPane = new ScrollPane(menuGrid);
        scrollPane.setFitToWidth(true);
        mainLayout.setCenter(scrollPane);

        // 🎭 Scene Setup
        Scene scene = new Scene(mainLayout, 1450, 700);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Customer Dashboard");
        primaryStage.show();
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
    closedStage.setTitle("Store Closed");

    VBox layout = new VBox(20);
    layout.setPadding(new Insets(30));
    layout.setAlignment(Pos.CENTER);

    Label message = new Label("🚫 Store is currently closed!");
    message.setFont(Font.font("Arial", FontWeight.BOLD, 16));
    
    Label note = new Label("We will notify you once the store is open again. Please check back later!");
    note.setWrapText(true);
    note.setTextAlignment(TextAlignment.CENTER);

    layout.getChildren().addAll(message, note);

    Scene scene = new Scene(layout, 400, 200);
    closedStage.setScene(scene);
    closedStage.show();
}


    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: #333; -fx-padding: 15;");

        // 🔍 Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search menu...");
        searchField.setPrefWidth(200);
        
       
      // Create a more elegant cart button with counter
            Button cartBtn = new Button();
            cartBtn.setGraphic(new FontIcon("fas-shopping-cart"));  // Using FontAwesome icon
            cartBtn.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 5px;");
            cartBtn.setOnAction(e -> showCart());

            // Create a more refined cart count indicator
            cartCountLabel = new Label("0");
            cartCountLabel.setStyle(
                "-fx-background-color: #e74c3c;" +   // Softer red color
                "-fx-text-fill: white;" +
                "-fx-font-size: 10px;" +             // Smaller font
                "-fx-padding: 1px 4px;" +            // Tighter padding
                "-fx-background-radius: 10;" +       // Smaller circle
                "-fx-min-width: 16px;" +             // Consistent width
                "-fx-alignment: center;" +           // Center text
                "-fx-font-weight: bold;"             // Bold text for better readability
            );
            cartCountLabel.setVisible(false);        // Initially hidden if cart is empty

            // Position the count indicator properly
            StackPane cartButtonPane = new StackPane();
            cartButtonPane.getChildren().addAll(cartBtn, cartCountLabel);
            StackPane.setAlignment(cartCountLabel, Pos.TOP_RIGHT);
            StackPane.setMargin(cartCountLabel, new Insets(-2, -2, 0, 0));  // Slight adjustment to position

            
        // 🔔 Notification Button
        Button notifBtn = new Button("🔔");
        notifBtn.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 5px;");
        notifBtn.setOnAction(e -> showNotification());

       // 🔢 Notification count badge
        Label notifCountLabel = new Label();
        notifCountLabel.setStyle(
            "-fx-background-color: #e74c3c;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 10px;" +
            "-fx-padding: 1px 4px;" +
            "-fx-background-radius: 10;" +
            "-fx-min-width: 16px;" +
            "-fx-alignment: center;" +
            "-fx-font-weight: bold;"
        );
        notifCountLabel.setVisible(false); // Hide if no unread notifications

        // 🔁 Function to update the count dynamically
        int customerId = getCustomerIdFromUserId(userID);
        int unreadCount = getUnreadNotificationCount(customerId);

        if (unreadCount > 0) {
            notifCountLabel.setText(String.valueOf(unreadCount));
            notifCountLabel.setVisible(true);  // Show if there's a count > 0
        } else {
            notifCountLabel.setVisible(false); // Hide if no unread notifications
        }

        // 📦 Stack notif button and badge
        StackPane notifButtonPane = new StackPane();
        notifButtonPane.getChildren().addAll(notifBtn, notifCountLabel);
        StackPane.setAlignment(notifCountLabel, Pos.TOP_RIGHT);
        StackPane.setMargin(notifCountLabel, new Insets(-2, -2, 0, 0));  // Position adjustment

        // 👤 Profile Button
        Button profileBtn = new Button("👤");
        profileBtn.setOnAction(e -> {
            Customer customer = CustomerDAO.getCustomerByUserId(userID);
            if (customer != null) {
                
                CustomerProfile profile = new CustomerProfile();
                profile.show(primaryStage, customer, userID); // ← perfect!

            } else {
                System.out.println("⚠️ No customer found for user ID " + userID);
            }
});


        topBar.getChildren().addAll(searchField, notifBtn, cartButtonPane, profileBtn);
        return topBar;
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


   
    
   private VBox createMenuItemBox(String itemName, String imagePath, double price) {
        VBox itemBox = new VBox(10);
        itemBox.setPadding(new Insets(10));
        itemBox.setAlignment(Pos.CENTER);
        itemBox.setStyle("-fx-background-color: #f8f8f8; -fx-border-radius: 10px; -fx-padding: 10px;");

        ImageView imageView = new ImageView(new Image("file:" + imagePath));
        

        imageView.setFitWidth(120);
        imageView.setFitHeight(120);

        Label nameLabel = new Label(itemName);
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Add price label
        Label priceLabel = new Label("₱" + price);
        priceLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #555555;");

        // ⏩ Open details window on click (you might need to pass the price here too)
        itemBox.setOnMouseClicked(e -> MenuDetails.showItemDetails(itemName));

        itemBox.getChildren().addAll(imageView, nameLabel, priceLabel);
        return itemBox;
    }
   


    private void openItemDetails(String itemName) {
        Stage itemStage = new Stage();
        VBox itemLayout = new VBox(10);
        itemLayout.setPadding(new Insets(20));
        itemLayout.setAlignment(Pos.CENTER);

        Label itemLabel = new Label("Details for: " + itemName);
        itemLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> itemStage.close());

        itemLayout.getChildren().addAll(itemLabel, closeButton);

        Scene scene = new Scene(itemLayout, 300, 200);
        itemStage.setScene(scene);
        itemStage.setTitle(itemName + " Details");
        itemStage.show();
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
                categoryBtn.setOnAction(e -> loadCategoryItems(categoryID)); // ✅ Load menu items on click
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


private void loadCategoryItems(int categoryId) {
    menuGrid.getChildren().clear();
    System.out.println("Loading items for category ID: " + categoryId);

    // Only load items that are marked as 'Available'
    String query = "SELECT * FROM menu_items WHERE category_id = ? AND availability = 'Available'";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        int row = 0, col = 0;
        while (rs.next()) {
            String itemName = rs.getString("name");
            String imagePath = rs.getString("image_path");
            double price = rs.getDouble("price");

            VBox itemBox = createMenuItemBox(itemName, imagePath, price);
            menuGrid.add(itemBox, col, row);

            col++;
            if (col > 2) { // 3 columns per row
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




 private void showNotification() {
  // Replace with your session logic
    int customerId = getCustomerIdFromUserId(userID); // ✅ Fetch based on userID
    System.out.println("Fetching notifications from " + customerId);

    if (customerId == -1) {
        Alert alert = new Alert(Alert.AlertType.ERROR, "Unable to fetch notifications (no customer found).", ButtonType.OK);
        alert.showAndWait();
        return;
    }
    List<Notification> notifications = fetchNotifications(customerId);

    if (notifications.isEmpty()) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No new notifications.", ButtonType.OK);
        alert.showAndWait();
        return;
    }

    // Create a dialog
    Dialog<Void> dialog = new Dialog<>();
    dialog.setTitle("🔔 Notifications");

    VBox notificationList = new VBox(10);
    notificationList.setPadding(new Insets(10));

    for (Notification notif : notifications) {
        String snippet = notif.message.length() > 50
                ? notif.message.substring(0, 50) + "..."
                : notif.message;

        Label label = new Label(snippet);
        label.setStyle("""
            -fx-background-color: #f9f9f9;
            -fx-padding: 10px;
            -fx-border-color: #cccccc;
            -fx-border-width: 1px;
            -fx-border-radius: 8px;
            -fx-background-radius: 8px;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 0, 2);
        """);
        label.setMaxWidth(Double.MAX_VALUE);
        label.setWrapText(true);

        label.setOnMouseClicked(e -> {
            Alert fullMessage = new Alert(Alert.AlertType.INFORMATION);
            fullMessage.setTitle("📨 Full Notification");
            fullMessage.setHeaderText("From Andok's");
            fullMessage.setContentText(notif.message);
            fullMessage.showAndWait();

            markAsRead(notif.notificationId);
        });

        notificationList.getChildren().add(label);
    }

    ScrollPane scrollPane = new ScrollPane(notificationList);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefHeight(400);

    dialog.getDialogPane().setContent(scrollPane);
    dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
    dialog.showAndWait();
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



    private void showProfile() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile Details Here", ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
