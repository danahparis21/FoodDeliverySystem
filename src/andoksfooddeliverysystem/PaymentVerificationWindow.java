/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.*;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javax.mail.MessagingException;

public class PaymentVerificationWindow {
   


 public static void show(Order order, Label paymentStatusLabel, VBox orderBox, VBox ordersContainer,
                        Label statusLabel, Circle statusCircle,
                        Button verifyPaymentButton, Button assignToRiderButton, Button orderPickedUpButton,
                        int adminId) {
    
    // Log admin ID
    System.out.println("Admin ID received: " + adminId);
    
    // Define color palette
    String RED = "#FF3B30";      // Primary red
    String YELLOW = "#FFCC00";   // Primary yellow
    String WHITE = "#FFFFFF";    // Background white
    String DARK_RED = "#CC2A24"; // Darker red for hover
    String DARK_YELLOW = "#D9AC00"; // Darker yellow for hover
    String LIGHT_GRAY = "#F5F5F5"; // Light gray for background elements
    String DARK_TEXT = "#333333";  // Dark text color
    String MEDIUM_GRAY = "#888888"; // Medium gray for secondary text
    
    // Create and configure stage
    Stage verificationStage = new Stage();
    verificationStage.initModality(Modality.APPLICATION_MODAL);
    verificationStage.setTitle("Payment Verification");
    
    // Create main layout
    BorderPane mainLayout = new BorderPane();
    mainLayout.setStyle("-fx-background-color: " + WHITE + ";");
    
    // Create header
    HBox headerBox = new HBox();
    headerBox.setPadding(new Insets(15, 20, 15, 20));
    headerBox.setStyle("-fx-background-color: " + RED + ";");
    
    Label headerLabel = new Label("PAYMENT VERIFICATION");
    headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + WHITE + ";");
    headerBox.getChildren().add(headerLabel);
    headerBox.setAlignment(Pos.CENTER);
    
    // Add yellow accent line below header
    Rectangle accentLine = new Rectangle();
    accentLine.setHeight(4);
    accentLine.setFill(Paint.valueOf(YELLOW));
    accentLine.widthProperty().bind(mainLayout.widthProperty());
    
    // Create VBox for content
    VBox contentBox = new VBox(15);
    contentBox.setPadding(new Insets(20));
    
   
    // Title for proof of payment section
    Label proofTitleLabel = new Label("PROOF OF PAYMENT");
    proofTitleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
    
    // Create container for the payment proof
    StackPane imageContainer = new StackPane();
    imageContainer.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-background-radius: 5px; -fx-padding: 10px;");
    imageContainer.setMinHeight(200);
    
    // Add proof of payment image or message
    String proofPath = order.getProofOfPaymentImagePath();
    System.out.println("Proof path: " + proofPath);
    
    if (proofPath != null && !proofPath.isEmpty()) {
        try {
            ImageView receiptView = new ImageView(new Image("file:" + proofPath));
            receiptView.setFitWidth(300);
            receiptView.setPreserveRatio(true);
            
            // Add a scroll pane to handle large images
            ScrollPane scrollPane = new ScrollPane(receiptView);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(250);
            scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            
            imageContainer.getChildren().add(scrollPane);
        } catch (Exception e) {
            Label errorLabel = new Label("Error loading image: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: " + RED + "; -fx-font-style: italic;");
            imageContainer.getChildren().add(errorLabel);
        }
    } else {
        VBox noProofBox = new VBox(10);
        noProofBox.setAlignment(Pos.CENTER);
        
        // Add an icon indicating no image
        Label iconLabel = new Label("❌");
        iconLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: " + MEDIUM_GRAY + ";");
        
        Label noProofLabel = new Label("No proof of payment uploaded");
        noProofLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + MEDIUM_GRAY + "; -fx-font-style: italic;");
        
