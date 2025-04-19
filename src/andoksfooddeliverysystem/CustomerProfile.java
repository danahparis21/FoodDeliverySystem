

package andoksfooddeliverysystem;

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

public class CustomerProfile {

    private Stage profileStage;
    private VBox sidebar;
    private Stage dashboardStage;  // 👈 this will store the dashboard window
        private ImageView profileImage; 
        private int userID;
        
      public  void show(Stage owner, Customer customer, int userID) {
         this.dashboardStage = owner; // ✅ save it to use on logout
         this.userID = userID;
             
        Stage profileStage = new Stage();
        profileStage.setTitle("Customer Profile");
        profileStage.initStyle(StageStyle.UNDECORATED);
        profileStage.initOwner(owner);
        profileStage.initModality(Modality.WINDOW_MODAL);

        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setStyle("-fx-background-color: white; -fx-border-color: #ccc;");
        sidebar.setPrefWidth(300);

        // =====================
    // 1. Profile Image (Clickable)
    // =====================
        String imagePath = customer.getCustomerImage();
if (imagePath == null || imagePath.isEmpty()) {
    imagePath = "/icons/default.png";  // Fallback to default image
}

profileImage = new ImageView();
try {
    // Load the image using the absolute path from the database
    File file = new File(imagePath);
    if (file.exists()) {
        profileImage.setImage(new Image(new FileInputStream(file)));
    } else {
        profileImage.setImage(new Image(CustomerProfile.class.getResourceAsStream("/icons/default.png")));
    }
} catch (FileNotFoundException e) {
    e.printStackTrace();
}

profileImage.setFitWidth(100);
profileImage.setFitHeight(100);
profileImage.setPreserveRatio(true);
profileImage.setCursor(Cursor.HAND);
profileImage.setStyle("-fx-effect: dropshadow(gaussian, #ccc, 10, 0, 0, 2);");

profileImage.setOnMouseClicked(e -> {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Choose Profile Image");
    fileChooser.getExtensionFilters().addAll(
        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
    );
    File selectedFile = fileChooser.showOpenDialog(profileStage);
    if (selectedFile != null) {
        try {
            // Save the absolute path of the selected file
            String absolutePath = selectedFile.getAbsolutePath();  // Get the full file path

            // Load the image using the absolute path
            FileInputStream inputStream = new FileInputStream(selectedFile);
            Image newImage = new Image(inputStream);
            profileImage.setImage(newImage);

            // Update the database with the absolute path
            updateCustomerImage(customer.getCustomerId(), absolutePath, userID);  // Save the path in the DB
            customer.setCustomerImage(absolutePath);  // Set the customer image path

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
});

        // =====================
        // 2. Name (Editable on Click)
        // =====================
        Label nameLabel = new Label(customer.getName());
        nameLabel.setFont(Font.font("Poppins", FontWeight.BOLD, 18));
        nameLabel.setCursor(Cursor.HAND);

        nameLabel.setOnMouseClicked(event -> {
            TextField nameField = new TextField(nameLabel.getText());
            nameField.setFont(Font.font("Poppins", FontWeight.NORMAL, 16));

            nameField.setOnAction(e -> {
                String newName = nameField.getText();
                customer.setName(newName);
                updateCustomerName(customer.getCustomerId(), newName, userID);
                sidebar.getChildren().set(sidebar.getChildren().indexOf(nameField), nameLabel);
                nameLabel.setText(newName);
            });

            sidebar.getChildren().set(sidebar.getChildren().indexOf(nameLabel), nameField);
        });
        // Track opened stages in a list or set
        Set<Stage> openedStages = new HashSet<>();

       // =====================
        // 3. Address (Clickable to Show List)
        // =====================
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
        addressLabel.setCursor(Cursor.HAND);
        
   addressLabel.setOnMouseClicked(e -> {
    List<Order> orders = OrderFetcher.fetchOrdersbyCustomerID(customer.getCustomerId());
    Set<String> seen = new HashSet<>(); // optional: prevent duplicates

    ListView<String> addressList = new ListView<>();

    for (Order order : orders) {
        // Manually create address string using available fields
        String street = order.getStreet();
        String barangay = order.getBarangay();
        String addressType = null; // leave null or default if not in order
        String contactNumber = order.getContactNumber();

        Address addr = new Address(
            0,               // dummy ID, not used
            street,
            barangay,
            addressType,     // still null unless you fetch it
            false,           // isDefault not used for display
            contactNumber
        );

        String addressStr = addr.toString(); // "street, barangay (type)"

        if (!seen.contains(addressStr)) {
            addressList.getItems().add(addressStr);
            seen.add(addressStr);
        }
    }
    // Optional: show popup or VBox with the ListView
    Alert addressAlert = new Alert(Alert.AlertType.INFORMATION);
        addressAlert.setTitle("Your Addresses");
        addressAlert.setHeaderText("Saved Addresses for " + customer.getName());
        addressAlert.getDialogPane().setContent(addressList);
        addressAlert.showAndWait();
    });
        
       Button historyBtn = new Button("View Order History");
        historyBtn.setOnAction(e -> {
            List<Order> orders = OrderFetcher.fetchOrdersbyCustomerID(customer.getCustomerId());

            // Sort orders by orderId in descending order
            orders.sort((o1, o2) -> Integer.compare(o2.getOrderId(), o1.getOrderId()));

            Stage orderHistoryStage = new Stage();
            orderHistoryStage.setTitle("Order History");
            VBox orderHistoryLayout = new VBox(10);
            orderHistoryLayout.setPadding(new Insets(10));

            // ListView to display orders
            ListView<HBox> orderListView = new ListView<>();

            for (Order order : orders) {
                Label label = new Label("Order #" + order.getOrderId() + " - " + order.getOrderDate());

                // Color-coding based on status
                String status = order.getOrderStatus().toLowerCase();
                if (status.equals("completed")) {
                    label.setTextFill(Color.GREEN);
                } else if (status.equals("cancelled")) {
                    label.setTextFill(Color.RED);
                }

                HBox item = new HBox(label);
                item.setPadding(new Insets(5));
                orderListView.getItems().add(item);
            }

           // Detect double-click to show details
        orderListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                HBox selectedBox = orderListView.getSelectionModel().getSelectedItem();
                if (selectedBox != null) {
                    Label label = (Label) selectedBox.getChildren().get(0);
                    String selectedOrderText = label.getText();
                    int orderId = Integer.parseInt(selectedOrderText.split(" ")[1].replace("#", ""));

                    // Find the order based on orderId
                    Order selectedOrderDetails = orders.stream()
                            .filter(order -> order.getOrderId() == orderId)
                            .findFirst()
                            .orElse(null);

                    // If the order is found, show its summary
                    if (selectedOrderDetails != null) {
                        OrderSummary orderSummary = new OrderSummary();
                        Stage orderSummaryStage = orderSummary.show(selectedOrderDetails, userID); // Get the stage

                        // Track the opened order summary stage
                        openedStages.add(orderSummaryStage);  // Add to the set of opened stages
                    }
                }
            }
        });

            orderHistoryLayout.getChildren().add(orderListView);

            Scene scene = new Scene(orderHistoryLayout, 400, 300);
            orderHistoryStage.setScene(scene);

            // Close when clicking outside
            orderHistoryStage.initModality(Modality.WINDOW_MODAL);
            orderHistoryStage.initOwner(((Node)e.getSource()).getScene().getWindow());
            orderHistoryStage.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
                if (!isNowFocused) {
                    orderHistoryStage.close();
                }
            });
            // Add to the set of opened stages
                openedStages.add(orderHistoryStage);

            orderHistoryStage.show();
        });



        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(e -> profileStage.close());
        
        
        // ✅ Logout button
        Button logoutBtn = new Button("Logout");
        logoutBtn.setStyle("-fx-background-color: red; -fx-text-fill: white;");
        logoutBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Logout");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to log out?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Close the main profile and dashboard windows
                profileStage.close();
                if (dashboardStage != null) {
                    dashboardStage.close();
                }

                // Close any additional opened stages (order history, order summary, etc.)
                for (Stage stage : openedStages) {
                    stage.close();
                }

                try {
                    Main mainApp = new Main(); // Main extends Application
                    Stage loginStage = new Stage();
                    mainApp.start(loginStage); // open login
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });




        sidebar.getChildren().addAll(profileImage, nameLabel, addressLabel, historyBtn, closeBtn, logoutBtn);

        Scene scene = new Scene(sidebar);
        profileStage.setScene(scene);

        profileStage.setX(Screen.getPrimary().getVisualBounds().getMaxX() - sidebar.getPrefWidth());
        profileStage.setY(100);

        profileStage.show();
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
