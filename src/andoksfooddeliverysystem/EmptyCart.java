
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.animation.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Circle;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.util.Random;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class EmptyCart {

    public static void showEmptyCartMessage() {
        Stage emptyStage = new Stage();
    emptyStage.initModality(Modality.APPLICATION_MODAL);
    emptyStage.setTitle("Empty Cart");
    emptyStage.initStyle(StageStyle.UNDECORATED); // Remove window decorations for modern look
    
    // Create main container
    StackPane root = new StackPane();
    root.setStyle("-fx-background-color: white;");
    
    // Create content container with rounded corners
    VBox contentBox = new VBox(20);
    contentBox.setMaxWidth(450);
    contentBox.setMaxHeight(700);
    contentBox.setStyle("-fx-background-color: white; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 2); " +
                        "-fx-background-radius: 20px; " +
                        "-fx-padding: 30px;");
    contentBox.setAlignment(Pos.CENTER);
    
    // Close button at top-right
    Button closeButton = new Button("✕");
    closeButton.setStyle("-fx-background-color: transparent; " +
                         "-fx-text-fill: #D32F2F; " +
                         "-fx-font-size: 18px; " +
                         "-fx-cursor: hand;");
    closeButton.setOnAction(e -> {
        // Fade out animation when closing
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> emptyStage.close());
        fadeOut.play();
    });
    
    // Container for close button
    HBox closeContainer = new HBox();
    closeContainer.setAlignment(Pos.TOP_RIGHT);
    closeContainer.getChildren().add(closeButton);
    
    // Empty cart illustration
    SVGPath cartIcon = new SVGPath();
    cartIcon.setContent("M3,6h16.5l-1.4,10H5L3,6z M8,20c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S9.1,20,8,20z M16,20c-1.1,0-2,0.9-2,2s0.9,2,2,2s2-0.9,2-2S17.1,20,16,20z");
    cartIcon.setStyle("-fx-fill: #FFC107;"); // Yellow cart
    cartIcon.setScaleX(3);
    cartIcon.setScaleY(3);
    
    Circle circle = new Circle(60);
    circle.setStyle("-fx-fill: #FFECB3;"); // Light yellow background
    
    StackPane iconContainer = new StackPane();
    iconContainer.getChildren().addAll(circle, cartIcon);
    iconContainer.setPadding(new Insets(20));
    
    // Main message
    Label mainMessage = new Label("Your cart is empty");
    mainMessage.setStyle("-fx-font-size: 24px; " +
                         "-fx-font-weight: bold; " +
                         "-fx-text-fill: #D32F2F;"); // Red text
    
    // Sub message
    Label subMessage = new Label("Hungry? Add some delicious items from our menu to your cart.");
    subMessage.setStyle("-fx-font-size: 14px; " +
                        "-fx-text-fill: #757575; " +
                        "-fx-text-alignment: center; " +
                        "-fx-wrap-text: true;");
    subMessage.setMaxWidth(350);
    subMessage.setAlignment(Pos.CENTER);
    
    // "Back to Menu" button
    Button menuButton = new Button("BROWSE MENU");
    menuButton.setPrefWidth(200);
    menuButton.setPrefHeight(45);
    menuButton.setStyle("-fx-background-color: #D32F2F; " + // Red button
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-background-radius: 25px; " +
                        "-fx-cursor: hand;");
    
    // Hover effect for button
    menuButton.setOnMouseEntered(e -> menuButton.setStyle("-fx-background-color: #FF5252; " + // Lighter red on hover
                                                          "-fx-text-fill: white; " +
                                                          "-fx-font-size: 14px; " +
                                                          "-fx-font-weight: bold; " +
                                                          "-fx-background-radius: 25px; " +
                                                          "-fx-cursor: hand;"));
    menuButton.setOnMouseExited(e -> menuButton.setStyle("-fx-background-color: #D32F2F; " +
                                                         "-fx-text-fill: white; " +
                                                         "-fx-font-size: 14px; " +
                                                         "-fx-font-weight: bold; " +
                                                         "-fx-background-radius: 25px; " +
                                                         "-fx-cursor: hand;"));
    
    // Button press effect
    menuButton.setOnMousePressed(e -> menuButton.setStyle("-fx-background-color: #B71C1C; " + // Darker red when pressed
                                                          "-fx-text-fill: white; " +
                                                          "-fx-font-size: 14px; " +
                                                          "-fx-font-weight: bold; " +
                                                          "-fx-background-radius: 25px; " +
                                                          "-fx-cursor: hand;"));
    menuButton.setOnMouseReleased(e -> menuButton.setStyle("-fx-background-color: #D32F2F; " +
                                                           "-fx-text-fill: white; " +
                                                           "-fx-font-size: 14px; " +
                                                           "-fx-font-weight: bold; " +
                                                           "-fx-background-radius: 25px; " +
                                                           "-fx-cursor: hand;"));
    
    // Close action
    menuButton.setOnAction(e -> {
        // Fade out animation when closing
        FadeTransition fadeOut = new FadeTransition(Duration.millis(300), root);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(event -> emptyStage.close());
        fadeOut.play();
    });
    
    // Food emoji decorations with random rotations
    Random random = new Random();
    for (String emoji : new String[]{"🍔", "🍕", "🍟", "🌮", "🍦"}) {
        Label foodEmoji = new Label(emoji);
        foodEmoji.setStyle("-fx-font-size: 24px;");
        foodEmoji.setRotate(random.nextInt(40) - 20); // Random rotation between -20 and 20 degrees
        
        // Position emojis around the edge of the dialog
        double angle = random.nextDouble() * 360;
        double radius = 180; // Distance from center
        double x = Math.cos(Math.toRadians(angle)) * radius;
        double y = Math.sin(Math.toRadians(angle)) * radius;
        
        StackPane.setMargin(foodEmoji, new Insets(y + 250, 0, 0, x + 225));
        root.getChildren().add(foodEmoji);
        
        // Create floating animation for each emoji
        TranslateTransition floatAnimation = new TranslateTransition(
                Duration.seconds(2 + random.nextDouble() * 2), foodEmoji);
        floatAnimation.setByY(-15 - random.nextDouble() * 15);
        floatAnimation.setCycleCount(Animation.INDEFINITE);
        floatAnimation.setAutoReverse(true);
        floatAnimation.play();
    }
    
    // Assemble content container
    contentBox.getChildren().addAll(closeContainer, iconContainer, mainMessage, subMessage, menuButton);
    
    // Add content to root
    root.getChildren().add(contentBox);
    
    // Set up scene
    Scene scene = new Scene(root, 450, 700);
    scene.setFill(Color.TRANSPARENT);
    
    // Add ability to drag the window
    final Delta dragDelta = new Delta();
    scene.setOnMousePressed(mouseEvent -> {
        dragDelta.x = emptyStage.getX() - mouseEvent.getScreenX();
        dragDelta.y = emptyStage.getY() - mouseEvent.getScreenY();
    });
    scene.setOnMouseDragged(mouseEvent -> {
        emptyStage.setX(mouseEvent.getScreenX() + dragDelta.x);
        emptyStage.setY(mouseEvent.getScreenY() + dragDelta.y);
    });
    
    // Make background semi-transparent for better focus
    scene.setFill(Color.TRANSPARENT);
    emptyStage.initStyle(StageStyle.TRANSPARENT);
    emptyStage.setScene(scene);
    
    // Add entrance animation
    root.setOpacity(0);
    FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
    fadeIn.setFromValue(0.0);
    fadeIn.setToValue(1.0);
    
    ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300), contentBox);
    scaleIn.setFromX(0.8);
    scaleIn.setFromY(0.8);
    scaleIn.setToX(1.0);
    scaleIn.setToY(1.0);
    
    ParallelTransition parallelTransition = new ParallelTransition(fadeIn, scaleIn);
    parallelTransition.play();
    
    // Show stage
    emptyStage.showAndWait();
}

// Helper class for window dragging
    private static class Delta {
        double x, y;
    }
}
