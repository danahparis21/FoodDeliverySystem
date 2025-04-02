/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;


import java.sql.*;
import java.util.HashMap;

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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import netscape.javascript.JSException;
import netscape.javascript.JSObject;

public class CheckOutWindow {
    static Label subtotalLabel;
    static Label totalLabel;

    public static void displayCheckout(Map<Integer, Integer> cartItems) {
        Stage checkoutStage = new Stage();
        checkoutStage.initStyle(StageStyle.UTILITY);
        checkoutStage.initModality(Modality.APPLICATION_MODAL);
        System.setProperty("prism.order", "sw"); // Forces software rendering
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
        Rectangle progressFill = new Rectangle(250, 10, Color.GREEN); // Adjust width to show progress
        progressFill.setArcWidth(10);
        progressFill.setArcHeight(10);

        // Stack the background and progressFill
        StackPane progressBar = new StackPane(progressBackground, progressFill);
        StackPane.setAlignment(progressBackground, Pos.CENTER_LEFT);
        StackPane.setAlignment(progressFill, Pos.CENTER_LEFT);
        
      // Animation: Smoothly grow progressFill width to 250 (Cart stage)
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(progressFill.widthProperty(), 250, Interpolator.EASE_BOTH)),
            new KeyFrame(Duration.seconds(1.5), new KeyValue(progressFill.widthProperty(), 450, Interpolator.EASE_BOTH))
        );
        timeline.setCycleCount(1); // Runs only once
        timeline.play();
        // Ensure progressBar grows to the right
        HBox.setHgrow(progressBar, Priority.ALWAYS);

        // Add to the progress container
        progressContainer.getChildren().add(progressBar);

        // Step Labels
        Label step1 = new Label("✔ Menu");
        Label step2 = new Label("✔ Cart");
        Label step3 = new Label("💳 Checkout");

        step1.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        step2.setStyle("-fx-font-size: 14px; -fx-text-fill: green;");
        step3.setStyle("-fx-font-size: 14px; -fx-text-fill: blue;");

        // Position Labels in an HBox, aligned to the right
        HBox stepLabels = new HBox(160, step1, step2, step3); // Adjust spacing between steps
        stepLabels.setAlignment(Pos.CENTER_LEFT);
        stepLabels.setPrefWidth(1200);
        
        // Combine Progress Bar and Labels
        VBox progressSection = new VBox(5, progressContainer, stepLabels);
        progressSection.setAlignment(Pos.CENTER);

      // Step 1: Create Address Input Section
    VBox addressSection = new VBox(10);
    addressSection.setPadding(new Insets(10));
    addressSection.setStyle("-fx-border-color: gray; -fx-border-radius: 10; -fx-padding: 10;");

    // Address fields
    Label addressLabel = new Label("Enter Shipping Address:");
    TextField streetField = new TextField();
    streetField.setPromptText("Street Address");
    TextField cityField = new TextField();
    cityField.setPromptText("City");
    TextField postalCodeField = new TextField();
    postalCodeField.setPromptText("Postal Code");
    TextField countryField = new TextField();
    countryField.setPromptText("Country");

    // Save button
    Button saveButton = new Button("Save Address");
    saveButton.setOnAction(e -> saveAddressToDatabase(
        streetField.getText(),
        cityField.getText(),
        postalCodeField.getText(),
        countryField.getText()
    ));

    addressSection.getChildren().addAll(
        addressLabel, 
        streetField, 
        cityField, 
        postalCodeField, 
        countryField,
        saveButton
    );
    addressSection.setPrefWidth(800);
        
        
        // Step 3: Create Payment Method Section (ComboBox)
        Label paymentMethodLabel = new Label("Select Payment Method:");
        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.getItems().addAll("Cash", "Credit/Debit Card");
        paymentMethodComboBox.setValue("Cash"); // Set default payment option

        // Order Summary Box
        VBox orderSummary = new VBox(5);
        orderSummary.setPadding(new Insets(10));
        orderSummary.setStyle("-fx-border-color: black; -fx-border-radius: 10; -fx-padding: 10;");
        orderSummary.setPrefWidth(350);

        double subtotal = 0;
        // Display detailed cart items
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();

            String itemName = getItemNameById(itemId);
            double itemPrice = getItemPriceById(itemId);
            double totalPrice = itemPrice * quantity;
            subtotal += totalPrice;

            // Create Item Display in Checkout
            HBox itemRow = new HBox(15);
            itemRow.setAlignment(Pos.CENTER_LEFT);

            // Item Image
            ImageView itemImage = new ImageView(new Image(getItemImageById(itemId)));
            itemImage.setFitWidth(60);
            itemImage.setFitHeight(60);

            // Item Name & Price
            VBox namePriceBox = new VBox(5);
            Label nameLabel = new Label(itemName);
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            Label priceLabel = new Label("₱" + String.format("%.2f", itemPrice));
            priceLabel.setStyle("-fx-font-size: 14px;");
            namePriceBox.getChildren().addAll(nameLabel, priceLabel);
            
            

            // Add item row to the summary
            itemRow.getChildren().addAll(itemImage, namePriceBox);
            orderSummary.getChildren().add(itemRow);
        }

        // Total Labels
        Label subtotalLabel = new Label("Subtotal: ₱" + String.format("%.2f", subtotal));
        Label deliveryLabel = new Label("Delivery Fee: ₱49");
        Label totalLabel = new Label("Total: ₱" + String.format("%.2f", subtotal + 49));
        
        // Place Order Button
            Button placeOrderBtn = new Button("Place Order");

            // Close Button
            Button closeBtn = new Button("Close");
            closeBtn.setOnAction(e -> checkoutStage.close());


        orderSummary.getChildren().addAll(subtotalLabel, deliveryLabel, totalLabel, placeOrderBtn, closeBtn);

       // Step 5: Combine all into Main Layout (VBox)
        VBox mainLayout = new VBox(15);
        mainLayout.setPadding(new Insets(20));

        // Title
        Label title = new Label("Checkout");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Create an HBox to align Progress Section and Address Section with Payment Method vertically
        HBox leftLayout = new HBox(15);
        leftLayout.setAlignment(Pos.CENTER_LEFT);
        leftLayout.getChildren().addAll( addressSection,orderSummary);

        // Combine HBox and Order Summary into the final layout
