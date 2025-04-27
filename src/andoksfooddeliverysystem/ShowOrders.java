package andoksfooddeliverysystem;
import static andoksfooddeliverysystem.Database.connect;
import java.util.List;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import java.sql.Connection;
import javafx.stage.Modality;


import java.sql.PreparedStatement;

import java.sql.SQLException;
import java.util.stream.Collectors;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javax.mail.MessagingException;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Separator;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.scene.control.ListCell;





public class ShowOrders {
    private VBox root;
    private List<Order> allOrders; // store original list
    private VBox ordersContainer;
    private int adminId; // the logged-in admin's ID
    
    // Color constants for the theme
    private static final String PRIMARY_COLOR = "#E53935"; // Red
    private static final String SECONDARY_COLOR = "#FFEB3B"; // Yellow
    private static final String BACKGROUND_COLOR = "#FFFFFF"; // White
    private static final String ACCENT_COLOR = "#FFD600"; // Dark Yellow
    private static final String TEXT_COLOR = "#212121"; // Almost black
    private static final String LIGHT_GRAY = "#F5F5F5"; // Light gray for borders

    // Status colors
    private static final Color STATUS_PENDING = Color.web("#E53935"); // Red
    private static final Color STATUS_OUT_FOR_DELIVERY = Color.web("#FFC107"); // Amber
    private static final Color STATUS_COMPLETED = Color.web("#4CAF50"); // Green
    private static final Color STATUS_CANCELLED = Color.web("#9E9E9E"); // Gray
    private static final Color STATUS_READY_PICKUP = Color.web("#2196F3"); // Blue
    
    public ShowOrders(int adminId) {
        this.adminId = adminId;
        root = new VBox(10);
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");
 
        allOrders = OrderFetcher.fetchOrders(); // Fetch all orders once

        // Create header with title
        Label headerLabel = new Label("Order Management");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        // --- UI CONTROLS ---
        TextField searchField = new TextField();
        searchField.setPromptText("Search orders...");
        searchField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");
        
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Out for delivery", "Completed", "Cancelled", "Ready for Pick-up");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-radius: 5;");

        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.getItems().addAll("Order # Ascending", "Order # Descending", "Most Recent", "Oldest");
        sortBy.setValue("Order # Ascending");
        sortBy.setStyle("-fx-background-radius: 5;");
        
        HBox controlsWrapper = new HBox(10);
        controlsWrapper.setAlignment(Pos.CENTER_LEFT);
        controlsWrapper.setPadding(new Insets(15));
        controlsWrapper.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 5;");
        
        Label searchLabel = new Label("Search:");
        searchLabel.setStyle("-fx-font-weight: bold;");
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-weight: bold;");
        Label sortLabel = new Label("Sort by:");
        sortLabel.setStyle("-fx-font-weight: bold;");
        
        controlsWrapper.getChildren().addAll(
            searchLabel, searchField,
            filterLabel, statusFilter,
            sortLabel, sortBy
        );

        // Create the main container with scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); 
        scrollPane.setStyle("-fx-background: " + BACKGROUND_COLOR + "; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
             
        // Create a VBox to hold all orders
        ordersContainer = new VBox(15); // Increased spacing between orders
        ordersContainer.setPadding(new Insets(15)); 
        ordersContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        ordersContainer.getChildren().clear(); // Prevent duplicates if refreshing

        // Initial render of orders
        for (Order order : allOrders) {
            createOrderBox(order);
        }
        
        // Set the orders container as the content of the scroll pane
        scrollPane.setContent(ordersContainer);

        // --- LOGIC: Filtering/Sorting/Search ---
        Runnable updateList = () -> {
            String search = searchField.getText().toLowerCase();
            String selectedStatus = statusFilter.getValue();
            String selectedSort = sortBy.getValue();

            List<Order> filtered = allOrders.stream()
                .filter(order -> {
                    boolean matchesStatus = selectedStatus.equals("All") || 
                        order.getOrderStatus().equalsIgnoreCase(selectedStatus);

                    boolean matchesSearch = search.isEmpty() || (
                        String.valueOf(order.getOrderId()).contains(search) ||
                        order.getStreet().toLowerCase().contains(search) ||
                        order.getBarangay().toLowerCase().contains(search) ||
                        order.getPaymentMethod().toLowerCase().contains(search) ||
                        order.getPaymentStatus().toLowerCase().contains(search) ||
                        order.getOrderStatus().toLowerCase().contains(search) ||
                        order.getCustomerName().toLowerCase().contains(search)
                    );

                    return matchesStatus && matchesSearch;
                })
                .sorted((o1, o2) -> {
                    switch (selectedSort) {
                        case "Order # Ascending":
                            return Integer.compare(o1.getOrderId(), o2.getOrderId());
                        case "Order # Descending":
                            return Integer.compare(o2.getOrderId(), o1.getOrderId());
                        case "Most Recent":
                             return o2.getOrderDate().compareTo(o1.getOrderDate());
                        case "Oldest":
                             return o1.getOrderDate().compareTo(o2.getOrderDate());
                        default:
                            return 0;
                    }
                })
                .collect(Collectors.toList());

            refreshOrders(filtered);
        };

        // --- ADD LISTENERS ---
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateList.run());
        statusFilter.setOnAction(e -> updateList.run());
        sortBy.setOnAction(e -> updateList.run());

