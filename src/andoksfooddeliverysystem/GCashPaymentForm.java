/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;
import java.io.File;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.*;
import javafx.stage.*;
import javafx.util.Duration;
import java.io.File;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;

public class GCashPaymentForm {

    private int customerID;
    private int addressId;
    private double totalPrice;
      private Button processPaymentBtn;

    public GCashPaymentForm(int customerID, int addressId, double totalPrice) {
        this.customerID = customerID;
        this.addressId = addressId;
        this.totalPrice = totalPrice;
    }

    public void showGCashPaymentForm() {
    // Create a new Stage for GCash Payment with modern styling
    Stage gcashStage = new Stage();
    gcashStage.initStyle(StageStyle.UNDECORATED);
    gcashStage.initModality(Modality.APPLICATION_MODAL);
    
    // Main container with semi-transparent overlay
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: rgba(0,0,0,0.2);");
    
    // Modern card container
    VBox cardContainer = new VBox(20);
    cardContainer.setMaxWidth(550);
    cardContainer.setMaxHeight(750);
    cardContainer.setPadding(new Insets(30));
    cardContainer.setStyle("-fx-background-color: white; " +
                          "-fx-background-radius: 15px; " +
                          "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
    cardContainer.setAlignment(Pos.TOP_CENTER);
    
    // Header with GCash logo and close button
    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(0, 0, 15, 0));
    
    // GCash logo
    HBox logoBox = new HBox(10);
    logoBox.setAlignment(Pos.CENTER_LEFT);
    
    Rectangle logoRect = new Rectangle(30, 30);
    logoRect.setFill(Color.web("#007EFF"));
    logoRect.setArcWidth(8);
    logoRect.setArcHeight(8);
    
    Text logoText = new Text("G");
    logoText.setFill(Color.WHITE);
    logoText.setFont(Font.font("System", FontWeight.BOLD, 20));
    
    StackPane logoStack = new StackPane(logoRect, logoText);
    
    Label gcashLabel = new Label("GCash Payment");
    gcashLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #007EFF;");
    
    logoBox.getChildren().addAll(logoStack, gcashLabel);
    
    // Spacer and close button
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    Button closeButton = new Button("✕");
    closeButton.setStyle("-fx-background-color: transparent; " +
                         "-fx-text-fill: #757575; " +
                         "-fx-font-size: 16px; " +
                         "-fx-cursor: hand;");
    closeButton.setOnAction(e -> {
        // Add closing animation
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> gcashStage.close());
        fadeOut.play();
    });
    
    header.getChildren().addAll(logoBox, spacer, closeButton);
    
    // Payment steps indicator
    HBox stepsBox = new HBox(5);
    stepsBox.setAlignment(Pos.CENTER);
    stepsBox.setPadding(new Insets(0, 0, 20, 0));
    
    // Step 1 circle
    StackPane step1 = createStepCircle("1", true);
    
    // Line between steps
    Line line1 = new Line(0, 0, 50, 0);
    line1.setStroke(Color.web("#007EFF"));
    line1.setStrokeWidth(2);
    
    // Step 2 circle
    StackPane step2 = createStepCircle("2", false);
    
    stepsBox.getChildren().addAll(step1, line1, step2);
    
    // Instruction Text
    VBox instructionBox = new VBox(5);
    instructionBox.setAlignment(Pos.CENTER);
    
    Label stepLabel = new Label("Step 1: Scan QR Code to Pay");
    stepLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    
    Label infoLabel = new Label("Scan this QR code with your GCash app to make payment");
    infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575; -fx-text-alignment: center;");
    infoLabel.setWrapText(true);
    
    instructionBox.getChildren().addAll(stepLabel, infoLabel);
    
    // QR Code with frame
    VBox qrContainer = new VBox(10);
    qrContainer.setAlignment(Pos.CENTER);
    qrContainer.setPadding(new Insets(15));
    qrContainer.setMaxWidth(350);
    qrContainer.setStyle("-fx-background-color: white; " +
                        "-fx-border-color: #E0E0E0; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 10px; " +
                        "-fx-background-radius: 10px;");
    
    // Load GCash QR image and resize it to fit nicely
    Image gcashQRImage = new Image("file:C:/Users/63945/Documents/AndoksFoodDeliverySystem/AndoksFoodDeliverySystem/src/icons/gcash_qrCode.jpg");
    ImageView gcashQRImageView = new ImageView(gcashQRImage);
    gcashQRImageView.setFitWidth(300);  // Adjusted width
    gcashQRImageView.setFitHeight(300); // Keep aspect ratio but limit height
    gcashQRImageView.setPreserveRatio(true);
    
    Label scanLabel = new Label("Scan with GCash App");
    scanLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #007EFF;");
    
