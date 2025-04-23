/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;


import java.awt.Desktop;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javax.mail.MessagingException;
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
    private static double deliveryFee;

    static double subtotal;
    static ComboBox<Card> savedCardComboBox = new ComboBox<>();
    static ComboBox<String> pickupTimeCombo;
    static ComboBox<String> paymentMethodComboBox = new ComboBox<>();
    static Label deliveryLabel;
    private static final String WHITE = "#FFFFFF";
    private static final String LIGHT_GRAY = "#F5F5F5";
    private static final String MEDIUM_GRAY = "#E0E0E0";
   
    // Define style constants for reuse
    private static final String MAIN_BACKGROUND = "-fx-background-color: #FFFFFF;";
    private static final String HEADER_STYLE = "-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;";
    private static final String SUBHEADER_STYLE = "-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;";
    private static final String LABEL_STYLE = "-fx-font-size: 14px; -fx-text-fill: #333333;";
    private static final String PRICE_STYLE = "-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;";
    private static final String SECTION_STYLE = "-fx-border-color: #FFCC00; -fx-border-radius: 8; -fx-background-color: #FFFFFF; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);";
    private static final String BUTTON_STYLE = "-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; -fx-background-radius: 5;";
    private static final String SECONDARY_BUTTON_STYLE = "-fx-background-color: #FFCC00; -fx-text-fill: #333333; -fx-font-weight: bold; -fx-cursor: hand; -fx-padding: 10 20; -fx-background-radius: 5;";
    private static final String FIELD_STYLE = "-fx-background-radius: 5; -fx-border-radius: 5; -fx-border-color: #CCCCCC; -fx-padding: 8;";
    private static final String COMBO_STYLE = 
    "-fx-background-radius: 5; " +
    "-fx-border-radius: 5; " +
    "-fx-background-color: " + WHITE + "; " +
    "-fx-border-color: " + MEDIUM_GRAY + "; " +
    "-fx-padding: 8;";

    static {
        pickupTimeCombo = new ComboBox<>();
        pickupTimeCombo.setPromptText("Select Pickup Time");
        pickupTimeCombo.setStyle(COMBO_STYLE);
    }

    public static void displayCheckout(int userId, Map<Integer, Integer> cartItems, Map<Integer, String> variations, Map<Integer, String> instructions) {
        System.out.println("✅ Checkout opened for User ID: " + userId); // Debugging
        int customerId = getCustomerIdFromUserId(userId);

        System.out.println("✅ Checkout opened for User ID: " + userId + ", Customer ID: " + customerId);
        subtotal = 0.0;
        totalPrice = 0.0;
        deliveryFee = 0.0;
        deliveryLabel = new Label("Delivery Fee: ₱" + String.format("%.2f", deliveryFee));
        deliveryLabel.setStyle(PRICE_STYLE);
        
        subtotalLabel = new Label("Subtotal: ₱" + String.format("%.2f", subtotal));
        subtotalLabel.setStyle(PRICE_STYLE);
        
        totalLabel = new Label("Total: ₱" + String.format("%.2f", subtotal + deliveryFee));
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");

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
        Rectangle progressBackground = new Rectangle(600, 8, Color.web("#EEEEEE"));
        progressBackground.setArcWidth(8);
        progressBackground.setArcHeight(8);

        // Progress Bar Fill (Represents progress)
        Rectangle progressFill = new Rectangle(250, 8, Color.web("#D32F2F")); // Red color for progress
        progressFill.setArcWidth(8);
        progressFill.setArcHeight(8);

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

        step1.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;"); // Green check for completed
        step2.setStyle("-fx-font-size: 14px; -fx-text-fill: #4CAF50;"); // Green check for completed
        step3.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;"); // Current step in red

        // Position Labels in an HBox, aligned to the right
        HBox stepLabels = new HBox(160, step1, step2, step3); // Adjust spacing between steps
        stepLabels.setAlignment(Pos.CENTER_LEFT);
        stepLabels.setPrefWidth(1200);
        
        // Combine Progress Bar and Labels
        VBox progressSection = new VBox(5, progressContainer, stepLabels);
        progressSection.setAlignment(Pos.CENTER);
        progressSection.setPadding(new Insets(0, 0, 15, 0));
        progressSection.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #EEEEEE;");

        // Step 1: Create Address Input Section
        VBox addressSection = new VBox(15);
        addressSection.setPadding(new Insets(20));
        addressSection.setStyle(SECTION_STYLE);

        // Address section header
        Label addressSectionHeader = new Label("Delivery Details");
        addressSectionHeader.setStyle(SUBHEADER_STYLE);

        // Address fields
        ComboBox<String> deliveryTypeCombo = new ComboBox<>();
        deliveryTypeCombo.getItems().addAll("Delivery", "For Pick Up");
        deliveryTypeCombo.setValue("Enter Shipping Address (Delivery)");
        deliveryTypeCombo.setStyle(COMBO_STYLE);
        deliveryTypeCombo.setPrefWidth(300);
        
        
        // ✅ Manually call the update logic once
        updateDeliveryAndTotal(deliveryTypeCombo, deliveryLabel, totalLabel);

        // === DELIVERY SECTION ===
        VBox deliverySection = new VBox(15);
        deliverySection.setPadding(new Insets(10, 0, 0, 0));
     
        Label barangayLabel = new Label("Select Barangay");
        barangayLabel.setStyle(LABEL_STYLE);
        
        ComboBox<String> barangayCombo = new ComboBox<>();
        barangayCombo.getItems().addAll(getBarangays()); // Get barangays from the database
        barangayCombo.getSelectionModel().selectFirst();
        barangayCombo.setPromptText("Barangay");
        barangayCombo.setStyle(COMBO_STYLE);
        barangayCombo.setPrefWidth(300);
        
        barangayCombo.setOnAction(event -> {
            String selectedBarangay = barangayCombo.getValue();
            deliveryFee = getDeliveryFeeByBarangay(selectedBarangay);

            deliveryLabel.setText("Delivery Fee: ₱" + String.format("%.2f", deliveryFee));
            double newTotal = subtotal + deliveryFee;
            totalLabel.setText("Total: ₱" + String.format("%.2f", newTotal));
            totalPrice = newTotal; // Update global total
        });
        
        Label streetLabel = new Label("Street Address");
        streetLabel.setStyle(LABEL_STYLE);
        
        TextField streetField = new TextField();
        streetField.setPromptText("Street Address");
        streetField.setStyle(FIELD_STYLE);
        streetField.setPrefWidth(300);
     
        Label contactLabel = new Label("Contact Number");
        contactLabel.setStyle(LABEL_STYLE);
        
        // Contact number TextField
        TextField contactNumberField = new TextField();
        contactNumberField.setPromptText("Contact Number");
        contactNumberField.setStyle(FIELD_STYLE);
        contactNumberField.setPrefWidth(300);

        // Address type selection
        Label typeLabel = new Label("Address Type");
        typeLabel.setStyle(LABEL_STYLE);
        
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("HOME", "WORK", "OTHER");
        typeCombo.getSelectionModel().selectFirst();
        typeCombo.setPromptText("Address Type");
        typeCombo.setStyle(COMBO_STYLE);
        typeCombo.setPrefWidth(300);

        // Default address toggle
        CheckBox defaultCheck = new CheckBox("Set as default address");
        defaultCheck.setStyle("-fx-font-size: 13px; -fx-text-fill: black;");


       // Address list display (ListView)
        ListView<Address> addressListView = new ListView<>();
        addressListView.setItems(getCustomerAddresses(customerId)); // Load customer addresses

      Button saveButton = new Button("Save Address");
        saveButton.setStyle(SECONDARY_BUTTON_STYLE);
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
            
    // Save address to database
        addressId = saveAddressToDatabase(
            customerId,
            streetField.getText().trim(),
            barangayCombo.getSelectionModel().getSelectedItem(),
            typeCombo.getValue(),
            defaultCheck.isSelected(),
            contactNumberField.getText().trim(),
            userId
        );

        if (addressId != -1) {
            // Create the new address object BEFORE clearing fields
            Address newAddress = new Address(
                addressId,
                streetField.getText().trim(),
                barangayCombo.getSelectionModel().getSelectedItem(),
                typeCombo.getValue(),
                defaultCheck.isSelected(),
                contactNumberField.getText().trim()
            );

            // Clear fields
            streetField.clear();
            barangayCombo.getSelectionModel().clearSelection();
            contactNumberField.clear();
            defaultCheck.setSelected(false);

            // Show success message
            showAlert("Success", "Address saved successfully!");

            // OPTION 1: Refresh the entire list (more reliable)
            addressListView.setItems(getCustomerAddresses(customerId));

            // OR OPTION 2: Add just the new address (faster but make sure your getCustomerAddresses is correct)
            // addressListView.getItems().add(newAddress);
        } else {
            showAlert("Error", "Failed to save address.");
        }
    });
        // After save:
        System.out.println("After save:");
        getCustomerAddresses(customerId).forEach(addr -> System.out.println(addr));

       addressListView.setPrefHeight(100);  // Preferred height
        addressListView.setMinHeight(100);   // Minimum height
        addressListView.setMaxHeight(Double.MAX_VALUE); // Allow it to grow if needed

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

        
//       
        // Group form fields in pairs for better layout
        VBox barangayGroup = new VBox(5, barangayLabel, barangayCombo);
        VBox streetGroup = new VBox(5, streetLabel, streetField);
        VBox contactGroup = new VBox(5, contactLabel, contactNumberField);
        VBox typeGroup = new VBox(5, typeLabel, typeCombo);
        
        HBox formRow1 = new HBox(20, barangayGroup, streetGroup);
        HBox formRow2 = new HBox(20, contactGroup, typeGroup);
        
        deliverySection.getChildren().addAll(
            formRow1,
            formRow2,
            defaultCheck,
            saveButton,
           new Label("Saved Addresses:"),
            addressListView
        );
        // Make sure the VBox gives the ListView priority for extra space
