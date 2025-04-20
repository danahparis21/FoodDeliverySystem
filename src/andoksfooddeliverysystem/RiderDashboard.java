/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javax.mail.MessagingException;


public class RiderDashboard extends Application {
    private int userID;
    private int riderId;
    private VBox sidebar;
    private BorderPane mainLayout;
    private boolean sidebarVisible = true;
    private VBox mainContent;
    ListView<String> variationList;
    
    // Andok's color palette
    // Define brand colors
        private static final String PRIMARY_RED = "#C81D24";     // Andok's red
        private static final String ACCENT_YELLOW = "#FFBA00";   // Vibrant yellow
        private static final String WHITE = "#FFFFFF";           // Pure white
        private static final String LIGHT_GRAY = "#F5F5F5";      // For hover states
        private static final String DARK_TEXT = "#2B2B2B";       // For text
        private static final String SIDEBAR_BG = "#1A1A1A";      // Dark sidebar background
    
    // Constructor to receive userID
    public RiderDashboard(int userID) {
        this.userID = userID;
        this.riderId = RiderFetcher.getRiderIdFromUserId(userID); // Fetch rider_id
        if (riderId == -1) {
            System.out.println("❌ No rider found for User ID: " + userID);
        } else {
            System.out.println("✅ Rider ID: " + riderId + " fetched for User ID: " + userID);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        mainLayout = new BorderPane();
        
        // Main content area with modern styling
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
        
        // Apply CSS to the entire scene
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
        primaryStage.setTitle("Andok's Rider Dashboard");
        primaryStage.show();
        
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
        Label dashboardTitle = new Label("ANDOK'S RIDER DASHBOARD");
        dashboardTitle.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_RED + ";"
        );
        
        // User info section on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Rider ID: " + riderId);
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
        String[] menuItems = {"Rider Dashboard", "Assigned Orders"};
        String[] menuIcons = {"🏠", "🛵️"};
        
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
                updateRiderStatusToOffline();    // Update status to Offline
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
            case "Rider Dashboard":
                navItem.setOnMouseClicked(e -> {
                    showMainDashboard();
                    setActiveNavItem(navItem, index);
                });
            
                break;
            case "Assigned Orders":
                navItem.setOnMouseClicked(e -> {
                    showAssignedOrders();
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
    
   
    
    
    private void updateRiderStatusToOffline() {
    String sql = "UPDATE riders SET online_status = 'Offline', last_modified_by = ? WHERE rider_id = ?";
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, userID); // Logged-in user ID (pass it from constructor or store globally)
        stmt.setInt(2, riderId); // Assuming rider is linked by user_id
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}
    

    
    
    private void showMainDashboard() {
      mainContent.getChildren().clear();
      System.out.println("Switching to Rider Dashboard");

      ShowRiderDashboard showRiderDashboard = new ShowRiderDashboard(riderId);
      Node riderUI = showRiderDashboard.getRoot();

      if (riderUI == null) {
          System.out.println("❌ Rider Dashboard UI is null!");  // Debugging
      } else {
          System.out.println("✅ Adding Rider Dashboard UI to mainContent");
          mainContent.getChildren().add(riderUI);
      }
  }

    
    
 private void showAssignedOrders() {
        mainContent.getChildren().clear();
        System.out.println("Switching to orders");

        AssignedOrders assignedOrders = new AssignedOrders(riderId);
        Node riderUI = assignedOrders.getRoot();

        if (riderUI == null) {
            System.out.println("❌ Order UI is null!");  // Debugging
        } else {
            System.out.println("✅ Adding Assigned Orders UI to mainContent");
            mainContent.getChildren().add(riderUI);
        }
    }
}