    qrContainer.getChildren().addAll(gcashQRImageView, scanLabel);
    
    // Upload proof section
    VBox uploadSection = new VBox(20);
    uploadSection.setAlignment(Pos.CENTER);
    uploadSection.setPadding(new Insets(20, 0, 10, 0));
    uploadSection.setStyle("-fx-background-color: #F5F7FF; " +  // Light blue background
                          "-fx-background-radius: 10px; " +
                          "-fx-padding: 20px;");
    
    Label uploadLabel = new Label("Step 2: Upload Proof of Payment");
    uploadLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    
    // File selection area with icon
    HBox fileSelectionBox = new HBox(15);
    fileSelectionBox.setAlignment(Pos.CENTER);
    
    // File icon
    SVGPath fileIcon = new SVGPath();
    fileIcon.setContent("M6 2c-1.1 0-1.99.9-1.99 2L4 20c0 1.1.89 2 1.99 2H18c1.1 0 2-.9 2-2V8l-6-6H6zm7 7V3.5L18.5 9H13z");
    fileIcon.setFill(Color.web("#007EFF"));
    
    // File path field with modern styling
    TextField proofFilePathField = new TextField();
    proofFilePathField.setPromptText("No file selected");
    proofFilePathField.setPrefWidth(300);
    proofFilePathField.setEditable(false);
    proofFilePathField.setStyle("-fx-background-color: white; " +
                              "-fx-border-color: #E0E0E0; " +
                              "-fx-border-width: 1px; " +
                              "-fx-border-radius: 5px; " +
                              "-fx-background-radius: 5px; " +
                              "-fx-padding: 8px;");
    HBox.setHgrow(proofFilePathField, Priority.ALWAYS);
    
    fileSelectionBox.getChildren().addAll(fileIcon, proofFilePathField);
    
    // Upload button with blue styling
    Button uploadButton = new Button("Choose File");
    uploadButton.setStyle("-fx-background-color: #007EFF; " +
                         "-fx-text-fill: white; " +
                         "-fx-font-weight: bold; " +
                         "-fx-background-radius: 5px; " +
                         "-fx-padding: 8px 15px;");
    
    // Button hover effects
    uploadButton.setOnMouseEntered(e -> {
        uploadButton.setStyle("-fx-background-color: #0066CC; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-padding: 8px 15px;");
    });
    
    uploadButton.setOnMouseExited(e -> {
        uploadButton.setStyle("-fx-background-color: #007EFF; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 5px; " +
                             "-fx-padding: 8px 15px;");
    });
    
    uploadButton.setOnAction(e -> {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(gcashStage);
        if (selectedFile != null) {
            proofFilePathField.setText(selectedFile.getAbsolutePath());
            
            // Extract filename for display
            String filename = selectedFile.getName();
            if (filename.length() > 20) {
                filename = filename.substring(0, 17) + "...";
            }
            proofFilePathField.setText(filename);
            
            // Update UI to show the selected file
            fileIcon.setFill(Color.web("#4CAF50"));  // Change icon to green when file selected
            
            // Enable the process payment button
            processPaymentBtn.setDisable(false);
            
            // Store the full path separately
            proofFilePathField.setUserData(selectedFile.getAbsolutePath());
        }
    });
    
    uploadSection.getChildren().addAll(uploadLabel, fileSelectionBox, uploadButton);
    
    // Process payment button - initially disabled
     processPaymentBtn = new Button("Process GCash Payment");
    processPaymentBtn.setPrefWidth(250);
    processPaymentBtn.setPrefHeight(45);
    processPaymentBtn.setDisable(true);  // Disabled until file is uploaded
    processPaymentBtn.setStyle("-fx-background-color: #007EFF; " +
                              "-fx-text-fill: white; " +
                              "-fx-font-size: 15px; " +
                              "-fx-font-weight: bold; " +
                              "-fx-background-radius: 25px;");
    
    // Button hover effects
    processPaymentBtn.setOnMouseEntered(e -> {
        if (!processPaymentBtn.isDisabled()) {
            processPaymentBtn.setStyle("-fx-background-color: #0066CC; " +
                                     "-fx-text-fill: white; " +
                                     "-fx-font-size: 15px; " +
                                     "-fx-font-weight: bold; " +
                                     "-fx-background-radius: 25px;");
        }
    });
    
    processPaymentBtn.setOnMouseExited(e -> {
        if (!processPaymentBtn.isDisabled()) {
            processPaymentBtn.setStyle("-fx-background-color: #007EFF; " +
                                     "-fx-text-fill: white; " +
                                     "-fx-font-size: 15px; " +
                                     "-fx-font-weight: bold; " +
                                     "-fx-background-radius: 25px;");
        }
    });
    
