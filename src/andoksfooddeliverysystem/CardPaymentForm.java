/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;



import com.toedter.calendar.JDateChooser;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.animation.*;
import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.Duration;

public class CardPaymentForm {
     private Runnable onPaymentSuccess;


    public void setOnPaymentSuccess(Runnable callback) {
    this.onPaymentSuccess = callback;
}
    
    public void showCardPaymentForm(int customerId, int userId) {
    Stage cardStage = new Stage();
    cardStage.initStyle(StageStyle.UNDECORATED);
    cardStage.initModality(Modality.APPLICATION_MODAL);
    cardStage.setTitle("Card Payment");

    // Main container
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: rgba(0,0,0,0.3);");

    // Card form container with rounded corners
    VBox cardFormContainer = new VBox(25);
    cardFormContainer.setMaxWidth(450);
    cardFormContainer.setMaxHeight(600);
    cardFormContainer.setPadding(new Insets(30));
    cardFormContainer.setStyle("-fx-background-color: white; " +
                       "-fx-background-radius: 16px; " +
                       "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);");
    cardFormContainer.setAlignment(Pos.TOP_CENTER);

    // Top header with title and close button
    HBox header = new HBox();
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(0, 0, 10, 0));
    header.setSpacing(10);

    // Credit card icon
    SVGPath cardIcon = new SVGPath();
    cardIcon.setContent("M1,5 L19,5 C20.1,5 21,5.9 21,7 L21,17 C21,18.1 20.1,19 19,19 L1,19 C-0.1,19 -1,18.1 -1,17 L-1,7 C-1,5.9 -0.1,5 1,5 Z M1,9 L19,9 L19,7 L1,7 L1,9 Z M1,13 L5,13 L5,11 L1,11 L1,13 Z M6,13 L10,13 L10,11 L6,11 L6,13 Z");
    cardIcon.setFill(Color.web("#FFA726"));
    cardIcon.setScaleX(1.2);
    cardIcon.setScaleY(1.2);

    // Page title
    Label titleLabel = new Label("Card Payment");
    titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #424242;");
    
    HBox titleBox = new HBox(10, cardIcon, titleLabel);
    titleBox.setAlignment(Pos.CENTER_LEFT);
    
