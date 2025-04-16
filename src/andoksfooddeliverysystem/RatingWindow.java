/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
public class RatingWindow {
    // Color constants for the Andok's theme
    private static final Color ANDOKS_RED = Color.web("#E62E2E");
    private static final Color ANDOKS_DARK_RED = Color.web("#B71C1C");
    private static final Color ANDOKS_YELLOW = Color.web("#FFD700");
    private static final Color ANDOKS_WHITE = Color.web("#FFFFFF");
    private static final Color ANDOKS_LIGHT_GRAY = Color.web("#F5F5F5");
    
    // Star rating values
    private int foodRatingValue = 0;
    private int deliveryRatingValue = 0;
    
    public RatingWindow(Order order) {
        Stage ratingStage = new Stage();
        ratingStage.setTitle("Andok's - Rate Your Order");
        ratingStage.initStyle(StageStyle.DECORATED);
        
        // Main container with gradient background
        BorderPane mainContainer = new BorderPane();
        
        // Create header with logo and title
        HBox header = createHeader();
        mainContainer.setTop(header);
        
        // Content container
        VBox contentContainer = new VBox(20);
        contentContainer.setPadding(new Insets(30, 25, 30, 25));
        contentContainer.setAlignment(Pos.TOP_CENTER);
        
        // Scrollable area for content
        ScrollPane scrollPane = new ScrollPane(contentContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("content-scroll");
        mainContainer.setCenter(scrollPane);
        
        // Food rating section
        contentContainer.getChildren().add(createSectionHeader("How was your food?"));
        
        // Star rating for food with animation
        HBox foodStars = createAnimatedStarRating(rating -> foodRatingValue = rating);
        contentContainer.getChildren().add(foodStars);
        
        // Food feedback textfield
        TextArea foodReview = createStylizedTextArea("Tell us what you thought about your meal...");
        contentContainer.getChildren().add(foodReview);
        
        // Only show delivery rating if it's not pickup
        TextArea deliveryReview = null;
        if (!order.getOrderType().replaceAll("\\s", "").equalsIgnoreCase("Pickup")) {
            // Add some spacing
            contentContainer.getChildren().add(new Separator());
            
            // Delivery rating section
            contentContainer.getChildren().add(createSectionHeader("How was your delivery?"));
            
            // Star rating for delivery with animation
            HBox deliveryStars = createAnimatedStarRating(rating -> deliveryRatingValue = rating);
            contentContainer.getChildren().add(deliveryStars);
            
            // Delivery feedback textfield
            deliveryReview = createStylizedTextArea("Tell us about your delivery experience...");
            contentContainer.getChildren().add(deliveryReview);
        }

        // Add some spacing before submit button
        contentContainer.getChildren().add(new Separator());
        
        // Submit button
        Button submitBtn = createGradientButton("SUBMIT REVIEW");
        HBox buttonContainer = new HBox(submitBtn);
        buttonContainer.setAlignment(Pos.CENTER);
        contentContainer.getChildren().add(buttonContainer);
        
        // Capture the final reference for delivery review for use in lambda
        final TextArea finalDeliveryReview = deliveryReview;
        
        submitBtn.setOnAction(e -> {
            boolean success;
            String foodText = foodReview.getText().trim();
            
            // Validate input
            if (foodRatingValue == 0) {
                showAnimatedAlert("Please rate your food before submitting.");
                return;
            }
            
            if (order.getOrderType().replaceAll("\\s", "").equalsIgnoreCase("Pickup")) {
                success = savePickupRatingToDB(order.getOrderId(), foodRatingValue, foodText);
            } else {
                // Validate delivery rating
                if (deliveryRatingValue == 0) {
                    showAnimatedAlert("Please rate your delivery before submitting.");
                    return;
                }
                
                String deliveryText = finalDeliveryReview.getText().trim();
                success = saveRatingToDB(order.getOrderId(), foodRatingValue, foodText, deliveryRatingValue, deliveryText);
            }
            
            if (success) {
                showThankYouAnimation(ratingStage);
            } else {
                showAnimatedAlert("We couldn't save your review. Please try again.");
            }
        });
        
        // Apply styles
        Scene scene = new Scene(mainContainer, 450, 650);
        scene.getStylesheets().add(getClass().getResource("/styles/rating.css").toExternalForm());
        
        // Set background gradient for the scene
        setBackgroundGradient(mainContainer);
        
        ratingStage.setScene(scene);
        ratingStage.setResizable(false);
        animateWindowOpen(ratingStage);
    }
    
    private HBox createHeader() {
        HBox header = new HBox(15);
        header.setPadding(new Insets(15));
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Create gradient background for header
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, ANDOKS_RED),
                new Stop(1, ANDOKS_DARK_RED));
        
