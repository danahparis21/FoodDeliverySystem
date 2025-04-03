/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import static andoksfooddeliverysystem.Database.connect;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.List;
import javafx.geometry.Insets;
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
        scrollPane.setFitToWidth(true); // Makes content use full width
        scrollPane.setStyle("-fx-background: white; -fx-border-color: transparent;");
        // Remove the fixed height, let it fill available space
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // If you don't want horizontal scrolling

        // Create a VBox to hold all orders
        VBox ordersContainer = new VBox(10);
        ordersContainer.setPadding(new Insets(10)); // Add padding around all orders
        ordersContainer.setStyle("-fx-background-color: white;");

        // Loop through the orders and create UI elements for each order
        for (Order order : orders) {
                        VBox orderBox = new VBox(10);
            orderBox.setStyle("-fx-background-color: white; -fx-border-color: lightgray; -fx-padding: 10;");

            // Create a container for the non-expandable content
            VBox mainContent = new VBox(10);

            // Create labels for order summary with better formatting
            Label orderIdLabel = new Label("Order #" + order.getOrderId());
            orderIdLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            Label dateLabel = new Label("Date: " + order.getOrderDate());
            Label totalLabel = new Label(String.format("Total: ₱%.2f", order.getTotalPrice()));
            totalLabel.setStyle("-fx-text-fill: green; -fx-font-size: 14px;");

            // Address section with better layout
            VBox addressBox = new VBox(5);
            Label addressHeader = new Label("Delivery Address:");
            addressHeader.setStyle("-fx-font-weight: bold;");

            HBox streetBox = new HBox(5, new Label("Street:"), new Label(order.getStreet()));
            HBox barangayBox = new HBox(5, new Label("Barangay:"), new Label(order.getBarangay()));
            HBox contactBox = new HBox(5, new Label("Contact:"), new Label(order.getContactNumber()));

            addressBox.getChildren().addAll(addressHeader, streetBox, barangayBox, contactBox);

            // Add main content to container
            mainContent.getChildren().addAll(orderIdLabel, dateLabel, totalLabel, addressBox);

            // Create the expandable details section
            VBox orderDetailsBox = new VBox(10);
            orderDetailsBox.setVisible(false); // Start hidden
            orderDetailsBox.setPadding(new Insets(10, 0, 0, 10)); // Top padding to separate from address

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
            // When the assign button is clicked, we show the rider selection dialog
             uploadProofButton.setOnAction(uploadProof -> {
                Stage stage = (Stage) root.getScene().getWindow();

                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png", "*.jpeg"));
                File file = fileChooser.showOpenDialog(stage);

                if (file != null) {
                    String imagePath = file.getAbsolutePath();  // Get the file path of the uploaded image

                    // Upload the proof of delivery (either update the orders table or insert into a separate table)
                    uploadProofOfDelivery(order, imagePath);  // Or insertProofOfDelivery(order.getOrderId(), imagePath);

                    // Display the image in the ImageView
                    Image image = new Image("file:" + imagePath);
                    imageView.setImage(image);

                    // Disable the "Upload Proof" button and enable "Complete Order"
                    uploadProofButton.setDisable(true);
                    completeOrderButton.setDisable(false);
                }
            });

            // Action for "Order Picked Up" button
            completeOrderButton.setOnAction(pickedUp -> {
            // Mark the order as picked up in the database
            markOrderCompleted(order);

            // Disable the button after it's picked up
            completeOrderButton.setDisable(true);

            orderBox.setStyle("-fx-background-color: #d3d3d3;"); // Light gray color

            // Move the order box to the last row (for example, assuming 'ordersContainer' is your ScrollPane or VBox)
            ordersContainer.getChildren().remove(orderBox);  // Remove it from current position
            ordersContainer.getChildren().add(orderBox);     // Add it at the last position

            
        });


            // Add the buttons to the details box
            orderDetailsBox.getChildren().addAll(uploadProofButton, imageView, completeOrderButton);

            // Expand/collapse button
            Button expandButton = new Button("▼ Show Details");
            expandButton.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

            expandButton.setOnAction(e -> {
                boolean isExpanded = !orderDetailsBox.isVisible();
                orderDetailsBox.setVisible(isExpanded);
                expandButton.setText(isExpanded ? "▲ Hide Details" : "▼ Show Details");

                // Ensure the details appear below all content
                if (isExpanded && !orderBox.getChildren().contains(orderDetailsBox)) {
                    orderBox.getChildren().add(orderDetailsBox);
                }
            });

            // Add all components to main container
            orderBox.getChildren().addAll(mainContent, expandButton, orderDetailsBox);
            ordersContainer.getChildren().add(orderBox);
        }
        
        // Set the orders container as the content of the scroll pane
        scrollPane.setContent(ordersContainer);


        root.getChildren().add(scrollPane);
   
        VBox.setVgrow(scrollPane, Priority.ALWAYS); 
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


    private void showRiderSelectionDialog(Order order) {
        // Assuming you have a method to get the connection
        Connection connection = connect();

        // Create a new Stage (window) for the rider selection
        Stage riderSelectionStage = new Stage();
        riderSelectionStage.setTitle("Assign Rider to Order #" + order.getOrderId());

        // Create a VBox layout for the rider selection window
        VBox riderSelectionLayout = new VBox(10);
        riderSelectionLayout.setPadding(new Insets(20));

        // Create a ComboBox to display the available riders
        ComboBox<Rider> riderComboBox = new ComboBox<>();

        // Fetch riders from the database (you can use the RiderService for this)
        RiderService riderService = new RiderService(connection);  // Assuming 'connection' is your DB connection
        List<Rider> riders = riderService.getAllRiders();

        // Add riders to the ComboBox
        riderComboBox.getItems().addAll(riders);

        // Label to inform the user
        Label instructionLabel = new Label("Select a rider to assign to this order:");

        // Create a button to confirm rider assignment
        Button assignButton = new Button("Assign Rider");
        assignButton.setOnAction(e -> {
            Rider selectedRider = riderComboBox.getValue();
            if (selectedRider != null) {
                // Assign the order to the selected rider in the database
                assignOrderToRider(order.getOrderId(), selectedRider.getRiderId());

                // Close the rider selection window
                riderSelectionStage.close();

                // Optionally, show confirmation or refresh the UI
                showAlert("Success", "Order #" + order.getOrderId() + " has been assigned to " + selectedRider.getName());
            } else {
                showAlert("Error", "Please select a rider.");
            }
        });

        // Add all components to the layout
        riderSelectionLayout.getChildren().addAll(instructionLabel, riderComboBox, assignButton);

        // Set the layout to the scene of the stage
        Scene scene = new Scene(riderSelectionLayout, 300, 200);
        riderSelectionStage.setScene(scene);

        // Show the window
        riderSelectionStage.show();
    }
        private void assignOrderToRider(int orderId, int riderId) {
            // Assuming you have a method to get the connection
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