    // Close button
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
        fadeOut.setOnFinished(event -> cardStage.close());
        fadeOut.play();
    });
    
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    header.getChildren().addAll(titleBox, spacer, closeButton);
    
    // Payment method selection (could be expanded in the future)
    HBox paymentMethodBox = new HBox(10);
    paymentMethodBox.setAlignment(Pos.CENTER);
    
    // Credit card images
    HBox cardTypesBox = new HBox(5);
    cardTypesBox.setAlignment(Pos.CENTER);
    
    // Card type icons using colored rectangles as placeholders
    Rectangle visa = new Rectangle(40, 25);
    visa.setFill(Color.web("#1A1F71"));
    visa.setArcWidth(5);
    visa.setArcHeight(5);
    
    Rectangle mastercard = new Rectangle(40, 25);
    mastercard.setFill(Color.web("#EB001B"));
    mastercard.setArcWidth(5);
    mastercard.setArcHeight(5);
    
    Rectangle amex = new Rectangle(40, 25);
    amex.setFill(Color.web("#006FCF"));
    amex.setArcWidth(5);
    amex.setArcHeight(5);
    
    cardTypesBox.getChildren().addAll(visa, mastercard, amex);
    paymentMethodBox.getChildren().add(cardTypesBox);
    
    // Card Fields Container
    VBox fieldsContainer = new VBox(20);
    fieldsContainer.setAlignment(Pos.CENTER);
    fieldsContainer.setPadding(new Insets(10, 0, 10, 0));
    
    // Card Number
    VBox cardNumberBox = createFormField("Card Number", "1234 5678 9012 3456");
    TextField cardNumberField = (TextField) cardNumberBox.getChildren().get(1);
   cardNumberField.textProperty().addListener((obs, oldVal, newVal) -> {
    if (!newVal.matches("[\\d ]*")) {
        cardNumberField.setText(oldVal); // Prevent non-digit input (but allow existing spaces)
        return;
    }

    // Remove spaces to get just digits
    String digitsOnly = newVal.replaceAll(" ", "");

    // Limit to 16 digits max
    if (digitsOnly.length() > 16) {
        cardNumberField.setText(oldVal);
        return;
    }

    // Format card number with spaces every 4 digits
    StringBuilder formatted = new StringBuilder();
    for (int i = 0; i < digitsOnly.length(); i++) {
        if (i > 0 && i % 4 == 0) {
            formatted.append(" ");
        }
        formatted.append(digitsOnly.charAt(i));
    }

    // Avoid recursive loop
    if (!formatted.toString().equals(newVal)) {
        cardNumberField.setText(formatted.toString());
    }
});


    
    // Card Details Row (Expiry & CVC side by side)
    HBox cardDetailsRow = new HBox(20);
    cardDetailsRow.setAlignment(Pos.CENTER);
    
   // Expiry Date Field
    VBox expiryDateBox = createFormField("Expiry Date", "MM/YY");
    TextField expiryDateField = (TextField) expiryDateBox.getChildren().get(1);
    expiryDateField.textProperty().addListener((obs, oldVal, newVal) -> {
        // Ensure only numbers and '/' are accepted
        if (!newVal.matches("\\d*[/]?\\d*")) {
            expiryDateField.setText(oldVal);
            return;
        }

        // Insert '/' after the first two digits
        if (newVal.length() == 2 && oldVal.length() == 1) {
            expiryDateField.setText(newVal + "/");
        }

        // Limit the length of the expiry date to 5 (MM/YY)
        if (newVal.length() > 5) {
            expiryDateField.setText(oldVal);
        }
    });

    
    // CVC Field
    VBox cvcBox = createFormField("CVC", "123");
    TextField cvcField = (TextField) cvcBox.getChildren().get(1);
    cvcField.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal.matches("\\d*") || newVal.length() > 3) {
            cvcField.setText(oldVal);
        }
    });
    
    cardDetailsRow.getChildren().addAll(expiryDateBox, cvcBox);
    HBox.setHgrow(expiryDateBox, Priority.ALWAYS);
    HBox.setHgrow(cvcBox, Priority.ALWAYS);
    
    // Cardholder Name
    VBox cardholderNameBox = createFormField("Cardholder Name", "John Doe");
    TextField cardholderNameField = (TextField) cardholderNameBox.getChildren().get(1);
    
    // Save Card Checkbox with modern styling
    CheckBox saveCardCheckBox = new CheckBox("Save card for faster checkout");
    saveCardCheckBox.setStyle("-fx-text-fill: #616161; -fx-font-size: 14px;");
    
    // Styling the checkbox
    Region checkBoxRegion = (Region) saveCardCheckBox.lookup(".box");
    if (checkBoxRegion != null) {
        checkBoxRegion.setStyle("-fx-background-color: white; " +
                                "-fx-border-color: #BDBDBD; " +
                                "-fx-border-radius: 4px;");
    }
    
    // Submit Button with animation
    Button submitButton = new Button("Pay Now");
    submitButton.setPrefWidth(280);
    submitButton.setPrefHeight(45);
    submitButton.setStyle("-fx-background-color: #FF6F00; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-size: 16px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 25px; " +
                           "-fx-cursor: hand;");
    
    // Button hover effects
    submitButton.setOnMouseEntered(e -> {
        submitButton.setStyle("-fx-background-color: #F57C00; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 16px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 25px; " +
                             "-fx-cursor: hand; " +
                             "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 2);");
    });
    
    submitButton.setOnMouseExited(e -> {
        submitButton.setStyle("-fx-background-color: #FF6F00; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 16px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 25px; " +
                             "-fx-cursor: hand;");
    });
    
    submitButton.setOnMousePressed(e -> {
        submitButton.setStyle("-fx-background-color: #E65100; " +
                             "-fx-text-fill: white; " +
                             "-fx-font-size: 16px; " +
                             "-fx-font-weight: bold; " +
                             "-fx-background-radius: 25px; " +
                             "-fx-cursor: hand;");
    });
    
    submitButton.setOnAction(e -> {
        String cardNumber = cardNumberField.getText().replaceAll("\\s", ""); // Remove spaces
        String expiryDate = expiryDateField.getText();
        String cvc = cvcField.getText();
        String cardholderName = cardholderNameField.getText();
        boolean saveCard = saveCardCheckBox.isSelected();

        if (validateCardDetails(cardNumber, expiryDate, cvc, cardholderName)) {
            // Show processing animation
            ProgressIndicator progress = new ProgressIndicator();
            progress.setStyle("-fx-progress-color: #FF6F00;");
            progress.setPrefSize(40, 40);
            submitButton.setGraphic(progress);
            submitButton.setText("");
            
            // Simulate processing
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(event -> {
                if (saveCard) {
                    saveCardDetails(customerId, cardNumber, expiryDate, cvc, cardholderName, userId);
                }
                processPayment(cardNumber, expiryDate, cvc, cardholderName, saveCard);
                
                // Show success before closing
                submitButton.setGraphic(null);
                submitButton.setText("✓ Payment Successful");
                submitButton.setStyle("-fx-background-color: #43A047; " +
                                     "-fx-text-fill: white; " +
                                     "-fx-font-size: 16px; " +
                                     "-fx-font-weight: bold; " +
                                     "-fx-background-radius: 25px;");
                
                // Close after showing success
                PauseTransition successPause = new PauseTransition(Duration.seconds(1));
                successPause.setOnFinished(evt -> {
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
                    fadeOut.setFromValue(1.0);
                    fadeOut.setToValue(0.0);
                    fadeOut.setOnFinished(e2 -> cardStage.close());
                    fadeOut.play();
                });
                successPause.play();
            });
            pause.play();
        } else {
            // Show validation error
            submitButton.setText("Please check card details");
            submitButton.setStyle("-fx-background-color: #D32F2F; " +
                                 "-fx-text-fill: white; " +
                                 "-fx-font-size: 14px; " +
                                 "-fx-font-weight: bold; " +
                                 "-fx-background-radius: 25px;");
            
            // Reset button after showing error
            PauseTransition errorPause = new PauseTransition(Duration.seconds(2));
            errorPause.setOnFinished(evt -> {
                submitButton.setText("Pay Now");
                submitButton.setStyle("-fx-background-color: #FF6F00; " +
                                     "-fx-text-fill: white; " +
                                     "-fx-font-size: 16px; " +
                                     "-fx-font-weight: bold; " +
                                     "-fx-background-radius: 25px; " +
                                     "-fx-cursor: hand;");
            });
            errorPause.play();
        }
        
           
    });
    
    // Add secure payment message
    HBox securePaymentBox = new HBox(5);
    securePaymentBox.setAlignment(Pos.CENTER);
    
    SVGPath lockIcon = new SVGPath();
    lockIcon.setContent("M4,8 L4,6 C4,3.8 5.8,2 8,2 C10.2,2 12,3.8 12,6 L12,8 L4,8 Z M8,12 C8.6,12 9,11.6 9,11 C9,10.4 8.6,10 8,10 C7.4,10 7,10.4 7,11 C7,11.6 7.4,12 8,12 Z");
    lockIcon.setFill(Color.web("#757575"));
    lockIcon.setScaleX(0.8);
    lockIcon.setScaleY(0.8);
    
    Label secureLabel = new Label("Secure payment");
    secureLabel.setStyle("-fx-text-fill: #757575; -fx-font-size: 12px;");
    
    securePaymentBox.getChildren().addAll(lockIcon, secureLabel);
    
    // Assemble the form
    fieldsContainer.getChildren().addAll(
        cardNumberBox,
        cardDetailsRow,
        cardholderNameBox,
        saveCardCheckBox
    );
    
    cardFormContainer.getChildren().addAll(
        header,
        paymentMethodBox,
        fieldsContainer,
        submitButton,
        securePaymentBox
    );
    
    root.getChildren().add(cardFormContainer);
    
    // Make window draggable
    final Delta dragDelta = new Delta();
    cardFormContainer.setOnMousePressed(mouseEvent -> {
        dragDelta.x = cardStage.getX() - mouseEvent.getScreenX();
        dragDelta.y = cardStage.getY() - mouseEvent.getScreenY();
    });
    cardFormContainer.setOnMouseDragged(mouseEvent -> {
        cardStage.setX(mouseEvent.getScreenX() + dragDelta.x);
        cardStage.setY(mouseEvent.getScreenY() + dragDelta.y);
    });
    
    Scene scene = new Scene(root, 450, 600);
    scene.setFill(Color.TRANSPARENT);
    cardStage.initStyle(StageStyle.TRANSPARENT);
    cardStage.setScene(scene);
    
    // Add entrance animation
    root.setOpacity(0);
    cardFormContainer.setScaleX(0.9);
    cardFormContainer.setScaleY(0.9);
    
    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    
    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), cardFormContainer);
    scaleIn.setFromX(0.9);
    scaleIn.setFromY(0.9);
    scaleIn.setToX(1.0);
    scaleIn.setToY(1.0);
    scaleIn.setInterpolator(Interpolator.EASE_OUT);
       

    
    ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn);
    parallelTransition.play();
    
    cardStage.showAndWait();
}

