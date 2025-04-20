

package andoksfooddeliverysystem;

import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.shape.Circle;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid; // For solid style icons
// or
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular; // For regular style icons


public class CustomerProfile {

    private Stage profileStage;
    private VBox sidebar;
    private Stage dashboardStage;
    private ImageView profileImage; 
    private int userID;
        
    public void show(Stage owner, Customer customer, int userID) {
        this.dashboardStage = owner;
        this.userID = userID;
             
        profileStage = new Stage();
        profileStage.setTitle("Customer Profile");
        profileStage.initStyle(StageStyle.UNDECORATED);
        profileStage.initOwner(owner);
        profileStage.initModality(Modality.WINDOW_MODAL);

        // Primary color scheme
        String primaryRed = "#e63946";
        String accentYellow = "#ffb703";
        String backgroundWhite = "#f8f9fa";
        String textDark = "#343a40";
        String subtleGrey = "#dee2e6";

        // Create main container with drop shadow
        VBox mainContainer = new VBox(0);
        mainContainer.setStyle(
            "-fx-background-color: " + backgroundWhite + ";" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);" +
            "-fx-background-radius: 15px;"
        );

        // Header section with brand accent
        StackPane header = new StackPane();
        header.setStyle(
            "-fx-background-color: " + primaryRed + ";" +
            "-fx-background-radius: 15px 15px 0 0;" +
            "-fx-padding: 15px;"
        );
        
        Label profileTitle = new Label("My Profile");
        profileTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        profileTitle.setTextFill(Color.WHITE);
        header.getChildren().add(profileTitle);
        header.setMinHeight(80);
        
        // Main content container
        sidebar = new VBox(20);
        sidebar.setPadding(new Insets(25));
        sidebar.setStyle("-fx-background-color: " + backgroundWhite + ";");
        sidebar.setPrefWidth(350);
        sidebar.setAlignment(Pos.TOP_CENTER);

        // =====================
        // 1. Profile Image (Clickable)
        // =====================
        String imagePath = customer.getCustomerImage();
        if (imagePath == null || imagePath.isEmpty()) {
            imagePath = "/icons/default.png";
        }

