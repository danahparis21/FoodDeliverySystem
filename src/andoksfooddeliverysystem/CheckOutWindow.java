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
import java.util.Optional;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javafx.animation.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    static List<OrderItem> itemsList = new ArrayList<>();
    
    // At the top of your class, outside of any method:
    private static int addressId = -1;
    private static Address selectedAddress;
     private static double totalPrice;

     static double subtotal = 0;

   public static void displayCheckout(int customerID, Map<Integer, Integer> cartItems, Map<Integer, String> variations, Map<Integer, String> instructions) {
          System.out.println("✅ Checkout opened for User ID: " + customerID); // Debugging

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
        
        //BArrangay
         
        ComboBox<String> barangayCombo = new ComboBox<>();
        barangayCombo.getItems().addAll(getBarangays()); // Get barangays from the database
        barangayCombo.getSelectionModel().selectFirst();
        barangayCombo.setPromptText("Barangay");
        
        TextField streetField = new TextField();
        streetField.setPromptText("Street Address");
     
        // Contact number TextField
        TextField contactNumberField = new TextField();
        contactNumberField.setPromptText("Contact Number");

        // Address type selection
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("HOME", "WORK", "OTHER");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.setPromptText("Address Type");

        // Default address toggle
        CheckBox defaultCheck = new CheckBox("Set as default address");

       
      Button saveButton = new Button("Save Address");
        saveButton.setOnAction(e -> {
            if (streetField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Street is required");
                return;
            }
            if (barangayCombo.getSelectionModel().getSelectedItem() == null) {
                showAlert("Validation Error", "Barangay is required");
                return;
            }
            if (contactNumberField.getText().trim().isEmpty()) {
                showAlert("Validation Error", "Contact number is required");
                return;
            }

            // Save address to the database and get the address_id
            addressId = saveAddressToDatabase(
                customerID, // ✅ Use correct customerID
                streetField.getText().trim(),
                barangayCombo.getSelectionModel().getSelectedItem(),
                typeCombo.getValue(),
                defaultCheck.isSelected(),
                contactNumberField.getText().trim()
            );

            if (addressId != -1) {
                // Clear the fields if the address was successfully saved
                streetField.clear();
                barangayCombo.getSelectionModel().clearSelection();
                contactNumberField.clear();
                defaultCheck.setSelected(false);

                // Optionally show a success message
                showAlert("Success", "Address saved successfully!");
            } else {
                showAlert("Error", "Failed to save address.");
            }
        });

        
        // Address list display (ListView)
        ListView<Address> addressListView = new ListView<>();
        addressListView.setItems(getCustomerAddresses(customerID)); // Load customer addresses

        // Make the list scrollable
        addressListView.setPrefHeight(100);  // Adjust height as needed

        // Track selected address's ID
    
        // When an address is selected, populate the fields
        addressListView.setOnMouseClicked(event -> {
                 selectedAddress = addressListView.getSelectionModel().getSelectedItem();
            if (selectedAddress != null) {
                
                addressId = selectedAddress.getAddressId(); // Set the address ID
                streetField.setText(selectedAddress.getStreet());
                barangayCombo.setValue(selectedAddress.getBarangay()); // Set Barangay from selected address
                typeCombo.setValue(selectedAddress.getAddressType());
                defaultCheck.setSelected(selectedAddress.isDefault());
                contactNumberField.setText(selectedAddress.getContactNumber()); 
            }
        });

        // Add components to layout
        addressSection.getChildren().addAll(
            addressLabel,
            barangayCombo, // Add Barangay combo box
            streetField,
             contactNumberField, // Add Contact Number field
            new Label("Address Type:"),
            typeCombo,
            defaultCheck,
            saveButton,
            addressListView
        );
        addressSection.setPrefWidth(800);
        
        // Step 3: Create Payment Method Section (ComboBox)
        Label paymentMethodLabel = new Label("Select Payment Method:");
        ComboBox<String> paymentMethodComboBox = new ComboBox<>();
        paymentMethodComboBox.getItems().addAll("Cash", "Credit/Debit Card", "GCash");
        paymentMethodComboBox.setValue("Cash"); // Set default payment option
        
        VBox cardSelectionContainer = new VBox();
        
        VBox paymentSelectionContainer = new VBox();


        


        // Order Summary Box
        VBox orderSummary = new VBox(5);
        orderSummary.setPadding(new Insets(10));
        orderSummary.setStyle("-fx-border-color: black; -fx-border-radius: 10; -fx-padding: 10;");
        orderSummary.setPrefWidth(350);

  
        // Display detailed cart items
         for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            
            

            String variationText = variations.getOrDefault(itemId, "No variation");
            String instructionsText = instructions.getOrDefault(itemId, "No instructions");

            System.out.println("Item ID: " + itemId + " | Quantity: " + quantity);
            System.out.println("🔄 Variation: " + variationText);
            System.out.println("✏ Instructions: " + instructionsText);
            
            String itemName = getItemNameById(itemId);
            double itemPrice = getItemPriceById(itemId);
             totalPrice = itemPrice * quantity;
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
            OrderItem orderItem = new OrderItem(itemId, quantity, subtotal);
            itemsList.add(orderItem);
        }
         
          paymentMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
        cardSelectionContainer.getChildren().clear(); // Clear previous UI

        if ("Credit/Debit Card".equals(newValue)) {
            List<Card> savedCards = getSavedCardsForCustomer(customerID); // Your DB method

            if (!savedCards.isEmpty()) {
                Label selectCardLabel = new Label("Select Saved Card:");
                ComboBox<Card> savedCardComboBox = new ComboBox<>();
                savedCardComboBox.setPromptText("Choose a saved card");

                // Add saved cards to the combo box
                savedCardComboBox.getItems().addAll(savedCards);

                // Add a "dummy" card item for adding a new card
                Card addNewCard = new Card("➕ Add New Card", "", true);

                savedCardComboBox.getItems().add(addNewCard);

                savedCardComboBox.setOnAction(e -> {
                    Card selectedCard = savedCardComboBox.getValue();
                    if (selectedCard != null) {
                        if (selectedCard.isDummyCard()) {  // Check if it's the "Add New Card" option
                            // Launch new card form
                            CardPaymentForm cardForm = new CardPaymentForm();
                            cardForm.showCardPaymentForm(customerID);
                        } else {
                            // Prompt confirmation before payment for existing cards
                            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmAlert.setTitle("Confirm Payment");
                            confirmAlert.setHeaderText("Use saved card ending in " + getLast4Digits(selectedCard.getCardNumber()) + "?");
                            confirmAlert.setContentText("Do you want to proceed with this card?");

                            Optional<ButtonType> result = confirmAlert.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                // Proceed to show payment success alert
                                CardPaymentForm cardForm = new CardPaymentForm();
                                cardForm.showPaymentSuccessAlert(
                                    selectedCard.getCardholderName(),
                                    selectedCard.getCardNumber()
                                );
                            }
                        }
                    }
                });

                cardSelectionContainer.getChildren().addAll(selectCardLabel, savedCardComboBox);
            } else {
                cardSelectionContainer.getChildren().add(new Label("No saved cards found."));

                // Add fallback "Add New Card" button
                Button addNewCardBtn = new Button("Add a New Card");
                addNewCardBtn.setOnAction(ev -> {
                    CardPaymentForm cardForm = new CardPaymentForm();
                    cardForm.showCardPaymentForm(customerID);
                });
                cardSelectionContainer.getChildren().add(addNewCardBtn);
            }
        }
        
        //GCASH LOGIC
       else if ("GCash".equals(newValue)) {
            // Create and show the GCash Payment Form
            GCashPaymentForm gcashPaymentForm = new GCashPaymentForm(customerID, addressId, totalPrice);
            gcashPaymentForm.showGCashPaymentForm();
        } 
    });

        // Total Labels
        Label subtotalLabel = new Label("Subtotal: ₱" + String.format("%.2f", subtotal));
        Label deliveryLabel = new Label("Delivery Fee: ₱49");
        Label totalLabel = new Label("Total: ₱" + String.format("%.2f", subtotal + 49));
        
        Button placeOrderBtn = new Button("Place Order");
        placeOrderBtn.setOnAction(e -> {
            // Ensure address_id is available, assume it's stored in a variable like `addressId`
            if (addressId == -1) {
                showAlert("Error", "Please select an address before placing the order.");
                return;
            }

            String paymentMethod = paymentMethodComboBox.getSelectionModel().getSelectedItem() != null 
                                    ? paymentMethodComboBox.getSelectionModel().getSelectedItem() 
                                    : "COD"; // Default to "COD" if nothing is selected

            double totalPrice = subtotal + 49.00;  // Adding delivery fee

            // Save the order using the address_id
            int orderId = saveOrderToDatabase(customerID, addressId, totalPrice, paymentMethod);
            if (orderId != -1) {
                // If order is saved successfully, save the order items
                saveOrderItemsToDatabase(orderId);

                // Show success message
                showAlert("Success", "Your order has been placed successfully!");
                
                // ✅ Clear cart
                CartSession.clearCart();
                CartSession.notifyCartChanged(); 
                

                // ✅ Close the checkout window
                ((Stage) placeOrderBtn.getScene().getWindow()).close();

            } else {
                showAlert("Error", "Failed to place the order.");
            }
        });

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


        mainLayout.getChildren().addAll(progressSection, title, leftLayout,paymentMethodComboBox, cardSelectionContainer);

        Scene scene = new Scene(mainLayout, 1200, 700);
        checkoutStage.setScene(scene);
        checkoutStage.setTitle("Cart Summary");
        checkoutStage.showAndWait();
    }
   
     private static String getLast4Digits(String cardNumber) {
        if (cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }

    
    public static List<Card> getSavedCardsForCustomer(int customerID) {
     List<Card> cards = new ArrayList<>();
     Connection conn = Database.connect();

     try {
         PreparedStatement stmt = conn.prepareStatement("SELECT card_number, cardholder_name FROM saved_cards WHERE customer_id = ?");
         stmt.setInt(1, customerID);
         ResultSet rs = stmt.executeQuery();

         while (rs.next()) {
             String number = rs.getString("card_number");
             String name = rs.getString("cardholder_name");
             cards.add(new Card(number, name, false));
         }

     } catch (SQLException e) {
         e.printStackTrace();
     }

     return cards;
 }

   
    // Method to save order items to the database

    public static void saveOrderItemsToDatabase(int orderId) {
        Map<Integer, Integer> cartItems = CartSession.getCartItems();
        Map<Integer, String> variations = CartSession.getVariations();
        Map<Integer, String> instructions = CartSession.getInstructions();

        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
            double subtotal = getItemPriceById(itemId) * quantity;

            String variationText = variations.getOrDefault(itemId, null);
            String instructionsText = instructions.getOrDefault(itemId, null);

            // ✅ Open connection inside try-with-resources
            try (Connection conn = Database.connect()) {  
                String query = "INSERT INTO order_items (order_id, item_id, quantity, subtotal, variation, instructions) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, itemId);
                    stmt.setInt(3, quantity);
                    stmt.setDouble(4, subtotal);
                    stmt.setString(5, variationText);
                    stmt.setString(6, instructionsText);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error saving order item: " + e.getMessage());
            }
        }
    }



    public static int saveOrderToDatabase(int customerId, int addressId, double totalPrice, String paymentMethod) {
      try (Connection conn = Database.connect()) {

          // Determine payment_status based on paymentMethod
          String paymentStatus;
          switch (paymentMethod) {
              case "GCash":
                  paymentStatus = "Pending Verification";
                  break;
              case "Credit/Debit Card":
                  paymentStatus = "Paid";
                  break;
              default: // Cash
                  paymentStatus = "Pending Payment";
                  break;
          }

          // Update query to include payment_status
          String sql = "INSERT INTO orders (customer_id, address_id, total_price, payment_method, payment_status, status) " +
                       "VALUES (?, ?, ?, ?, ?, 'Pending')";

          PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
          pstmt.setInt(1, customerId);
          pstmt.setInt(2, addressId);
          pstmt.setDouble(3, totalPrice);
          pstmt.setString(4, paymentMethod);
          pstmt.setString(5, paymentStatus);

          int affectedRows = pstmt.executeUpdate();

          if (affectedRows > 0) {
              ResultSet rs = pstmt.getGeneratedKeys();
              if (rs.next()) {
                  return rs.getInt(1); // Return the generated order_id
              }
          }
      } catch (SQLException ex) {
          System.err.println("❌ Error saving order: " + ex.getMessage());
      }
      return -1; // If order saving failed
  }



    private static ObservableList<Address> getCustomerAddresses(int customerId) {
        ObservableList<Address> addresses = FXCollections.observableArrayList();
        try (Connection conn = Database.connect()) {
            // Modified SQL query to include contact_number
            String sql = "SELECT address_id, street, barangay_id, address_type, is_default, contact_number FROM addresses WHERE customer_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, customerId);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int addressId = rs.getInt("address_id"); // Get the address_id
                String street = rs.getString("street");
                int barangayId = rs.getInt("barangay_id");
                String addressType = rs.getString("address_type");
                boolean isDefault = rs.getBoolean("is_default");
                String contactNumber = rs.getString("contact_number"); // Get contact number

                String barangay = getBarangayNameById(barangayId); // Function to get barangay name by ID
                addresses.add(new Address(addressId, street, barangay, addressType, isDefault, contactNumber)); // Pass contact number to Address constructor
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving addresses: " + ex.getMessage());
        }
        return addresses;
    }


    // Helper method to fetch barangay name by ID
    private static String getBarangayNameById(int barangayId) {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT barangay_name FROM Barangay WHERE barangay_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, barangayId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("barangay_name");
            }
        } catch (SQLException ex) {
            System.err.println("Error retrieving barangay name: " + ex.getMessage());
        }
        return "";
    }

    
    // Database: Fetch Barangays
    private static List<String> getBarangays() {
        List<String> barangays = new ArrayList<>();
        try (Connection conn = Database.connect()) {
            String sql = "SELECT barangay_name FROM Barangay"; // Assuming barangays table
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                barangays.add(rs.getString("barangay_name"));
            }
        } catch (SQLException ex) {
            System.err.println("Database error: " + ex.getMessage());
        }
        return barangays;
    }

    // Helper method to show alerts