        // Initial load
        updateList.run();

        // Add all elements to the root layout
        root.getChildren().addAll(headerLabel, controlsWrapper, scrollPane);
        VBox.setMargin(headerLabel, new Insets(15, 0, 0, 15));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }
    
    private void refreshOrders(List<Order> orders) {
        ordersContainer.getChildren().clear();

        for (Order order : orders) {
            createOrderBox(order);
        }
    }
    
    /**
     * Helper method to create an order box to avoid duplicate code
     */
    private void createOrderBox(Order order) {
        // Get the status
        String status = order.getOrderStatus().toLowerCase();
        String orderType = order.getOrderType().toLowerCase();
        
        // Create the main order box
        VBox orderBox = new VBox(15); // Increased spacing between elements
        orderBox.setPadding(new Insets(20));
        orderBox.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";" +
                         "-fx-border-color: " + LIGHT_GRAY + ";" +
                         "-fx-border-width: 1;" +
                         "-fx-border-radius: 8;" +
                         "-fx-background-radius: 8;");

        // ========== MAIN CONTENT ==========
        HBox topSection = new HBox(20);
        topSection.setAlignment(Pos.CENTER_LEFT);
        
        // Create order number section with big prominent number
        VBox orderNumberSection = new VBox(5);
        orderNumberSection.setAlignment(Pos.CENTER);
        orderNumberSection.setMinWidth(90);
        orderNumberSection.setStyle("-fx-background-color: " + PRIMARY_COLOR + ";" +
                                  "-fx-background-radius: 5;" +
                                  "-fx-padding: 10;");
        
        Label orderNumLabel = new Label("#" + order.getOrderId());
        orderNumLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        Label orderText = new Label("ORDER");
        orderText.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
        
        orderNumberSection.getChildren().addAll(orderNumLabel, orderText);
        
        // Order details section
        VBox orderDetailsSection = new VBox(8);
        orderDetailsSection.setStyle("-fx-padding: 0 0 0 10;");
        
        // Customer name section
        Label nameLabel = new Label(order.getCustomerName());
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Order date and total with horizontal layout
        HBox dateAndTotal = new HBox(20);
        
        Label dateLabel = new Label(order.getOrderDate());
        dateLabel.setStyle("-fx-font-size: 14px;");
        
        Label totalLabel = new Label(String.format("₱%.2f", order.getTotalPrice()));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        dateAndTotal.getChildren().addAll(dateLabel, totalLabel);
        
        // Status display with color circle
        HBox statusDisplay = new HBox(8);
        statusDisplay.setAlignment(Pos.CENTER_LEFT);
        
        Circle statusCircle = new Circle(6);
        Label statusLabel = new Label(capitalize(status));
        statusLabel.setStyle("-fx-font-weight: bold;");
        
        // Set status circle color
        switch (status) {
            case "pending":
                statusCircle.setFill(STATUS_PENDING);
                statusLabel.setTextFill(STATUS_PENDING);
                break;
            case "out for delivery":
                statusCircle.setFill(STATUS_OUT_FOR_DELIVERY);
                statusLabel.setTextFill(STATUS_OUT_FOR_DELIVERY);
                break;
            case "completed":
                statusCircle.setFill(STATUS_COMPLETED);
                statusLabel.setTextFill(STATUS_COMPLETED);
                break;
            case "cancelled":
                statusCircle.setFill(STATUS_CANCELLED);
                statusLabel.setTextFill(STATUS_CANCELLED);
                break;
            case "ready for pick-up":
                statusCircle.setFill(STATUS_READY_PICKUP);
                statusLabel.setTextFill(STATUS_READY_PICKUP);
                break;
            default:
                statusCircle.setFill(Color.BLACK);
                statusLabel.setTextFill(Color.BLACK);
        }
        
        statusDisplay.getChildren().addAll(statusCircle, statusLabel);
        
        orderDetailsSection.getChildren().addAll(nameLabel, dateAndTotal, statusDisplay);
        
        // Add the main sections to top section
        topSection.getChildren().addAll(orderNumberSection, orderDetailsSection);
        
        // Create info cards section (for Payment, Contact, Address)
        HBox infoCards = new HBox(15);
        infoCards.setAlignment(Pos.CENTER_LEFT);
        
        // Payment info card
        VBox paymentCard = createInfoCard("Payment", new String[]{
            "Method: " + order.getPaymentMethod(),
            "Status: " + order.getPaymentStatus()
        });
        
        // Assuming the labels are at index 1 and so on inside the card's children
        Label paymentStatusLabel = (Label) paymentCard.getChildren().get(1); // "Status: [status]"

        // Contact info card
        VBox contactCard = createInfoCard("Contact", new String[]{
            order.getContactNumber()
        });
        
        // Address/Order Type info card
        String[] addressInfo;
        if ("delivery".equals(orderType)) {
            addressInfo = new String[]{
                "Type: Delivery",
                "Street: " + order.getStreet(),
                "Barangay: " + order.getBarangay()
            };
        } else {
            addressInfo = new String[]{
                "Type: Pick-up",
                "Time: " + order.getPickupTime()
            };
        }
        VBox addressCard = createInfoCard("Address", addressInfo);
        
        infoCards.getChildren().addAll(paymentCard, contactCard, addressCard);
        
        // Action buttons section
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        
        // Create the expandable details section (initially hidden)
        VBox orderDetailsBox = new VBox(15);
        orderDetailsBox.setVisible(false);
        orderDetailsBox.setPadding(new Insets(15, 0, 0, 0));
        orderDetailsBox.setStyle("-fx-background-color: " + LIGHT_GRAY + ";" +
                               "-fx-background-radius: 5;" +
                               "-fx-padding: 15;");
        orderDetailsBox.setVisible(false);
        orderDetailsBox.setManaged(false);  // Add this line
        
        // Add order items to details
        Label itemsHeader = new Label("Order Items");
        itemsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        orderDetailsBox.getChildren().add(itemsHeader);
        
        
        // Add all order items
        for (DetailedOrderItem item : order.getOrderItems()) {
            HBox itemBox = new HBox(15);
            itemBox.setPadding(new Insets(8, 0, 8, 0));
            
            String itemName = (item.getItemName() == null || item.getItemName().isEmpty())
                ? "Unknown Item" 
                : item.getItemName();

            // Quantity circle with number
            StackPane quantityCircle = new StackPane();
            Circle circle = new Circle(15);
            circle.setFill(Color.web(SECONDARY_COLOR));
            
            Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
            quantityLabel.setStyle("-fx-font-weight: bold;");
            
            quantityCircle.getChildren().addAll(circle, quantityLabel);
            
            // Item details
            VBox itemDetailsBox = new VBox(3);
            
            Label itemNameLabel = new Label(itemName);
            itemNameLabel.setStyle("-fx-font-weight: bold;");
            
            String variationText = (item.getVariation() == null || item.getVariation().isEmpty()) 
                ? "No variation" 
                : item.getVariation();
                
            Label variantLabel = new Label("Variant: " + variationText);
            variantLabel.setStyle("-fx-font-size: 12px;");
            
            String instructionsText = (item.getInstructions() == null || item.getInstructions().isEmpty())
                ? "No special instructions"
                : item.getInstructions();
                
            Label instructionsLabel = new Label("Notes: " + instructionsText);
            instructionsLabel.setStyle("-fx-font-size: 12px;");
            
            itemDetailsBox.getChildren().addAll(itemNameLabel, variantLabel, instructionsLabel);
            
            // Item price
            Label priceLabel = new Label(String.format("₱%.2f", item.getSubtotal()));
            priceLabel.setStyle("-fx-font-weight: bold;");
            
            // Add elements with proper spacing
            itemBox.getChildren().addAll(quantityCircle, itemDetailsBox);
            
            // Use region to push price to the right
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            itemBox.getChildren().addAll(spacer, priceLabel);
            
            // Add a separator after each item except the last one
            orderDetailsBox.getChildren().add(itemBox);
            
            if (order.getOrderItems().indexOf(item) < order.getOrderItems().size() - 1) {
                Separator separator = new Separator();
                separator.setStyle("-fx-opacity: 0.3;");
                orderDetailsBox.getChildren().add(separator);
            }
        }
        
        // Button to show/hide details
        Button expandButton = new Button("▼ Show Details");
        expandButton.setStyle("-fx-background-color: transparent; -fx-text-fill: " + PRIMARY_COLOR + 
                            "; -fx-font-weight: bold; -fx-cursor: hand;");
                            
        expandButton.setOnAction(e -> {
            boolean isExpanded = !orderDetailsBox.isVisible();
             orderDetailsBox.setVisible(isExpanded);
            orderDetailsBox.setManaged(isExpanded);  // Add this line
            expandButton.setText(isExpanded ? "▲ Hide Details" : "▼ Show Details");
        });
        
        // Add action buttons based on order status and type
        // Verify Payment Button
        final Button verifyPaymentButton = new Button("Verify Payment");
        styleActionButton(verifyPaymentButton, PRIMARY_COLOR);

        // Ready for Pickup Button
        final Button readyForPickupButton = new Button("Mark as Ready for Pick-up");
        styleActionButton(readyForPickupButton, ACCENT_COLOR);
        
        // Assign to Rider Button
        final Button assignToRiderButton = new Button("Assign to Rider");
        styleActionButton(assignToRiderButton, SECONDARY_COLOR);
        
        // Order Picked Up / Complete Order Button
        final Button orderPickedUpButton = new Button("Order Picked Up");
        
        if ("pick up".equalsIgnoreCase(orderType)) {
            orderPickedUpButton.setText("Complete Order");
            styleActionButton(orderPickedUpButton, "#4CAF50"); // Green color
            orderPickedUpButton.setDisable(false);
        } else {
            styleActionButton(orderPickedUpButton, "#2196F3"); // Blue color
            orderPickedUpButton.setDisable(true);
        }
        
        // Add buttons and handlers for different order types and statuses
        if ("delivery".equalsIgnoreCase(orderType)) {
            actionButtons.getChildren().add(assignToRiderButton);
            
            assignToRiderButton.setOnAction(assign -> {
                showRiderSelectionDialog(order);
                assignToRiderButton.setDisable(true);
                orderPickedUpButton.setDisable(false);
            });
        }
        
        if ("pick up".equalsIgnoreCase(orderType)) {
            actionButtons.getChildren().add(readyForPickupButton);
            
            readyForPickupButton.setOnAction(e -> {
                Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                confirmAlert.setTitle("Confirm Ready for Pick-up");
                confirmAlert.setHeaderText("Are you sure you want to mark this order as Ready for Pick-up?");
                confirmAlert.setContentText("This will notify the customer that their order is ready.");

                Optional<ButtonType> result = confirmAlert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    try {
                        markOrderReadyForPickup(order);
                    } catch (MessagingException ex) {
                        Logger.getLogger(ShowOrders.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    readyForPickupButton.setDisable(true);
                    statusLabel.setText("Ready for Pick-up");
                    statusLabel.setTextFill(STATUS_READY_PICKUP);
                    statusCircle.setFill(STATUS_READY_PICKUP);
                    orderBox.setStyle(orderBox.getStyle() + "-fx-background-color: #E3F2FD;"); // Light blue background
                }
            });
        }
        
        // For order picked up / complete order button
        orderDetailsBox.getChildren().add(orderPickedUpButton);
        
        orderPickedUpButton.setOnAction(pickedUp -> {
            String orderTypeLower = orderType.toLowerCase();
            String message = "Are you sure this order has been picked up?";
            String details = "This will complete the order and notify the customer.";

            if ("delivery".equals(orderTypeLower)) {
                message = "Are you sure this order is now Out for Delivery?";
                details = "This will notify the customer that their order is out for delivery.";
            }

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Order Status");
            confirmAlert.setHeaderText(message);
            confirmAlert.setContentText(details);

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    markOrderPickedUp(order);
                } catch (MessagingException ex) {
                    Logger.getLogger(ShowOrders.class.getName()).log(Level.SEVERE, null, ex);
                }

                readyForPickupButton.setDisable(true); 
                orderPickedUpButton.setDisable(true);
                verifyPaymentButton.setDisable(true);
                assignToRiderButton.setDisable(true);

                if ("delivery".equals(orderTypeLower)) {
                    order.setOrderStatus("out for delivery");
                    statusLabel.setText("Out for Delivery");
                    statusLabel.setTextFill(STATUS_OUT_FOR_DELIVERY);
                    statusCircle.setFill(STATUS_OUT_FOR_DELIVERY);
                } else if ("pick up".equals(orderTypeLower)) {
                    order.setOrderStatus("completed");
                    statusLabel.setText("Completed");
                    statusLabel.setTextFill(STATUS_COMPLETED);
                    statusCircle.setFill(STATUS_COMPLETED);
                }

                // Gray out completed order
                orderBox.setStyle(orderBox.getStyle() + "-fx-background-color: #EEEEEE;");
                ordersContainer.getChildren().remove(orderBox);
                ordersContainer.getChildren().add(orderBox);
            }
        });
        
        // Handle payment verification
        if ("pending verification".equalsIgnoreCase(order.getPaymentStatus())
            && !"cancelled".equalsIgnoreCase(order.getOrderStatus())
            && !"completed".equalsIgnoreCase(order.getOrderStatus())) {
            
            actionButtons.getChildren().add(verifyPaymentButton);

            verifyPaymentButton.setOnAction(e -> {
               PaymentVerificationWindow.show(
                order, paymentStatusLabel, orderBox, ordersContainer,
                statusLabel, statusCircle,
                verifyPaymentButton, assignToRiderButton, orderPickedUpButton,
                adminId
            );
            });
        }
        
        // Add proof of delivery image if applicable
        ImageView imageView = new ImageView();
        String proofOfDeliveryImagePath = order.getProofOfDeliveryImagePath();
        
        if (status.equalsIgnoreCase("completed") && proofOfDeliveryImagePath != null 
            && !proofOfDeliveryImagePath.isEmpty()) {
            
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            Image image = new Image("file:" + proofOfDeliveryImagePath);
            imageView.setImage(image);
            
            // Create image container with frame
            VBox imageContainer = new VBox(5);
            imageContainer.setAlignment(Pos.CENTER);
            imageContainer.setPadding(new Insets(10));
            imageContainer.setStyle("-fx-border-color: " + LIGHT_GRAY + "; -fx-border-radius: 5;");
            
            Label imageLabel = new Label("Proof of Delivery");
            imageLabel.setStyle("-fx-font-weight: bold;");
            
            imageContainer.getChildren().addAll(imageLabel, imageView);
            orderDetailsBox.getChildren().add(imageContainer);
        }
        
        // Disable buttons for completed/cancelled orders
        if ("completed".equalsIgnoreCase(order.getOrderStatus()) || 
            "cancelled".equalsIgnoreCase(order.getOrderStatus()) || 
            "out for delivery".equalsIgnoreCase(order.getOrderStatus())) {
            
            readyForPickupButton.setDisable(true);
            verifyPaymentButton.setDisable(true);
            assignToRiderButton.setDisable(true);
            orderPickedUpButton.setDisable(true);
            
            if ("completed".equalsIgnoreCase(order.getOrderStatus())) {
                orderBox.setStyle(orderBox.getStyle() + "-fx-background-color: #E8F5E9;"); // Light green for completed
            } else if ("cancelled".equalsIgnoreCase(order.getOrderStatus())) {
                orderBox.setStyle(orderBox.getStyle() + "-fx-background-color: #EEEEEE;"); // Gray for cancelled
            } else if ("out for delivery".equalsIgnoreCase(order.getOrderStatus())) {
                orderBox.setStyle(orderBox.getStyle() + "-fx-background-color: #FFF8E1;"); // Light yellow for delivery
            }
        }
        
        if ("ready for pick-up".equalsIgnoreCase(status)) {
            readyForPickupButton.setDisable(true);
            orderBox.setStyle(orderBox.getStyle() + "-fx-background-color: #E3F2FD;"); // Light blue for ready to pickup
        }
        
        // Button row with expand button at left and action buttons at right
        HBox buttonRow = new HBox();
        buttonRow.setAlignment(Pos.CENTER_LEFT);
        
        // Create spacer to push action buttons to the right
        Region buttonSpacer = new Region();
        HBox.setHgrow(buttonSpacer, Priority.ALWAYS);
        
        buttonRow.getChildren().addAll(expandButton, buttonSpacer);
        
        // Only add the action buttons container if it has children
        if (!actionButtons.getChildren().isEmpty()) {
            buttonRow.getChildren().add(actionButtons);
        }
        
        // Assemble the order box with all components
        orderBox.getChildren().addAll(topSection, infoCards, buttonRow, orderDetailsBox);
        
        // Add to appropriate position in orders container
        if (status.equalsIgnoreCase("out for delivery") || 
            status.equalsIgnoreCase("completed") || 
            status.equalsIgnoreCase("cancelled")) {
            ordersContainer.getChildren().add(orderBox); // Add later so it ends up at the bottom
        } else if (status.equalsIgnoreCase("ready for pick-up")) {
            ordersContainer.getChildren().add(orderBox);
        } else {
            ordersContainer.getChildren().add(0, orderBox); // Add to the top for pending-type orders
        }
    }
    
    /**
     * Helper method to create info cards
     */
    private VBox createInfoCard(String title, String[] details) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: " + LIGHT_GRAY + ";" +
                    "-fx-background-radius: 5;");
        card.setMinWidth(150);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        card.getChildren().add(titleLabel);
        
        for (String detail : details) {
            Label detailLabel = new Label(detail);
            detailLabel.setStyle("-fx-font-size: 12px;");
            card.getChildren().add(detailLabel);
        }
        
        return card;
    }
    
    /**
     * Helper method to style action buttons consistently
     */
    private void styleActionButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + ";" +
                      "-fx-text-fill: white;" +
                      "-fx-font-weight: bold;" + 
                      "-fx-background-radius: 5;" +
                      "-fx-padding: 8 15;");
    }
    
    // The capitalize helper method (assuming it exists in your original code)
    private String capitalize(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    // Getter for the root node
    public VBox getRoot() {
        return root;
    }
    
   private void markOrderReadyForPickup(Order order) throws MessagingException {
    String newStatus = "Ready for Pick-up";
    String updateQuery = "UPDATE orders SET status = ?, last_modified_by = ? WHERE order_id = ?";

    try (Connection connection = Database.connect(); 
         PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

        preparedStatement.setString(1, newStatus);
        preparedStatement.setInt(2, adminId);
        preparedStatement.setInt(3, order.getOrderId());

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Order marked as 'Ready for Pick-up' successfully!");

            // Fetch customer_id and email based on customer name
            String callProc = "{CALL GetCustomerDetailsByName(?)}";
            try (CallableStatement procStmt = connection.prepareCall(callProc)) {
                procStmt.setString(1, order.getCustomerName());
                try (ResultSet rs = procStmt.executeQuery()) {
                    if (rs.next()) {
                        int customerId = rs.getInt("customer_id");
                        String email = rs.getString("email");


                        String subject = "Your Order is Ready for Pick-up! 🍗";
                        String message = """
                                Hi %s,

                                Your order #%d is now ready for pick-up at Andok’s!
                                Feel free to drop by anytime during our business hours.

                                Thank you for choosing Andok’s!
                                
                                Love,  
                                The Andok’s Team ❤️
                                """.formatted(order.getCustomerName(), order.getOrderId());

                      String callNotifProc = "{CALL InsertNotification(?, ?, ?, ?)}";
                    try (CallableStatement notifStmt = connection.prepareCall(callNotifProc)) {
                        notifStmt.setInt(1, customerId);
                        notifStmt.setString(2, message);
                        notifStmt.setString(3, "order_ready_for_pickup");
                        notifStmt.setInt(4, adminId);
                        notifStmt.executeUpdate();
                    }


                        // Send email
                        SendEmail.sendEmail(email, subject, message);
                        System.out.println("Email sent to " + email);
                    }
                }
            }
        } else {
            System.out.println("Failed to update the order.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    

  
    private void markOrderPickedUp(Order order) throws MessagingException {
    String newStatus;
    String subject = "";
    String message = "";

    // Check order type (pickup or delivery)
    if ("pick up".equalsIgnoreCase(order.getOrderType())) {
        newStatus = "Completed"; // pickup = done once picked up
        subject = "Your Order is Completed!";
        message = "Hi there,\n\nYour order #" + order.getOrderId() + " has been successfully picked up and is now complete.\n\nThank you for choosing Andok's!\n\nDon't forget to send your ratings!⭐⭐⭐⭐⭐ \n\nBest regards, \nThe Andok's Team ❤️";
    } else {
        newStatus = "Out for Delivery"; // delivery = still needs delivery
        subject = "Your Order is Out for Delivery!";
        message = "Hi there,\n\nYour order #" + order.getOrderId() + " is now out for delivery. It will reach you shortly!\n\nThank you for your patience.\n\nBest regards, \nThe Andok's Team ❤️";
    }

    // Update order status in database
    String updateQuery = "UPDATE orders SET status = ?, last_modified_by = ? WHERE order_id = ?";

    try (Connection connection = Database.connect(); 
         PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

        preparedStatement.setString(1, newStatus);
        preparedStatement.setInt(2, adminId); // 👈 use the passed admin ID
        preparedStatement.setInt(3, order.getOrderId());

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Order marked as '" + newStatus + "' successfully!");

            // Fetch customer_id based on customerName
            String callProc = "{CALL GetCustomerDetailsByName(?)}";
            try (CallableStatement procStmt = connection.prepareCall(callProc)) {
                procStmt.setString(1, order.getCustomerName());
                try (ResultSet rs = procStmt.executeQuery()) {
                    if (rs.next()) {
                        int customerId = rs.getInt("customer_id");
                        String email = rs.getString("email");

                        String callNotifProc = "{CALL InsertNotification(?, ?, ?, ?)}";
                    try (CallableStatement notifStmt = connection.prepareCall(callNotifProc)) {
                        notifStmt.setInt(1, customerId);
                        notifStmt.setString(2, message);
                        notifStmt.setString(3, newStatus.equals("Completed") ? "order_completed" : "order_out_for_delivery");
                        notifStmt.setInt(4, adminId);
                        notifStmt.executeUpdate();
                    }


                        // Send email notification using SendEmail class
                        SendEmail.sendEmail(email, subject, message);
                    }
                }
            }
        } else {
            System.out.println("Failed to update the order.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




    private void showRiderSelectionDialog(Order order) {
    Connection connection = connect();
    Stage riderSelectionStage = new Stage();
    riderSelectionStage.setTitle("Assign Rider");
    riderSelectionStage.initModality(Modality.APPLICATION_MODAL);

    // Color palette
    String RED = "#FF3B30";
    String YELLOW = "#FFCC00";
    String WHITE = "#FFFFFF";
    String DARK_RED = "#CC2A24";
    String LIGHT_GRAY = "#F5F5F5";
    String DARK_TEXT = "#333333";

    // Main layout
    VBox mainLayout = new VBox(20);
    mainLayout.setPadding(new Insets(30));
    mainLayout.setStyle("-fx-background-color: " + WHITE + ";");

    // Header with order ID
    Label headerLabel = new Label("ORDER #" + order.getOrderId());
    headerLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + RED + ";");

    // Divider
    Separator separator = new Separator();
    separator.setStyle("-fx-background-color: " + YELLOW + "; -fx-opacity: 0.8;");

    // Instruction label with icon
    HBox instructionBox = new HBox(10);
    instructionBox.setAlignment(Pos.CENTER_LEFT);
    
    // Replace with your actual icon implementation or comment out if not available
    Label iconLabel = new Label("🛵");
    iconLabel.setStyle("-fx-font-size: 18px;");
    
    Label instructionLabel = new Label("Select rider to assign:");
    instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: normal; -fx-text-fill: " + DARK_TEXT + ";");
    
    instructionBox.getChildren().addAll(iconLabel, instructionLabel);

    // Styled combo box for rider selection
    ComboBox<Rider> riderComboBox = new ComboBox<>();
    riderComboBox.setPromptText("Choose a rider");
    riderComboBox.setPrefHeight(40);
    riderComboBox.setPrefWidth(Double.MAX_VALUE);
    riderComboBox.setStyle("-fx-font-size: 14px; -fx-background-radius: 5px; -fx-border-radius: 5px; " +
                        "-fx-background-color: " + LIGHT_GRAY + "; -fx-border-color: #E0E0E0;");

    // Custom cell factory for better display
    riderComboBox.setCellFactory(lv -> new ListCell<Rider>() {
        @Override
        protected void updateItem(Rider rider, boolean empty) {
            super.updateItem(rider, empty);
            if (empty || rider == null) {
                setText(null);
            } else {
                setText(rider.getName() + " (Assigned Pending Orders: " + rider.getAssignedOrders() + ")");
            }
        }
    });
    
    // Set the same display format for the selected item
    riderComboBox.setButtonCell(new ListCell<Rider>() {
        @Override
        protected void updateItem(Rider rider, boolean empty) {
            super.updateItem(rider, empty);
            if (empty || rider == null) {
                setText(null);
            } else {
                setText(rider.getName() + " (ID: " + rider.getRiderId() + ")");
            }
        }
    });

    // Load riders
    RiderService riderService = new RiderService(connection);
    List<Rider> riders = riderService.getAllRiders();
    riderComboBox.getItems().addAll(riders);

    // Buttons container
    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER_RIGHT);
    buttonBox.setPadding(new Insets(10, 0, 0, 0));

    // Cancel button
    Button cancelButton = new Button("Cancel");
    cancelButton.setPrefHeight(40);
    cancelButton.setPrefWidth(100);
    cancelButton.setStyle("-fx-background-color: " + LIGHT_GRAY + "; " +
                         "-fx-text-fill: " + DARK_TEXT + "; " +
                         "-fx-font-weight: bold; " +
                         "-fx-background-radius: 5px; " +
                         "-fx-cursor: hand;");
    cancelButton.setOnAction(e -> riderSelectionStage.close());

    // Assign button
    Button assignButton = new Button("Assign");
    assignButton.setPrefHeight(40);
    assignButton.setPrefWidth(100);
    assignButton.setStyle("-fx-background-color: " + RED + "; " +
                         "-fx-text-fill: " + WHITE + "; " +
                         "-fx-font-weight: bold; " +
                         "-fx-background-radius: 5px; " +
                         "-fx-cursor: hand;");
    
    // Button hover effects
    assignButton.setOnMouseEntered(e -> 
        assignButton.setStyle("-fx-background-color: " + DARK_RED + "; " +
                             "-fx-text-fill: " + WHITE + "; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-cursor: hand;"));
    
    assignButton.setOnMouseExited(e -> 
        assignButton.setStyle("-fx-background-color: " + RED + "; " +
                             "-fx-text-fill: " + WHITE + "; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-cursor: hand;"));

    // Assign button action
    assignButton.setOnAction(e -> {
        Rider selectedRider = riderComboBox.getValue();
        if (selectedRider != null) {
            assignOrderToRider(order.getOrderId(), selectedRider.getRiderId());
            riderSelectionStage.close();
            
            // Show success notification with animation
            showSuccessNotification("Order #" + order.getOrderId() + " has been assigned to " + selectedRider.getName());
        } else {
            // Error notification
            showErrorAlert("Please select a rider to continue.");
        }
    });

    buttonBox.getChildren().addAll(cancelButton, assignButton);

    // Add yellow accent at the top
    Rectangle accentBar = new Rectangle();
    accentBar.setHeight(8);
    accentBar.widthProperty().bind(mainLayout.widthProperty());
    accentBar.setFill(Paint.valueOf(YELLOW));

    // Assemble layout
    mainLayout.getChildren().addAll(accentBar, headerLabel, separator, instructionBox, 
                                   riderComboBox, buttonBox);

    Scene scene = new Scene(mainLayout, 400, 300);
    riderSelectionStage.setScene(scene);
    riderSelectionStage.setResizable(false);
    riderSelectionStage.show();
}

// Helper method for success notification
private void showSuccessNotification(String message) {
    Stage notificationStage = new Stage();
    notificationStage.initStyle(StageStyle.UNDECORATED);
    notificationStage.setAlwaysOnTop(true);
    
    HBox notificationBox = new HBox(15);
    notificationBox.setPadding(new Insets(15, 20, 15, 20));
    notificationBox.setStyle("-fx-background-color: #FFCC00; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 10, 0, 0, 2);");
    
    Label checkIcon = new Label("✓");
    checkIcon.setStyle("-fx-font-size: 18px; -fx-text-fill: #FFFFFF; -fx-font-weight: bold;");
    
    Label messageLabel = new Label(message);
    messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333; -fx-font-weight: bold;");
    
    notificationBox.getChildren().addAll(checkIcon, messageLabel);
    
    Scene scene = new Scene(notificationBox);
    scene.setFill(Color.TRANSPARENT);
    notificationStage.setScene(scene);
    
    // Position at the top right corner
    Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
    notificationStage.setX(bounds.getMaxX() - notificationBox.getPrefWidth() - 20);
    notificationStage.setY(bounds.getMinY() + 40);
    
    notificationStage.show();
    
    // Auto-hide after 3 seconds
    PauseTransition delay = new PauseTransition(Duration.seconds(3));
    delay.setOnFinished(e -> notificationStage.close());
    delay.play();
}

// Helper method for error alert
    private void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Style the alert dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #FFFFFF;");
        dialogPane.getStyleClass().add("custom-alert");

        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        alertStage.getIcons().add(new Image("path/to/your/error-icon.png")); // Replace with your icon path or remove

        alert.showAndWait();
    }

        private void assignOrderToRider(int orderId, int riderId) {
        
        Connection connection = connect();

        String updateQuery = "UPDATE orders SET rider_id = ?, last_modified_by = ? WHERE order_id = ?";
  
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, riderId);
            preparedStatement.setInt(2, adminId);
            preparedStatement.setInt(3, orderId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
        private void showAlert(String title, String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle(title);
    alert.setContentText(message);
    alert.showAndWait();
}
}

