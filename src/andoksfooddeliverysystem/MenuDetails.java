package andoksfooddeliverysystem;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import java.sql.*;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;

public class MenuDetails {
     private static VBox content;
     private static  HBox variationBox;
    
    public static void showItemDetails(String itemName) {
        Stage itemStage = new Stage();
        itemStage.initStyle(StageStyle.UNDECORATED); // No default window buttons
     
   
        // 🖼 Fetch item details from DB
        String query = "SELECT * FROM menu_items WHERE name = ?";
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, itemName);
            ResultSet rs = stmt.executeQuery();
             System.out.println(itemName);
            
            if (rs.next()) {
                 int itemId = rs.getInt("item_id"); // ✅ Fetch item_id
                 System.out.println(itemId);
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("price");
                String description = rs.getString("description");

                // 🌟 Image
                ImageView imageView = new ImageView(new Image("file:" + imagePath));
                imageView.setFitWidth(250);
                imageView.setFitHeight(250);
                imageView.setStyle("-fx-border-radius: 10px;");

                // 📛 Name
                Label nameLabel = new Label(itemName);
                nameLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

                // 💰 Price
                Label priceLabel = new Label("₱" + price);
                priceLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: green;");

                // 📜 Description
                Label descLabel = new Label(description);
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(300);

               
               
               // 🔄 Variations (Only if available)
            ComboBox<String> variationsBox = new ComboBox<>();
            String variationQuery = "SELECT variation_name, variation_price FROM menu_variations WHERE item_id = ?";
            boolean hasVariations = false;

            try (PreparedStatement variationStmt = conn.prepareStatement(variationQuery)) {
                variationStmt.setInt(1, itemId);
                ResultSet variationRs = variationStmt.executeQuery();

                while (variationRs.next()) {
                    hasVariations = true;
                    String variationName = variationRs.getString("variation_name");
                    double variationPrice = variationRs.getDouble("variation_price");
                    variationsBox.getItems().add(variationName + " (+₱" + (variationPrice - price) + ")");
                }
            }

            if (hasVariations) {
                Label variationLabel = new Label("Variations:");
                variationLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                 variationBox = new HBox(10, variationLabel, variationsBox); // 🔄 Label + ComboBox
                variationBox.setAlignment(Pos.CENTER_LEFT);
        }


            // ✏ Special Instructions
            TextField specialInstructions = new TextField();
            specialInstructions.setPromptText("Add special instructions...");

            // 🔢 Quantity Selector
            Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, 1);

            // ❌ If product is unavailable
            ComboBox<String> unavailableBox = new ComboBox<>();
            unavailableBox.getItems().addAll("Call me", "Remove from order");
            unavailableBox.setValue("Remove from order");

            // 🛒 Add to Cart Button
            Button addToCart = new Button("Add to Cart");
            addToCart.setStyle("-fx-background-color: #ff5733; -fx-text-fill: white; -fx-font-size: 16px;");
            addToCart.setOnAction(e -> {
                System.out.println("Added to cart: " + itemName + " x" + quantitySpinner.getValue());
                itemStage.close();
            });

            // ❌ Close Button
            Button closeButton = new Button("✖");
            closeButton.setStyle("-fx-background-color: transparent; -fx-font-size: 20px;");
            closeButton.setOnAction(e -> itemStage.close());

            // 🏗 Layout
            HBox topBar = new HBox(closeButton);
            topBar.setAlignment(Pos.TOP_RIGHT);

            content = new VBox(10, imageView, nameLabel, priceLabel, descLabel, specialInstructions,
                    new HBox(10, new Label("Quantity:"), quantitySpinner),
                    new HBox(10, new Label("If unavailable:"), unavailableBox),
                    addToCart);

            if (hasVariations) {
                content.getChildren().add(3, variationBox); // Insert variationsBox in the right order
            }

            content.setPadding(new Insets(20));
            content.setAlignment(Pos.CENTER);

                // 🌟 Make Scrollable
                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setPannable(true);
                scrollPane.setStyle("-fx-background: white; -fx-padding: 10px;");
                // Force viewport background to be white
                scrollPane.setStyle("-fx-background: white;");
                scrollPane.setContent(content);

                
                VBox mainLayout = new VBox(topBar, scrollPane);
                mainLayout.setPadding(new Insets(20));
                mainLayout.setStyle("-fx-background-color: white; -fx-border-radius: 10px; -fx-padding: 20px;");
                mainLayout.setEffect(new DropShadow(10, Color.GRAY));

                

                // 🎭 Floating Animation (Smooth Upward)
                TranslateTransition floatUp = new TranslateTransition(Duration.millis(300), mainLayout);
                floatUp.setFromY(50);
                floatUp.setToY(0);
                floatUp.setInterpolator(Interpolator.EASE_OUT);
                floatUp.play();
                
                 // ✨ Close with Floating Down Animation when losing focus
            // ✨ Close with Floating Down Animation when losing focus (with delay)
        // ✨ Close with Smooth Floating Down Animation when losing focus
        itemStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                // Keep the window on top during animation
                itemStage.setAlwaysOnTop(true);

                TranslateTransition floatDown = new TranslateTransition(Duration.millis(300), mainLayout);
                floatDown.setFromY(0);
                floatDown.setToY(50);
                floatDown.setInterpolator(Interpolator.EASE_IN);

                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), mainLayout);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);

                ParallelTransition exitAnimation = new ParallelTransition(floatDown, fadeOut);

                // Disable interactions to prevent weird clicks
                mainLayout.setDisable(true);

                exitAnimation.setOnFinished(e -> {
                    itemStage.close();
                });

                exitAnimation.play();
            }
        });


                
                Scene scene = new Scene(mainLayout, 500, 800);
                itemStage.setScene(scene);
                itemStage.setTitle("Item Details");
                
                 itemStage.show();

               
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
