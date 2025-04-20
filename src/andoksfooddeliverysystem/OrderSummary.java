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
import java.util.List;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class OrderSummary {
    private String currentGifPath = null;
    private final String PRIMARY_COLOR = "#FF5252"; // Red
    private final String SECONDARY_COLOR = "#FFD740"; // Yellow
    private final String BACKGROUND_COLOR = "#FFFFFF"; // White
    private final String LIGHT_GRAY = "#F5F5F5";
    private final String TEXT_COLOR = "#333333";

    public Stage show(Order order, int userId) {
        Stage stage = new Stage();
        stage.setTitle("Order Summary - Order #" + order.getOrderId());

        // Main container
        VBox root = new VBox(20);
        root.setStyle("-fx-background-color: " + BACKGROUND_COLOR + "; -fx-font-family: 'Segoe UI', Arial, sans-serif;");
        root.setPadding(new Insets(25));

        // ===== STATUS SECTION (PROMINENT AT TOP) =====
        String statusColor = getStatusColor(order.getOrderStatus());
        Label statusLabel = new Label(order.getOrderStatus().toUpperCase());
        statusLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + 
                             "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
        
        // Animated status indicator
        StackPane statusIndicator = createStatusIndicator(order.getOrderStatus());
        
        VBox statusBox = new VBox(15, statusLabel, statusIndicator);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 10px; " +
                           "-fx-padding: 20px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 3);");
        
        // ===== TOP SECTION WITH REFRESH CONTROL =====
        Button refreshButton = new Button("↻ Refresh");
        refreshButton.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; " +
                               "-fx-font-size: 14px; -fx-padding: 8 15; -fx-background-radius: 5px; " +
                               "-fx-cursor: hand; -fx-font-weight: bold;");
        
        // Auto-refresh label with animation
        Label autoRefreshLabel = new Label("Auto-refreshing every 10s...");
        autoRefreshLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(2), autoRefreshLabel);
        fadeTransition.setFromValue(0.5);
        fadeTransition.setToValue(1.0);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
        
        VBox refreshBox = new VBox(5, refreshButton, autoRefreshLabel);
        refreshBox.setAlignment(Pos.CENTER_RIGHT);
        
        // ===== ORDER INFO CARD =====
        VBox infoBox = createInfoCard(order);
        
        // Animation GIF based on status
        ImageView gifView = new ImageView();
        VBox gifBox = new VBox(10); 
        gifBox.setAlignment(Pos.CENTER);
        
        String gifPath = getGifForStatus(order.getOrderStatus());
        if (gifPath != null) {
            Image gif = new Image(gifPath);
            gifView.setImage(gif);
            gifView.setFitWidth(350);
            gifView.setPreserveRatio(true);
            gifView.setSmooth(true);
            gifView.setCache(true);
            
            Label gifLabel = new Label("Order Status");
            gifLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_COLOR + ";");
            
            gifBox.getChildren().addAll(gifLabel, gifView);
        }
        
        // ===== ACTION BUTTONS SECTION =====
        HBox actionButtonsBox = new HBox(15);
        actionButtonsBox.setAlignment(Pos.CENTER);
        
        // Handle Cancel button for Pending orders
        Button cancelButton = new Button("Cancel Order");
        styleCancelButton(cancelButton);
        
        if ("Completed".equalsIgnoreCase(order.getOrderStatus().trim())) {
            Button rateButton = new Button("★ Rate Order");
            styleRateButton(rateButton);
            rateButton.setOnAction(e -> new RatingWindow(order, userId));
            actionButtonsBox.getChildren().add(rateButton);
        }
        
        if ("Pending".equalsIgnoreCase(order.getOrderStatus())) {
            actionButtonsBox.getChildren().add(cancelButton);
            setupCancelButton(cancelButton, order.getOrderId(), userId);
        }
        
        // ===== ITEMS SECTION WITH IMPROVED TABLE =====
        Label itemsHeader = new Label("Order Items");
        itemsHeader.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_COLOR + ";");
        
        VBox itemsContainer = createItemsContainer(order.getOrderItems());
        
        // ===== PROOF OF PAYMENT SECTION =====
        VBox paymentProofBox = new VBox();
        if (order.getProofOfPaymentImagePath() != null && !order.getProofOfPaymentImagePath().isEmpty()) {
            try {
                Image proofImg = new Image("file:" + order.getProofOfPaymentImagePath(), 250, 0, true, true);
                ImageView imageView = new ImageView(proofImg);
                imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);");
                
                Label imageLabel = new Label("Proof of Payment");
                imageLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_COLOR + ";");
                
                paymentProofBox = new VBox(10, imageLabel, imageView);
                paymentProofBox.setAlignment(Pos.CENTER);
                paymentProofBox.setPadding(new Insets(10));
                paymentProofBox.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 8px;");
            } catch (Exception e) {
                paymentProofBox.getChildren().add(new Label("Failed to load proof of payment image."));
            }
        }
        
        // ===== ASSEMBLING THE UI =====
        HBox topSection = new HBox(20, infoBox, gifBox);
        
        // Add everything to the root container
        root.getChildren().addAll(
            refreshBox,
            statusBox,
            topSection,
            itemsHeader,
            itemsContainer,
            actionButtonsBox
        );
        
        // Add payment proof if exists
        if (!paymentProofBox.getChildren().isEmpty()) {
            root.getChildren().add(paymentProofBox);
        }
        
        // Set up refresh functionality
        setupRefreshFunctionality(refreshButton, order, userId, statusLabel, 
                                 gifBox, gifView, infoBox, itemsContainer, 
                                 cancelButton, actionButtonsBox, statusBox);
        
        // Wrap in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: " + BACKGROUND_COLOR + "; -fx-background-color: transparent;");
        
        Scene scene = new Scene(scrollPane, 900, 700);
        scene.getStylesheets().add("https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap");
        
        stage.setScene(scene);
        stage.show();
        
        // Apply entrance animation
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        return stage;
    }
    
    private StackPane createStatusIndicator(String status) {
        Circle circle = new Circle(12);
        String color = getStatusColor(status);
        circle.setFill(Color.web(color));
        
        // Add pulsating animation
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(1.5), circle);
        pulse.setFromX(0.8);
        pulse.setFromY(0.8);
        pulse.setToX(1.2);
        pulse.setToY(1.2);
        pulse.setCycleCount(Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
        
        StackPane indicator = new StackPane(circle);
        indicator.setPadding(new Insets(10));
        return indicator;
    }
    
    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "pending": return "#FFD740"; // Yellow
            case "processing": return "#29B6F6"; // Blue
            case "in transit": return "#8C9EFF"; // Purple
            case "completed": return "#66BB6A"; // Green
            case "cancelled": return "#FF5252"; // Red
            default: return "#757575"; // Gray
        }
    }
    
    private VBox createInfoCard(Order order) {
        // Create styled Labels
           HBox orderIdLabel = createInfoLabel("Order ID", String.valueOf(order.getOrderId()));
        HBox dateLabel = createInfoLabel("Date", order.getOrderDate());
        HBox priceLabel = createInfoLabel("Total Price", "₱" + order.getTotalPrice());
        HBox contactLabel = createInfoLabel("Contact", order.getContactNumber());
        HBox orderTypeLabel = createInfoLabel("Order Type", order.getOrderType());
        HBox paymentMethodLabel = createInfoLabel("Payment Method", order.getPaymentMethod());
        HBox paymentStatusLabel = createInfoLabel("Payment Status", order.getPaymentStatus());
        
        VBox infoCard = new VBox(8);
        infoCard.getChildren().addAll(
            orderIdLabel, dateLabel, priceLabel, contactLabel,
            orderTypeLabel, paymentMethodLabel, paymentStatusLabel
        );
        
        // Add address or pickup info
        if (order.getOrderType().equalsIgnoreCase("Pickup")) {
            infoCard.getChildren().add(createInfoLabel("Pickup Time", order.getPickupTime()));
        } else {
            infoCard.getChildren().add(createInfoLabel("Address", order.getStreet() + ", " + order.getBarangay()));
        }
        
        infoCard.setPadding(new Insets(20));
        infoCard.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 10px; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2);");
        
        return infoCard;
    }
    
    private HBox createInfoLabel(String label, String value) {
        Label labelText = new Label(label + ":");
        labelText.setStyle("-fx-font-weight: bold; -fx-min-width: 120px; -fx-text-fill: " + TEXT_COLOR + ";");
        
        Label valueText = new Label(value);
        valueText.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
        
        HBox container = new HBox(10, labelText, valueText);
        container.setAlignment(Pos.CENTER_LEFT);
        return container;
    }
    
    private VBox createItemsContainer(List<DetailedOrderItem> items) {
        VBox itemsBox = new VBox(15);
        itemsBox.setPadding(new Insets(15));
        itemsBox.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 10px;");
        
        int itemCount = 1;
        for (DetailedOrderItem item : items) {
            itemsBox.getChildren().add(createItemCard(item, itemCount));
            itemCount++;
        }
        
        // Summary line for total
        double total = items.stream().mapToDouble(DetailedOrderItem::getSubtotal).sum();
        Label totalLabel = new Label("Total: ₱" + String.format("%.2f", total));
        totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 0 0 0;");
        
        HBox totalBox = new HBox(totalLabel);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        itemsBox.getChildren().add(totalBox);
        
        return itemsBox;
    }
    
    private VBox createItemCard(DetailedOrderItem item, int itemNumber) {
        // Item name and price in top row
        Label nameLabel = new Label(item.getItemName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + TEXT_COLOR + ";");
        
        Label priceLabel = new Label("₱" + String.format("%.2f", item.getSubtotal()));
        priceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: " + PRIMARY_COLOR + ";");
        
        HBox topRow = new HBox(nameLabel, priceLabel);
        topRow.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(nameLabel, Priority.ALWAYS);
        
        // Details in second row
        Label qtyLabel = new Label("Qty: " + item.getQuantity());
        qtyLabel.setStyle("-fx-text-fill: #666666;");
        
        String variationText = item.getVariation() != null && !item.getVariation().isEmpty() ? 
                              "Variation: " + item.getVariation() : "";
        Label variationLabel = new Label(variationText);
        variationLabel.setStyle("-fx-text-fill: #666666;");
        
        HBox detailsRow = new HBox(20, qtyLabel);
        if (!variationText.isEmpty()) {
            detailsRow.getChildren().add(variationLabel);
        }
        
        // Instructions if any
        VBox instructionsBox = new VBox();
        if (item.getInstructions() != null && !item.getInstructions().isEmpty()) {
            Label instructionsHeader = new Label("Special Instructions:");
            instructionsHeader.setStyle("-fx-font-style: italic; -fx-text-fill: #666666;");
            
            Label instructionsText = new Label(item.getInstructions());
            instructionsText.setStyle("-fx-text-fill: #666666; -fx-wrap-text: true;");
            
            instructionsBox.getChildren().addAll(instructionsHeader, instructionsText);
            instructionsBox.setPadding(new Insets(5, 0, 0, 0));
        }
        
        // Assemble the item card
        VBox itemCard = new VBox(5, topRow, detailsRow);
        if (!instructionsBox.getChildren().isEmpty()) {
            itemCard.getChildren().add(instructionsBox);
        }
        
        itemCard.setPadding(new Insets(15));
        itemCard.setStyle("-fx-background-color: white; -fx-background-radius: 8px; " +
                         "-fx-border-color: #EEEEEE; -fx-border-radius: 8px;");
        
        return itemCard;
    }
    
    private void styleCancelButton(Button button) {
        button.setStyle("-fx-background-color: " + PRIMARY_COLOR + "; -fx-text-fill: white; " +
                       "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5px; " +
                       "-fx-cursor: hand; -fx-font-weight: bold;");
    }
    
    private void styleRateButton(Button button) {
        button.setStyle("-fx-background-color: " + SECONDARY_COLOR + "; -fx-text-fill: " + TEXT_COLOR + "; " +
                       "-fx-font-size: 14px; -fx-padding: 10 20; -fx-background-radius: 5px; " +
                       "-fx-cursor: hand; -fx-font-weight: bold;");
    }
    
    private void setupCancelButton(Button cancelButton, int orderId, int userId) {
        cancelButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, 
                                   "Are you sure you want to cancel this order?", 
                                   ButtonType.YES, ButtonType.NO);
            alert.setHeaderText("Cancel Order");
            alert.setTitle("Confirm Cancellation");
            
            // Style the alert
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + BACKGROUND_COLOR + "; -fx-font-family: 'Segoe UI';");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    boolean success = cancelOrder(orderId, userId);
                    
                    if (success) {
                        cancelButton.setDisable(true);
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
    }
    
    private void setupRefreshFunctionality(Button refreshButton, Order order, int userId,
                                         Label statusLabel, VBox gifBox, ImageView gifView,
                                         VBox infoBox, VBox itemsContainer, Button cancelButton,
                                         HBox actionButtonsBox, VBox statusBox) {
        refreshButton.setOnAction(e -> {
            Order updatedOrder = OrderFetcher.fetchOrderById(order.getOrderId());
            
            // Update status section
            statusLabel.setText(updatedOrder.getOrderStatus().toUpperCase());
            statusLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + 
                                getStatusColor(updatedOrder.getOrderStatus()) + 
                                "; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
            
            // Update status indicator
            StackPane newStatusIndicator = createStatusIndicator(updatedOrder.getOrderStatus());
            statusBox.getChildren().setAll(statusLabel, newStatusIndicator);
            
            // Rebuild info box
            infoBox.getChildren().clear();
            VBox newInfoBox = createInfoCard(updatedOrder);
            infoBox.getChildren().addAll(newInfoBox.getChildren());
            
            // Update items container
            itemsContainer.getChildren().clear();
            VBox newItemsContainer = createItemsContainer(updatedOrder.getOrderItems());
            itemsContainer.getChildren().addAll(newItemsContainer.getChildren());
            
            // Update GIF
            String updatedGifPath = getGifForStatus(updatedOrder.getOrderStatus());
            if (updatedGifPath != null) {
                Image newGif = new Image(updatedGifPath);
                gifView.setImage(newGif);
            }
            
            // Update buttons
            actionButtonsBox.getChildren().clear();
            
            if ("Completed".equalsIgnoreCase(updatedOrder.getOrderStatus().trim())) {
                Button rateButton = new Button("★ Rate Order");
                styleRateButton(rateButton);
                rateButton.setOnAction(ev -> new RatingWindow(updatedOrder, userId));
                actionButtonsBox.getChildren().add(rateButton);
            }
            
            if ("Pending".equalsIgnoreCase(updatedOrder.getOrderStatus())) {
                cancelButton.setDisable(false);
                actionButtonsBox.getChildren().add(cancelButton);
            } else {
                cancelButton.setDisable(true);
            }
            
            // Animate the refresh
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(300), statusBox);
            scaleTransition.setFromX(0.95);
            scaleTransition.setFromY(0.95);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
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