private static void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
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
 
 // Save address to database with barangay_id as a foreign key
    private static int saveAddressToDatabase(int customerId, String street,
                                                String barangay, String addressType, boolean isDefault, String contactNumber) {
        street = street.trim();
      try (Connection conn = Database.connect()) {
          // First, get barangay_id based on barangay name
          String barangayIdQuery = "SELECT barangay_id FROM Barangay WHERE barangay_name = ?";
          PreparedStatement pstmtBarangay = conn.prepareStatement(barangayIdQuery);
          pstmtBarangay.setString(1, barangay);
          ResultSet rs = pstmtBarangay.executeQuery();

          int barangayId = -1; // Default value if not found
          if (rs.next()) {
              barangayId = rs.getInt("barangay_id");
          }

          if (barangayId == -1) {
              System.err.println("Invalid Barangay selected!");
              return -1; // Barangay not found
          }

         // Check if the address already exists for this customer based on street and barangay
        String checkAddressQuery = "SELECT address_id FROM addresses WHERE customer_id = ? AND street = ? AND barangay_id = ?";
        PreparedStatement pstmtCheck = conn.prepareStatement(checkAddressQuery);
        pstmtCheck.setInt(1, customerId);
        pstmtCheck.setString(2, street);
        pstmtCheck.setInt(3, barangayId);
        ResultSet rsCheck = pstmtCheck.executeQuery();
        System.out.println("Checking if address exists for customer: " + customerId + " and street: " + street + " barangay_id: " + barangayId);

          if (rsCheck.next()) {
              // Address exists, so update the existing record
              int existingAddressId = rsCheck.getInt("address_id");

              String updateAddressQuery = "UPDATE addresses SET barangay_id = ?, address_type = ?, is_default = ?, contact_number = ? WHERE address_id = ?";
              PreparedStatement pstmtUpdate = conn.prepareStatement(updateAddressQuery);
              pstmtUpdate.setInt(1, barangayId);
              pstmtUpdate.setString(2, addressType);
              pstmtUpdate.setBoolean(3, isDefault);
              pstmtUpdate.setString(4, contactNumber);
              pstmtUpdate.setInt(5, existingAddressId);

              int affectedRows = pstmtUpdate.executeUpdate();
              if (affectedRows > 0) {
                  System.out.println("Address updated successfully!");
                   showAlert("Success", "Address updated successfully!");
                  return existingAddressId;
              } else {
                  System.out.println("Failed to update address");
                  return -1;
              }
          } else {
              // Address does not exist, insert a new record
              String insertAddressQuery = "INSERT INTO addresses (customer_id, street, barangay_id, address_type, is_default, contact_number) " +
                                          "VALUES (?, ?, ?, ?, ?, ?)";
              PreparedStatement pstmtInsert = conn.prepareStatement(insertAddressQuery);
              pstmtInsert.setInt(1, customerId);
              pstmtInsert.setString(2, street);
              pstmtInsert.setInt(3, barangayId);
              pstmtInsert.setString(4, addressType);
              pstmtInsert.setBoolean(5, isDefault);
              pstmtInsert.setString(6, contactNumber);

              int affectedRows = pstmtInsert.executeUpdate();
                ResultSet rsInsert = pstmtInsert.getGeneratedKeys();
              if (affectedRows > 0) {
                  int newAddressId = rsInsert.getInt(1); // Get the generated address_id
                    System.out.println("Address saved successfully!");
                    return newAddressId; // Return the generated address_id
              } else {
                  System.out.println("Failed to save address");
                  return -1;
              }
          }
      } catch (SQLException ex) {
          System.err.println("Database error: " + ex.getMessage());
          // Show alert to user
          Platform.runLater(() -> {
              Alert alert = new Alert(Alert.AlertType.ERROR);
              alert.setTitle("Database Error");
              alert.setHeaderText("Could not save address");
              alert.setContentText(ex.getMessage());
              alert.showAndWait();
          });
          return -1;
      }
  }


}