    processPaymentBtn.setOnAction(e -> {
        String proofFilePath = (String) proofFilePathField.getUserData();
        if (proofFilePath == null || proofFilePath.isEmpty()) {
            showAlert("Error", "Please upload proof of payment.");
            return;
        }
        
        // Show processing animation
        ProgressIndicator progress = new ProgressIndicator();
        progress.setStyle("-fx-progress-color: white;");
        progress.setPrefSize(25, 25);
        processPaymentBtn.setGraphic(progress);
        processPaymentBtn.setText("Processing...");
        
        // Simulate processing
        PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
        pause.setOnFinished(event -> {
            // Save the order to the database with Pending Verification status
            int orderId = CheckOutWindow.saveOrderToDatabase(customerID, addressId, totalPrice, "GCash");
            if (orderId != -1) {
                // Save the proof of payment image path
                saveProofOfPayment(orderId, proofFilePath);
                
                // Close GCash window
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
                fadeOut.setFromValue(1.0);
                fadeOut.setToValue(0.0);
                fadeOut.setOnFinished(evt -> {
                    gcashStage.close();
                    
                    // Show verification pending alert
                    showVerificationPendingAlert(orderId);
                });
                fadeOut.play();
            } else {
                showAlert("Error", "Failed to place the order.");
                processPaymentBtn.setGraphic(null);
                processPaymentBtn.setText("Process GCash Payment");
            }
        });
        pause.play();
    });
    
    // Assemble the form
    cardContainer.getChildren().addAll(
        header,
        stepsBox,
        instructionBox,
        qrContainer,
        uploadSection,
        processPaymentBtn
    );
    
    // Add to root
    root.getChildren().add(cardContainer);
    
    // Make window draggable
    final Delta dragDelta = new Delta();
    header.setOnMousePressed(mouseEvent -> {
        dragDelta.x = gcashStage.getX() - mouseEvent.getScreenX();
        dragDelta.y = gcashStage.getY() - mouseEvent.getScreenY();
    });
    header.setOnMouseDragged(mouseEvent -> {
        gcashStage.setX(mouseEvent.getScreenX() + dragDelta.x);
        gcashStage.setY(mouseEvent.getScreenY() + dragDelta.y);
    });
    
    // Set up scene
    Scene scene = new Scene(root, 600, 800);
    scene.setFill(Color.TRANSPARENT);
    gcashStage.initStyle(StageStyle.TRANSPARENT);
    gcashStage.setScene(scene);
    
    // Add entrance animation
    root.setOpacity(0);
    cardContainer.setScaleX(0.95);
    cardContainer.setScaleY(0.95);
    
    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    
    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), cardContainer);
    scaleIn.setFromX(0.95);
    scaleIn.setFromY(0.95);
    scaleIn.setToX(1.0);
    scaleIn.setToY(1.0);
    scaleIn.setInterpolator(Interpolator.EASE_OUT);
    
    ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn);
    parallelTransition.play();
    
    gcashStage.show();
}

// Helper method to create styled step circles
private StackPane createStepCircle(String number, boolean active) {
    Circle circle = new Circle(15);
    circle.setFill(active ? Color.web("#007EFF") : Color.web("#E0E0E0"));
    
    Text text = new Text(number);
    text.setFill(Color.WHITE);
    text.setFont(Font.font("System", FontWeight.BOLD, 14));
    
    StackPane stack = new StackPane(circle, text);
    return stack;
}