        noProofBox.getChildren().addAll(iconLabel, noProofLabel);
        imageContainer.getChildren().add(noProofBox);
    }
    
    // Create buttons with hover effects
    Button approveBtn = new Button("APPROVE PAYMENT");
    approveBtn.setStyle("-fx-background-color: " + YELLOW + "; " +
                       "-fx-text-fill: " + DARK_TEXT + "; " +
                       "-fx-font-weight: bold; " +
                       "-fx-font-size: 14px; " +
                       "-fx-padding: 10px 20px; " +
                       "-fx-background-radius: 5px; " +
                       "-fx-cursor: hand;");
    approveBtn.setPrefWidth(180);
    
    // Hover effects
    approveBtn.setOnMouseEntered(e -> 
        approveBtn.setStyle("-fx-background-color: " + DARK_YELLOW + "; " +
                           "-fx-text-fill: " + DARK_TEXT + "; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-padding: 10px 20px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-cursor: hand;"));
    
    approveBtn.setOnMouseExited(e -> 
        approveBtn.setStyle("-fx-background-color: " + YELLOW + "; " +
                           "-fx-text-fill: " + DARK_TEXT + "; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-padding: 10px 20px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-cursor: hand;"));
    
    Button declineBtn = new Button("DECLINE PAYMENT");
    declineBtn.setStyle("-fx-background-color: " + RED + "; " +
                       "-fx-text-fill: " + WHITE + "; " +
                       "-fx-font-weight: bold; " +
                       "-fx-font-size: 14px; " +
                       "-fx-padding: 10px 20px; " +
                       "-fx-background-radius: 5px; " +
                       "-fx-cursor: hand;");
    declineBtn.setPrefWidth(180);
    
    // Hover effects
    declineBtn.setOnMouseEntered(e -> 
        declineBtn.setStyle("-fx-background-color: " + DARK_RED + "; " +
                           "-fx-text-fill: " + WHITE + "; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-padding: 10px 20px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-cursor: hand;"));
    
    declineBtn.setOnMouseExited(e -> 
        declineBtn.setStyle("-fx-background-color: " + RED + "; " +
                           "-fx-text-fill: " + WHITE + "; " +
                           "-fx-font-weight: bold; " +
                           "-fx-font-size: 14px; " +
                           "-fx-padding: 10px 20px; " +
                           "-fx-background-radius: 5px; " +
                           "-fx-cursor: hand;"));
    
    // Button actions
    approveBtn.setOnAction(ae -> {
        Dialog<ButtonType> confirmDialog = createModernConfirmDialog(
            "Confirm Approval",
            "Are you sure you want to approve this payment?",
            "This will mark the order as Paid.",
            YELLOW
        );
        
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updatePaymentStatus(order.getOrderId(), "Paid");
            order.setPaymentStatus("Paid");
           paymentStatusLabel.setText("Payment Status: " + order.getPaymentStatus()); // ✅ use the one passed in

            verifyPaymentButton.setDisable(true);
            
            // Show success toast
            showToast("Payment approved successfully", YELLOW, DARK_TEXT);
            
            verificationStage.close();
        }
    });
    
    declineBtn.setOnAction(de -> {
        Dialog<ButtonType> confirmDialog = createModernConfirmDialog(
            "Confirm Decline",
            "Are you sure you want to decline this payment?",
            "This will cancel the order and mark the payment as Declined.",
            RED
        );
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updatePaymentStatus(order.getOrderId(), "Payment Declined");
            try {
                updateOrderStatus(order.getOrderId(), "Cancelled", adminId);
            } catch (MessagingException ex) {
                Logger.getLogger(PaymentVerificationWindow.class.getName()).log(Level.SEVERE, null, ex);
                
                // Show error toast
                showToast("Error: " + ex.getMessage(), RED, WHITE);
            }
            
            order.setPaymentStatus("Payment Declined");
            paymentStatusLabel.setText("Payment Status: Cancelled - Invalid Proof of Payment");
            order.setOrderStatus("Cancelled");
            
            // Disable all related buttons
            verifyPaymentButton.setDisable(true);
            assignToRiderButton.setDisable(true);
            orderPickedUpButton.setDisable(true);
            
            // Update status label & circle
            statusLabel.setText("Cancelled");
            statusLabel.setTextFill(Color.GRAY);
            statusCircle.setFill(Color.GRAY);
            
            // Gray the box and move to bottom
            orderBox.setStyle("-fx-background-color: #d3d3d3;");
            ordersContainer.getChildren().remove(orderBox);
            ordersContainer.getChildren().add(orderBox);
            
            // Show decline toast
            showToast("Payment declined and order cancelled", RED, WHITE);
            
            verificationStage.close();
        }
    });
    
    // Button layout
    HBox buttonBox = new HBox(15);
    buttonBox.setAlignment(Pos.CENTER);
    buttonBox.setPadding(new Insets(10, 0, 5, 0));
    buttonBox.getChildren().addAll(approveBtn, declineBtn);
    
    // Add all components to content box
    contentBox.getChildren().addAll( proofTitleLabel, imageContainer, buttonBox);
    
    // Add components to main layout
    VBox topSection = new VBox();
    topSection.getChildren().addAll(headerBox, accentLine);
    
    mainLayout.setTop(topSection);
    mainLayout.setCenter(contentBox);
    
    // Create scene and show
    Scene scene = new Scene(mainLayout, 500, 550);
    verificationStage.setScene(scene);
    verificationStage.setResizable(false);
    verificationStage.show();
}

