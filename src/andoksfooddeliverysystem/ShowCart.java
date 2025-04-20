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
import javafx.geometry.HPos;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ShowCart {
    private static List<String> cartItems = new ArrayList<>(); // Store cart items
    private static Stage cartStage;

    public static void addToCart(String item) {
        cartItems.add(item);
    }

    public static List<String> getCartItems() {
        return cartItems;
    }
    static Label subtotalLabel;
    static Label totalLabel;
    static Label subtotalValue;
    static Label deliveryValue;
    static Label totalValue; // If you also use it


    // ✅ Modify displayCart() to accept customerID
    public static void displayCart(int userID) {
        System.out.println("✅ Opening Cart for User ID: " + userID); // Debugging

        cartStage = new Stage();
        cartStage.initStyle(StageStyle.UTILITY);
        cartStage.initModality(Modality.APPLICATION_MODAL);
        
        // ========= STYLE CONSTANTS =========
        String PRIMARY_RED = "#FF3B30";
        String ACCENT_YELLOW = "#FFCC00";
        String BACKGROUND_WHITE = "#FFFFFF";
        String LIGHT_GRAY = "#F2F2F7";
        String TEXT_BLACK = "#1C1C1E";
        String SUCCESS_GREEN = "#34C759";
        
        // ========= PROGRESS BAR SECTION =========
        HBox progressContainer = new HBox();
        progressContainer.setAlignment(Pos.CENTER);
        progressContainer.setPadding(new Insets(20, 10, 10, 10));
        progressContainer.setPrefWidth(1200);
        progressContainer.setStyle("-fx-background-color: " + BACKGROUND_WHITE + ";");

        // Progress Bar Background (Unfilled)
        Rectangle progressBackground = new Rectangle(600, 8, Color.web(LIGHT_GRAY));
        progressBackground.setArcWidth(10);
        progressBackground.setArcHeight(10);

        // Progress Bar Fill (Represents progress)
        Rectangle progressFill = new Rectangle(0, 8, Color.web(PRIMARY_RED)); 
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

        // Step Labels with Icons
        Label step1 = new Label("✓ Menu");
        Label step2 = new Label("🛒 Cart");
        Label step3 = new Label("📦 Checkout");

        step1.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + SUCCESS_GREEN + ";");
        step2.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_RED + ";");
        step3.setStyle("-fx-font-size: 16px; -fx-font-weight: normal; -fx-text-fill: " + LIGHT_GRAY + ";");

        // Position Labels in an HBox, aligned to the right
        HBox stepLabels = new HBox(160, step1, step2, step3); // Adjust spacing between steps
        stepLabels.setAlignment(Pos.CENTER_LEFT);
        stepLabels.setPrefWidth(1200);
        
        // Combine Progress Bar and Labels
        VBox progressSection = new VBox(10, progressContainer, stepLabels);
        progressSection.setAlignment(Pos.CENTER);
        progressSection.setStyle("-fx-background-color: " + BACKGROUND_WHITE + "; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");
        progressSection.setPadding(new Insets(0, 0, 10, 0));

        // ========= MAIN LAYOUT =========
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle("-fx-background-color: " + BACKGROUND_WHITE + ";");
        
        // Cart Title with Icon
        HBox titleBox = new HBox(10);
        Label cartIcon = new Label("🛒");
        cartIcon.setStyle("-fx-font-size: 24px;");
        Label title = new Label("Your Cart");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_BLACK + ";");
        titleBox.getChildren().addAll(cartIcon, title);
        
        // Items Container
        VBox itemsBox = new VBox(15);
        itemsBox.setPadding(new Insets(15));
        itemsBox.setStyle("-fx-background-color: " + BACKGROUND_WHITE + "; -fx-border-color: " + LIGHT_GRAY + 
                       "; -fx-border-radius: 12; -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 5, 0, 0, 1);");
        
        double subtotal = 0;
        double deliveryFee = 49;  // Standard delivery fee
        Map<Integer, Integer> cartItems = new HashMap<>(CartSession.getCartItems()); // Copy for modification

        ScrollPane scrollableItemsBox = new ScrollPane();
        scrollableItemsBox.setContent(itemsBox);
        scrollableItemsBox.setFitToWidth(true);
        scrollableItemsBox.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollableItemsBox.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollableItemsBox.setPrefHeight(450);
        scrollableItemsBox.setStyle("-fx-background: " + BACKGROUND_WHITE + "; -fx-background-color: transparent; -fx-padding: 0;");
        
        if (cartItems.isEmpty()) {
            VBox emptyCartBox = new VBox(20);
            emptyCartBox.setAlignment(Pos.CENTER);
            
            // Big empty cart icon
            Label emptyIcon = new Label("🛒");
            emptyIcon.setStyle("-fx-font-size: 60px; -fx-text-fill: " + LIGHT_GRAY + ";");
            
            Label emptyLabel = new Label("Your cart is empty");
            emptyLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_BLACK + ";");
            
            Label emptySubLabel = new Label("Add some delicious items to get started!");
            emptySubLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
            
            emptyCartBox.getChildren().addAll(emptyIcon, emptyLabel, emptySubLabel);
            itemsBox.getChildren().add(emptyCartBox);
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

                // Item card with shadow and rounded corners
                VBox itemCard = new VBox(10);
                itemCard.setPadding(new Insets(15));
                itemCard.setStyle("-fx-background-color: " + BACKGROUND_WHITE + "; -fx-border-color: " + LIGHT_GRAY + 
                               "; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 3, 0, 0, 1);");

                HBox itemRow = new HBox(15);
                itemRow.setAlignment(Pos.CENTER_LEFT);

                // Image with rounded corners
                ImageView itemImage = new ImageView(new Image(getItemImageById(itemId)));
                itemImage.setFitWidth(80);
                itemImage.setFitHeight(80);
                
                // Apply clip for rounded image corners
                Rectangle clip = new Rectangle(80, 80);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                itemImage.setClip(clip);

                // Item Details
                VBox namePriceBox = new VBox(5);
                namePriceBox.setPrefWidth(250);
                
                Label nameLabel = new Label(itemName);
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_BLACK + ";");
                nameLabel.setWrapText(true);
                
                Label priceLabel = new Label("₱" + String.format("%.2f", itemPrice));
                priceLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: " + PRIMARY_RED + "; -fx-font-weight: bold;");
                
                namePriceBox.getChildren().addAll(nameLabel, priceLabel);

