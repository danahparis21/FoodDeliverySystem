package andoksfooddeliverysystem;

import com.mysql.cj.Session;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.*;

public class OrderSummary{
    private String currentGifPath = null;


    public Stage show(Order order, int userId) {
        Stage stage = new Stage();
        stage.setTitle("Order Summary - Order #" + order.getOrderId());

        VBox root = new VBox(10);
        root.setPadding(new Insets(15));

        // Auto-refresh label (optional)
        Label autoRefreshLabel = new Label("Auto-refreshing every 10s...");
        autoRefreshLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        // Refresh Button
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 12;");
    
        
        
       // Declare outside so you can update it later
        ImageView gifView = new ImageView();
        VBox gifBox = new VBox(); // empty for now
                // Animation GIF based on status
        String gifPath = getGifForStatus(order.getOrderStatus());
        System.out.println(order.getOrderStatus()+ " " + gifPath);
            if (gifPath != null) {
         Image gif = new Image(gifPath);
         gifView.setImage(gif);
         gifView.setFitWidth(300);
         gifView.setPreserveRatio(true);
         gifView.setSmooth(true);
         gifView.setCache(true);

         gifBox.getChildren().addAll(new Label("Status:"), gifView);
         gifBox.setAlignment(Pos.CENTER);
         root.getChildren().add(gifBox); // only add once
     }

        // Top info section
        Label orderIdLabel = new Label("Order ID: " + order.getOrderId());
        Label dateLabel = new Label("Date: " + order.getOrderDate());
        Label priceLabel = new Label("Total Price: ₱" + order.getTotalPrice());
        Label contactLabel = new Label("Contact: " + order.getContactNumber());
        Label orderTypeLabel = new Label("Order Type: " + order.getOrderType());
        Label paymentMethodLabel = new Label("Payment Method: " + order.getPaymentMethod());
        Label paymentStatusLabel = new Label("Payment Status: " + order.getPaymentStatus());
        Label orderStatusLabel = new Label("Order Status: " + order.getOrderStatus());

        VBox infoBox = new VBox(5, orderIdLabel, dateLabel, priceLabel, contactLabel,
                orderTypeLabel, paymentMethodLabel, paymentStatusLabel, orderStatusLabel);

        if (order.getOrderType().equalsIgnoreCase("Pickup")) {
            infoBox.getChildren().add(new Label("Pickup Time: " + order.getPickupTime()));
        } else {
            infoBox.getChildren().add(new Label("Address: " + order.getStreet() + ", " + order.getBarangay()));
        }

        VBox refreshBox = new VBox(3, refreshButton, autoRefreshLabel);
        HBox refreshRow = new HBox(refreshBox);
        refreshRow.setAlignment(Pos.TOP_RIGHT);
        VBox topSection = new VBox(10, refreshRow, infoBox);
        root.getChildren().add(topSection);
      
            
        if ("Completed".equalsIgnoreCase(order.getOrderStatus().trim()))
 {
            Button rateButton = new Button("⭐ Rate Order");
            rateButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 12;");

              rateButton.setOnAction(e -> new RatingWindow(order, userId)); // ✅ correct

            refreshBox.getChildren().add(rateButton);
            }

        

        // Table for items
        TableView<DetailedOrderItem> table = new TableView<>();
        table.setPrefHeight(200);

        TableColumn<DetailedOrderItem, String> nameCol = new TableColumn<>("Item");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("itemName"));

        TableColumn<DetailedOrderItem, Integer> qtyCol = new TableColumn<>("Qty");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<DetailedOrderItem, Double> subtotalCol = new TableColumn<>("Subtotal");
        subtotalCol.setCellValueFactory(new PropertyValueFactory<>("subtotal"));

        TableColumn<DetailedOrderItem, String> variationCol = new TableColumn<>("Variation");
        variationCol.setCellValueFactory(new PropertyValueFactory<>("variation"));

        TableColumn<DetailedOrderItem, String> instructionsCol = new TableColumn<>("Instructions");
        instructionsCol.setCellValueFactory(new PropertyValueFactory<>("instructions"));

        table.getColumns().addAll(nameCol, qtyCol, subtotalCol, variationCol, instructionsCol);
        table.getItems().addAll(order.getOrderItems());

        root.getChildren().add(table);

        // Image if exists
        if (order.getProofOfPaymentImagePath() != null && !order.getProofOfPaymentImagePath().isEmpty()) {
            try {
                Image proofImg = new Image("file:" + order.getProofOfPaymentImagePath(), 200, 150, true, true);
                ImageView imageView = new ImageView(proofImg);
                Label imageLabel = new Label("Proof of Payment:");
                VBox imageBox = new VBox(5, imageLabel, imageView);
                root.getChildren().add(imageBox);
            } catch (Exception e) {
                root.getChildren().add(new Label("Failed to load proof of payment image."));
            }
        }