// Helper method to create modern confirmation dialogs
private static Dialog<ButtonType> createModernConfirmDialog(String title, String header, String content, String accentColor) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle(title);
    
    // Get the dialog pane
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.setHeaderText(header);
    dialogPane.setContentText(content);
    dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
    
    // Style the dialog
    dialogPane.setStyle("-fx-background-color: white;");
    
    // Add a custom stylesheet for the buttons
    String stylesheet = 
        ".button { -fx-background-color: white; -fx-text-fill: #333333; -fx-border-color: #cccccc; -fx-border-radius: 3px; }" +
        ".button:hover { -fx-background-color: #f5f5f5; }";
    
    dialogPane.getStylesheets().add(
        PseudoClass.getPseudoClass("stylesheet").toString().replace(":", "") + "?" + stylesheet
    );
    
    // Style the OK button with the accent color
    Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
    okButton.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: " + 
                    (accentColor.equals("#FF3B30") ? "white" : "#333333") + "; -fx-border-color: transparent;");
    
    return dialog;
}

// Helper method to show toast notifications
private static void showToast(String message, String backgroundColor, String textColor) {
    Stage toastStage = new Stage();
    toastStage.initStyle(StageStyle.TRANSPARENT);
    toastStage.setAlwaysOnTop(true);
    
    Label toastLabel = new Label(message);
    toastLabel.setStyle("-fx-font-size: 14px; -fx-padding: 15px 25px; -fx-background-radius: 5px; " +
                      "-fx-background-color: " + backgroundColor + "; -fx-text-fill: " + textColor + "; " +
                      "-fx-font-weight: bold; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 3);");
    
    StackPane root = new StackPane(toastLabel);
    root.setStyle("-fx-background-color: transparent;");
    
    Scene scene = new Scene(root);
    scene.setFill(Color.TRANSPARENT);
    toastStage.setScene(scene);
    
    // Position toast at bottom center
    Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
    toastStage.setX((screenBounds.getWidth() - toastLabel.getWidth()) / 2);
    toastStage.setY(screenBounds.getHeight() - 100);
    
    // Show toast with fade-in animation
    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    
    // Auto-dismiss with fade-out after delay
    PauseTransition delay = new PauseTransition(Duration.seconds(2.5));
    FadeTransition fadeOut = new FadeTransition(Duration.millis(500), root);
    fadeOut.setFromValue(1.0);
    fadeOut.setToValue(0.0);
    fadeOut.setOnFinished(e -> toastStage.close());
    
    delay.setOnFinished(e -> fadeOut.play());
    
    // Show toast
    toastStage.show();
    fadeIn.play();
    delay.play();
}
   
  private static void updateOrderStatus(int orderId, String newStatus, int adminId) throws MessagingException {
    String updateQuery = "UPDATE orders SET status = ?, last_modified_by = ? WHERE order_id = ?";

    try (Connection connection = Database.connect();
         PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

        preparedStatement.setString(1, newStatus);
        preparedStatement.setInt(2, adminId);
        preparedStatement.setInt(3, orderId);

        int rowsAffected = preparedStatement.executeUpdate();
        if (rowsAffected > 0) {
            System.out.println("Order status updated to: " + newStatus);

            // Only notify if it's a cancellation due to declined payment
            if ("Cancelled".equalsIgnoreCase(newStatus)) {
                // Fetch customer details
                String fetchCustomerQuery = """
                        SELECT c.customer_id, c.email, c.name
                        FROM orders o
                        JOIN customers c ON o.customer_id = c.customer_id
                        WHERE o.order_id = ?
                        """;
                try (PreparedStatement customerStmt = connection.prepareStatement(fetchCustomerQuery)) {
                    customerStmt.setInt(1, orderId);
                    try (ResultSet rs = customerStmt.executeQuery()) {
                        if (rs.next()) {
                            int customerId = rs.getInt("customer_id");
                            String email = rs.getString("email");
                            String name = rs.getString("name");

                            String subject = "Order Cancelled - Payment Declined";
                            String message = """
                                    Hi %s,

                                    Unfortunately, your order #%d has been cancelled because your payment could not be verified.

                                    If this was a mistake or you’d like to place a new order, feel free to try again with a valid payment.

                                    Thank you for understanding.

                                    - The Andok's Team
                                    """.formatted(name, orderId);

                            // Insert into notifications
                          String sql = "{CALL InsertNotification(?, ?, ?, ?)}";
                        try (CallableStatement stmt = connection.prepareCall(sql)) {
                            stmt.setInt(1, customerId);
                            stmt.setString(2, message);
                            stmt.setString(3, "payment_declined");
                            stmt.setInt(4, adminId);
                            stmt.executeUpdate();
                        }

                            // Send email
                            SendEmail.sendEmail(email, subject, message);
                            System.out.println("Payment decline email sent to: " + email);
                        }
                    }
                }
            }

        } else {
            System.out.println("Failed to update order status.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




    private static void updatePaymentStatus(int orderId, String newStatus) {
        String updateQuery = "UPDATE orders SET payment_status = ? WHERE order_id = ?";
        try (Connection connection = Database.connect(); 
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, orderId);

            int rowsAffected = preparedStatement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Order status updated to: " + newStatus);
            } else {
                System.out.println("Failed to update order status.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    }
