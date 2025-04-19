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
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javax.mail.MessagingException;

public class PaymentVerificationWindow {

  public static void show(Order order, Label paymentStatusLabel, VBox orderBox, VBox ordersContainer,
                            Label statusLabel, Circle statusCircle,
                            Button verifyPaymentButton, Button assignToRiderButton, Button orderPickedUpButton,
                            int adminId) {
        // Now you can use adminId here
        System.out.println("Admin ID received: " + adminId);
        
    Stage verificationStage = new Stage();
    VBox layout = new VBox(10);
    layout.setPadding(new Insets(10));

    String proofPath = order.getProofOfPaymentImagePath();
    System.out.println("Proof path: " + proofPath);

    if (proofPath != null && !proofPath.isEmpty()) {
        ImageView receiptView = new ImageView(new Image("file:" + proofPath));
        receiptView.setFitWidth(300);
        receiptView.setPreserveRatio(true);
        layout.getChildren().add(receiptView);
    } else {
        layout.getChildren().add(new Label("No proof of payment uploaded."));
    }

    Button approveBtn = new Button("Approve");
    Button declineBtn = new Button("Decline");
    HBox buttonBox = new HBox(10, approveBtn, declineBtn);
    layout.getChildren().add(buttonBox);

    approveBtn.setOnAction(ae -> {
        updatePaymentStatus(order.getOrderId(), "Paid");
        
        order.setPaymentStatus("Paid");
        paymentStatusLabel.setText("Payment Status: Paid");
        // Disable all related buttons
        verifyPaymentButton.setDisable(true);
       
        verificationStage.close();
    });

    declineBtn.setOnAction(de -> {
        
        updatePaymentStatus(order.getOrderId(), "Payment Declined");
            try {
                updateOrderStatus(order.getOrderId(), "Cancelled", adminId);
            } catch (MessagingException ex) {
                Logger.getLogger(PaymentVerificationWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
         

        order.setPaymentStatus("Payment Declined");
        
        paymentStatusLabel.setText("Payment Status: Cancelled - Invalid Proof of Payment");
      
        order.setOrderStatus("Cancelled"); // Add this line to reflect new status in the object itself

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

        verificationStage.close();
    });


    Scene scene = new Scene(layout, 350, 400);
    verificationStage.setTitle("Verify Payment Proof");
    verificationStage.setScene(scene);
    verificationStage.show();
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
