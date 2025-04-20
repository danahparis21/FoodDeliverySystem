/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import static andoksfooddeliverysystem.Database.connect;
import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.mail.MessagingException;


public class AssignedOrders {
    private VBox root;
    private List<Order> allOrders;
    private VBox ordersContainer;    
    private int riderId;
    private int userId;
    
    // Color constants for consistent styling
    private static final String PRIMARY_COLOR = "#E53935"; // Red
    private static final String SECONDARY_COLOR = "#FFC107"; // Yellow/Gold
    private static final String LIGHT_GREY = "#F5F5F5";
    private static final String WHITE = "#FFFFFF";
    private static final String GREEN_COLOR = "#4CAF50";
    private static final String BLUE_COLOR = "#2196F3";
     private static final String LIGHT_GRAY = "#F5F5F5"; // Light gray for borders

    
    public AssignedOrders(int riderId) {
        this.riderId = riderId;
        this.userId = getUserIdFromRiderId(riderId);
        root = new VBox(15);
        root.setStyle("-fx-background-color: " + WHITE + ";");
        System.out.println("UserID: " + userId);
        
        allOrders = OrderFetcher.fetchOrdersByRider(riderId);
        
        setupUI();
    }
    
    private void setupUI() {
        // Header with title
        Label headerLabel = new Label("Assigned Orders");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        // Search and filter controls
        HBox controls = createControlsPane();
        
        // Create the main container with scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + WHITE + "; -fx-border-color: transparent;");
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        // Create container for orders
        ordersContainer = new VBox(15);
        ordersContainer.setPadding(new Insets(15));
        ordersContainer.setStyle("-fx-background-color: " + LIGHT_GREY + ";");
        
        // Load initial orders
        refreshOrders(allOrders);
        
        scrollPane.setContent(ordersContainer);
        
        // Add all components to root
        root.getChildren().addAll(headerLabel, controls, scrollPane);
        VBox.setMargin(headerLabel, new Insets(15, 0, 0, 15));
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
    }
    
    private HBox createControlsPane() {
        TextField searchField = new TextField();
        searchField.setPromptText("Search orders...");
        searchField.setPrefWidth(250);
        searchField.setStyle("-fx-background-radius: 20; -fx-border-radius: 20;");
        
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Out for delivery", "Completed", "Cancelled", "Ready for Pick-up");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-radius: 5;");
        
        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.getItems().addAll("Order # Ascending", "Order # Descending", "Most Recent", "Oldest");
        sortBy.setValue("Order # Ascending");
        sortBy.setStyle("-fx-background-radius: 5;");
        