// Helper method to create consistent form fields
private VBox createFormField(String labelText, String promptText) {
    VBox field = new VBox(5);
    field.setAlignment(Pos.CENTER_LEFT);
    
    Label label = new Label(labelText);
    label.setStyle("-fx-text-fill: #616161; -fx-font-size: 13px; -fx-font-weight: bold;");
    
    TextField textField = new TextField();
    textField.setPromptText(promptText);
    textField.setStyle("-fx-background-color: #F5F5F5; " +
                      "-fx-border-color: transparent; " +
                      "-fx-background-radius: 8px; " +
                      "-fx-padding: 12px; " +
                      "-fx-font-size: 14px;");
    
    // Add focus effect
    textField.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
        if (isFocused) {
            textField.setStyle("-fx-background-color: white; " +
                              "-fx-border-color: #FF6F00; " +
                              "-fx-border-width: 2px; " +
                              "-fx-background-radius: 8px; " +
                              "-fx-border-radius: 8px; " +
                              "-fx-padding: 12px; " +
                              "-fx-font-size: 14px;");
        } else {
            textField.setStyle("-fx-background-color: #F5F5F5; " +
                              "-fx-border-color: transparent; " +
                              "-fx-background-radius: 8px; " +
                              "-fx-padding: 12px; " +
                              "-fx-font-size: 14px;");
        }
    });
    
    field.getChildren().addAll(label, textField);
    return field;
}