        // Create circular clip for profile image
        profileImage = new ImageView();
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                profileImage.setImage(new Image(new FileInputStream(file)));
            } else {
                profileImage.setImage(new Image(CustomerProfile.class.getResourceAsStream("/icons/default.png")));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Make profile image circular
        profileImage.setFitWidth(120);
        profileImage.setFitHeight(120);
        profileImage.setPreserveRatio(true);
        
        // Create a circular clip
        Circle clip = new Circle(60);
        clip.setCenterX(60);
        clip.setCenterY(60);
        profileImage.setClip(clip);
        
        // Create container for profile image with border
        StackPane imageContainer = new StackPane();
        Circle border = new Circle(62);
        border.setFill(Color.TRANSPARENT);
        border.setStroke(Color.web(accentYellow));
        border.setStrokeWidth(3);
        
        // Camera icon
        // Camera icon (Solid style)
        FontIcon cameraIcon = new FontIcon(FontAwesomeSolid.CAMERA);
        // or for Regular style:
        // FontIcon cameraIcon = new FontIcon(FontAwesomeRegular.CAMERA);

        cameraIcon.setIconSize(20);
        cameraIcon.setIconColor(Color.WHITE);
        cameraIcon.setOpacity(0.7);
        imageContainer.getChildren().addAll(profileImage, border, cameraIcon);
        
        // Hover effects for image change hint
        imageContainer.setOnMouseEntered(e -> {
            profileImage.setEffect(new ColorAdjust(0, 0, -0.3, 0));
            cameraIcon.setOpacity(0.8);
        });
        
        imageContainer.setOnMouseExited(e -> {
            profileImage.setEffect(null);
            cameraIcon.setOpacity(0);
        });
        
        imageContainer.setCursor(Cursor.HAND);
        
        // Original click handler for file choosing
        imageContainer.setOnMouseClicked(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Choose Profile Image");
            fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
            );
            File selectedFile = fileChooser.showOpenDialog(profileStage);
            if (selectedFile != null) {
                try {
                    String absolutePath = selectedFile.getAbsolutePath();
                    FileInputStream inputStream = new FileInputStream(selectedFile);
                    Image newImage = new Image(inputStream);
                    profileImage.setImage(newImage);
                    updateCustomerImage(customer.getCustomerId(), absolutePath, userID);
                    customer.setCustomerImage(absolutePath);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        // =====================
        // 2. Name (Editable on Click)
        // =====================
        VBox nameContainer = new VBox(5);
        nameContainer.setAlignment(Pos.CENTER);
        
        Label nameLabel = new Label(customer.getName());
        nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 22));
        nameLabel.setTextFill(Color.web(textDark));
        nameLabel.setCursor(Cursor.HAND);
        
        Label editHintLabel = new Label("Tap to edit");
        editHintLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 12));
        editHintLabel.setTextFill(Color.web("#adb5bd"));
        
        nameContainer.getChildren().addAll(nameLabel, editHintLabel);

        nameLabel.setOnMouseClicked(event -> {
            TextField nameField = new TextField(nameLabel.getText());
            nameField.setFont(Font.font("Poppins", FontWeight.NORMAL, 20));
            nameField.setAlignment(Pos.CENTER);
            nameField.setMaxWidth(250);
            nameField.setStyle(
                "-fx-background-color: " + backgroundWhite + ";" +
                "-fx-border-color: " + accentYellow + ";" +
                "-fx-border-radius: 5px;" +
                "-fx-padding: 5px;"
            );

            nameField.setOnAction(e -> {
                String newName = nameField.getText();
                customer.setName(newName);
                updateCustomerName(customer.getCustomerId(), newName, userID);
                sidebar.getChildren().set(sidebar.getChildren().indexOf(nameContainer), nameContainer);
                nameLabel.setText(newName);
            });

            VBox editNameContainer = new VBox(8);
            editNameContainer.setAlignment(Pos.CENTER);
            editNameContainer.getChildren().addAll(nameField, editHintLabel);
            sidebar.getChildren().set(sidebar.getChildren().indexOf(nameContainer), editNameContainer);
            nameField.requestFocus();
        });

        // Add a separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: " + subtleGrey + ";");
        separator.setPrefWidth(300);
        
        // Set of opened stages
        Set<Stage> openedStages = new HashSet<>();

        // =====================
        // 3. Address Section
        // =====================
        VBox addressSection = new VBox(8);
        addressSection.setAlignment(Pos.CENTER_LEFT);
        addressSection.setPadding(new Insets(10, 0, 10, 0));
        
        Label addressTitle = new Label("DELIVERY ADDRESS");
        addressTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 12));
        addressTitle.setTextFill(Color.web("#6c757d"));
        
        Address defaultAddress = customer.getDefaultAddress();
        Label addressLabel;

        if (defaultAddress != null) {
            addressLabel = new Label(
                defaultAddress.getStreet() + ", " +
                defaultAddress.getBarangay() + "\n📞 " +
                defaultAddress.getContactNumber() + "\n🏷️ " +
                defaultAddress.getAddressType()
            );
        } else {
            addressLabel = new Label("No default address set.");
        }

        addressLabel.setFont(Font.font("Poppins", 14));
        addressLabel.setWrapText(true);
        addressLabel.setPadding(new Insets(10));
        addressLabel.setCursor(Cursor.HAND);
        addressLabel.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: " + subtleGrey + ";" +
            "-fx-border-radius: 8px;" +
            "-fx-background-radius: 8px;"
        );
        
        // Hover effect for address
        addressLabel.setOnMouseEntered(e -> 
            addressLabel.setStyle(
                "-fx-background-color: #f8f9fa;" +
                "-fx-border-color: " + accentYellow + ";" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;"
            )
        );
        
        addressLabel.setOnMouseExited(e -> 
            addressLabel.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + subtleGrey + ";" +
                "-fx-border-radius: 8px;" +
                "-fx-background-radius: 8px;"
            )
        );
        
        // Original click handler for address
        addressLabel.setOnMouseClicked(e -> {
            List<Order> orders = OrderFetcher.fetchOrdersbyCustomerID(customer.getCustomerId());
            Set<String> seen = new HashSet<>();

            ListView<String> addressList = new ListView<>();
            addressList.setStyle(
                "-fx-background-color: white;" +
                "-fx-border-color: " + subtleGrey + ";" +
                "-fx-border-radius: 5px;"
            );

            for (Order order : orders) {
                String street = order.getStreet();
                String barangay = order.getBarangay();
                String addressType = null;
                String contactNumber = order.getContactNumber();

                Address addr = new Address(
                    0,
                    street,
                    barangay,
                    addressType,
                    false,
                    contactNumber
                );

                String addressStr = addr.toString();

                if (!seen.contains(addressStr)) {
                    addressList.getItems().add(addressStr);
                    seen.add(addressStr);
                }
            }
            
            Dialog<Void> addressDialog = new Dialog<>();
            addressDialog.setTitle("Your Addresses");
            addressDialog.setHeaderText(null);
            addressDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            addressDialog.setResizable(true);

            // Main content VBox
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));
            content.setStyle(
                "-fx-background-color: " + backgroundWhite + ";" +
                "-fx-font-family: 'Segoe UI', sans-serif;" +
                "-fx-font-size: 13px;" +
                "-fx-text-fill: #333;"
            );

            Label titleLabel = new Label("Saved Addresses for " + customer.getName());
            titleLabel.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #444;");

            // Make address list scrollable
            ScrollPane scrollPane = new ScrollPane(addressList);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(200);
            scrollPane.setStyle("-fx-background-color: transparent;");

            content.getChildren().addAll(titleLabel, scrollPane);

            DialogPane dialogPane = addressDialog.getDialogPane();
            dialogPane.setContent(content);
            dialogPane.setPrefWidth(400);
            dialogPane.setPrefHeight(300);
            dialogPane.setStyle(
                "-fx-background-color: " + backgroundWhite + ";" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10;"
            );

            addressDialog.showAndWait();
        });
        
        addressSection.getChildren().addAll(addressTitle, addressLabel);

        // =====================
        // 4. Buttons Section
        // =====================
        // History button with icon
        HBox historyButtonContainer = new HBox();
        historyButtonContainer.setAlignment(Pos.CENTER);
        historyButtonContainer.setPadding(new Insets(10, 0, 0, 0));
        
        Button historyBtn = createStyledButton("Order History", primaryRed, accentYellow);
        
        try {
        FontIcon historyIcon = new FontIcon(FontAwesomeSolid.HISTORY);
        historyIcon.setIconColor(Color.WHITE);
        historyIcon.setIconSize(16);
        historyBtn.setGraphic(historyIcon);
        historyBtn.setGraphicTextGap(8);
    } catch (Exception e) {
        historyBtn.setText("HISTORY");
    }
        
        historyBtn.setOnAction(e -> {
            List<Order> orders = OrderFetcher.fetchOrdersbyCustomerID(customer.getCustomerId());
            orders.sort((o1, o2) -> Integer.compare(o2.getOrderId(), o1.getOrderId()));

            Stage orderHistoryStage = new Stage();
            orderHistoryStage.setTitle("Order History");
            orderHistoryStage.initStyle(StageStyle.UNDECORATED);
            
            VBox orderHistoryLayout = new VBox(0);
            
            // Header for order history
            StackPane historyHeader = new StackPane();
            historyHeader.setStyle(
                "-fx-background-color: " + primaryRed + ";" +
                "-fx-padding: 15px;"
            );
            
            Label historyTitle = new Label("Order History");
            historyTitle.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
            historyTitle.setTextFill(Color.WHITE);
            historyHeader.getChildren().add(historyTitle);
            
            // ListView with custom styling
            ListView<HBox> orderListView = new ListView<>();
            orderListView.setStyle(
                "-fx-background-color: white;" +
                "-fx-background-insets: 0;" +
                "-fx-padding: 10px;"
            );
            
            VBox listContainer = new VBox(orderListView);
            listContainer.setPadding(new Insets(15));
            listContainer.setStyle("-fx-background-color: " + backgroundWhite + ";");

            for (Order order : orders) {
                HBox item = new HBox(10);
                item.setPadding(new Insets(12, 10, 12, 10));
                item.setAlignment(Pos.CENTER_LEFT);
                item.setStyle(
                    "-fx-background-color: white;" +
                    "-fx-border-color: " + subtleGrey + ";" +
                    "-fx-border-radius: 8px;" +
                    "-fx-background-radius: 8px;" +
                    "-fx-margin: 5px 0;"
                );
                
                // Status indicator circle
                Circle statusCircle = new Circle(6);
                String status = order.getOrderStatus().toLowerCase();
                if (status.equals("completed")) {
                    statusCircle.setFill(Color.GREEN);
                } else if (status.equals("cancelled")) {
                    statusCircle.setFill(Color.RED);
                } else {
                    statusCircle.setFill(Color.web(accentYellow)); // pending/processing
                }
                
                // Order details with better layout
                VBox orderDetails = new VBox(3);
                Label orderIdLabel = new Label("Order #" + order.getOrderId());
                orderIdLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
                
                Label dateLabel = new Label(order.getOrderDate());
                dateLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 12));
                dateLabel.setTextFill(Color.web("#6c757d"));
                
                Label statusLabel = new Label(order.getOrderStatus());
                statusLabel.setFont(Font.font("Poppins", FontWeight.NORMAL, 12));
                
                // Set color based on status
                if (status.equals("completed")) {
                    statusLabel.setTextFill(Color.GREEN);
                } else if (status.equals("cancelled")) {
                    statusLabel.setTextFill(Color.RED);
                } else {
                    statusLabel.setTextFill(Color.web(accentYellow));
                }
                
                orderDetails.getChildren().addAll(orderIdLabel, dateLabel, statusLabel);
                
                // Arrow indicator to show it's clickable
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);
                FontIcon arrowIcon = new FontIcon(FontAwesomeSolid.CHEVRON_RIGHT);
                arrowIcon.setIconColor(Color.web(subtleGrey)); // Or a direct hex color like Color.web("#B0BEC5")

                item.getChildren().addAll(statusCircle, orderDetails, spacer, arrowIcon);
                
                // Hover effect
                item.setOnMouseEntered(event -> {
                    item.setStyle(
                        "-fx-background-color: #f8f9fa;" +
                        "-fx-border-color: " + accentYellow + ";" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;" +
                        "-fx-cursor: hand;"
                    );
                    arrowIcon.setFill(Color.web(primaryRed));
                });
                
                item.setOnMouseExited(event -> {
                    item.setStyle(
                        "-fx-background-color: white;" +
                        "-fx-border-color: " + subtleGrey + ";" +
                        "-fx-border-radius: 8px;" +
                        "-fx-background-radius: 8px;"
                    );
                    arrowIcon.setFill(Color.web(subtleGrey));
                });
                
                orderListView.getItems().add(item);
            }

            // Keep original mouse click handler for order details
            orderListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    HBox selectedBox = orderListView.getSelectionModel().getSelectedItem();
                    if (selectedBox != null) {
                        VBox orderDetailsBox = (VBox) selectedBox.getChildren().get(1);
                        Label label = (Label) orderDetailsBox.getChildren().get(0);
                        String selectedOrderText = label.getText();
                        int orderId = Integer.parseInt(selectedOrderText.split(" ")[1].replace("#", ""));

                        Order selectedOrderDetails = orders.stream()
                                .filter(order -> order.getOrderId() == orderId)
                                .findFirst()
                                .orElse(null);

                        if (selectedOrderDetails != null) {
                            OrderSummary orderSummary = new OrderSummary();
                            Stage orderSummaryStage = orderSummary.show(selectedOrderDetails, userID);
                            openedStages.add(orderSummaryStage);
                        }
                    }
                }
            });
            
            // Close button for order history
            Button closeHistoryBtn = createStyledButton("Close", subtleGrey, textDark);
            closeHistoryBtn.setTextFill(Color.web(textDark));
            closeHistoryBtn.setOnAction(evt -> orderHistoryStage.close());
            
            StackPane buttonBar = new StackPane(closeHistoryBtn);
            buttonBar.setPadding(new Insets(10));
            buttonBar.setStyle("-fx-background-color: " + backgroundWhite + ";");
            
            orderHistoryLayout.getChildren().addAll(historyHeader, listContainer, buttonBar);

            Scene scene = new Scene(orderHistoryLayout, 400, 500);
            orderHistoryStage.setScene(scene);

            // Close when clicking outside
            orderHistoryStage.initModality(Modality.WINDOW_MODAL);
            orderHistoryStage.initOwner(((Node)e.getSource()).getScene().getWindow());
            orderHistoryStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    orderHistoryStage.close();
                }
            });
            
            openedStages.add(orderHistoryStage);
            orderHistoryStage.show();
        });
        
        historyButtonContainer.getChildren().add(historyBtn);
        
        // Button container for action buttons
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(15, 0, 0, 0));
        
        // Close button
        Button closeBtn = createStyledButton("Close", subtleGrey, textDark);
        closeBtn.setTextFill(Color.web(textDark));
        closeBtn.setOnAction(e -> profileStage.close());
                closeBtn.setStyle(
            "-fx-background-color: " + subtleGrey + ";" +
            "-fx-text-fill: " + textDark + ";" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 12;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        );

        // Set hover behavior manually
        closeBtn.setOnMouseEntered(event -> closeBtn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + textDark + ";" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 12;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        ));

        closeBtn.setOnMouseExited(event -> closeBtn.setStyle(
            "-fx-background-color: " + subtleGrey + ";" +
            "-fx-text-fill: " + textDark + ";" +
            "-fx-font-size: 13px;" +
            "-fx-cursor: hand;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 12;" +
            "-fx-border-color: transparent;" +
            "-fx-border-width: 1;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 2, 0, 0, 1);"
        ));

        // Logout button
        Button logoutBtn = createStyledButton("Logout", primaryRed, "#ff8888");
        
       
        FontIcon logoutIcon = new FontIcon(FontAwesomeSolid.SIGN_OUT_ALT);
        logoutIcon.setIconSize(14);
        logoutIcon.setIconColor(Color.WHITE);
        logoutBtn.setGraphic(logoutIcon);
        logoutBtn.setGraphicTextGap(8);
        
        logoutBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to log out?");
            
            // Style the dialog
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: " + backgroundWhite + ";");
            dialogPane.getStyleClass().add("modern-alert");
            
            // Style the buttons
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setStyle(
                "-fx-background-color: " + primaryRed + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 5px;"
            );
            
            Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            cancelButton.setStyle(
                "-fx-background-color: " + subtleGrey + ";" +
                "-fx-text-fill: " + textDark + ";" +
                "-fx-background-radius: 5px;"
            );

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                profileStage.close();
                if (dashboardStage != null) {
                    dashboardStage.close();
                }

                for (Stage stage : openedStages) {
                    stage.close();
                }

                try {
                    Main mainApp = new Main();
                    Stage loginStage = new Stage();
                    mainApp.start(loginStage);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
        
        buttonContainer.getChildren().addAll(closeBtn, logoutBtn);
        
        // Add all components to sidebar
        sidebar.getChildren().addAll(
            imageContainer, 
            nameContainer, 
            separator, 
            addressSection, 
            historyButtonContainer, 
            buttonContainer
        );
        
        // Add components to main container
        mainContainer.getChildren().addAll(header, sidebar);
        
        Scene scene = new Scene(mainContainer);
        
        // Add CSS for custom styling
        scene.getRoot().setStyle("-fx-font-family: 'Poppins', 'Segoe UI', sans-serif;");
        profileStage.setScene(scene);

        // Position the window
        profileStage.setX(Screen.getPrimary().getVisualBounds().getMaxX() - 380);
        profileStage.setY(100);

        profileStage.show();
    }
    
    // Helper method to create consistent button styling
    private Button createStyledButton(String text, String bgColor, String hoverColor) {
        Button button = new Button(text);
        button.setFont(Font.font("Poppins", FontWeight.BOLD, 14));
        button.setPadding(new Insets(10, 20, 10, 20));
        button.setTextFill(Color.WHITE);
        button.setStyle(
            "-fx-background-color: " + bgColor + ";" + 
            "-fx-background-radius: 5px;" +
            "-fx-cursor: hand;"
        );
        
        // Add hover effect
        button.setOnMouseEntered(e -> 
            button.setStyle(
                "-fx-background-color: " + hoverColor + ";" + 
                "-fx-background-radius: 5px;" +
                "-fx-cursor: hand;"
            )
        );
        
        button.setOnMouseExited(e -> 
            button.setStyle(
                "-fx-background-color: " + bgColor + ";" + 
                "-fx-background-radius: 5px;" +
                "-fx-cursor: hand;"
            )
        );
        
        return button;
    }
      
   public static List<Address> getAddressesByCustomerId(int customerId) {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT * FROM addresses WHERE customer_id = ?";
        try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String addressType = rs.getString("address_type");
                boolean isDefault = addressType != null && addressType.equals("default");  // You can adjust this logic

                addresses.add(new Address(
                    rs.getInt("address_id"),
                    rs.getString("street"),
                    rs.getString("barangay"),
                    addressType,
                    isDefault,  // Now correctly setting isDefault
                    rs.getString("contact_number")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return addresses;
    }


      
public static void updateCustomerImage(int customerId, String imagePath, int userId) {
    String query = "UPDATE customers SET customer_image = ?, last_modified_by = ? WHERE customer_id = ?";
    try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, imagePath);
        stmt.setInt(2, userId);
        stmt.setInt(3, customerId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void updateCustomerName(int customerId, String name, int userId) {
    String query = "UPDATE customers SET name = ?, last_modified_by = ? WHERE customer_id = ?";
    try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, name);
        stmt.setInt(2, userId);
        stmt.setInt(3, customerId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}




    private void createProfileContent() {
        // Image
        ImageView profileImage = new ImageView(new Image(getClass().getResourceAsStream("/images/profile.png")));
        profileImage.setFitWidth(100);
        profileImage.setFitHeight(100);
        profileImage.setPreserveRatio(true);

        // Info
        Label nameLabel = new Label("John Doe");
        nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));

        Label addressLabel = new Label("123 Andoks Street\nQuezon City, Metro Manila");

        // Buttons
        Button historyBtn = new Button("View Order History");
        historyBtn.setOnAction(e -> showOrderHistory());

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> profileStage.close());

        sidebar.getChildren().addAll(profileImage, nameLabel, addressLabel, historyBtn, closeBtn);
    }

    private void showOrderHistory() {
        sidebar.getChildren().clear();

        Label title = new Label("Order History");
        title.setFont(Font.font("Poppins", FontWeight.BOLD, 18));

        ListView<String> orderList = new ListView<>();
        orderList.getItems().addAll("Order #123 - April 10", "Order #124 - April 12", "Order #125 - April 15");

        orderList.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                String selected = orderList.getSelectionModel().getSelectedItem();
                if (selected != null) {
                    int orderId = extractOrderId(selected);
                    Order order = OrderFetcher.getOrderById(orderId);
                    OrderSummary summary = new OrderSummary();
                    summary.show(order, userID);

                }
            }
        });

        Button backBtn = new Button("← Back");
        backBtn.setOnAction(e -> {
            sidebar.getChildren().clear();
            createProfileContent();
        });

        sidebar.getChildren().addAll(title, orderList, backBtn);
    }

    private int extractOrderId(String item) {
        try {
            return Integer.parseInt(item.split("#")[1].split(" ")[0]);
        } catch (Exception e) {
            return -1;
        }
    }

    public void show() {
        profileStage.show();
    }
}