        BackgroundFill backgroundFill = new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY);
        header.setBackground(new Background(backgroundFill));
        
        // Logo - replace with actual Andok's logo path
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/icons/Andoks.png")));
        logo.setFitHeight(40);
        logo.setPreserveRatio(true);
        
        // Title
        Label title = new Label("Rate Your Experience");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        title.setTextFill(ANDOKS_WHITE);
        
        header.getChildren().addAll(logo, title);
        
        // Add drop shadow
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.4));
        shadow.setOffsetY(3);
        shadow.setRadius(5);
        header.setEffect(shadow);
        
        return header;
    }
    
    private Label createSectionHeader(String text) {
        Label header = new Label(text);
        header.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        header.setTextFill(ANDOKS_DARK_RED);
        header.setPadding(new Insets(10, 0, 5, 0));
        return header;
    }
    
    private void setBackgroundGradient(Pane pane) {
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, ANDOKS_WHITE),
                new Stop(1, ANDOKS_LIGHT_GRAY));
        
        BackgroundFill backgroundFill = new BackgroundFill(gradient, CornerRadii.EMPTY, Insets.EMPTY);
        pane.setBackground(new Background(backgroundFill));
    }
    
   private HBox createAnimatedStarRating(RatingChangeListener listener) {
        HBox starsContainer = new HBox(8);
        starsContainer.setAlignment(Pos.CENTER);
        starsContainer.setPadding(new Insets(10, 0, 15, 0));

        ImageView[] stars = new ImageView[5];

        // Create a proper implementation of the listener that stores the rating
        final RatingChangeListener ratingListener = new RatingChangeListener() {
            private int currentRating = 0;

            @Override
            public void onRatingChanged(int rating) {
                currentRating = rating;
            }

            @Override
            public int getCurrentRating() {
                return currentRating;
            }
        };

        // Create 5 stars
        for (int i = 0; i < 5; i++) {
            final int starIndex = i + 1;
            ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/icons/star_empty.png")));
            star.setFitHeight(45);
            star.setFitWidth(45);

            // Add hover effect
            star.setOnMouseEntered(e -> {
                highlightStars(stars, starIndex);

                // Add pulse animation on hover
                ScaleTransition pulse = new ScaleTransition(Duration.millis(300), star);
                pulse.setToX(1.2);
                pulse.setToY(1.2);
                pulse.play();
            });

            star.setOnMouseExited(e -> {
                // Use the stored rating from the listener
                resetStars(stars, ratingListener.getCurrentRating());

                // Reset scale
                ScaleTransition pulse = new ScaleTransition(Duration.millis(300), star);
                pulse.setToX(1.0);
                pulse.setToY(1.0);
                pulse.play();
            });

            star.setOnMouseClicked(e -> {
                // Update the listener with the new rating
                ratingListener.onRatingChanged(starIndex);
                // Also update the original listener passed in
                listener.onRatingChanged(starIndex);
                animateStarSelection(stars, starIndex);
            });

            // Add to container
            stars[i] = star;
            starsContainer.getChildren().add(star);
        }

        return starsContainer;
    }
    
    private void highlightStars(ImageView[] stars, int count) {
        for (int i = 0; i < stars.length; i++) {
            Image starImage = (i < count) 
                ? new Image(getClass().getResourceAsStream("/icons/star_filled.png"))
                : new Image(getClass().getResourceAsStream("/icons/star_empty.png"));
            stars[i].setImage(starImage);
        }
    }
    
    private void resetStars(ImageView[] stars, int selectedCount) {
        highlightStars(stars, selectedCount);
    }
    
    private void animateStarSelection(ImageView[] stars, int count) {
        highlightStars(stars, count);
        
        // Animate the stars being selected
        for (int i = 0; i < count; i++) {
            final ImageView star = stars[i];
            
            // First grow
            ScaleTransition growTransition = new ScaleTransition(Duration.millis(150), star);
            growTransition.setToX(1.4);
            growTransition.setToY(1.4);
            
            // Then shrink back
            ScaleTransition shrinkTransition = new ScaleTransition(Duration.millis(150), star);
            shrinkTransition.setToX(1.0);
            shrinkTransition.setToY(1.0);
            
            // Play sequence with delay based on star position
            SequentialTransition sequence = new SequentialTransition(star, 
                    new PauseTransition(Duration.millis(i * 80)), 
                    growTransition, 
                    shrinkTransition);
            sequence.play();
        }
    }
    
    private TextArea createStylizedTextArea(String promptText) {
        TextArea textArea = new TextArea();
        textArea.setPromptText(promptText);
        textArea.setPrefRowCount(3);
        textArea.setWrapText(true);
        textArea.getStyleClass().add("custom-text-area");
        
        // Apply styles programmatically
        textArea.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E0E0E0;" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;" +
            "-fx-padding: 10px;" +
            "-fx-font-family: 'Poppins';" +
            "-fx-font-size: 14px;"
        );
        
        return textArea;
    }
    
    private Button createGradientButton(String text) {
        Button button = new Button(text);
        button.setPrefSize(200, 50);
        button.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
        button.getStyleClass().add("submit-button");
        
        // Style the button programmatically with gradient
        button.setStyle(
            "-fx-background-color: linear-gradient(to right, #E62E2E, #B71C1C);" +
            "-fx-text-fill: white;" +
            "-fx-background-radius: 25px;" +
            "-fx-cursor: hand;" +
            "-fx-padding: 10px 30px;" +
            "-fx-font-weight: bold;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to right, #B71C1C, #E62E2E);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10px 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 15, 0, 0, 7);"
            );
            
            // Add scale animation on hover
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.05);
            scaleTransition.setToY(1.05);
            scaleTransition.play();
        });
        
        button.setOnMouseExited(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to right, #E62E2E, #B71C1C);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10px 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 5);"
            );
            
            // Scale back to normal on mouse exit
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
        
        // Add click effect
        button.setOnMousePressed(e -> {
            button.setStyle(
                "-fx-background-color: linear-gradient(to right, #AB1919, #951717);" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 25px;" +
                "-fx-cursor: hand;" +
                "-fx-padding: 10px 30px;" +
                "-fx-font-weight: bold;" +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 2);"
            );
        });
        
        return button;
    }
    
    private void showAnimatedAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Andok's");
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Style the alert
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStyleClass().add("custom-alert");
        dialogPane.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #E62E2E;" +
            "-fx-border-width: 2px;" +
            "-fx-border-radius: 5px;" +
            "-fx-font-family: 'Poppins';"
        );
        
        // Add animation
        Stage alertStage = (Stage) dialogPane.getScene().getWindow();
        alertStage.setOpacity(0);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), dialogPane);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        Platform.runLater(() -> {
            alertStage.setOpacity(1);
        });
        
        alert.showAndWait();
    }
    
    private void showThankYouAnimation(Stage parentStage) {
        // Create thank you dialog
        Stage thankYouStage = new Stage();
        thankYouStage.initOwner(parentStage);
        
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        
        // Create gradient background
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, ANDOKS_RED),
                new Stop(1, ANDOKS_DARK_RED));
        
        BackgroundFill backgroundFill = new BackgroundFill(gradient, new CornerRadii(10), Insets.EMPTY);
        content.setBackground(new Background(backgroundFill));
        
        // Thank you message
        Label thankYouLabel = new Label("Thank You!");
        thankYouLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 24));
        thankYouLabel.setTextFill(ANDOKS_WHITE);
        
        Label messageLabel = new Label("We appreciate your feedback!");
        messageLabel.setFont(Font.font("Poppins", 16));
        messageLabel.setTextFill(ANDOKS_WHITE);
        
        // Add animated stars around text
        StackPane starContainer = new StackPane();
        starContainer.getChildren().addAll(thankYouLabel);
        
        // Add animation for stars
        for (int i = 0; i < 5; i++) {
            ImageView star = new ImageView(new Image(getClass().getResourceAsStream("/icons/star_filled.png")));
            star.setFitHeight(30);
            star.setFitWidth(30);
            star.setOpacity(0);
            
            // Position star randomly around text
            double angle = i * (360.0 / 5);
            double radius = 80;
            double x = Math.cos(Math.toRadians(angle)) * radius;
            double y = Math.sin(Math.toRadians(angle)) * radius;
            
            star.setTranslateX(x);
            star.setTranslateY(y);
            
            starContainer.getChildren().add(star);
            
            // Create animation
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), star);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            
            RotateTransition rotate = new RotateTransition(Duration.millis(500), star);
            rotate.setByAngle(360);
            
            ScaleTransition scale = new ScaleTransition(Duration.millis(500), star);
            scale.setToX(1.2);
            scale.setToY(1.2);
            scale.setAutoReverse(true);
            scale.setCycleCount(2);
            
            ParallelTransition starAnim = new ParallelTransition(fadeIn, rotate, scale);
            starAnim.setDelay(Duration.millis(i * 200));
            starAnim.play();
        }
        
