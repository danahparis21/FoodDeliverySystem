package andoksfooddeliverysystem;
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

import java.util.List;
import java.util.Map;

public class CustomerDashboard extends Application {
    private int userID;
    private BorderPane mainLayout;
    private VBox sideBar;
    private GridPane menuGrid;
    
    // Constructor to receive userID
    public CustomerDashboard(int userID) {
        this.userID = userID;
        System.out.println("✅ CustomerDashboard opened with User ID: " + userID); // Debugging
    }
    
    @Override
    public void start(Stage primaryStage) {
        
        mainLayout = new BorderPane();
        
          // Fetch and display customer details based on userID
        fetchCustomerData(userID);

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
    
   


    private HBox createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setStyle("-fx-background-color: #333; -fx-padding: 15;");

        // 🔍 Search Bar
        TextField searchField = new TextField();
        searchField.setPromptText("Search menu...");
        searchField.setPrefWidth(200);
        
        // 🔔 Notification Button
        Button cartBtn = new Button("🛒");
        cartBtn.setOnAction(e -> showCart());

        // 🔔 Notification Button
        Button notifBtn = new Button("🔔");
        notifBtn.setOnAction(e -> showNotification());

        // 👤 Profile Button
        Button profileBtn = new Button("👤");
        profileBtn.setOnAction(e -> showProfile());

        topBar.getChildren().addAll(searchField, notifBtn, cartBtn, profileBtn);
        return topBar;
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
    String query = "SELECT category_name, category_id FROM categories"; 

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
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
        String sql = "SELECT * FROM Customers WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
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
    
    String query = "SELECT * FROM menu_items WHERE category_id = ?";
    
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
         
        stmt.setInt(1, categoryId);
        ResultSet rs = stmt.executeQuery();

        int row = 0, col = 0;
        while (rs.next()) {
            String itemName = rs.getString("name"); // Adjusted column name
            String imagePath = rs.getString("image_path");
            double price = rs.getDouble("price"); // Get the price from the database
            
            
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

  

   
   

    private void showNotification() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "No new notifications.", ButtonType.OK);
        alert.showAndWait();
    }
    
  
        private void showCart() {
            ShowCart.displayCart(userID); // ✅ Pass customerID from logged-in user
        }



    private void showProfile() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Profile Details Here", ButtonType.OK);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
