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
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;



public class ShowOrders {
    private VBox root;

    public ShowOrders() {
        root = new VBox(10); // Adjusted spacing
     
        // Fetch orders from the database
        List<Order> orders = OrderFetcher.fetchOrders();
        

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

             mainContent.getChildren().addAll(idStatusBox, dateLabel, totalLabel, addressBox);

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

              Button orderPickedUpButton = new Button("Order Picked Up");
            orderPickedUpButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
            orderPickedUpButton.setDisable(true); // Initially disabled

            Button assignToRiderButton = new Button("Assign to Rider");
            assignToRiderButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

            ImageView imageView = new ImageView();
            imageView.setFitWidth(200); // Set the width of the image
            imageView.setFitHeight(200); // Set the height of the image
            imageView.setPreserveRatio(true); // Maintain the aspect ratio of the image

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
            
           assignToRiderButton.setOnAction(assign -> {
                showRiderSelectionDialog(order);
                assignToRiderButton.setDisable(true);
                orderPickedUpButton.setDisable(false);
            });

        
            orderPickedUpButton.setOnAction(pickedUp -> {
                markOrderPickedUp(order);
                orderPickedUpButton.setDisable(true);
                orderBox.setStyle("-fx-background-color: #d3d3d3;");
                ordersContainer.getChildren().remove(orderBox);
                ordersContainer.getChildren().add(orderBox);
            });


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

           
             orderDetailsBox.getChildren().addAll(assignToRiderButton, orderPickedUpButton, imageView);
            orderBox.getChildren().addAll(mainContent, expandButton);

             // === ADD ORDER BOX ===
            if (status.equalsIgnoreCase("out for delivery") || status.equalsIgnoreCase("completed") || status.equalsIgnoreCase("cancelled")) {
                orderBox.setStyle("-fx-background-color: #d3d3d3;");
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

  
        private void markOrderPickedUp(Order order) {
            String updateQuery = "UPDATE orders SET status = 'Out for Delivery' WHERE order_id = ?";

            try (Connection connection = Database.connect(); 
                 PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

                preparedStatement.setInt(1, order.getOrderId()); // Set the order_id

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Order marked as 'Out for Delivery' successfully!");
                } else {
                    System.out.println("Failed to mark the order.");
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

        String updateQuery = "UPDATE orders SET rider_id = ? WHERE order_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, riderId);
            preparedStatement.setInt(2, orderId);
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

