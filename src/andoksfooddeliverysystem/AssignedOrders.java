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
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public class AssignedOrders {
    private VBox root;


    public AssignedOrders(int riderId) {
        root = new VBox(10); // Adjusted spacing
     
        // Fetch orders from the database
         List<Order> orders = OrderFetcher.fetchOrdersByRider(riderId);
        

        // Create the main container with scroll pane
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true); 
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
      
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); 
        
        
        // Create a VBox to hold all orders
        VBox ordersContainer = new VBox(10);
        ordersContainer.setPadding(new Insets(10)); // Add padding around all orders
        ordersContainer.setStyle("-fx-background-color: white;");

       ordersContainer.getChildren().clear(); // Prevent duplicates if refreshing

        for (Order order : orders) {
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

            // Address section
            VBox addressBox = new VBox(5);
            Label addressHeader = new Label("Delivery Address:");
            addressHeader.setStyle("-fx-font-weight: bold;");

            HBox streetBox = new HBox(5, new Label("Street:"), new Label(order.getStreet()));
            HBox barangayBox = new HBox(5, new Label("Barangay:"), new Label(order.getBarangay()));
            HBox contactBox = new HBox(5, new Label("Contact:"), new Label(order.getContactNumber()));

            addressBox.getChildren().addAll(addressHeader, streetBox, barangayBox, contactBox);

            
      
              Button trackButton = new Button("📍 Track");
            trackButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");

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
            
            // Create HBox to hold both the addressBox and the Track button
            HBox trackAddressBox = new HBox(10); // Increase spacing if needed
            trackAddressBox.setAlignment(Pos.CENTER_LEFT); // Align items to the left
            trackAddressBox.getChildren().addAll(addressBox, trackButton);

              // Add order type (delivery/pickup)
            String orderType = order.getOrderType();  // Get the order type
            Label orderTypeLabel = new Label("Order Type: " + orderType);
            orderTypeLabel.setStyle("-fx-font-weight: bold;");
        
             // Add payment method and status
            String paymentMethod = order.getPaymentMethod();
            String paymentStatus = order.getPaymentStatus(); // e.g., Paid, Pending, etc.
            Label paymentMethodLabel = new Label("Payment Method: " + paymentMethod);
            Label paymentStatusLabel = new Label("Payment Status: " + paymentStatus);

       
             mainContent.getChildren().addAll(idStatusBox, dateLabel, totalLabel, trackAddressBox, orderTypeLabel, paymentMethodLabel, paymentStatusLabel);

             
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
            
          




            ImageView imageView = new ImageView();
            imageView.setFitWidth(200); // Set the width of the image
            imageView.setFitHeight(200); // Set the height of the image
            imageView.setPreserveRatio(true); // Maintain the aspect ratio of the image

             Button uploadProofButton = new Button("Upload Proof of Delivery");
            uploadProofButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

            Button completeOrderButton = new Button("Complete Order");
            completeOrderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            completeOrderButton.setDisable(true); // Initially disabled
          
            // Assuming you are getting the proof of delivery image path from the database (proof_of_delivery_image_path)
            String proofOfDeliveryImagePath = order.getProofOfDeliveryImagePath();

            // If the order is completed and the proof of delivery image exists, load the image
            if (status.equalsIgnoreCase("completed") && proofOfDeliveryImagePath != null && !proofOfDeliveryImagePath.isEmpty()) {
                // Load the image from the file path
                Image image = new Image("file:" + proofOfDeliveryImagePath);
                imageView.setImage(image);
            } else {
                // Optionally, set the image view to null or a placeholder if no proof image
                imageView.setImage(null); // or set a placeholder image
            }

             uploadProofButton.setOnAction(uploadProof -> {
                Stage stage = (Stage) root.getScene().getWindow();

                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
                File file = fileChooser.showOpenDialog(stage);

                if (file != null) {
                    String imagePath = file.getAbsolutePath();
                    uploadProofOfDelivery(order, imagePath);  
                    Image image = new Image("file:" + imagePath);
                    imageView.setImage(image);

                    uploadProofButton.setDisable(true);
                    completeOrderButton.setDisable(false);
                }
            });

            completeOrderButton.setOnAction(pickedUp -> {
            markOrderCompleted(order);
            completeOrderButton.setDisable(true);
            orderBox.setStyle("-fx-background-color: #d3d3d3;"); // Light gray color
            ordersContainer.getChildren().remove(orderBox);  
            ordersContainer.getChildren().add(orderBox);    

            
        });

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
            
         
            
            orderDetailsBox.getChildren().addAll(uploadProofButton, imageView, completeOrderButton);

            orderBox.getChildren().addAll(mainContent, expandButton);
            
             // === ADD ORDER BOX ===
            if (status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("cancelled")) {
                orderBox.setStyle("-fx-background-color: #d3d3d3;");
                completeOrderButton.setDisable(true);
                uploadProofButton.setDisable(true);
                
                ordersContainer.getChildren().add(orderBox); // Add later so it ends up at the bottom
            } else {
                ordersContainer.getChildren().add(0, orderBox); // Add to the top for pending-type orders
            }

        }

        // Set the orders container as the content of the scroll pane
        scrollPane.setContent(ordersContainer);


        root.getChildren().add(scrollPane);
   
        VBox.setVgrow(scrollPane, Priority.ALWAYS); 
    }
    
       private String capitalize(String text) {
    if (text == null || text.isEmpty()) return "";
    return text.substring(0, 1).toUpperCase() + text.substring(1);
}
    // Method to upload proof of delivery (image)
public void uploadProofOfDelivery(Order order, String imagePath) {
    String query = "UPDATE orders SET proof_of_delivery_image_path = ? WHERE order_id = ?";
    
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {
         
        stmt.setString(1, imagePath);  // Path to the image
        stmt.setInt(2, order.getOrderId());  // Order ID
        stmt.executeUpdate();
        
        System.out.println("Proof of delivery uploaded successfully.");
    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("Failed to upload proof of delivery.");
    }
}

    
    // Method to mark the order as picked up in the database
        private void markOrderCompleted(Order order) {
            String updateQuery = "UPDATE orders SET status = 'Completed' WHERE order_id = ?";

            try (Connection connection = Database.connect(); 
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                preparedStatement.setInt(1, order.getOrderId()); // Set the order_id

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Order marked as Completed successfully! Thank you for your Hardwork!");
                } else {
                    System.out.println("Failed to mark the order.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


 

    public VBox getRoot() {
        return root;
    }
}