// Method to show verification pending alert
private void showVerificationPendingAlert(int orderId) {
    // Create a new stage for the alert
    Stage alertStage = new Stage();
    alertStage.initStyle(StageStyle.TRANSPARENT);
    alertStage.initModality(Modality.APPLICATION_MODAL);
    
    // Root container with semi-transparent background
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
    
    // Alert container
    VBox alertBox = new VBox(20);
    alertBox.setMaxWidth(450);
    alertBox.setMaxHeight(500);
    alertBox.setPadding(new Insets(30));
    alertBox.setAlignment(Pos.CENTER);
    alertBox.setStyle("-fx-background-color: white; " +
                     "-fx-background-radius: 15px; " +
                     "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
    
    // Pending verification icon
    Circle pendingCircle = new Circle(40);
    pendingCircle.setFill(Color.web("#FFC107"));
    
    SVGPath pendingIcon = new SVGPath();
    pendingIcon.setContent("M12,2 C6.48,2 2,6.48 2,12 C2,17.52 6.48,22 12,22 C17.52,22 22,17.52 22,12 C22,6.48 17.52,2 12,2 Z M12,20 C7.59,20 4,16.41 4,12 C4,7.59 7.59,4 12,4 C16.41,4 20,7.59 20,12 C20,16.41 16.41,20 12,20 Z M12,6 C11.45,6 11,6.45 11,7 L11,13 C11,13.55 11.45,14 12,14 C12.55,14 13,13.55 13,13 L13,7 C13,6.45 12.55,6 12,6 Z M11,15 L13,15 L13,17 L11,17 L11,15 Z");
    pendingIcon.setFill(Color.WHITE);
    
    StackPane pendingIconPane = new StackPane(pendingCircle, pendingIcon);
    
    // Order information
    Label headingLabel = new Label("Payment Pending Verification");
    headingLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #333333;");
    
    VBox messageBox = new VBox(10);
    messageBox.setAlignment(Pos.CENTER);
    
    Label messageLabel1 = new Label("Thank you for your GCash payment!");
    messageLabel1.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
    
    Label messageLabel2 = new Label("Your payment is being verified by our team.");
    messageLabel2.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
    
    Label messageLabel3 = new Label("This usually takes less than 30 minutes.");
    messageLabel3.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
    
    messageBox.getChildren().addAll(messageLabel1, messageLabel2, messageLabel3);
    
    // Order ID information
    VBox orderInfoBox = new VBox(5);
    orderInfoBox.setAlignment(Pos.CENTER);
    orderInfoBox.setStyle("-fx-background-color: #F5F7FF; -fx-background-radius: 10px; -fx-padding: 15px;");
    
    Label orderIdLabel = new Label("Order ID: " + orderId);
    orderIdLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #007EFF;");
    
    Label statusLabel = new Label("Status: Pending Verification");
    statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #FFA000;");
    
    Label processingLabel = new Label("We'll notify you once your payment is confirmed");
    processingLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #757575; -fx-wrap-text: true;");
    processingLabel.setWrapText(true);
    processingLabel.setTextAlignment(TextAlignment.CENTER);
    
    orderInfoBox.getChildren().addAll(orderIdLabel, statusLabel, processingLabel);
    
    // Close button
    Button closeButton = new Button("OK, Got it!");
    closeButton.setPrefWidth(200);
    closeButton.setPrefHeight(40);
    closeButton.setStyle("-fx-background-color: #007EFF; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 20px;");
    
    // Button hover effects
    closeButton.setOnMouseEntered(e -> {
        closeButton.setStyle("-fx-background-color: #0066CC; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-size: 14px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 20px;");
    });
    
    closeButton.setOnMouseExited(e -> {
        closeButton.setStyle("-fx-background-color: #007EFF; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-size: 14px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 20px;");
    });
    
    closeButton.setOnAction(e -> {
        // Animate closing
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(evt -> alertStage.close());
        fadeOut.play();
    });
    
    // Assemble alert
    alertBox.getChildren().addAll(pendingIconPane, headingLabel, messageBox, orderInfoBox, closeButton);
    root.getChildren().add(alertBox);
    
    // Set up scene
    Scene scene = new Scene(root, 500, 500);
    scene.setFill(Color.TRANSPARENT);
    alertStage.setScene(scene);
    
    // Add entrance animation
    alertBox.setScaleX(0.9);
    alertBox.setScaleY(0.9);
    root.setOpacity(0);
    
    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    
    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(350), alertBox);
    scaleIn.setFromX(0.9);
    scaleIn.setFromY(0.9);
    scaleIn.setToX(1.0);
    scaleIn.setToY(1.0);
    scaleIn.setInterpolator(Interpolator.EASE_OUT);
    
    // Create a pulsing animation for the pending icon
    ScaleTransition pulse = new ScaleTransition(Duration.millis(1000), pendingCircle);
    pulse.setFromX(1.0);
    pulse.setFromY(1.0);
    pulse.setToX(1.1);
    pulse.setToY(1.1);
    pulse.setCycleCount(Animation.INDEFINITE);
    pulse.setAutoReverse(true);
    
    // Play animations
    ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn);
    parallelTransition.setOnFinished(e -> pulse.play());
    parallelTransition.play();
    
    alertStage.showAndWait();
}

// Helper class for window dragging
private static class Delta {
    double x, y;
}

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(message);
        alert.showAndWait();
    }

   
        private static void saveProofOfPayment(int orderId, String proofFilePath) {
        try (Connection conn = Database.connect()) {
            String sql = "UPDATE orders SET payment_proof_path = ? WHERE order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, proofFilePath);  // Save the path of the uploaded proof image
            pstmt.setInt(2, orderId);

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Proof of payment saved successfully!");
            } else {
                System.err.println("❌ Error saving proof of payment.");
            }
        } catch (SQLException ex) {
            System.err.println("Error saving proof of payment: " + ex.getMessage());
        }
    }

}
