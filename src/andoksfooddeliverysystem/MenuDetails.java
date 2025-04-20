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
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.stage.Modality;

public class MenuDetails {
    private static VBox content;
    private static HBox variationBox;
    
    // Define Andok's colors as constants for easy reference
    private static final String ANDOKS_RED = "#D32F2F";
    private static final String ANDOKS_DARK_RED = "#B71C1C";
    private static final String ANDOKS_YELLOW = "#FFC107";
    private static final String ANDOKS_LIGHT_YELLOW = "#FFECB3";
    private static final String ANDOKS_WHITE = "#FFFFFF";
    private static final String ANDOKS_GRAY = "#F8F8F8";
    
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

                // 🌟 Image - Enhanced with rounded corners and border
                ImageView imageView = new ImageView(new Image("file:" + imagePath));
                imageView.setFitWidth(300);
                imageView.setFitHeight(300);
                imageView.setPreserveRatio(true);
                
                // Create an image container with stylized border
                StackPane imageContainer = new StackPane(imageView);
                imageContainer.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";" +
                                       "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 5, 0, 0, 1);");
                imageContainer.setPadding(new Insets(5));

                Label nameLabel = new Label(itemName);
                nameLabel.setWrapText(true);
                nameLabel.setMaxWidth(300); // or however wide your layout allows
                nameLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_DARK_RED + ";");

                Label priceLabel = new Label("₱" + price);
                priceLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_RED + ";" +
                                   "-fx-background-color: " + ANDOKS_LIGHT_YELLOW + ";" +
                                   "-fx-padding: 5 15 5 15;" +
                                   "-fx-background-radius: 20px;");

                // Use VBox to stack name and price if name is too long
                VBox headerBox = new VBox(5, nameLabel, priceLabel);
                headerBox.setAlignment(Pos.CENTER_LEFT);

                // 📜 Description - Enhanced with styled container
                Label descLabel = new Label(description);
                descLabel.setWrapText(true);
                descLabel.setMaxWidth(450);
                descLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #333333;");
                
                VBox descBox = new VBox(5, descLabel);
                descBox.setStyle("-fx-background-color: " + ANDOKS_GRAY + ";" +
                               "-fx-background-radius: 8px;" +
                               "-fx-padding: 15px;");
                
                // 🔄 Variations (Only if available)
                ComboBox<String> variationsBox = new ComboBox<>();
                variationsBox.setStyle("-fx-font-size: 15px;" +
                                     "-fx-background-radius: 5px;" +
                                     "-fx-border-color: " + ANDOKS_YELLOW + ";" +
                                     "-fx-border-radius: 5px;" +
                                     "-fx-padding: 5px;");
                
                String variationQuery = "SELECT variation_name, variation_price FROM menu_variations WHERE item_id = ?";
                boolean hasVariations = false;

                try (PreparedStatement variationStmt = conn.prepareStatement(variationQuery)) {
                    variationStmt.setInt(1, itemId);
                    ResultSet variationRs = variationStmt.executeQuery();

                    while (variationRs.next()) {
                        hasVariations = true;
                        String variationName = variationRs.getString("variation_name");
                        double variationPrice = variationRs.getDouble("variation_price");
                        variationsBox.getItems().add(variationName + " (₱" + variationPrice + ")");
                    }
                }

                if (hasVariations) {
                    Label variationLabel = new Label("Variations:");
                    variationLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_DARK_RED + ";");

                    variationBox = new HBox(10, variationLabel, variationsBox); // 🔄 Label + ComboBox
                    variationBox.setAlignment(Pos.CENTER_LEFT);
                    variationBox.setPadding(new Insets(10, 0, 10, 0));
                }

                // ✏ Special Instructions - Styled text field
                Label instructionsLabel = new Label("Special Instructions:");
                instructionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_DARK_RED + ";");
                
                TextField specialInstructions = new TextField();
                specialInstructions.setPromptText("Add special instructions...");
                specialInstructions.setPrefWidth(450);
                specialInstructions.setStyle("-fx-font-size: 14px;" +
                                          "-fx-background-radius: 5px;" +
                                          "-fx-border-color: " + ANDOKS_YELLOW + ";" +
                                          "-fx-border-radius: 5px;" +
                                          "-fx-padding: 8px;");
                
                VBox instructionsBox = new VBox(5, instructionsLabel, specialInstructions);
                instructionsBox.setPadding(new Insets(10, 0, 10, 0));

                // 🔢 Quantity Selector - Styled spinner
                Label quantityLabel = new Label("Quantity:");
                quantityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_DARK_RED + ";");
                
                Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, 1);
                quantitySpinner.setEditable(true);
                quantitySpinner.setPrefWidth(100);
                quantitySpinner.getStyleClass().add("quantity-spinner");
                quantitySpinner.setStyle("-fx-font-size: 14px;");

                HBox quantityBox = new HBox(10, quantityLabel, quantitySpinner);
                quantityBox.setAlignment(Pos.CENTER_LEFT);
                quantityBox.setPadding(new Insets(10, 0, 15, 0));

                // 🛒 Add to Cart Button - Styled with Andok's colors
                Button addToCart = new Button("ADD TO CART");
                addToCart.setPrefWidth(200);
                addToCart.setPrefHeight(45);
                addToCart.setStyle("-fx-background-color: " + ANDOKS_RED + ";" +
                                 "-fx-text-fill: white;" +
                                 "-fx-font-size: 16px;" +
                                 "-fx-font-weight: bold;" +
                                 "-fx-background-radius: 25px;" +
                                 "-fx-cursor: hand;" +
                                 "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 1);");
                
                // Add hover effect with event handlers
                addToCart.setOnMouseEntered(e -> 
                    addToCart.setStyle("-fx-background-color: " + ANDOKS_DARK_RED + ";" +
                                     "-fx-text-fill: white;" +
                                     "-fx-font-size: 16px;" +
                                     "-fx-font-weight: bold;" +
                                     "-fx-background-radius: 25px;" +
                                     "-fx-cursor: hand;" +
                                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 8, 0, 0, 2);")
                );
                
                addToCart.setOnMouseExited(e -> 
                    addToCart.setStyle("-fx-background-color: " + ANDOKS_RED + ";" +
                                     "-fx-text-fill: white;" +
                                     "-fx-font-size: 16px;" +
                                     "-fx-font-weight: bold;" +
                                     "-fx-background-radius: 25px;" +
                                     "-fx-cursor: hand;" +
                                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 1);")
                );
                
                addToCart.setOnAction(e -> {
                    int quantity = quantitySpinner.getValue();
                    String selectedVariation = variationsBox.getValue();
                    String instructions = specialInstructions.getText();

                    addItemToCart(itemId, quantity, selectedVariation, instructions);
                    itemStage.close();
                });

                // Create an action section with "Add to Cart" centered
                HBox actionBox = new HBox(addToCart);
                actionBox.setAlignment(Pos.CENTER);
                actionBox.setPadding(new Insets(10, 0, 10, 0));

                // ❌ Close Button - Styled with red circle
                Button closeButton = new Button("✖");
                closeButton.setStyle("-fx-background-color: " + ANDOKS_RED + ";" +
                                   "-fx-text-fill: white;" +
                                   "-fx-font-size: 16px;" +
                                   "-fx-font-weight: bold;" +
                                   "-fx-background-radius: 50%;" +
                                   "-fx-min-width: 30px;" +
                                   "-fx-min-height: 30px;" +
                                   "-fx-max-width: 30px;" +
                                   "-fx-max-height: 30px;" +
                                   "-fx-padding: 0;" +
                                   "-fx-cursor: hand;");
                
                closeButton.setOnMouseEntered(e -> 
                    closeButton.setStyle("-fx-background-color: " + ANDOKS_DARK_RED + ";" +
                                       "-fx-text-fill: white;" +
                                       "-fx-font-size: 16px;" +
                                       "-fx-font-weight: bold;" +
                                       "-fx-background-radius: 50%;" +
                                       "-fx-min-width: 30px;" +
                                       "-fx-min-height: 30px;" +
                                       "-fx-max-width: 30px;" +
                                       "-fx-max-height: 30px;" +
                                       "-fx-padding: 0;" +
                                       "-fx-cursor: hand;")
                );
                
                closeButton.setOnMouseExited(e -> 
                    closeButton.setStyle("-fx-background-color: " + ANDOKS_RED + ";" +
                                       "-fx-text-fill: white;" +
                                       "-fx-font-size: 16px;" +
                                       "-fx-font-weight: bold;" +
                                       "-fx-background-radius: 50%;" +
                                       "-fx-min-width: 30px;" +
                                       "-fx-min-height: 30px;" +
                                       "-fx-max-width: 30px;" +
                                       "-fx-max-height: 30px;" +
                                       "-fx-padding: 0;" +
                                       "-fx-cursor: hand;")
                );
                
                closeButton.setOnAction(e -> itemStage.close());

                // Add Andok's branding icon to top bar
                Label titleLabel = new Label("Item Details");
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + ANDOKS_WHITE + ";");
                
                // Create a branded top bar
                HBox topBar = new HBox(15);
                topBar.setAlignment(Pos.CENTER_RIGHT);
                topBar.getChildren().addAll(titleLabel, new Region(), closeButton);
                topBar.setStyle("-fx-background-color: " + ANDOKS_RED + ";" +
                              "-fx-padding: 12px 15px;" +
                              "-fx-background-radius: 10px 10px 0 0;");
                
                // Use Region as a spacer (grows to take available space)
                Region region = (Region) topBar.getChildren().get(1);
                HBox.setHgrow(region, Priority.ALWAYS);
                
                // 🏗 Layout
                content = new VBox(15);
                content.getChildren().addAll(
                    imageContainer,
                    headerBox,
                    descBox,
                    instructionsBox,
                    quantityBox,
                    actionBox
                );

                if (hasVariations) {
                    content.getChildren().add(3, variationBox); // Insert variationsBox in the right order
                }

                content.setPadding(new Insets(20));
                content.setAlignment(Pos.TOP_CENTER);
                content.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";");

                // 🌟 Make Scrollable
                ScrollPane scrollPane = new ScrollPane(content);
                scrollPane.setFitToWidth(true);
                scrollPane.setPannable(true);
                scrollPane.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
                scrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
                scrollPane.setStyle("-fx-background: " + ANDOKS_WHITE + ";" +
                                  "-fx-background-color: " + ANDOKS_WHITE + ";" +
                                  "-fx-padding: 0;");

                // Main layout structure
                VBox mainLayout = new VBox(topBar, scrollPane);
                mainLayout.setStyle("-fx-background-color: " + ANDOKS_WHITE + ";" +
                                    "-fx-border-radius: 10px;" +
                                    "-fx-background-radius: 10px;" +
                        
                                    "-fx-padding: 20px;"); // Optional padding for spacing

                // 🔥 Enhanced DropShadow Effect
                DropShadow shadow = new DropShadow();
                shadow.setRadius(30);           // Bigger blur radius
                shadow.setSpread(0.3);          // More intense spread
                shadow.setOffsetX(0);           // No horizontal shift
                shadow.setOffsetY(12);          // Strong vertical drop
                shadow.setColor(Color.rgb(0, 0, 0, 0.6)); // Darker shadow color
                mainLayout.setEffect(shadow);

                // 🎭 Floating Animation (Smooth Upward)
                TranslateTransition floatUp = new TranslateTransition(Duration.millis(300), mainLayout);
                floatUp.setFromY(50);
                floatUp.setToY(0);
                floatUp.setInterpolator(Interpolator.EASE_OUT);
                floatUp.play();
                
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

                Scene scene = new Scene(mainLayout, 550, 800);
                scene.setFill(Color.TRANSPARENT);
                
                itemStage.initStyle(StageStyle.TRANSPARENT);
                itemStage.setScene(scene);
                itemStage.setTitle("Item Details");
                itemStage.show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static void addItemToCart(int itemId, int quantity, String selectedVariation, String instructions) {
        CartSession.addToCart(itemId, quantity, selectedVariation, instructions);
        System.out.println("Item added to cart: " + itemId);
    }


}