// Helper method for card validation
private boolean validateCardDetails(String cardNumber, String expiryDate, String cvc, String cardholderName) {
    // Basic validation - improve this based on your requirements
    if (cardNumber == null || cardNumber.length() < 15 || cardNumber.length() > 19) return false;
    if (expiryDate == null || !expiryDate.matches("\\d{2}/\\d{2}")) return false;
    if (cvc == null || cvc.length() < 3) return false;
    if (cardholderName == null || cardholderName.trim().isEmpty()) return false;
    
    return true;
}

// Drag helper class
private static class Delta {
    double x, y;
}

    public void saveCardDetails(int customerId, String cardNumber, String expiryDate, String cvc, String cardholderName, int userId) {
        // Example: Use your database connection logic to save the card info
        String query = "INSERT INTO saved_cards (customer_id, card_number, expiry_date, cvc, cardholder_name, is_saved, last_modified_by) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.connect();
            PreparedStatement stmt = conn.prepareStatement(query)){
            stmt.setInt(1, customerId);  // Assuming you have a currentUserId variable
            stmt.setString(2, cardNumber);
            stmt.setString(3, expiryDate);
            stmt.setString(4, cvc);
            stmt.setString(5, cardholderName);
            stmt.setBoolean(6, true);  // Card saved for future use
            stmt.setInt(7, userId);
            stmt.executeUpdate();
            System.out.println("Card saved successfully");
        } catch (SQLException e) {
            System.out.println("Error saving card details: " + e.getMessage());
        }
    }


    private void processPayment(String cardNumber, String expiryDate, String cvc, String cardholderName, boolean saveCard) {
    // Payment logic here (e.g. call API, verify etc.)
    System.out.println("Processing payment for card: " + cardNumber);
    
    // Create and show beautiful alert dialog
    showPaymentSuccessAlert(cardholderName, cardNumber);
    
}

