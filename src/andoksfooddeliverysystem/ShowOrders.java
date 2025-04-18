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
import java.util.logging.Level;
import java.util.logging.Logger;



public class ShowOrders {
    private VBox root;
    private List<Order> allOrders; // store original list
    private VBox ordersContainer;
     private int adminId; // the logged-in admin's ID

  
    
    public ShowOrders(int adminId) {
        this.adminId = adminId;
        root = new VBox(10); // Adjusted spacing
 
        allOrders = OrderFetcher.fetchOrders(); // Fetch all orders once

        // --- UI CONTROLS ---

        TextField searchField = new TextField();
        searchField.setPromptText("Search orders...");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Out for delivery", "Completed", "Cancelled", "Ready for Pick-up");
        statusFilter.setValue("All");

        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.getItems().addAll("Order # Ascending", "Order # Descending", "Most Recent", "Oldest");
        sortBy.setValue("Order # Ascending");

        HBox controls = new HBox(10, searchField, statusFilter, sortBy);
        controls.setAlignment(Pos.CENTER_LEFT);
        controls.setPadding(new Insets(10));

        // Create the main container with scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); 
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
      
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
             
        // Create a VBox to hold all orders
         ordersContainer = new VBox(10);
        ordersContainer.setPadding(new Insets(10)); // Add padding around all orders
        ordersContainer.setStyle("-fx-background-color: white;");

       ordersContainer.getChildren().clear(); // Prevent duplicates if refreshing

       for (Order order : allOrders) {

            VBox orderBox = new VBox(10);
            orderBox.setPadding(new Insets(10));
            orderBox.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");


            VBox mainContent = new VBox(10);
            
           /// === STATUS LABEL SETUP ===//
            String status = order.getOrderStatus(); // Get status from DB
            Label statusLabel = new Label(); // We will build text and style below
            statusLabel.setStyle("-fx-font-weight: bold;");

            String statusText = status.toLowerCase();
            Color statusColor = Color.BLACK; // Default color
            Circle statusCircle = new Circle(10); // Create a small circle for the status indicator

            switch (statusText) {
                case "pending":
                    statusCircle.setFill(Color.RED); // Red circle for pending
                    statusColor = Color.RED;
                    break;
                case "out for delivery":
                    statusCircle.setFill(Color.GOLD); // Yellow circle for out for delivery
                    statusColor = Color.GOLD;
                    break;
                case "completed":
                    statusCircle.setFill(Color.GREEN); // Green circle for completed
                    statusColor = Color.GREEN;
                    break;
                case "cancelled":
                    statusCircle.setFill(Color.GRAY); // Gray circle for cancelled
                    statusColor = Color.GRAY;
                    break;
                case "ready for pickup":
                    statusCircle.setFill(Color.BLUE); // Blue circle for ready for pickup
                    statusColor = Color.BLUE;
                    break;
                default:
                    statusCircle.setFill(Color.BLACK); // Default black circle
                    statusColor = Color.BLACK;
            }

                      
                statusLabel.setText(capitalize(statusText));
                // Add the status circle next to the label
                HBox statusBox = new HBox(5, statusCircle, statusLabel);
                statusBox.setAlignment(Pos.CENTER_LEFT);

            // Create labels for order summary with better formatting
            Label orderIdLabel = new Label("Order #" + order.getOrderId());
            orderIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            
             HBox idStatusBox = new HBox(10, orderIdLabel, statusBox);
            idStatusBox.setAlignment(Pos.CENTER_LEFT);

            Label dateLabel = new Label("Date: " + order.getOrderDate());
            Label totalLabel = new Label(String.format("Total: ₱%.2f", order.getTotalPrice()));
            totalLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
            
            Label nameLabel = new Label("Customer: " + order.getCustomerName());
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        


            // Address section
            VBox addressBox = new VBox(5);
            
            Label addressHeader = new Label("Delivery Address:");
            addressHeader.setStyle("-fx-font-weight: bold;");

            HBox streetBox = new HBox(5, new Label("Street:"), new Label(order.getStreet()));
            HBox barangayBox = new HBox(5, new Label("Barangay:"), new Label(order.getBarangay()));
            HBox contactBox = new HBox(5, new Label("Contact:"), new Label(order.getContactNumber()));

            addressBox.getChildren().addAll(addressHeader, streetBox, barangayBox, contactBox);

            
            // Add order type (delivery/pickup)
            String orderType = order.getOrderType();  // Get the order type
            Label orderTypeLabel = new Label("Order Type: " + orderType);
            orderTypeLabel.setStyle("-fx-font-weight: bold;");
        
             // Add payment method and status
            String paymentMethod = order.getPaymentMethod();
            String paymentStatus = order.getPaymentStatus(); // e.g., Paid, Pending, etc.
            Label paymentMethodLabel = new Label("Payment Method: " + paymentMethod);
            Label paymentStatusLabel = new Label("Payment Status: " + paymentStatus);

            // If order is for pickup, show pickup time
            if ("pick up".equalsIgnoreCase(orderType)) {
                String pickupTime = order.getPickupTime();  // Get pickup time from database
                Label pickupTimeLabel = new Label("Pickup Time: " + pickupTime);
                mainContent.getChildren().add(pickupTimeLabel);
            }
        
            mainContent.getChildren().addAll(idStatusBox, dateLabel,  totalLabel, nameLabel,addressBox, orderTypeLabel, paymentMethodLabel, paymentStatusLabel);

         


            // === DETAILS BOX (initially hidden) ===
            VBox orderDetailsBox = new VBox(10);
            orderDetailsBox.setVisible(false);
            orderDetailsBox.setPadding(new Insets(10, 0, 0, 10));

            // Build details content
            for (DetailedOrderItem item : order.getOrderItems()) {
            HBox itemBox = new HBox(10);
    
             // Get the item name (with fallback to "Unknown Item" if null)
                String itemName = (item.getItemName() == null || item.getItemName().isEmpty())
                    ? "Unknown Item" 
                    : item.getItemName();

                Label itemLabel = new Label(String.format(
                    "%d x %s: ₱%.2f",  // Changed from "Item %d" to "%s" for name
                    item.getQuantity(), 
                    itemName,  // Using itemName instead of itemId
                    item.getSubtotal()
                ));
                itemLabel.setStyle("-fx-font-weight: bold;");

                VBox detailsBox = new VBox(3);
               String variationText = (item.getVariation() == null || item.getVariation().isEmpty()) 
                ? "No variation" 
                : item.getVariation();
            detailsBox.getChildren().add(new Label("Variant: " + variationText));

            // Handle instructions display
            String instructionsText = (item.getInstructions() == null || item.getInstructions().isEmpty())
                ? "No special instructions"
                : item.getInstructions();
            detailsBox.getChildren().add(new Label("Notes: " + instructionsText));

                itemBox.getChildren().addAll(itemLabel, detailsBox);
                orderDetailsBox.getChildren().add(itemBox);
            }
            
              // ✅ These should be local per order
            final Button assignToRiderButton = new Button("Assign to Rider");
            final Button orderPickedUpButton = new Button("Order Picked Up");
            final Button verifyPaymentButton = new Button("Verify Payment");
            // Add a new button for "Mark as Ready for Pickup"
            final Button readyForPickupButton = new Button("Mark as Ready for Pickup");


            if ("delivery".equalsIgnoreCase(orderType)) {
                orderDetailsBox.getChildren().add(assignToRiderButton);
                
                orderPickedUpButton.setDisable(true);
            }
           

            if ("pick up".equalsIgnoreCase(orderType)) {
            orderPickedUpButton.setText("Complete Order");
            orderPickedUpButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            orderPickedUpButton.setDisable(false);
             mainContent.getChildren().add(readyForPickupButton);
        } else {
            orderPickedUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        }


            
            orderPickedUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
           
            assignToRiderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

          
                        // Assuming you are getting the proof of delivery image path from the database (proof_of_delivery_image_path)
            String proofOfDeliveryImagePath = order.getProofOfDeliveryImagePath();
            ImageView imageView = new ImageView();
            if (status.equalsIgnoreCase("completed") && proofOfDeliveryImagePath != null && !proofOfDeliveryImagePath.isEmpty()) {
           
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            Image image = new Image("file:" + proofOfDeliveryImagePath);
            imageView.setImage(image);

            // Add only if the image is actually present
            orderBox.getChildren().add(imageView);
        }

            
           assignToRiderButton.setOnAction(assign -> {
                showRiderSelectionDialog(order);
                assignToRiderButton.setDisable(true);
                orderPickedUpButton.setDisable(false);
            });

        
          orderPickedUpButton.setOnAction(pickedUp -> {
                try {
                    markOrderPickedUp(order); // Update DB or internal state
                } catch (MessagingException ex) {
                    Logger.getLogger(ShowOrders.class.getName()).log(Level.SEVERE, null, ex);
                }
                readyForPickupButton.setDisable(true); 
            orderPickedUpButton.setDisable(true);
            verifyPaymentButton.setDisable(true);
            assignToRiderButton.setDisable(true);

            String orderTypeLower = orderType.toLowerCase(); // "delivery" or "pick up"

            if ("delivery".equals(orderTypeLower)) {
                order.setOrderStatus("out for delivery"); // Update internal status
                statusLabel.setText("Out for Delivery");
                statusLabel.setTextFill(Color.GOLD);
                statusCircle.setFill(Color.GOLD);
            } else if ("pick up".equals(orderTypeLower)) {
                order.setOrderStatus("completed"); // Update internal status
                statusLabel.setText("Completed");
                statusLabel.setTextFill(Color.GREEN);
                statusCircle.setFill(Color.GREEN);
            }

            // Optional: visually "gray out" the order to show it’s done
            orderBox.setStyle("-fx-background-color: #d3d3d3;");
            ordersContainer.getChildren().remove(orderBox);
            ordersContainer.getChildren().add(orderBox);
        });

          
        readyForPickupButton.setOnAction(e -> {
                try {
                    markOrderReadyForPickup(order); // Update DB or internal state
                } catch (MessagingException ex) {
                    Logger.getLogger(ShowOrders.class.getName()).log(Level.SEVERE, null, ex);
                }
            readyForPickupButton.setDisable(true);  // Disable the button after it is clicked

            // Optionally change the status label to "Ready for Pickup"
            statusLabel.setText("Ready for Pickup");
            statusLabel.setTextFill(Color.BLUE);  // A distinct color for "Ready for Pickup"
            statusCircle.setFill(Color.BLUE);  // Color of the status circle to blue

            // Optional: visually update the order box to reflect this state
            orderBox.setStyle("-fx-background-color: #add8e6;"); // Light blue to indicate ready for pickup
         
        });
            
//             System.out.println("Checking order " + order.getOrderId());
//            System.out.println("Payment Status: " + order.getPaymentStatus());
//            System.out.println("Order Status: " + order.getOrderStatus());

            if ("pending verification".equalsIgnoreCase(order.getPaymentStatus())
                && !"cancelled".equalsIgnoreCase(order.getOrderStatus())
                && !"completed".equalsIgnoreCase(order.getOrderStatus())) {

               
                mainContent.getChildren().add(verifyPaymentButton);

                verifyPaymentButton.setOnAction(e -> {
                   PaymentVerificationWindow.show(
                        order, paymentStatusLabel, orderBox, ordersContainer,
                        statusLabel, statusCircle,
                        verifyPaymentButton, assignToRiderButton, orderPickedUpButton,
                        adminId
                    );
                });

            } else {
//                System.out.println("Button should be disabled or not shown.");
            }

                          if ("completed".equalsIgnoreCase(order.getOrderStatus()) || 
            "cancelled".equalsIgnoreCase(order.getOrderStatus()) || "out for delivery".equalsIgnoreCase(order.getOrderStatus())) {

            readyForPickupButton.setDisable(true);
            verifyPaymentButton.setDisable(true);
            assignToRiderButton.setDisable(true);
            orderPickedUpButton.setDisable(true);
        }

//            orderDetailsBox.getChildren().addAll(assignToRiderButton, orderPickedUpButton);

            // Expand/collapse button
            Button expandButton = new Button("▼ Show Details");
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

           
             orderDetailsBox.getChildren().addAll( orderPickedUpButton, imageView);
            orderBox.getChildren().addAll(mainContent, expandButton);

             // === ADD ORDER BOX ===
            if (status.equalsIgnoreCase("out for delivery") || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("cancelled")) {
                orderBox.setStyle("-fx-background-color: #d3d3d3;");
                ordersContainer.getChildren().add(orderBox); // Add later so it ends up at the bottom
            }
             else if (status.equalsIgnoreCase("ready for pick-up") ) {
                readyForPickupButton.setDisable(true); 
                 statusLabel.setTextFill(Color.BLUE);  // A distinct color for "Ready for Pickup"
                statusCircle.setFill(Color.BLUE);  // Color of the status circle to blue

                orderBox.setStyle("-fx-background-color: #add8e6;"); // Light blue to indicate ready for pickup

            } else {
                ordersContainer.getChildren().add(0, orderBox); // Add to the top for pending-type orders
            }
            
            

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
        


      root.getChildren().addAll(controls, scrollPane);

   
        VBox.setVgrow(scrollPane, Priority.ALWAYS); 
    }
    
   
    private void refreshOrders(List<Order> orders) {
    ordersContainer.getChildren().clear();

    for (Order order : orders) {
        VBox orderBox = new VBox(10);
        orderBox.setPadding(new Insets(10));
        orderBox.setStyle("-fx-background-color: white; -fx-border-color: lightgray;");

       // ========== MAIN CONTENT ==========
        VBox mainContent = new VBox(10);

        // === STATUS LABEL SETUP ===
        String status = order.getOrderStatus();
        Label statusLabel = new Label();
        statusLabel.setStyle("-fx-font-weight: bold;");

        String statusText = status.toLowerCase();
        Color statusColor = Color.BLACK;
        Circle statusCircle = new Circle(10);

        switch (statusText) {
            case "pending":
                statusCircle.setFill(Color.RED);
                statusColor = Color.RED;
                break;
            case "out for delivery":
                statusCircle.setFill(Color.GOLD);
                statusColor = Color.GOLD;
                break;
            case "completed":
                statusCircle.setFill(Color.GREEN);
                statusColor = Color.GREEN;
                break;
            case "cancelled":
                statusCircle.setFill(Color.GRAY);
                statusColor = Color.GRAY;
                break;
            case "ready for pickup":
                statusCircle.setFill(Color.BLUE);
                statusColor = Color.BLUE;
                break;
            default:
                statusCircle.setFill(Color.BLACK);
                statusColor = Color.BLACK;
        }

        statusLabel.setText(capitalize(statusText));
        HBox statusBox = new HBox(5, statusCircle, statusLabel);
        statusBox.setAlignment(Pos.CENTER_LEFT);

        Label orderIdLabel = new Label("Order #" + order.getOrderId());
        orderIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        HBox idStatusBox = new HBox(10, orderIdLabel, statusBox);
        idStatusBox.setAlignment(Pos.CENTER_LEFT);

        Label dateLabel = new Label("Date: " + order.getOrderDate());
        Label totalLabel = new Label(String.format("Total: ₱%.2f", order.getTotalPrice()));
        totalLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");
        
         Label nameLabel = new Label("Customer: " + order.getCustomerName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        


        // Address section
        VBox addressBox = new VBox(5);
        Label addressHeader = new Label("Delivery Address:");
        addressHeader.setStyle("-fx-font-weight: bold;");
        HBox streetBox = new HBox(5, new Label("Street:"), new Label(order.getStreet()));
        HBox barangayBox = new HBox(5, new Label("Barangay:"), new Label(order.getBarangay()));
        HBox contactBox = new HBox(5, new Label("Contact:"), new Label(order.getContactNumber()));
        addressBox.getChildren().addAll(addressHeader, streetBox, barangayBox, contactBox);

        // Order type, payment
        String orderType = order.getOrderType();
        Label orderTypeLabel = new Label("Order Type: " + orderType);
        orderTypeLabel.setStyle("-fx-font-weight: bold;");

        String paymentMethod = order.getPaymentMethod();
        String paymentStatus = order.getPaymentStatus();
        Label paymentMethodLabel = new Label("Payment Method: " + paymentMethod);
        Label paymentStatusLabel = new Label("Payment Status: " + paymentStatus);

        // Add everything to mainContent
        mainContent.getChildren().addAll(
            idStatusBox,
            dateLabel,
            totalLabel,
            nameLabel,
            addressBox,
            orderTypeLabel,
            paymentMethodLabel,
            paymentStatusLabel
        );

        // Only show pickup time if it's for pickup
        if ("pick up".equalsIgnoreCase(orderType)) {
            String pickupTime = order.getPickupTime();
            Label pickupTimeLabel = new Label("Pickup Time: " + pickupTime);
            mainContent.getChildren().add(pickupTimeLabel);
        }


        // ========== DETAILS BOX ==========
          VBox orderDetailsBox = new VBox(10);
            orderDetailsBox.setVisible(false);
            orderDetailsBox.setPadding(new Insets(10, 0, 0, 10));

            // Build details content
            for (DetailedOrderItem item : order.getOrderItems()) {
            HBox itemBox = new HBox(10);
    
             // Get the item name (with fallback to "Unknown Item" if null)
                String itemName = (item.getItemName() == null || item.getItemName().isEmpty())
                    ? "Unknown Item" 
                    : item.getItemName();

                Label itemLabel = new Label(String.format(
                    "%d x %s: ₱%.2f",  // Changed from "Item %d" to "%s" for name
                    item.getQuantity(), 
                    itemName,  // Using itemName instead of itemId
                    item.getSubtotal()
                ));
                itemLabel.setStyle("-fx-font-weight: bold;");

                VBox detailsBox = new VBox(3);
               String variationText = (item.getVariation() == null || item.getVariation().isEmpty()) 
                ? "No variation" 
                : item.getVariation();
            detailsBox.getChildren().add(new Label("Variant: " + variationText));

            // Handle instructions display
            String instructionsText = (item.getInstructions() == null || item.getInstructions().isEmpty())
                ? "No special instructions"
                : item.getInstructions();
            detailsBox.getChildren().add(new Label("Notes: " + instructionsText));

                itemBox.getChildren().addAll(itemLabel, detailsBox);
                orderDetailsBox.getChildren().add(itemBox);
            }
            
              // ✅ These should be local per order
            final Button assignToRiderButton = new Button("Assign to Rider");
            final Button orderPickedUpButton = new Button("Order Picked Up");
            final Button verifyPaymentButton = new Button("Verify Payment");
            // Add a new button for "Mark as Ready for Pickup"
            final Button readyForPickupButton = new Button("Mark as Ready for Pickup");


            if ("delivery".equalsIgnoreCase(orderType)) {
                orderDetailsBox.getChildren().add(assignToRiderButton);
                
                orderPickedUpButton.setDisable(true);
            }
           

            if ("pick up".equalsIgnoreCase(orderType)) {
            orderPickedUpButton.setText("Complete Order");
            orderPickedUpButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
            orderPickedUpButton.setDisable(false);
             mainContent.getChildren().add(readyForPickupButton);
        } else {
            orderPickedUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        }


            
            orderPickedUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
           
            assignToRiderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

          
            // Assuming you are getting the proof of delivery image path from the database (proof_of_delivery_image_path)
            String proofOfDeliveryImagePath = order.getProofOfDeliveryImagePath();
            ImageView imageView = new ImageView();
            if (status.equalsIgnoreCase("completed") && proofOfDeliveryImagePath != null && !proofOfDeliveryImagePath.isEmpty()) {
           
            imageView.setFitWidth(200);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);

            Image image = new Image("file:" + proofOfDeliveryImagePath);
            imageView.setImage(image);

            // Add only if the image is actually present
            orderBox.getChildren().add(imageView);
        }

            
           assignToRiderButton.setOnAction(assign -> {
                showRiderSelectionDialog(order);
                assignToRiderButton.setDisable(true);
                orderPickedUpButton.setDisable(false);
            });

        
          orderPickedUpButton.setOnAction(pickedUp -> {
            try {
                markOrderPickedUp(order); // Update DB or internal state
            } catch (MessagingException ex) {
                Logger.getLogger(ShowOrders.class.getName()).log(Level.SEVERE, null, ex);
            }
            readyForPickupButton.setDisable(true); 
            orderPickedUpButton.setDisable(true);
            verifyPaymentButton.setDisable(true);
            assignToRiderButton.setDisable(true);

            String orderTypeLower = orderType.toLowerCase(); // "delivery" or "pick up"

            if ("delivery".equals(orderTypeLower)) {
                order.setOrderStatus("out for delivery"); // Update internal status
                statusLabel.setText("Out for Delivery");
                statusLabel.setTextFill(Color.GOLD);
                statusCircle.setFill(Color.GOLD);
            } else if ("pick up".equals(orderTypeLower)) {
                order.setOrderStatus("completed"); // Update internal status
                statusLabel.setText("Completed");
                statusLabel.setTextFill(Color.GREEN);
                statusCircle.setFill(Color.GREEN);
            }

            // Optional: visually "gray out" the order to show it’s done
            orderBox.setStyle("-fx-background-color: #d3d3d3;");
            ordersContainer.getChildren().remove(orderBox);
            ordersContainer.getChildren().add(orderBox);
        });

          
        readyForPickupButton.setOnAction(e -> {
            try {
                markOrderReadyForPickup(order); // Update DB or internal state
            } catch (MessagingException ex) {
                Logger.getLogger(ShowOrders.class.getName()).log(Level.SEVERE, null, ex);
            }
            readyForPickupButton.setDisable(true);  // Disable the button after it is clicked

            // Optionally change the status label to "Ready for Pickup"
            statusLabel.setText("Ready for Pickup");
            statusLabel.setTextFill(Color.BLUE);  // A distinct color for "Ready for Pickup"
            statusCircle.setFill(Color.BLUE);  // Color of the status circle to blue

            // Optional: visually update the order box to reflect this state
            orderBox.setStyle("-fx-background-color: #add8e6;"); // Light blue to indicate ready for pickup
         
        });
            
//             System.out.println("Checking order " + order.getOrderId());
//            System.out.println("Payment Status: " + order.getPaymentStatus());
//            System.out.println("Order Status: " + order.getOrderStatus());

            if ("pending verification".equalsIgnoreCase(order.getPaymentStatus())
                && !"cancelled".equalsIgnoreCase(order.getOrderStatus())
                && !"completed".equalsIgnoreCase(order.getOrderStatus())) {

               
                mainContent.getChildren().add(verifyPaymentButton);

                verifyPaymentButton.setOnAction(e -> {
                    PaymentVerificationWindow.show(
                        order, paymentStatusLabel, orderBox, ordersContainer,
                        statusLabel, statusCircle,
                        verifyPaymentButton, assignToRiderButton, orderPickedUpButton,
                        adminId
                    );
                });

            } else {
//                System.out.println("Button should be disabled or not shown.");
            }

            if ("completed".equalsIgnoreCase(order.getOrderStatus()) || 
            "cancelled".equalsIgnoreCase(order.getOrderStatus()) || "out for delivery".equalsIgnoreCase(order.getOrderStatus())) {
            readyForPickupButton.setDisable(true);
            verifyPaymentButton.setDisable(true);
            assignToRiderButton.setDisable(true);
            orderPickedUpButton.setDisable(true);
        }
            
        

        // ========== EXPAND BUTTON ==========
         // Expand/collapse button
            Button expandButton = new Button("▼ Show Details");
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

           
             orderDetailsBox.getChildren().addAll( orderPickedUpButton, imageView);
            orderBox.getChildren().addAll(mainContent, expandButton);

             // === ADD ORDER BOX ===
            if (status.equalsIgnoreCase("out for delivery") || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("cancelled")) {
                orderBox.setStyle("-fx-background-color: #d3d3d3;");
                ordersContainer.getChildren().add(orderBox); // Add later so it ends up at the bottom
            }
             else if (status.equalsIgnoreCase("ready for pick-up") ) {
                readyForPickupButton.setDisable(true); 
                 statusLabel.setTextFill(Color.BLUE);  // A distinct color for "Ready for Pickup"
                statusCircle.setFill(Color.BLUE);  // Color of the status circle to blue

                orderBox.setStyle("-fx-background-color: #add8e6;"); // Light blue to indicate ready for pickup
                ordersContainer.getChildren().add(orderBox);   
            } else {
                ordersContainer.getChildren().add(0, orderBox); // Add to the top for pending-type orders
            }
      }
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
            System.out.println("Order marked as 'Ready for Pickup' successfully!");

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


    
    
    private String capitalize(String text) {
    if (text == null || text.isEmpty()) return "";
    return text.substring(0, 1).toUpperCase() + text.substring(1);
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
        riderSelectionStage.setTitle("Assign Rider to Order #" + order.getOrderId());

        VBox riderSelectionLayout = new VBox(10);
        riderSelectionLayout.setPadding(new Insets(20));

        ComboBox<Rider> riderComboBox = new ComboBox<>();

        RiderService riderService = new RiderService(connection); 
        List<Rider> riders = riderService.getAllRiders();

        // Add riders to the ComboBox
        riderComboBox.getItems().addAll(riders);

        // Label to inform the user
        Label instructionLabel = new Label("Select a rider to assign to this order:");

        Button assignButton = new Button("Assign Rider");
        assignButton.setOnAction(e -> {
            Rider selectedRider = riderComboBox.getValue();
            if (selectedRider != null) {
         
                assignOrderToRider(order.getOrderId(), selectedRider.getRiderId());

     
                riderSelectionStage.close();

              
                showAlert("Success", "Order #" + order.getOrderId() + " has been assigned to " + selectedRider.getName());
            } else {
                showAlert("Error", "Please select a rider.");
            }
        });

    
        riderSelectionLayout.getChildren().addAll(instructionLabel, riderComboBox, assignButton);

        Scene scene = new Scene(riderSelectionLayout, 300, 200);
        riderSelectionStage.setScene(scene);

      
        riderSelectionStage.show();
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



 

    public VBox getRoot() {
        return root;
    }
}