//        VBox rightLayout = new VBox(20);
//        rightLayout.setAlignment(Pos.TOP_RIGHT);
//        rightLayout.getChildren().add();

        mainLayout.getChildren().addAll(progressSection, title, webView, leftLayout,paymentMethodComboBox);

        
        //contentBox.getChildren().addAll(checkOutDetails, scrollableItemsBox, summarySection);

        // Now, add contentBox to the main layout instead of individual components
     //  mainLayout.getChildren().addAll(progressSection, title, contentBox);
        Scene scene = new Scene(mainLayout, 1200, 700);
        checkoutStage.setScene(scene);
        checkoutStage.setTitle("Cart Summary");
        checkoutStage.showAndWait();
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
 
// Improved search method
private static void safeExecuteAddressSearch(WebEngine webEngine, String address) {
    System.out.println("🔵 Searching for: " + address);
    
    if (webEngine.getLoadWorker().getState() != Worker.State.SUCCEEDED) {
        System.out.println("⚠️ Waiting for page to load...");
        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> obs, Worker.State oldState, Worker.State newState) {
                if (newState == Worker.State.SUCCEEDED) {
                    Platform.runLater(() -> executeMoveToAddress(webEngine, address));
                    webEngine.getLoadWorker().stateProperty().removeListener(this);
                }
            }
        });
        return;
    }
    
    executeMoveToAddress(webEngine, address);
}

private static void executeMoveToAddress(WebEngine webEngine, String address) {
    try {
        String script = String.format(
            "if (typeof moveToAddress === 'function') {" +
            "   try {" +
            "       moveToAddress('%s');" +
            "       console.log('Address search executed successfully');" +
            "   } catch (e) {" +
            "       console.error('Error in moveToAddress:', e);" +
            "   }" +
            "} else {" +
            "   console.error('moveToAddress not found! Available functions:', " +
            "       Object.keys(window).filter(k => typeof window[k] === 'function'));" +
            "}", 
            address.replace("'", "\\'")
        );
        
        Object result = webEngine.executeScript(script);
        System.out.println("Search result: " + result);
    } catch (Exception e) {
        System.err.println("🔴 Search failed: " + e.getMessage());
        // Retry after 500ms
        PauseTransition pause = new PauseTransition(Duration.millis(500));
        pause.setOnFinished(event -> Platform.runLater(() -> executeMoveToAddress(webEngine, address)));
        pause.play();
    }
}

// JavaScript bridge class
public static class JavaBridge {
    private TextField streetField;
    private TextField cityField;
    private TextField postalCodeField;
    private TextField countryField;

    public JavaBridge(TextField streetField, TextField cityField, TextField postalCodeField, TextField countryField) {
        this.streetField = streetField;
        this.cityField = cityField;
        this.postalCodeField = postalCodeField;
        this.countryField = countryField;
    }

    
    public void updateAddress(String street, String city, String postalCode, String country) {
        // Update the JavaFX fields with the address details
        Platform.runLater(() -> {
            streetField.setText(street);
            cityField.setText(city);
            postalCodeField.setText(postalCode);
            countryField.setText(country);
        });
    }
}



    

    

}