//                // Total Price for item
//                Label totalItemPrice = new Label("₱" + String.format("%.2f", totalPrice));
//                totalItemPrice.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_RED + ";");
//                
                // Quantity Selector with custom styling
                Spinner<Integer> quantitySpinner = new Spinner<>(0, 10, quantity);
                quantitySpinner.setPrefWidth(100);
                quantitySpinner.setStyle("-fx-background-color: " + BACKGROUND_WHITE + "; -fx-border-color: " + LIGHT_GRAY + "; -fx-border-radius: 4;");

                quantitySpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    Platform.runLater(() -> {
                        if (newValue == 0) {
                            CartSession.removeFromCart(itemId); 
                            removeItem(itemId);
                            itemsBox.getChildren().remove(itemCard);
                        } else {
                            updateItemQuantity(itemId, newValue);
                        }
                        updateSubtotal(); // 🔥 Now updates the subtotal instantly!
                    });
                });

                String variationText = (variation == null || variation.isEmpty()) ? "" : "🔄 " + variation;
                String instructionsText = (instructions == null || instructions.isEmpty()) ? "" : "🍴" + instructions;

                // Add expandable details (Variation & Instructions) with better styling
                VBox detailsBox = new VBox(8);
                detailsBox.setPadding(new Insets(5, 0, 0, 0));
                detailsBox.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 6; -fx-padding: 10;");
                
                if (!variationText.isEmpty()) {
                    Label variationLabel = new Label(variationText);
                    variationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_BLACK + ";");
                    variationLabel.setWrapText(true);
                    detailsBox.getChildren().add(variationLabel);
                }
                
                if (!instructionsText.isEmpty()) {
                    Label instructionsLabel = new Label(instructionsText);
                    instructionsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + TEXT_BLACK + ";");
                    instructionsLabel.setWrapText(true);
                    detailsBox.getChildren().add(instructionsLabel);
                }

                // Initially hide details
                detailsBox.setVisible(false);
                detailsBox.setManaged(false);

                // Add the down arrow button for expanding/collapsing details
                Button expandButton = new Button("View Details");
                expandButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + ACCENT_YELLOW + 
                                  "; -fx-font-size: 13px; -fx-cursor: hand; -fx-underline: true;");

                // Toggle details visibility when the arrow is clicked
                expandButton.setOnAction(event -> {
                    boolean isVisible = detailsBox.isVisible();
                    detailsBox.setVisible(!isVisible);
                    detailsBox.setManaged(!isVisible);
                    expandButton.setText(isVisible ? "View Details" : "Hide Details");
                });

                // Position items properly with spacers
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                
                // Add to the main item row
                itemRow.getChildren().addAll(itemImage, namePriceBox, spacer, quantitySpinner);

                // Create a separator
                Separator separator = new Separator();
                separator.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");
                
                // Only show expand button if there's variation or instructions
                if (!variationText.isEmpty() || !instructionsText.isEmpty()) {
                    // Add the item row and its details to the container
                    itemCard.getChildren().addAll(itemRow, expandButton, detailsBox);
                } else {
                    itemCard.getChildren().add(itemRow);
                }
                
                itemsBox.getChildren().add(itemCard);
            }
        }

        // Create an HBox to hold both cart items and the summary
        HBox contentBox = new HBox(20);
        contentBox.setPadding(new Insets(10));
        contentBox.setPrefWidth(1200);
        contentBox.setAlignment(Pos.CENTER_LEFT);
        
        // Ensure scrollableItemsBox has a proper width
        scrollableItemsBox.setPrefWidth(800);
        
        // Summary Section with improved styling
        VBox summaryBox = new VBox(15);
        summaryBox.setPadding(new Insets(20));
        summaryBox.setStyle("-fx-background-color: " + BACKGROUND_WHITE + "; -fx-border-color: " + LIGHT_GRAY + 
                         "; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        summaryBox.setPrefWidth(350);
        summaryBox.setAlignment(Pos.TOP_LEFT);
        
        // Order Summary header
        Label summaryTitle = new Label("Order Summary");
        summaryTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_BLACK + ";");
        
        // Separator
        Separator summaryDivider = new Separator();
        summaryDivider.setPadding(new Insets(5, 0, 10, 0));
        
        // Price breakdown with better alignment
        GridPane priceGrid = new GridPane();
        priceGrid.setHgap(10);
        priceGrid.setVgap(15);

        subtotalLabel = new Label("Subtotal:");
        subtotalLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: " + TEXT_BLACK + ";");
          subtotalValue = new Label("₱" + String.format("%.2f", subtotal));
        subtotalValue.setStyle("-fx-font-size: 15px; -fx-text-fill: " + TEXT_BLACK + ";");

        Label deliveryLabel = new Label("Delivery Fee:");
        deliveryLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: " + TEXT_BLACK + ";");
         deliveryValue = new Label("₱" + String.format("%.2f", deliveryFee));
        deliveryValue.setStyle("-fx-font-size: 15px; -fx-text-fill: " + TEXT_BLACK + ";");
        
        priceGrid.add(subtotalLabel, 0, 0);
        priceGrid.add(subtotalValue, 1, 0);
        priceGrid.add(deliveryLabel, 0, 1);
        priceGrid.add(deliveryValue, 1, 1);
        
        GridPane.setHalignment(subtotalValue, HPos.RIGHT);
        GridPane.setHalignment(deliveryValue, HPos.RIGHT);
        
        // Set column constraints to push values to the right
        ColumnConstraints col1 = new ColumnConstraints();
        ColumnConstraints col2 = new ColumnConstraints();
        col1.setHgrow(Priority.ALWAYS);
        col2.setHalignment(HPos.RIGHT);
        priceGrid.getColumnConstraints().addAll(col1, col2);
        
        // Total Row with divider
        Separator totalDivider = new Separator();
        totalDivider.setPadding(new Insets(10, 0, 10, 0));
        
        HBox totalRow = new HBox();
        totalRow.setAlignment(Pos.CENTER_LEFT);
        
        totalLabel = new Label("Total:");
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_BLACK + ";");
        
         totalValue = new Label("₱" + String.format("%.2f", subtotal + deliveryFee));
        totalValue.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_RED + ";");
        
        Region totalSpacer = new Region();
        HBox.setHgrow(totalSpacer, Priority.ALWAYS);
        totalRow.getChildren().addAll(totalLabel, totalSpacer, totalValue);
        
        // Buttons with improved styling
        Button reviewPaymentBtn = new Button("Review Payment & Address");
        reviewPaymentBtn.setOnAction(e -> {
            CheckOutWindow.displayCheckout(
                userID,
                CartSession.getCartItems(),
                CartSession.getVariations(),
                CartSession.getInstructions()
            );
            cartStage.close();
        });

        Button closeBtn = new Button("Continue Shopping");
        
        reviewPaymentBtn.setPrefWidth(350);
        reviewPaymentBtn.setPrefHeight(40);
        reviewPaymentBtn.setStyle("-fx-background-color: " + PRIMARY_RED + "; -fx-text-fill: white; " +
                            "-fx-font-size: 15px; -fx-font-weight: bold; -fx-background-radius: 8;");
        
        closeBtn.setPrefWidth(350);
        closeBtn.setPrefHeight(40);
        closeBtn.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-text-fill: " + TEXT_BLACK + "; " +
                     "-fx-font-size: 15px; -fx-background-radius: 8;");
        
        closeBtn.setOnAction(e -> cartStage.close());
        
        VBox buttonBox = new VBox(10, reviewPaymentBtn, closeBtn);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));
        
        // Assemble summary box
        summaryBox.getChildren().addAll(summaryTitle, summaryDivider, priceGrid, totalDivider, totalRow, buttonBox);
        
        // Add both the scrollable cart and summary section to contentBox
        contentBox.getChildren().addAll(scrollableItemsBox, summaryBox);

        // Now, add contentBox to the main layout instead of individual components
        mainLayout.getChildren().addAll(progressSection, titleBox, contentBox);
        Scene scene = new Scene(mainLayout, 1200, 700);
        
        // Add CSS for the scene
        scene.getStylesheets().add(ShowCart.class.getResource("/styles/cart.css").toExternalForm());

        
        cartStage.setScene(scene);
        cartStage.setTitle("Your Cart");
        cartStage.showAndWait();
    }
    
        public static void removeItem(int itemId) {
        CartSession.getCartItems().remove(itemId); // ✅ Removes it from the map
        CartSession.getVariations().remove(itemId);
        CartSession.getInstructions().remove(itemId);
         CartSession.notifyCartChanged(); 
        System.out.println("Item " + itemId + " removed from cart.");
        
         // ✅ Check here instead
    System.out.println("Cart contents after removal: " + CartSession.getCartItems());

    if (CartSession.getCartItems().isEmpty()) {
        System.out.println("Cart is empty - switching view...");

        if (cartStage != null && cartStage.isShowing()) {
            cartStage.close();
        }

        Platform.runLater(() -> {
            EmptyCart.showEmptyCartMessage();
        });
    }
    }

    
  
    public static void updateItemQuantity(int itemId, int newQuantity) {
    if (newQuantity > 0) {
        CartSession.getCartItems().put(itemId, newQuantity);
        CartSession.notifyCartChanged();
        System.out.println("Updated item " + itemId + " to quantity: " + newQuantity);
    } else {
        removeItem(itemId); // this notifies too
        CartSession.notifyCartChanged(); 

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
       
         subtotalValue.setText(String.format("%.2f", newSubtotal));
          updateTotal(newSubtotal);
    }
    private static void updateTotal(double newSubtotal) {
        double deliveryFee = 49.0; // Standard delivery fee
        double newTotal = newSubtotal + deliveryFee;

       
        totalValue.setText(String.format("%.2f", newTotal));
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