VBox.setVgrow(addressListView, Priority.ALWAYS);


        deliverySection.setPrefWidth(800);
        
        // === PICKUP SECTION ===
        VBox pickupSection = new VBox(15);
        pickupSection.setVisible(false);
        pickupSection.setPrefWidth(800);
        pickupSection.setPadding(new Insets(10, 0, 0, 0));
        
        Label pickupLabel = new Label("Schedule Pickup Time");
        pickupLabel.setStyle(SUBHEADER_STYLE);

        Label dateLabel = new Label("Pickup Date");
        dateLabel.setStyle(LABEL_STYLE);
        
        DatePicker pickupDatePicker = new DatePicker();
        pickupDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // Disable everything except today
                setDisable(empty || !date.equals(LocalDate.now()));
            }
        });
        pickupDatePicker.setValue(LocalDate.now());
        pickupDatePicker.setStyle(FIELD_STYLE);
        pickupDatePicker.setPrefWidth(300);

        Label timeLabel = new Label("Pickup Time");
        timeLabel.setStyle(LABEL_STYLE);
        
        // ⏰ Replace TextField with ComboBox for time
        pickupTimeCombo.setPromptText("Select Pickup Time");
        pickupTimeCombo.setStyle(COMBO_STYLE);
        pickupTimeCombo.setPrefWidth(300);
        
        pickupDatePicker.setOnAction(ep -> {
            LocalDate selectedDate = pickupDatePicker.getValue();
            if (selectedDate != null) {
                updateTimeSlots(selectedDate);
            }
        });

        // ✅ Also call once on load to populate time slots for today
        updateTimeSlots(LocalDate.now());
        
        Label pickupAddressHeader = new Label("Pickup Location");
        pickupAddressHeader.setStyle(SUBHEADER_STYLE);
        
        Label pickupAddress = new Label("Andok's, Nasugbu, Batangas");
        pickupAddress.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

        // Track button
        Button trackButton = new Button("Track Location");
        trackButton.setStyle(SECONDARY_BUTTON_STYLE);
         trackButton.setGraphic(
        new ImageView(
                new Image(CheckOutWindow.class.getResource("/icons/map.png").toExternalForm(), 16, 16, true, true)
            )
        );


        trackButton.setOnAction(et -> {
            try {
                String coordinates = "14.072501,120.632110";
                String url = "https://www.google.com/maps/search/?api=1&query=" + URLEncoder.encode(coordinates, StandardCharsets.UTF_8);
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        // Group pickup form fields
        VBox dateGroup = new VBox(5, dateLabel, pickupDatePicker);
        VBox timeGroup = new VBox(5, timeLabel, pickupTimeCombo);
        
        HBox pickupFormRow = new HBox(20, dateGroup, timeGroup);

        VBox locationBox = new VBox(5, pickupAddressHeader, pickupAddress, trackButton);
        locationBox.setStyle("-fx-padding: 15 0 0 0;");

        pickupSection.getChildren().addAll(
            pickupLabel,
            pickupFormRow,
            locationBox
        );

        // Create a StackPane to hold both delivery and pickup sections
        StackPane addressModePane = new StackPane();
        addressModePane.getChildren().addAll(deliverySection, pickupSection);

        // Set only one visible at a time
        pickupSection.setVisible(false); // Start with delivery visible
        deliverySection.setVisible(true);

        //PAYMENT
        paymentMethodComboBox.getItems().clear();
        Label paymentMethodLabel = new Label("Payment Method");
        paymentMethodLabel.setStyle(SUBHEADER_STYLE);
        
        paymentMethodComboBox.setPromptText("Select Payment Method");
        paymentMethodComboBox.setStyle(COMBO_STYLE);
        paymentMethodComboBox.setPrefWidth(300);

        // === TOGGLE BETWEEN DELIVERY & PICKUP ===
        deliveryTypeCombo.setOnAction(ed -> {
            boolean isPickup = deliveryTypeCombo.getValue().equals("For Pick Up");
            deliverySection.setVisible(!isPickup);
            pickupSection.setVisible(isPickup);

            paymentMethodComboBox.getItems().clear();

            if (isPickup) {
                paymentMethodComboBox.getItems().addAll("Credit/Debit Card", "GCash");
                paymentMethodComboBox.setValue("Credit/Debit Card");
                showInfo("Only prepaid methods (Card or GCash) are allowed for pickup orders.");
            } else {
                paymentMethodComboBox.getItems().addAll("Cash", "Credit/Debit Card", "GCash");
                paymentMethodComboBox.setValue("Cash");
            }

            // ✅ Call the update method here
            updateDeliveryAndTotal(deliveryTypeCombo, deliveryLabel, totalLabel);
        });

        addressSection.getChildren().addAll(
            addressSectionHeader,
            new Label("Choose Delivery Method:"),
            deliveryTypeCombo,
            addressModePane
        );
        addressSection.setPrefHeight(400);
    
        //Card payment
        VBox cardSelectionContainer = new VBox(10);
        cardSelectionContainer.setPadding(new Insets(10, 0, 0, 0));
        
        VBox paymentSelectionContainer = new VBox(10);
        paymentSelectionContainer.setPadding(new Insets(20));
        paymentSelectionContainer.setStyle(SECTION_STYLE);
        

        // Order Summary Box
        VBox orderSummary = new VBox(15);
        orderSummary.setPadding(new Insets(20));
        orderSummary.setStyle(SECTION_STYLE + "-fx-background-color: #FFFCF0;"); // Light yellow background
        orderSummary.setPrefWidth(350);

        // Order summary header
        Label summaryHeader = new Label("Order Summary");
        summaryHeader.setStyle(SUBHEADER_STYLE);
        orderSummary.getChildren().add(summaryHeader);
  
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
            double itemSubtotal = itemPrice * quantity;
            subtotal += itemSubtotal;
            totalPrice += itemSubtotal;

            // Create Item Display in Checkout
            HBox itemRow = new HBox(15);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.setStyle("-fx-padding: 5 0; -fx-border-width: 0 0 1 0; -fx-border-color: #EEEEEE;");

            // Item Image
            ImageView itemImage = new ImageView(new Image(getItemImageById(itemId)));
            itemImage.setFitWidth(60);
            itemImage.setFitHeight(60);
            
            // Apply rounded corners to the image
            Rectangle clip = new Rectangle(60, 60);
            clip.setArcWidth(10);
            clip.setArcHeight(10);
            itemImage.setClip(clip);

            // Item Name & Price
            VBox namePriceBox = new VBox(5);
            Label nameLabel = new Label(itemName + " (x" + quantity + ")");
            nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            
        if (variationText != null && !variationText.equals("No variation")) {
            Label variationLabel = new Label("Option: " + variationText);
            variationLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic;");
            namePriceBox.getChildren().add(variationLabel);
        }

            Label priceLabel = new Label("₱" + String.format("%.2f", itemSubtotal));
            priceLabel.setStyle(PRICE_STYLE);
            namePriceBox.getChildren().addAll(nameLabel, priceLabel);

            // Add item row to the summary
            itemRow.getChildren().addAll(itemImage, namePriceBox);
            orderSummary.getChildren().add(itemRow);
            OrderItem orderItem = new OrderItem(itemId, quantity, itemSubtotal, itemName);
            itemsList.add(orderItem);
        }
         
        paymentMethodComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            cardSelectionContainer.getChildren().clear(); // Clear previous UI

            if ("Credit/Debit Card".equals(newValue)) {
                List<Card> savedCards = getSavedCardsForCustomer(customerId); // Your DB method

                if (!savedCards.isEmpty()) {
                    // Clear previous contents first to avoid duplicates
                    savedCardComboBox.getItems().clear();
                    cardSelectionContainer.getChildren().clear();

                    Label selectCardLabel = new Label("Select Saved Card:");
                    selectCardLabel.setStyle(LABEL_STYLE);
                    
                    savedCardComboBox.setPromptText("Choose a saved card");
                    savedCardComboBox.setStyle(COMBO_STYLE);
                    savedCardComboBox.setPrefWidth(300);

                    // Add saved cards to the combo box
                    savedCardComboBox.getItems().addAll(savedCards);

                    // Add the dummy "Add New Card" option at the end
                    Card addNewCard = new Card("➕ Add New Card", "", true);
                    savedCardComboBox.getItems().add(addNewCard);

                    cardSelectionContainer.getChildren().addAll(selectCardLabel, savedCardComboBox);
                } else {
                    // Clear previous content first
                    cardSelectionContainer.getChildren().clear();

                    Label noCardsLabel = new Label("No saved cards found.");
                    noCardsLabel.setStyle(LABEL_STYLE);
                    cardSelectionContainer.getChildren().add(noCardsLabel);

                    // Add fallback "Add New Card" button
                    Button addNewCardBtn = new Button("Add a New Card");
                    addNewCardBtn.setStyle(SECONDARY_BUTTON_STYLE);
                    addNewCardBtn.setOnAction(ev -> {
                        savedCardComboBox.setValue(new Card("➕ Add New Card", "", true));
                    });
                    cardSelectionContainer.getChildren().add(addNewCardBtn);
                }
            }
            //GCASH LOGIC
            else if ("GCash".equals(newValue)) {
                System.out.println("Switched to GCash Payment");
                
                // Add GCash icon or info here
                Label gcashInfoLabel = new Label("GCash payment will require proof of payment");
                gcashInfoLabel.setStyle(LABEL_STYLE);
                cardSelectionContainer.getChildren().add(gcashInfoLabel);
            } 
        });

        // Setup default values based on delivery type
        boolean isDelivery = deliveryTypeCombo.getValue().equals("Delivery");
        if (isDelivery) {
            deliveryFee = getDeliveryFeeByBarangay(barangayCombo.getValue());
        } else {
            deliveryFee = 0.0;
        }

        subtotalLabel.setText("Subtotal: ₱" + String.format("%.2f", subtotal));
        deliveryLabel.setText("Delivery Fee: ₱" + String.format("%.2f", deliveryFee));
        totalLabel.setText("Total: ₱" + String.format("%.2f", subtotal + deliveryFee));
        totalPrice = subtotal + deliveryFee; // Set initial total

        // Add a separator line for the totals
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #CCCCCC;");
        
        // Add spacing before the totals section
        VBox totalsSection = new VBox(8, separator, subtotalLabel, deliveryLabel, totalLabel);
        totalsSection.setPadding(new Insets(10, 0, 15, 0));
        
        Button placeOrderBtn = new Button("Place Order");
        placeOrderBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-font-size: 15px; -fx-background-radius: 5;");
        placeOrderBtn.setPrefWidth(Double.MAX_VALUE); // Make button full width
        
        // Add a hover effect for the button
        placeOrderBtn.setOnMouseEntered(ep -> 
            placeOrderBtn.setStyle("-fx-background-color: #B71C1C; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-font-size: 15px; -fx-background-radius: 5;"));
        placeOrderBtn.setOnMouseExited(ep -> 
            placeOrderBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 25; -fx-font-size: 15px; -fx-background-radius: 5;"));
        
        placeOrderBtn.setOnAction(ep -> {
            // Determine the effective addressId to use based on delivery type
            int effectiveAddressId = deliveryTypeCombo.getValue().equals("For Pick Up") ? 8 : addressId;

            // If it's not a pickup, ensure an address has been selected
            if (!deliveryTypeCombo.getValue().equals("For Pick Up") && addressId == -1) {
                showAlert("Error", "Please select an address before placing the order.");
                totalPrice = subtotal + 49.00;  // Adding delivery fee
                return;
            }
            
            if (deliveryTypeCombo.getValue().equals("For Pick Up")) {
                String selectedTime = pickupTimeCombo.getValue();

                if (selectedTime == null || selectedTime.equals("Select Pickup Time") || selectedTime.equals("No available time slots")) {
                    showAlert("Missing Pickup Time", "Please pick a pickup time first.");
                    return;
                }
            }
         
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setVisible(false); // Hide by default

            VBox loadingOverlay = new VBox(loadingIndicator);
            loadingOverlay.setStyle("-fx-background-color: rgba(0,0,0,0.5);");
            loadingOverlay.setAlignment(Pos.CENTER);
            loadingOverlay.setVisible(false);

            String paymentMethod = paymentMethodComboBox.getSelectionModel().getSelectedItem() != null 
                                    ? paymentMethodComboBox.getSelectionModel().getSelectedItem() 
                                    : "Cash"; // Default to "COD" if nothing is selected

            boolean isPickup = deliveryTypeCombo.getValue().equals("For Pick Up");
            String orderType = isPickup ? "Pick Up" : "Delivery";
            String pickupTime = isPickup ? pickupTimeCombo.getValue() : null;

            // Card selection validation
            if ("Credit/Debit Card".equals(paymentMethod)) {
                Card selectedCard = savedCardComboBox.getValue();
                if (selectedCard == null) {
                    showAlert("Error", "Please select a card for payment.");
                    return;
                }

                if (selectedCard.isDummyCard()) {
                    // 👇 Delay order saving until card payment is done
                    CardPaymentForm cardForm = new CardPaymentForm();
                    cardForm.setOnPaymentSuccess(() -> {
                       
                        int orderId = saveOrderToDatabase(customerId, effectiveAddressId, totalPrice, paymentMethod, orderType, pickupTime, userId);

                        if (orderId != -1) {
                            saveOrderItemsToDatabase(orderId, userId);
                            CartSession.clearCart();
                            CartSession.notifyCartChanged();
                            ((Stage) placeOrderBtn.getScene().getWindow()).close();
                            // 👉 Load full order from DB
                            Order order = OrderFetcher.getOrderById(orderId);
                            if (order != null) {
                                OrderSummary summaryWindow = new OrderSummary();
                                summaryWindow.show(order, userId);
                            }
                        } else {
                            showAlert("Error", "Failed to place the order.");
                        }
                    });
                    cardForm.showCardPaymentForm(customerId, userId);
                    return; // Don't continue until payment is complete
                } else {
                    // Saved card: confirm then process
                    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmAlert.setTitle("Confirm Payment");
                    confirmAlert.setHeaderText("Use saved card ending in " + getLast4Digits(selectedCard.getCardNumber()) + "?");
                    confirmAlert.setContentText("Do you want to proceed with this card?");

                    // Apply custom styling to alert
                    DialogPane dialogPane = confirmAlert.getDialogPane();
                    dialogPane.setStyle("-fx-background-color: white;");
                    dialogPane.getStyleClass().add("modern-alert");
                    dialogPane.getButtonTypes().stream()
                        .map(dialogPane::lookupButton)
                        .forEach(button -> button.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white;"));

                    Optional<ButtonType> result = confirmAlert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Simulate payment success
                        CardPaymentForm cardForm = new CardPaymentForm();
                        cardForm.showPaymentSuccessAlert(
                            selectedCard.getCardholderName(),
                            selectedCard.getCardNumber()
                        );

                        // ✅ Now place the order
                        int orderId = saveOrderToDatabase(customerId, effectiveAddressId, totalPrice, paymentMethod, orderType, pickupTime, userId);
                        if (orderId != -1) {
                            saveOrderItemsToDatabase(orderId, userId);
                            showAlert("Success", "Your order has been placed successfully!");
                            CartSession.clearCart();
                            CartSession.notifyCartChanged();
                            ((Stage) placeOrderBtn.getScene().getWindow()).close();
                            // 👉 Load full order from DB
                            Order order = OrderFetcher.getOrderById(orderId);
                            if (order != null) {
                                OrderSummary summaryWindow = new OrderSummary();
                                summaryWindow.show(order, userId);
                            }
                        } else {
                            showAlert("Error", "Failed to place the order.");
                        }
                    }
                    return;
                }
            }
             
            if ("GCash".equals(paymentMethod)) {
                GCashPaymentForm gcashForm = new GCashPaymentForm(customerId, addressId, totalPrice);
                // Save the order with effective addressId for pickup or selected address for delivery
                int orderId = saveOrderToDatabase(customerId, effectiveAddressId, totalPrice, paymentMethod, orderType, pickupTime, userId);
                
                gcashForm.setOnPaymentSuccess(() -> {
                    String proofPath = gcashForm.getProofFilePath(); // ← get the proof path from GCash form
            
                    if (orderId != -1) {
                        gcashForm.saveProofOfPayment(orderId, proofPath);
                        System.out.println("Proof path: " + proofPath);

                        saveOrderItemsToDatabase(orderId, userId);
                      
                        CartSession.clearCart();
                        CartSession.notifyCartChanged();
                        ((Stage) placeOrderBtn.getScene().getWindow()).close();
                   
                        // Use PauseTransition to add delay before showing the alert
                        PauseTransition pause = new PauseTransition(Duration.seconds(1)); // Adjust delay if needed
                        pause.setOnFinished(event -> {
                            gcashForm.showVerificationPendingAlert(orderId);  // Show the alert after the delay
                            // 👉 Load full order from DB
                            Order order = OrderFetcher.getOrderById(orderId);
                            if (order != null) {
                                OrderSummary summaryWindow = new OrderSummary();
                                summaryWindow.show(order, userId);
                            }
                        });
                        pause.play();
                    } else {
                        showAlert("Error", "Failed to place the order.");
                    }
                });
                gcashForm.showGCashPaymentForm();
                return;
            }

            // COD: direct order placement
            int orderId = saveOrderToDatabase(customerId, effectiveAddressId, totalPrice, paymentMethod, orderType, pickupTime, userId);

            if (orderId != -1) {
                saveOrderItemsToDatabase(orderId, userId);
                
                // Create a fancy success alert
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Order Placed");
                successAlert.setHeaderText("Your order has been placed successfully!");
                successAlert.setContentText("Order #" + orderId + " has been submitted. Thank you for your purchase!");
                
                // Apply custom styling to success alert
                DialogPane dialogPane = successAlert.getDialogPane();
                dialogPane.setStyle("-fx-background-color: white;");
                dialogPane.getStyleClass().add("modern-alert");
                dialogPane.getButtonTypes().stream()
                    .map(dialogPane::lookupButton)
                    .forEach(button -> button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"));
                
                successAlert.showAndWait();
                
                CartSession.clearCart();
                CartSession.notifyCartChanged();
                ((Stage) placeOrderBtn.getScene().getWindow()).close();
                // 👉 Load full order from DB
                Order order = OrderFetcher.getOrderById(orderId);
                if (order != null) {
                    OrderSummary summaryWindow = new OrderSummary();
                    summaryWindow.show(order, userId);
                }
            } else {
                showAlert("Error", "Failed to place the order.");
            }
        });

        // Close Button
        Button closeBtn = new Button("Cancel");
        closeBtn.setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #333333; -fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-padding: 10 20;");
        closeBtn.setPrefWidth(Double.MAX_VALUE); // Make button full width
        
        // Add a hover effect
        closeBtn.setOnMouseEntered(ec -> 
            closeBtn.setStyle("-fx-background-color: #EEEEEE; -fx-text-fill: #333333; -fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-padding: 10 20;"));
        closeBtn.setOnMouseExited(ec -> 
            closeBtn.setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #333333; -fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-padding: 10 20;"));
            
        closeBtn.setOnAction(ec -> checkoutStage.close());

        // Button container for order actions
        VBox buttonsBox = new VBox(10, placeOrderBtn, closeBtn);
        buttonsBox.setPadding(new Insets(5, 0, 0, 0));

        orderSummary.getChildren().addAll(totalsSection, buttonsBox);

        // Payment section
        VBox paymentSection = new VBox(15);
        paymentSection.setPadding(new Insets(20));
        paymentSection.setStyle(SECTION_STYLE);
        
        paymentSection.getChildren().addAll(
            paymentMethodLabel,
            paymentMethodComboBox,
            cardSelectionContainer
        );

        
        // Step 5: Combine all into Main Layout (VBox)
        VBox mainLayout = new VBox(20);
        mainLayout.setPadding(new Insets(20));
        mainLayout.setStyle(MAIN_BACKGROUND);

        // Title
        Label title = new Label("Checkout");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;");

      ImageView logoView = new ImageView(
            new Image(CheckOutWindow.class.getResource("/icons/andoksLogo.png").toExternalForm())
        );
         logoView.setFitHeight(40);
        logoView.setFitWidth(120);
        logoView.setPreserveRatio(true);

        // Create header with logo and title
        HBox header = new HBox(30, logoView, title);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 10, 0));
        header.setStyle("-fx-border-width: 0 0 1 0; -fx-border-color: #EEEEEE;");

        // Left section: Address + Payment
        VBox leftColumn = new VBox(20, addressSection, paymentSection);

        ScrollPane scrollPane = new ScrollPane(leftColumn);
        scrollPane.setFitToWidth(true); // Makes it expand to available width
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");


        
        // Right section: Order Summary
        VBox rightColumn = new VBox(orderSummary);

        // Create a split layout with address on left and summary on right
        HBox contentLayout = new HBox(20, scrollPane, rightColumn);
        contentLayout.setAlignment(Pos.TOP_CENTER);

        mainLayout.getChildren().addAll(header, progressSection, contentLayout);

        // Apply a modern flat design
        Scene scene = new Scene(mainLayout, 1530, 800);
        
        // Add external CSS if needed
        // scene.getStylesheets().add(getClass().getResource("/styles/checkout.css").toExternalForm());
        
        checkoutStage.setScene(scene);
        checkoutStage.setTitle("Checkout - Andok's Online Ordering");
        checkoutStage.showAndWait();
    }
                
   
   public static int getCustomerIdFromUserId(int userId) {
    try (Connection conn = Database.connect()) {
        String query = "SELECT customer_id FROM customers WHERE user_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userId);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("customer_id");
        } else {
            System.err.println("❌ No customer found for user ID: " + userId);
            return -1;
        }
    } catch (SQLException e) {
        System.err.println("❌ Error getting customerId: " + e.getMessage());
        return -1;
    }
}

  

   
    private static void updateTimeSlots(LocalDate selectedDate) {
      pickupTimeCombo.getItems().clear();

      // Time slots in 24-hour (military) format
      String[] timeSlots = {
          "08:00 - 09:00", "09:00 - 10:00", "10:00 - 11:00", "11:00 - 12:00",
          "12:00 - 13:00", "13:00 - 14:00", "14:00 - 15:00", "15:00 - 16:00",
          "16:00 - 17:00", "17:00 - 18:00", "18:00 - 19:00", "19:00 - 20:00"
      };

      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm"); // 24-hour

      if (selectedDate.equals(LocalDate.now())) {
          LocalTime now = LocalTime.now();

          for (String slot : timeSlots) {
              String startTimeStr = slot.split(" - ")[0]; // e.g. "15:00"

              try {
                  LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
                  if (startTime.isAfter(now)) {
                      pickupTimeCombo.getItems().add(slot);
                  }
              } catch (DateTimeParseException e) {
                  System.err.println("Failed to parse time slot: " + startTimeStr);
              }
          }

          if (pickupTimeCombo.getItems().isEmpty()) {
              pickupTimeCombo.getItems().add("No available time slots");
          }

      } else {
          pickupTimeCombo.getItems().add("No available slots for future dates");
      }

      pickupTimeCombo.getSelectionModel().clearSelection();
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
    private static void updateDeliveryAndTotal(ComboBox<String> deliveryTypeCombo, Label deliveryLabel, Label totalLabel) {
    boolean isDelivery = "Delivery".equals(deliveryTypeCombo.getValue());
    double fee = isDelivery ? 49.0 : 0.0;
    deliveryLabel.setText("Delivery Fee: ₱" + String.format("%.2f", fee));
    totalLabel.setText("Total: ₱" + String.format("%.2f", subtotal + fee));
    totalPrice = subtotal + fee;
}


   
    // Method to save order items to the database

    public static void saveOrderItemsToDatabase(int orderId, int userId) {
        Map<Integer, Integer> cartItems = CartSession.getCartItems();
        Map<Integer, String> variations = CartSession.getVariations();
        Map<Integer, String> instructions = CartSession.getInstructions();

        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            int itemId = entry.getKey();
            int quantity = entry.getValue();
             subtotal = getItemPriceById(itemId) * quantity;

            String variationText = variations.getOrDefault(itemId, null);
            String instructionsText = instructions.getOrDefault(itemId, null);

            // ✅ Open connection inside try-with-resources
            try (Connection conn = Database.connect()) {  
                String query = "INSERT INTO order_items (order_id, item_id, quantity, subtotal, variation, instructions, last_modified_by) VALUES (?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, itemId);
                    stmt.setInt(3, quantity);
                    stmt.setDouble(4, subtotal);
                    stmt.setString(5, variationText);
                    stmt.setString(6, instructionsText);
                    stmt.setInt(7, userId);
                    stmt.executeUpdate();
                }
            } catch (SQLException e) {
                System.out.println("❌ Error saving order item: " + e.getMessage());
            }
        }
    }


    