        HBox controls = new HBox(15, searchField, statusFilter, sortBy);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(15));
        controls.setStyle("-fx-background-color: " + WHITE + ";");
        
        // Add listeners for filtering and sorting
        setupFilterListeners(searchField, statusFilter, sortBy);
        
        return controls;
    }
    
    private void setupFilterListeners(TextField searchField, ComboBox<String> statusFilter, ComboBox<String> sortBy) {
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

        // Add listeners
        searchField.textProperty().addListener((obs, oldVal, newVal) -> updateList.run());
        statusFilter.setOnAction(e -> updateList.run());
        sortBy.setOnAction(e -> updateList.run());
    }
    
    private void refreshOrders(List<Order> orders) {
        ordersContainer.getChildren().clear();
        
        List<VBox> activeOrders = new ArrayList<>();
        List<VBox> completedOrders = new ArrayList<>();
        
        for (Order order : orders) {
            VBox orderBox = createOrderBox(order);
            
            // Separate completed/cancelled orders from active ones
            if (order.getOrderStatus().equalsIgnoreCase("completed") || 
                order.getOrderStatus().equalsIgnoreCase("cancelled")) {
                completedOrders.add(orderBox);
            } else {
                activeOrders.add(orderBox);
            }
        }
        
        // Add active orders first (at top)
        ordersContainer.getChildren().addAll(activeOrders);
        // Then add completed/cancelled orders
        ordersContainer.getChildren().addAll(completedOrders);
    }
    
    private VBox createOrderBox(Order order) {
        // Main order container
        VBox orderBox = new VBox(15);
        orderBox.setPadding(new Insets(20));
        orderBox.setStyle("-fx-background-color: " + WHITE + "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 5); " +
                         "-fx-background-radius: 8; -fx-border-radius: 8;");
        
        // Get status information
        String status = order.getOrderStatus().toLowerCase();
        boolean isCompleted = status.equals("completed") || status.equals("cancelled");
        
        if (isCompleted) {
            orderBox.setStyle(orderBox.getStyle() + "; -fx-opacity: 0.8;");
        }
        
        // Create main content section
        VBox mainContent = createMainContentSection(order);
        
        // Create expandable details section
        VBox orderDetailsBox = new VBox(15);
        orderDetailsBox.setVisible(false);
        orderDetailsBox.setPadding(new Insets(15, 0, 0, 15));
        orderDetailsBox.getChildren().addAll(
            createOrderItemsSection(order),
            createActionButtonsSection(order, orderBox)
        );
        
        // Create expand/collapse button
        Button expandButton = createExpandButton(orderDetailsBox, orderBox);
        
        // Add all components to the order box
        orderBox.getChildren().addAll(mainContent, expandButton);
        
        return orderBox;
    }
    
   private VBox createMainContentSection(Order order) {
    VBox mainContent = new VBox(15);
    
    // Header with Order ID and Status in a more prominent way
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
    
    // Order details section (customer name, date, status)
    VBox orderDetailsSection = new VBox(8);
    orderDetailsSection.setStyle("-fx-padding: 0 0 0 10;");
    
    // Customer name 
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
    String statusText = order.getOrderStatus().toLowerCase();
    Label statusLabel = new Label(capitalize(statusText));
    statusLabel.setStyle("-fx-font-weight: bold;");
    
    // Set status circle color based on status
    switch (statusText) {
        case "pending":
            statusCircle.setFill(Color.web(PRIMARY_COLOR)); // Red
            statusLabel.setTextFill(Color.web(PRIMARY_COLOR));
            break;
        case "out for delivery":
            statusCircle.setFill(Color.web(SECONDARY_COLOR)); // Yellow
            statusLabel.setTextFill(Color.web(SECONDARY_COLOR));
            break;
        case "completed":
            statusCircle.setFill(Color.web(GREEN_COLOR));
            statusLabel.setTextFill(Color.web(GREEN_COLOR));
            break;
        case "cancelled":
            statusCircle.setFill(Color.GRAY);
            statusLabel.setTextFill(Color.GRAY);
            break;
        case "ready for pick-up":
            statusCircle.setFill(Color.web(BLUE_COLOR));
            statusLabel.setTextFill(Color.web(BLUE_COLOR));
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
    infoCards.setPadding(new Insets(10, 0, 10, 0));
    
    // Payment info card
    VBox paymentCard = createInfoCard("Payment", new String[]{
        "Method: " + order.getPaymentMethod(),
        "Status: " + order.getPaymentStatus()
    });
    
    // Contact info card
    VBox contactCard = createInfoCard("Contact", new String[]{
        order.getContactNumber()
    });
    
    // Address/Order Type info card
    String orderType = order.getOrderType().toLowerCase();
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
            "Time: " + (order.getPickupTime() != null ? order.getPickupTime() : "Not specified")
        };
    }
    VBox addressCard = createInfoCard("Address", addressInfo);
    
    // Add cards to horizontal layout
    infoCards.getChildren().addAll(paymentCard, contactCard, addressCard);
    
    // Map tracking section (only shown for delivery orders)
    HBox trackingSection = new HBox(15);
    trackingSection.setAlignment(Pos.CENTER_RIGHT);
    
    if ("delivery".equals(orderType)) {
        Button trackButton = new Button("📍 Track Location");
        trackButton.setStyle("-fx-background-color: " + GREEN_COLOR + "; -fx-text-fill: white; " +
                           "-fx-background-radius: 5; -fx-cursor: hand; -fx-font-weight: bold;");
        
        trackButton.setOnAction(e -> {
            try {
                String street = order.getStreet();
                String barangay = order.getBarangay();
                
                // Prevent duplication
                String fullAddress = street;
                if (!street.toLowerCase().contains("nasugbu") && !street.toLowerCase().contains("batangas")) {
                    fullAddress += ", " + barangay + ", Nasugbu, Batangas";
                } else if (!street.toLowerCase().contains(barangay.toLowerCase())) {
                    fullAddress += ", " + barangay;
                }
                
                System.out.println("Final address: " + fullAddress);
                
                String encodedAddress = URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);
                String url = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
                
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        trackingSection.getChildren().add(trackButton);
    }
    
    // Add all components to main content
    mainContent.getChildren().addAll(topSection, infoCards);
    
    // Only add tracking section for delivery orders
    if ("delivery".equals(orderType)) {
        mainContent.getChildren().add(trackingSection);
    }
    
    return mainContent;
}

// Helper method for creating info cards (keeping your original method)
private VBox createInfoCard(String title, String[] details) {
    VBox card = new VBox(5);
    card.setPadding(new Insets(10));
    card.setStyle("-fx-background-color: " + LIGHT_GRAY + ";" +
                "-fx-background-radius: 5;");
    card.setMinWidth(150);
    HBox.setHgrow(card, Priority.ALWAYS); // Make cards expand equally
    
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
    private HBox createOrderHeaderSection(Order order) {
        // Order ID
        Label orderIdLabel = new Label("Order #" + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        // Status indicator
        String status = order.getOrderStatus().toLowerCase();
        Label statusLabel = new Label(capitalize(status));
        statusLabel.setStyle("-fx-font-weight: bold;");
        
        Circle statusCircle = new Circle(8);
        
        // Set status colors
        switch (status) {
            case "pending":
                statusCircle.setFill(Color.web(PRIMARY_COLOR));
                statusLabel.setTextFill(Color.web(PRIMARY_COLOR));
                break;
            case "out for delivery":
                statusCircle.setFill(Color.web(SECONDARY_COLOR));
                statusLabel.setTextFill(Color.web(SECONDARY_COLOR));
                break;
            case "ready for pick-up":
                statusCircle.setFill(Color.web(BLUE_COLOR));
                statusLabel.setTextFill(Color.web(BLUE_COLOR));
                break;
            case "completed":
                statusCircle.setFill(Color.web(GREEN_COLOR));
                statusLabel.setTextFill(Color.web(GREEN_COLOR));
                break;
            case "cancelled":
                statusCircle.setFill(Color.GRAY);
                statusLabel.setTextFill(Color.GRAY);
                break;
            default:
                statusCircle.setFill(Color.BLACK);
                statusLabel.setTextFill(Color.BLACK);
        }
        
        HBox statusBox = new HBox(5, statusCircle, statusLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        // Create header box with order ID and status
        HBox headerBox = new HBox();
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.setSpacing(15);
        headerBox.getChildren().addAll(orderIdLabel, statusBox);
        
        return headerBox;
    }
    
    private HBox createAddressSection(Order order) {
        // Address info
        VBox addressBox = new VBox(5);
        Label addressHeader = new Label("Delivery Address:");
        addressHeader.setStyle("-fx-font-weight: bold;");
        
        // Address details
        HBox streetBox = new HBox(5, new Label("Street:"), new Label(order.getStreet()));
        HBox barangayBox = new HBox(5, new Label("Barangay:"), new Label(order.getBarangay()));
        HBox contactBox = new HBox(5, new Label("Contact:"), new Label(order.getContactNumber()));
        
        addressBox.getChildren().addAll(addressHeader, streetBox, barangayBox, contactBox);
        
        // Track button
        Button trackButton = new Button("📍 Track");
        trackButton.setStyle("-fx-background-color: " + GREEN_COLOR + "; -fx-text-fill: white; " +
                           "-fx-background-radius: 5; -fx-cursor: hand;");
        
        trackButton.setOnAction(e -> {
            try {
                String street = order.getStreet();
                String barangay = order.getBarangay();
                
                // Prevent duplication
                String fullAddress = street;
                if (!street.toLowerCase().contains("nasugbu") && !street.toLowerCase().contains("batangas")) {
                    fullAddress += ", " + barangay + ", Nasugbu, Batangas";
                } else if (!street.toLowerCase().contains(barangay.toLowerCase())) {
                    fullAddress += ", " + barangay;
                }
                
                System.out.println("Final address: " + fullAddress);
                
                String encodedAddress = URLEncoder.encode(fullAddress, StandardCharsets.UTF_8);
                String url = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
                
                Desktop.getDesktop().browse(new URI(url));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        // Create HBox for address and track button
        HBox trackAddressBox = new HBox(15);
        trackAddressBox.setAlignment(Pos.CENTER_LEFT);
        trackAddressBox.getChildren().addAll(addressBox, trackButton);
        
        return trackAddressBox;
    }
    
    private VBox createOrderItemsSection(Order order) {
        VBox itemsSection = new VBox(12);
        itemsSection.setStyle("-fx-background-color: " + LIGHT_GREY + "; -fx-background-radius: 5; -fx-padding: 10;");
        
        Label itemsHeader = new Label("Order Items");
        itemsHeader.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        itemsSection.getChildren().add(itemsHeader);
        
        // Add each order item
        for (DetailedOrderItem item : order.getOrderItems()) {
            HBox itemBox = new HBox(15);
            itemBox.setPadding(new Insets(8));
            itemBox.setStyle("-fx-border-color: #DDDDDD; -fx-border-width: 0 0 1 0;");
            
            // Get the item name (with fallback)
            String itemName = (item.getItemName() == null || item.getItemName().isEmpty())
                ? "Unknown Item" 
                : item.getItemName();
            
            Label itemLabel = new Label(String.format(
                "%d x %s: ₱%.2f",
                item.getQuantity(), 
                itemName,
                item.getSubtotal()
            ));
            itemLabel.setStyle("-fx-font-weight: bold;");
            
            VBox detailsBox = new VBox(5);
            
            // Variation
            String variationText = (item.getVariation() == null || item.getVariation().isEmpty()) 
                ? "No variation" 
                : item.getVariation();
            Label variationLabel = new Label("Variant: " + variationText);
            
            // Instructions
            String instructionsText = (item.getInstructions() == null || item.getInstructions().isEmpty())
                ? "No special instructions"
                : item.getInstructions();
            Label instructionsLabel = new Label("Notes: " + instructionsText);
            
            detailsBox.getChildren().addAll(variationLabel, instructionsLabel);
            
            itemBox.getChildren().addAll(itemLabel, detailsBox);
            itemsSection.getChildren().add(itemBox);
        }
        
        return itemsSection;
    }
    
    private VBox createActionButtonsSection(Order order, VBox orderBox) {
        VBox actionsSection = new VBox(15);
        actionsSection.setAlignment(Pos.CENTER_LEFT);
        
        // Create image view for proof of delivery
        ImageView imageView = new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(true);
        
        // Check if proof already exists
        String proofOfDeliveryImagePath = order.getProofOfDeliveryImagePath();
        if (proofOfDeliveryImagePath != null && !proofOfDeliveryImagePath.isEmpty()) {
            Image image = new Image("file:" + proofOfDeliveryImagePath);
            imageView.setImage(image);
        }
        
        // Create buttons
        Button uploadProofButton = new Button("Upload Proof of Delivery");
        uploadProofButton.setStyle("-fx-background-color: " + BLUE_COLOR + "; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        
        Button completeOrderButton = new Button("Complete Order");
        completeOrderButton.setStyle("-fx-background-color: " + GREEN_COLOR + "; -fx-text-fill: white; " +
                                    "-fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        completeOrderButton.setDisable(true);
        
        // Disable buttons if completed/cancelled
        String status = order.getOrderStatus().toLowerCase();
        boolean isCompleted = status.equals("completed") || status.equals("cancelled");
        
        if (isCompleted) {
            uploadProofButton.setDisable(true);
            completeOrderButton.setDisable(true);
        }
        
        // Set up upload button action
        uploadProofButton.setOnAction(uploadProof -> {
            Stage stage = (Stage) root.getScene().getWindow();
            
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg")
            );
            File file = fileChooser.showOpenDialog(stage);
            
            if (file != null) {
                String imagePath = file.getAbsolutePath();
                uploadProofOfDelivery(order, imagePath, userId);
                Image image = new Image("file:" + imagePath);
                imageView.setImage(image);
                
                completeOrderButton.setDisable(false);
            }
        });
        
        // Set up complete order button action
        completeOrderButton.setOnAction(pickedUp -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Completion");
            alert.setHeaderText("Are you sure you want to complete this order?");
            alert.setContentText("This action will notify the customer and cannot be undone.");
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    markOrderCompleted(order, userId);
                } catch (MessagingException ex) {
                    Logger.getLogger(AssignedOrders.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                completeOrderButton.setDisable(true);
                order.setOrderStatus("completed");
                
                // Update UI to show completed status
                orderBox.setStyle(orderBox.getStyle() + "; -fx-opacity: 0.8;");
                
                // Move order to bottom
                ordersContainer.getChildren().remove(orderBox);
                ordersContainer.getChildren().add(orderBox);
            } else {
                System.out.println("Order completion canceled.");
            }
        });
        
        HBox buttonsBox = new HBox(15, uploadProofButton, completeOrderButton);
        
        // Add proof label if image exists
        Label proofLabel = null;
        if (proofOfDeliveryImagePath != null && !proofOfDeliveryImagePath.isEmpty()) {
            proofLabel = new Label("Proof of Delivery:");
            proofLabel.setStyle("-fx-font-weight: bold;");
            actionsSection.getChildren().add(proofLabel);
        }
        
        // Add all components
        actionsSection.getChildren().addAll(buttonsBox, imageView);
        
        return actionsSection;
    }
    
    private Button createExpandButton(VBox orderDetailsBox, VBox orderBox) {
        Button expandButton = new Button("▼ Show Details");
        expandButton.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: white; " +
                            "-fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        
        expandButton.setOnAction(e -> {
            boolean isExpanded = !orderDetailsBox.isVisible();
            orderDetailsBox.setVisible(isExpanded);
            expandButton.setText(isExpanded ? "▲ Hide Details" : "▼ Show Details");
            
            if (isExpanded && !orderBox.getChildren().contains(orderDetailsBox)) {
                orderBox.getChildren().add(orderDetailsBox);
            } else if (!isExpanded) {
                orderBox.getChildren().remove(orderDetailsBox);
            }
        });
        
        return expandButton;
    }
    
    // Helper methods
    private String getPaymentStatusStyle(String paymentStatus) {
        if (paymentStatus.equalsIgnoreCase("paid")) {
            return "-fx-text-fill: " + GREEN_COLOR + "; -fx-font-weight: bold;";
        } else if (paymentStatus.equalsIgnoreCase("pending")) {
            return "-fx-text-fill: " + SECONDARY_COLOR + "; -fx-font-weight: bold;";
        } else {
            return "-fx-font-weight: bold;";
        }
    }
    
    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
    
    public VBox getRoot() {
        return root;
    }

    
    
       
       
        private int getUserIdFromRiderId(int riderId) {
        // Assuming you have a method to get userId from riderId, using JDBC or an ORM like Hibernate
        int userId = -1; // Default to an invalid value if not found

        // Query the `riders` table to get the corresponding userId
        String query = "SELECT user_id FROM riders WHERE rider_id = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, riderId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                userId = rs.getInt("user_id");
            }
             System.out.println("Converted Rider ID:" + userId);
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions properly
        }

        return userId;
       
    }

    // Method to upload proof of delivery (image)
public void uploadProofOfDelivery(Order order, String imagePath, int userId) {
    String query = "UPDATE orders SET proof_of_delivery_image_path = ?, last_modified_by = ? WHERE order_id = ?";

    
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
         
        stmt.setString(1, imagePath);  // Path to the image
         stmt.setInt(2, userId); 
        stmt.setInt(3, order.getOrderId());  // Order ID
        stmt.executeUpdate();
        
        System.out.println("Proof of delivery uploaded successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Failed to upload proof of delivery.");
    }
}

    
   private void markOrderCompleted(Order order, int riderId) throws MessagingException {
    String newStatus;
    String subject = "";
    String message = "";

   
        newStatus = "Completed";
        subject = "Your Order is Completed!";
        message = "Hi there,\n\nYour order #" + order.getOrderId() + " has been successfully picked up and is now complete.\n\nThank you for choosing Andok's!\n\nDon't forget to send your ratings! ⭐⭐⭐⭐⭐\n\nBest regards,\nThe Andok's Team ❤️";
    

    String updateQuery = "UPDATE orders SET status = ?, last_modified_by = ? WHERE order_id = ?";

    try (Connection connection = Database.connect();
         PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

        preparedStatement.setString(1, newStatus);
        preparedStatement.setInt(2, riderId); // 👈 Set by the rider
        preparedStatement.setInt(3, order.getOrderId());

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("✅ Order updated to '" + newStatus + "'");

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
                            notifStmt.setInt(4, riderId); // Rider sending the notification
                            notifStmt.executeUpdate();
                        }


                        // Send email
                        SendEmail.sendEmail(email, subject, message);
                    }
                }
            }
        } else {
            System.out.println("❌ Failed to update order status.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

 
}
