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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class PaymentVerificationWindow {

   public static void show(Order order, Label paymentStatusLabel, VBox orderBox, VBox ordersContainer,
                        Label statusLabel, Circle statusCircle,
                        Button verifyPaymentButton, Button assignToRiderButton, Button orderPickedUpButton) 

{
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
         updateOrderStatus(order.getOrderId(), "Cancelled");
         

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
   
   private static void updateOrderStatus(int orderId, String newStatus) {
    String updateQuery = "UPDATE orders SET status = ? WHERE order_id = ?";
    
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
