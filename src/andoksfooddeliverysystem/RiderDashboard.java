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


public class RiderDashboard extends Application {
      private int userID;
      private int riderId;
    private VBox sidebar;
    private BorderPane mainLayout;
    private boolean sidebarVisible = true;
    private VBox mainContent;
    ListView<String> variationList;
    
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
        mainContent = new VBox();
        mainContent.setPadding(new Insets(20));
        mainLayout.setCenter(mainContent); // ✅ Add mainContent to the center


        // Sidebar
        sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setStyle("-fx-background-color: #333; -fx-pref-width: 200px;");

        Button assignedOrders = new Button("Assigned Orders");
        sidebar.getChildren().add(assignedOrders);
        assignedOrders.setOnAction(e -> showAssignedOrders());

      
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