public void showPaymentSuccessAlert(String cardholderName, String cardNumber) {
    
         
    // Create a custom alert stage
    Stage alertStage = new Stage();
    alertStage.initStyle(StageStyle.TRANSPARENT);
    alertStage.initModality(Modality.APPLICATION_MODAL);
    
    // Main container with semi-transparent background
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: rgba(0,0,0,0.3);");
    
    // Alert container
    VBox alertBox = new VBox(15);
    alertBox.setMaxWidth(400);
    alertBox.setMaxHeight(300);
    alertBox.setPadding(new Insets(25));
    alertBox.setAlignment(Pos.CENTER);
    alertBox.setStyle("-fx-background-color: white; " +
                      "-fx-background-radius: 10px; " +
                      "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
    
    // Success icon (checkmark in circle)
    Circle successCircle = new Circle(40);
    successCircle.setFill(Color.web("#4CAF50"));
    
    SVGPath checkmark = new SVGPath();
    checkmark.setContent("M5,12 L10,17 L20,7");
    checkmark.setStroke(Color.WHITE);
    checkmark.setStrokeWidth(3);
    checkmark.setFill(Color.TRANSPARENT);
    
    StackPane iconPane = new StackPane(successCircle, checkmark);
    iconPane.setPadding(new Insets(10));
    
    // Heading
    Label heading = new Label("Payment Successful!");
    heading.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #212121;");
    
    // Message with formatted card number
    String maskedCardNumber = "xxxx xxxx xxxx " + cardNumber.substring(Math.max(0, cardNumber.length() - 4));
    VBox messageBox = new VBox(5);
    messageBox.setAlignment(Pos.CENTER);
    
    Label message1 = new Label("Thank you, " + cardholderName);
    message1.setStyle("-fx-font-size: 16px; -fx-text-fill: #424242;");
    
    Label message2 = new Label("Your payment with card " + maskedCardNumber);
    message2.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
    
    Label message3 = new Label("has been processed successfully.");
    message3.setStyle("-fx-font-size: 14px; -fx-text-fill: #757575;");
    
    messageBox.getChildren().addAll(message1, message2, message3);
    
    // Order ID and timestamp
//    String orderId = generateOrderId();
    String timestamp = getCurrentTimestamp();
    
    VBox orderInfoBox = new VBox(3);
    orderInfoBox.setAlignment(Pos.CENTER);
    orderInfoBox.setPadding(new Insets(10, 0, 10, 0));
    orderInfoBox.setStyle("-fx-background-color: #F5F5F5; -fx-background-radius: 5px; -fx-padding: 10px;");
    
//    Label orderIdLabel = new Label("Order ID: " + orderId);
//    orderIdLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #616161;");
//    
    Label timestampLabel = new Label("Time: " + timestamp);
    timestampLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #616161;");
    
    orderInfoBox.getChildren().addAll( timestampLabel);
    
    // Close button
    Button closeButton = new Button("Close");
    closeButton.setPrefWidth(200);
    closeButton.setPrefHeight(40);
    closeButton.setStyle("-fx-background-color: #FF6F00; " +
                         "-fx-text-fill: white; " +
                         "-fx-font-size: 14px; " +
                         "-fx-font-weight: bold; " +
                         "-fx-background-radius: 20px;");
    
    // Button hover effects
    closeButton.setOnMouseEntered(e -> {
        closeButton.setStyle("-fx-background-color: #F57C00; " +
                           "-fx-text-fill: white; " +
                           "-fx-font-size: 14px; " +
                           "-fx-font-weight: bold; " +
                           "-fx-background-radius: 20px;");
    });
    
    closeButton.setOnMouseExited(e -> {
        closeButton.setStyle("-fx-background-color: #FF6F00; " +
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
    
//    // Add invisible email receipt button that appears with animation
//    Button emailButton = new Button("Email Receipt");
//    emailButton.setOpacity(0);
//    emailButton.setPrefWidth(200);
//    emailButton.setPrefHeight(30);
//    emailButton.setStyle("-fx-background-color: transparent; " +
//                        "-fx-text-fill: #FF6F00; " +
//                        "-fx-font-size: 12px; " +
//                        "-fx-border-color: #FF6F00; " +
//                        "-fx-border-width: 1px; " +
//                        "-fx-border-radius: 20px; " +
//                        "-fx-background-radius: 20px;");
    
    // Assemble alert container
    alertBox.getChildren().addAll(iconPane, heading, messageBox, orderInfoBox, closeButton);
    root.getChildren().add(alertBox);
    
    // Set up scene
    Scene scene = new Scene(root, 500, 500);
    scene.setFill(Color.TRANSPARENT);
    alertStage.setScene(scene);
    
    // Animations for the alert
    // 1. Initial animation - fade in the alert
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
    
    // 2. Animate the check mark drawing
    PathTransition checkmarkDraw = new PathTransition();
    checkmarkDraw.setDuration(Duration.millis(500));
    checkmarkDraw.setNode(new Circle(2, Color.WHITE));
    checkmarkDraw.setPath(checkmark);
    checkmarkDraw.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
    checkmarkDraw.setInterpolator(Interpolator.EASE_OUT);
    
//    // 3. Fade in the email receipt button after everything else
//    FadeTransition emailFadeIn = new FadeTransition(Duration.millis(500), emailButton);
//    emailFadeIn.setDelay(Duration.millis(1000));
//    emailFadeIn.setFromValue(0.0);
//    emailFadeIn.setToValue(1.0);
//    
    // Create a sequence of animations
    SequentialTransition sequence = new SequentialTransition(
        new ParallelTransition(fadeIn, scaleIn),
        checkmarkDraw
    );
    
    // Play the animation and show the alert
    
    // Play the animation and then show the alert
       sequence.setOnFinished(event -> {
        Platform.runLater(() -> {
            alertStage.showAndWait();
            
            if (onPaymentSuccess != null) {
                onPaymentSuccess.run();
            }
        });
    });
    sequence.play();
    
    
   


}

private String getCurrentTimestamp() {
    // Get current timestamp
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss");
    return now.format(formatter);
}
}
