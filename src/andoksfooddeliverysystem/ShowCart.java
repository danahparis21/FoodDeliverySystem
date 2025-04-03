/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;


import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.Map;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ShowCart {
    private static List<String> cartItems = new ArrayList<>(); // Store cart items

    public static void addToCart(String item) {
        cartItems.add(item);
    }

    public static List<String> getCartItems() {
        return cartItems;
    }
    static Label subtotalLabel;
    static Label totalLabel;

     // ✅ Modify displayCart() to accept customerID
    public static void displayCart(int customerID) {
        System.out.println("✅ Opening Cart for User ID: " + customerID); // Debugging

        Stage cartStage = new Stage();
        cartStage.initStyle(StageStyle.UTILITY);
        cartStage.initModality(Modality.APPLICATION_MODAL);
        
        
       // Container for Progress Bar
        HBox progressContainer = new HBox();
        progressContainer.setAlignment(Pos.CENTER);
        progressContainer.setPadding(new Insets(10));
        progressContainer.setPrefWidth(1200); // Set the same width as the window

        // Progress Bar Background (Unfilled)
        Rectangle progressBackground = new Rectangle(600, 10, Color.LIGHTGRAY);
        progressBackground.setArcWidth(10);
        progressBackground.setArcHeight(10);

        // Progress Bar Fill (Represents progress)
        Rectangle progressFill = new Rectangle(0, 10, Color.GREEN); // Adjust width to show progress
        progressFill.setArcWidth(10);
        progressFill.setArcHeight(10);

        // Stack the background and progressFill
        StackPane progressBar = new StackPane(progressBackground, progressFill);
        StackPane.setAlignment(progressBackground, Pos.CENTER_LEFT);
        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);
        
      // Animation: Smoothly grow progressFill width to 250 (Cart stage)
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressFill.widthProperty(), 0, Interpolator.EASE_BOTH)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(progressFill.widthProperty(), 250, Interpolator.EASE_BOTH))
        );
        timeline.setCycleCount(1); // Runs only once
        timeline.play();
        // Ensure progressBar grows to the right
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Add to the progress container
        progressContainer.getChildren().add(progressBar);

        // Step Labels
        Label step1 = new Label("✔ Menu");
        Label step2 = new Label("🛒 Cart");
        Label step3 = new Label("⬜ Checkout");

        step1.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        step2.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;");
        step3.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");

        // Position Labels in an HBox, aligned to the right
        HBox stepLabels = new HBox(160, step1, step2, step3); // Adjust spacing between steps
        stepLabels.setAlignment(Pos.CENTER_LEFT);
        stepLabels.setPrefWidth(1200);
        
        // Combine Progress Bar and Labels
        VBox progressSection = new VBox(5, progressContainer, stepLabels);
        progressSection.setAlignment(Pos.CENTER);

        // Add to Main Layout

        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        
        Label title = new Label("🛒 Your Cart");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        VBox itemsBox = new VBox(10);
        itemsBox.setPadding(new Insets(10));
        itemsBox.setStyle("-fx-border-color: #ccc; -fx-border-radius: 10; -fx-padding: 10;");
        
        double subtotal = 0;
        double deliveryFee = 49;  // Standard delivery fee
         Map<Integer, Integer> cartItems = new HashMap<>(CartSession.getCartItems()); // Copy for modification

         ScrollPane scrollableItemsBox = new ScrollPane();
        scrollableItemsBox.setContent(itemsBox);
        scrollableItemsBox.setFitToWidth(true);
        scrollableItemsBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollableItemsBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableItemsBox.setPrefHeight(550); // Adjust height as needed

        
        if (cartItems.isEmpty()) {
            Label emptyLabel = new Label("No Added Order.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: red;");
            itemsBox.getChildren().add(emptyLabel);
        } else {
            for (Map.Entry<Integer, Integer> entry : CartSession.getCartItems().entrySet())  {
                int itemId = entry.getKey();
                int quantity = entry.getValue();

                String itemName = getItemNameById(itemId);
                double itemPrice = getItemPriceById(itemId);
                double totalPrice = itemPrice * quantity;
                subtotal += totalPrice;
                
                 // Get Variation and Special Instructions
                String variation = CartSession.getItemVariation(itemId);
                String instructions = CartSession.getItemInstructions(itemId);

                HBox itemRow = new HBox(15);
                itemRow.setAlignment(Pos.CENTER_LEFT);

                // Image
                ImageView itemImage = new ImageView(new Image(getItemImageById(itemId)));
                itemImage.setFitWidth(60);
                itemImage.setFitHeight(60);

                // Name & Price
                VBox namePriceBox = new VBox(5);
                Label nameLabel = new Label(itemName);
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                Label priceLabel = new Label("₱" + String.format("%.2f", itemPrice));
                priceLabel.setStyle("-fx-font-size: 14px;");
                namePriceBox.getChildren().addAll(nameLabel, priceLabel);

                // Quantity Selector
                Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, quantity);
                quantitySpinner.setPrefWidth(60);

                quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    if (newValue == 0) {
                       removeItem(itemId);
                        itemsBox.getChildren().remove(itemRow);
                    } else {
                        updateItemQuantity(itemId, newValue);
                    }
                    updateSubtotal(); // 🔥 Now updates the subtotal instantly!
                   
                });
            });
                
            String variationText = (variation == null || variation.isEmpty()) ? "No Variation" : "🔄 Variation: " + variation;
            String instructionsText = (instructions == null || instructions.isEmpty()) ? "No Instructions" : "✏ Instructions: " + instructions;

             // Add expandable details (Variation & Instructions)
            VBox detailsBox = new VBox(5);
            Label variationLabel = new Label(variationText);
            Label instructionsLabel = new Label(instructionsText);

            detailsBox.getChildren().addAll(variationLabel, instructionsLabel);

            // Initially hide details
            detailsBox.setVisible(false);

            // Add the down arrow for expanding/collapsing details
            Label expandLabel = new Label("⬇️View More");
            expandLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;");

            // Toggle details visibility when the arrow is clicked
            expandLabel.setOnMouseClicked(event -> {
                boolean isVisible = detailsBox.isVisible();
                detailsBox.setVisible(!isVisible);  // Toggle visibility of details
                expandLabel.setText(isVisible ? "⬇View More" : "⬆️Hide Details");  // Change arrow direction
            });

            // Add to the main item row
            itemRow.getChildren().addAll(itemImage, namePriceBox, quantitySpinner, expandLabel);

            // Add the item row and its details to the container
            itemsBox.getChildren().add(itemRow);
            itemsBox.getChildren().add(detailsBox);  // Add details as a separate VBox
        }
            
        }

        // Create an HBox to hold both cart items and the summary
        HBox contentBox = new HBox(20); // Adds spacing between itemsBox and summary
        contentBox.setPadding(new Insets(10));
        contentBox.setPrefWidth(1200); // Adjust width as needed
        contentBox.setAlignment(Pos.CENTER_LEFT); // Ensure it aligns well
        
                // Ensure scrollableItemsBox has a proper width
        scrollableItemsBox.setPrefWidth(800); // Adjust width as needed
        // Ensure summaryBox has a fixed width and alignment
        itemsBox.setPrefWidth(500); // Adjust width as needed
        itemsBox.setAlignment(Pos.TOP_RIGHT);
       

        // Summary Section
        subtotalLabel = new Label("Subtotal: ₱" + String.format("%.2f", subtotal));
        Label deliveryLabel = new Label("Standard Delivery: ₱" + String.format("%.2f", deliveryFee));
        totalLabel = new Label("Total: ₱" + String.format("%.2f", subtotal + deliveryFee));
        
        subtotalLabel.setStyle("-fx-font-size: 16px;");
        deliveryLabel.setStyle("-fx-font-size: 16px;");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        VBox summaryBox = new VBox(5, subtotalLabel, deliveryLabel, totalLabel);
        summaryBox.setPadding(new Insets(10));
        summaryBox.setStyle("-fx-border-color: #000; -fx-border-radius: 10; -fx-padding: 10;");
        summaryBox.setPrefWidth(350); // Adjust width as needed
        summaryBox.setAlignment(Pos.TOP_RIGHT);
        // Buttons
        Button reviewPaymentBtn = new Button("Review Payment & Address");
        reviewPaymentBtn.setOnAction(e -> {
            CheckOutWindow.displayCheckout(
                customerID,
                CartSession.getCartItems(),
                CartSession.getVariations(),  // ✅ Added variations
                CartSession.getInstructions() // ✅ Added instructions
            );
            cartStage.close();
        });

        Button closeBtn = new Button("Close");
        
        reviewPaymentBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px;");
        closeBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-size: 16px;");
        
        closeBtn.setOnAction(e -> cartStage.close());
        
 
        HBox buttonBox = new HBox(10, reviewPaymentBtn, closeBtn);
        buttonBox.setPadding(new Insets(10));
        
                VBox summarySection = new VBox(15, summaryBox, buttonBox);
        summarySection.setAlignment(Pos.TOP_RIGHT);
        // Add both the scrollable cart and summary section to contentBox
        contentBox.getChildren().addAll(scrollableItemsBox, summarySection);

        // Now, add contentBox to the main layout instead of individual components
        mainLayout.getChildren().addAll(progressSection, title, contentBox);
        Scene scene = new Scene(mainLayout, 1200, 700);
        cartStage.setScene(scene);
        cartStage.setTitle("Cart Summary");
        cartStage.showAndWait();
    }
        
    public static void removeItem(int itemId) {
        CartSession.getCartItems().remove(itemId);
        System.out.println("Item " + itemId + " removed from cart.");
    }
    public static void updateItemQuantity(int itemId, int newQuantity) {
        if (newQuantity > 0) {
            CartSession.getCartItems().put(itemId, newQuantity);
            System.out.println("Updated item " + itemId + " to quantity: " + newQuantity);
        } else {
            removeItem(itemId);
        }
}

    private static void updateSubtotal() {
        double newSubtotal = 0.0;
        for (Map.Entry<Integer, Integer> entry : CartSession.getCartItems().entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            double itemPrice = getItemPriceById(itemId);
            newSubtotal += itemPrice * quantity;
        }
        subtotalLabel.setText("Subtotal: ₱" + String.format("%.2f", newSubtotal));
          updateTotal(newSubtotal);
    }
    private static void updateTotal(double newSubtotal) {
        double deliveryFee = 49.0; // Standard delivery fee
        double newTotal = newSubtotal + deliveryFee;

        totalLabel.setText("Total: ₱" + String.format("%.2f", newTotal));
    }
    
 


    private static String getItemNameById(int itemId) {
        String name = "Unknown";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT name FROM menu_items WHERE item_id = ?")) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                name = rs.getString("name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return name;
    }

    private static double getItemPriceById(int itemId) {
        double price = 0.0;
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement("SELECT price FROM menu_items WHERE item_id = ?")) {
            stmt.setInt(1, itemId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                price = rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return price;
    }
    
    private static String getItemImageById(int itemId) {
     String imagePath = "file:images/default.png"; // Default image if not found

     try (Connection conn = Database.connect();
          PreparedStatement stmt = conn.prepareStatement("SELECT image_path FROM menu_items WHERE item_id = ?")) {
         stmt.setInt(1, itemId);
         ResultSet rs = stmt.executeQuery();
         if (rs.next()) {
             String dbPath = rs.getString("image_path");
             if (dbPath != null && !dbPath.isEmpty()) {
                 imagePath = "file:" + dbPath; // Ensure correct format
             }
         }
     } catch (SQLException e) {
         e.printStackTrace();
     }
     return imagePath; 
 }

}