public static int saveOrderToDatabase(int customerId, int addressId, double totalPrice, String paymentMethod, String orderType, String pickupTime, int userId) {
    try (Connection conn = Database.connect()) {

        // Determine payment_status based on paymentMethod
        String paymentStatus = switch (paymentMethod) {
            case "GCash" -> "Pending Verification";
            case "Credit/Debit Card" -> "Paid";
            default -> "Pending Payment";
        };

        // Insert into orders table
        String sql = "INSERT INTO orders (customer_id, address_id, total_price, payment_method, payment_status, status, order_type, pickup_time, last_modified_by) " +
                     "VALUES (?, ?, ?, ?, ?, 'Pending', ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        pstmt.setInt(1, customerId);
        pstmt.setInt(2, addressId);
        pstmt.setDouble(3, totalPrice);
        pstmt.setString(4, paymentMethod);
        pstmt.setString(5, paymentStatus);
        pstmt.setString(6, orderType);
        if ("Pick Up".equalsIgnoreCase(orderType)) {
            pstmt.setString(7, pickupTime);
        } else {
            pstmt.setNull(7, java.sql.Types.NULL);
        }
        pstmt.setInt(8, userId);

        int affectedRows = pstmt.executeUpdate();
        if (affectedRows == 0) {
            return -1; // Order failed
        }

        ResultSet rs = pstmt.getGeneratedKeys();
        if (!rs.next()) return -1;
        int orderId = rs.getInt(1);

        // ✅ Fetch customer name & email
        String customerQuery = "SELECT name, email FROM customers WHERE customer_id = ?";
        String customerName = "", email = "";
        try (PreparedStatement customerStmt = conn.prepareStatement(customerQuery)) {
            customerStmt.setInt(1, customerId);
            try (ResultSet customerRs = customerStmt.executeQuery()) {
                if (customerRs.next()) {
                    customerName = customerRs.getString("name");
                    email = customerRs.getString("email");
                }
            }
        }

       // ✅ Fetch address info (if delivery)
        String addressText = "N/A";
        if (!"Pick Up".equalsIgnoreCase(orderType)) {
            String addressQuery = """
                SELECT a.street, b.barangay_name
                FROM addresses a
                JOIN barangay b ON a.barangay_id = b.barangay_id
                WHERE a.address_id = ?
                """;

            try (PreparedStatement addrStmt = conn.prepareStatement(addressQuery)) {
                addrStmt.setInt(1, addressId);
                try (ResultSet addrRs = addrStmt.executeQuery()) {
                    if (addrRs.next()) {
                        String street = addrRs.getString("street");
                        String barangayName = addrRs.getString("barangay_name");
                        addressText = street + ", " + barangayName;
                    }
                }
            }
        }

                // ✅ Fetch order items
        StringBuilder orderItemsText = new StringBuilder();
        String itemsQuery = """
            SELECT m.name, oi.quantity, oi.variation, oi.instructions, oi.subtotal
            FROM order_items oi
            JOIN menu_items m ON oi.item_id = m.item_id
            WHERE oi.order_id = ?
            """;

try (PreparedStatement itemsStmt = conn.prepareStatement(itemsQuery)) {
    itemsStmt.setInt(1, orderId);
    try (ResultSet itemsRs = itemsStmt.executeQuery()) {
        while (itemsRs.next()) {
            String itemName = itemsRs.getString("name");
            int quantity = itemsRs.getInt("quantity");
            String variation = itemsRs.getString("variation");
            String instructions = itemsRs.getString("instructions");
             subtotal = itemsRs.getDouble("subtotal");

            orderItemsText.append("- ")
                .append(itemName)
                .append(" x").append(quantity);

            if (variation != null && !variation.isBlank()) {
                orderItemsText.append(" (").append(variation).append(")");
            }

            if (instructions != null && !instructions.isBlank()) {
                orderItemsText.append(" [").append(instructions).append("]");
            }

            orderItemsText.append(" - ₱").append(String.format("%.2f", subtotal)).append("\n");
        }
    }
}
        // ✅ Build notification & email content
        String subject = "🛒 You placed an order! Order #" + orderId;
        String message = """
            Hi %s,

            Thank you for placing an order with Andok's!
            Your order #%d has been received. Here are the details:

            %s
            Total Price: ₱%.2f
            Payment Method: %s
            Order Type: %s
            %s

            We'll process your order shortly.

            — Andok's
            """.formatted(
                customerName,
                orderId,
                orderItemsText.toString(),
                totalPrice,
                paymentMethod,
                orderType,
                "Delivery Address: " + addressText
            );

        // ✅ Insert notification
        String callNotifProc = "{CALL InsertNotification(?, ?, ?, ?)}";
        try (CallableStatement notifStmt = conn.prepareCall(callNotifProc)) {
            notifStmt.setInt(1, customerId);
            notifStmt.setString(2, message);
            notifStmt.setString(3, "order_placed");
            notifStmt.setInt(4, userId); // User placing the order
            notifStmt.executeUpdate();
        }


        // ✅ Send email
        SendEmail.sendEmail(email, subject, message);
        System.out.println("📧 Order email sent to " + email);

        return orderId;

    } catch (SQLException ex) {
        System.err.println("❌ Error saving order: " + ex.getMessage());
        return -1;
    }   catch (MessagingException ex) {
            Logger.getLogger(CheckOutWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    return -1;
}

private static double getDeliveryFeeByBarangay(String barangayName) {
    double fee = 0.0;
    String query = "SELECT delivery_fee FROM barangay WHERE barangay_name = ?";
    
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        
        stmt.setString(1, barangayName);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            fee = rs.getDouble("delivery_fee");
        }
        
    } catch (SQLException e) {
        e.printStackTrace(); // Handle errors properly
    }
    return fee;
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
           String sql = "SELECT barangay_name FROM Barangay WHERE barangay_id != 36";
 // Assuming barangays table
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
// Creates a styled alert dialog
    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Apply custom styling to the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        
        // Style buttons
        dialogPane.getButtonTypes().stream()
            .map(dialogPane::lookupButton)
            .forEach(button -> {
                if (title.toLowerCase().contains("error")) {
                    button.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white;");
                } else if (title.toLowerCase().contains("success")) {
                    button.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                } else {
                    button.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #333333;");
                }
            });
        
        alert.showAndWait();
    }
    
        
   // Shows an informational popup
    private static void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Apply custom styling
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white;");
        
        // Style buttons
        dialogPane.getButtonTypes().stream()
            .map(dialogPane::lookupButton)
            .forEach(button -> button.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #333333;"));
        
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
     String imagePath = CheckOutWindow.class.getResource("/icons/andoksBag.png").toExternalForm();

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
                                                String barangay, String addressType, boolean isDefault, String contactNumber, int userId) {
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

              String updateAddressQuery = "UPDATE addresses SET barangay_id = ?, address_type = ?, is_default = ?, contact_number = ?, last_modified_by = ? WHERE address_id = ?";
              PreparedStatement pstmtUpdate = conn.prepareStatement(updateAddressQuery);
              pstmtUpdate.setInt(1, barangayId);
              pstmtUpdate.setString(2, addressType);
              pstmtUpdate.setBoolean(3, isDefault);
              pstmtUpdate.setString(4, contactNumber);
              pstmtUpdate.setInt(5, userId);
              pstmtUpdate.setInt(6, existingAddressId);

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
              String insertAddressQuery = "INSERT INTO addresses (customer_id, street, barangay_id, address_type, is_default, contact_number, last_modified_by) " +
                                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
             PreparedStatement pstmtInsert = conn.prepareStatement(insertAddressQuery, Statement.RETURN_GENERATED_KEYS);

              pstmtInsert.setInt(1, customerId);
              pstmtInsert.setString(2, street);
              pstmtInsert.setInt(3, barangayId);
              pstmtInsert.setString(4, addressType);
              pstmtInsert.setBoolean(5, isDefault);
              pstmtInsert.setString(6, contactNumber);
              pstmtInsert.setInt(7, userId);

              int affectedRows = pstmtInsert.executeUpdate();
                ResultSet rsInsert = pstmtInsert.getGeneratedKeys();
              if (rsInsert.next()) {
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