          Button cancelButton = new Button("Cancel Order");
  if ("Pending".equalsIgnoreCase(order.getOrderStatus())) {
  

    cancelButton.setOnAction(e -> {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to cancel this order?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                boolean success = cancelOrder(order.getOrderId(), userId);

                if (success) {
                    cancelButton.setDisable(true);
                    orderStatusLabel.setText("Order Status: Cancelled");
                    new Alert(Alert.AlertType.INFORMATION, "Order has been cancelled.").showAndWait();

                    // Close the window
                    Stage currentStage = (Stage) cancelButton.getScene().getWindow();
                    currentStage.close();
                } else {
                    new Alert(Alert.AlertType.ERROR, "Failed to cancel the order.").showAndWait();
                }
            }
        });
    });

    refreshBox.getChildren().add(cancelButton);
}


    refreshButton.setOnAction(e -> {
        Order updatedOrder = OrderFetcher.fetchOrderById(order.getOrderId());

        // Update labels
        dateLabel.setText("Date: " + updatedOrder.getOrderDate());
        priceLabel.setText("Total Price: ₱" + updatedOrder.getTotalPrice());
        contactLabel.setText("Contact: " + updatedOrder.getContactNumber());
        orderStatusLabel.setText("Order Status: " + updatedOrder.getOrderStatus());
        paymentStatusLabel.setText("Payment Status: " + updatedOrder.getPaymentStatus());
        table.getItems().setAll(updatedOrder.getOrderItems());

        // Update GIF
        String updatedGifPath = getGifForStatus(updatedOrder.getOrderStatus());
        if (updatedGifPath != null) {
            Image newGif = new Image(updatedGifPath);
            ImageView newGifView = new ImageView(newGif);
            newGifView.setFitWidth(300);
            newGifView.setPreserveRatio(true);
            newGifView.setSmooth(true);
            newGifView.setCache(true);

            gifBox.getChildren().setAll(new Label("Status:"), newGifView);
        }

        // 👇 Add logic for Rate Button here
        refreshBox.getChildren().removeIf(node -> node instanceof Button && ((Button) node).getText().contains("Rate Order"));

        if ("Completed".equalsIgnoreCase(updatedOrder.getOrderStatus().trim())) {
            Button rateButton = new Button("⭐ Rate Order");
            rateButton.setStyle("-fx-font-size: 14px; -fx-padding: 6 12;");
            rateButton.setOnAction(ev -> new RatingWindow(updatedOrder, userId));
            refreshBox.getChildren().add(rateButton);
        }
        // Disable Cancel button if not Pending
        cancelButton.setDisable(!"Pending".equalsIgnoreCase(updatedOrder.getOrderStatus()));

    });

           // ✅ Wrap root VBox in a ScrollPane
            ScrollPane scrollPane = new ScrollPane(root);
            scrollPane.setFitToWidth(true); // Allows root VBox to stretch horizontally
            scrollPane.setPadding(new Insets(10));
            scrollPane.setStyle("-fx-background: #f9f9f9;"); // optional styling

            Scene scene = new Scene(scrollPane, 800, 600);
        
        stage.setScene(scene);
        stage.show();
        return stage;
    }
        

        // Remove auto-refresh functionality to avoid stacking windows
        // Timeline autoRefresh = new Timeline(...);  // This is now removed
//        Timeline autoRefresh = new Timeline(
//        new KeyFrame(Duration.seconds(10), e -> {
//            Order updatedOrder = OrderFetcher.fetchOrderById(order.getOrderId());
//
//            // Update labels
//            dateLabel.setText("Date: " + updatedOrder.getOrderDate());
//            priceLabel.setText("Total Price: ₱" + updatedOrder.getTotalPrice());
//            contactLabel.setText("Contact: " + updatedOrder.getContactNumber());
//            orderStatusLabel.setText("Order Status: " + updatedOrder.getOrderStatus());
//            table.getItems().setAll(updatedOrder.getOrderItems());
//
//            // Only update GIF if status changed
//            String updatedGifPath = getGifForStatus(updatedOrder.getOrderStatus());
//            if (updatedGifPath != null && !updatedGifPath.equals(currentGifPath)) {
//                Image newGif = new Image("file:" + updatedGifPath, false); // disable background loading
//                gifView.setImage(newGif); // gifView should already exist
//                currentGifPath = updatedGifPath;
//            }
//        })
//    );
//
//    autoRefresh.setCycleCount(Animation.INDEFINITE); // loop forever
//    autoRefresh.play(); // start it

    private boolean cancelOrder(int orderId, int userId) {
     String sql = "{CALL CancelOrder(?, ?)}";

     try (Connection conn = Database.connect();
          CallableStatement stmt = conn.prepareCall(sql)) {

         stmt.setInt(1, orderId);
         stmt.setInt(2, userId);

         int rowsAffected = stmt.executeUpdate();
         return rowsAffected > 0;

     } catch (SQLException e) {
         e.printStackTrace();
         return false;
     }
 }


   private String getGifForStatus(String status) {
    switch (status.toLowerCase()) {
        case "pending":
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/PreparingGIF.gif";
        case "out for delivery":
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/OutForDelivery.gif";
        case "completed":
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/OrderComplete.gif";
            
        case "rated":
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/OrderRated.gif";
        case "cancelled":
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/Chicken.gif";
        case "ready for pick-up":
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/ReadyForPickup.gif";
        
        default:
            return "file:///C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/Chicken.gif"; // Default
    }
}



    
}