//        // Discount coupon
//        Rectangle couponBg = new Rectangle(300, 80);
//        couponBg.setFill(ANDOKS_WHITE);
//        couponBg.setArcWidth(20);
//        couponBg.setArcHeight(20);
//        couponBg.setStroke(ANDOKS_YELLOW);
//        couponBg.setStrokeWidth(2);
//        
//        Label couponLabel = new Label("Get 10% OFF on your next order!");
//        couponLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
//        couponLabel.setTextFill(ANDOKS_DARK_RED);
//        
//        Label couponCode = new Label("CODE: THANKYOU10");
//        couponCode.setFont(Font.font("Poppins", FontWeight.BOLD, 16));
//        couponCode.setTextFill(ANDOKS_RED);
//        
//        VBox couponContent = new VBox(10);
//        couponContent.setAlignment(Pos.CENTER);
//        couponContent.getChildren().addAll(couponLabel, couponCode);
//        
//        StackPane coupon = new StackPane();
//        coupon.getChildren().addAll(couponBg, couponContent);
        
        // Close button
        Button closeButton = createGradientButton("CLOSE");
        closeButton.setPrefWidth(150);
        
        closeButton.setOnAction(e -> {
            thankYouStage.close();
            parentStage.close();
        });
        
        content.getChildren().addAll(starContainer, messageLabel, closeButton);
        
        Scene scene = new Scene(content, 350, 400);
        thankYouStage.setScene(scene);
        thankYouStage.setResizable(false);
        
        // Fade in the thank you window
        thankYouStage.setOpacity(0);
        thankYouStage.show();
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), content);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(500), content);
        scaleIn.setFromX(0.8);
        scaleIn.setFromY(0.8);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        
        ParallelTransition parallel = new ParallelTransition(fadeIn, scaleIn);
        
        Platform.runLater(() -> {
            thankYouStage.setOpacity(1);
            parallel.play();
        });
    }
    
    private void animateWindowOpen(Stage stage) {
        stage.setOpacity(0);
        stage.show();
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300));
        fadeIn.setNode(stage.getScene().getRoot());
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(300));
        scaleIn.setNode(stage.getScene().getRoot());
        scaleIn.setFromX(0.9);
        scaleIn.setFromY(0.9);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        
        ParallelTransition parallel = new ParallelTransition(fadeIn, scaleIn);
        
        Platform.runLater(() -> {
            stage.setOpacity(1);
            parallel.play();
        });
    }
    
    // Interface for rating listener
    private interface RatingChangeListener {
        void onRatingChanged(int rating);
        default int getCurrentRating() {
            return 0;
        }
    }
    
    private boolean savePickupRatingToDB(int orderId, int foodRating, String foodReview) {
    String sql = "INSERT INTO ratings (order_id, food_rating, food_review) VALUES (?, ?, ?)";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, orderId);
        stmt.setInt(2, foodRating);
        stmt.setString(3, foodReview);

        int rows = stmt.executeUpdate();
        return rows > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    private boolean saveRatingToDB(int orderId, int foodRating, String foodReview, int deliveryRating, String deliveryReview) {
    String insertSql = "INSERT INTO ratings (order_id, food_rating, food_review, delivery_rating, delivery_review) VALUES (?, ?, ?, ?, ?)";
    String riderUpdateSql = """
        UPDATE riders
        SET 
            average_rating = (
                SELECT AVG(rt.delivery_rating)
                FROM `orders` o
                JOIN ratings rt ON o.order_id = rt.order_id
                WHERE o.rider_id = riders.rider_id
            ),
            total_reviews = (
                SELECT COUNT(rt.delivery_rating)
                FROM `orders` o
                JOIN ratings rt ON o.order_id = rt.order_id
                WHERE o.rider_id = riders.rider_id
            )
        WHERE riders.rider_id = (
            SELECT rider_id FROM `orders` WHERE order_id = ?
        )
        """;

    try (Connection conn = Database.connect()) {
        conn.setAutoCommit(false); // Ensure atomic operation

        try (
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            PreparedStatement updateStmt = conn.prepareStatement(riderUpdateSql)
        ) {
            // Insert the rating
            insertStmt.setInt(1, orderId);
            insertStmt.setInt(2, foodRating);
            insertStmt.setString(3, foodReview);
            insertStmt.setInt(4, deliveryRating);
            insertStmt.setString(5, deliveryReview);

            int rows = insertStmt.executeUpdate();

            if (rows > 0) {
                // Update the rider's rating and review count
                updateStmt.setInt(1, orderId);
                updateStmt.executeUpdate();

                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }

        } catch (Exception e) {
            conn.rollback();
            e.printStackTrace();
            return false;
        }

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}


    private int getStarValue(HBox starBox) {
    int rating = 0;
    for (javafx.scene.Node node : starBox.getChildren()) {
        if (node instanceof Label) {
            Label star = (Label) node;
            if ("★".equals(star.getText())) {
                rating++;
            }
        }
    }
    return rating;
}

     private HBox createStarRating() {
        HBox starBox = new HBox(5);
        for (int i = 1; i <= 5; i++) {
            Label star = new Label("☆");
            star.setStyle("-fx-font-size: 24px; -fx-cursor: hand;");
            final int rating = i;
            star.setOnMouseClicked(e -> {
                for (int j = 0; j < 5; j++) {
                    Label s = (Label) starBox.getChildren().get(j);
                    s.setText(j < rating ? "★" : "☆");
                }
            });
            starBox.getChildren().add(star);
        }
        return starBox;
    }

    
}
