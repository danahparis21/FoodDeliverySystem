

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
import javafx.scene.Parent;

public class CustomerProfile {

    private Stage profileStage;
    private VBox sidebar;
    private Stage dashboardStage;  // 👈 this will store the dashboard window
        private ImageView profileImage; 
        
      public  void show(Stage owner, Customer customer) {
         this.dashboardStage = owner; // ✅ save it to use on logout

             
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
            updateCustomerImage(customer.getCustomerId(), absolutePath);  // Save the path in the DB
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
                updateCustomerName(customer.getCustomerId(), newName);
                sidebar.getChildren().set(sidebar.getChildren().indexOf(nameField), nameLabel);
                nameLabel.setText(newName);
            });

            sidebar.getChildren().set(sidebar.getChildren().indexOf(nameLabel), nameField);
        });

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
            // Fetch orders for the current customer (using the customer's ID)
            List<Order> orders = OrderFetcher.fetchOrdersbyCustomerID(customer.getCustomerId());

            // Create a new window to show the order history
            Stage orderHistoryStage = new Stage();
            orderHistoryStage.setTitle("Order History");
            VBox orderHistoryLayout = new VBox(10);
            orderHistoryLayout.setPadding(new Insets(10));

            // ListView to display orders
            ListView<String> orderListView = new ListView<>();
            orderListView.getItems().addAll(orders.stream()
                    .map(order -> "Order #" + order.getOrderId() + " - " + order.getOrderDate())
                    .collect(Collectors.toList()));

            // Event handler to view order details when an order is selected
            orderListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) {
                    String selectedOrder = orderListView.getSelectionModel().getSelectedItem();
                    if (selectedOrder != null) {
                        int orderId = Integer.parseInt(selectedOrder.split(" ")[1].replace("#", ""));
                        Order selectedOrderDetails = orders.stream()
                                .filter(order -> order.getOrderId() == orderId)
                                .findFirst()
                                .orElse(null);

                        if (selectedOrderDetails != null) {
                             OrderSummary orderSummary = new OrderSummary();
                            orderSummary.show(selectedOrderDetails);
                        }
                    }
                }
            });

            orderHistoryLayout.getChildren().add(orderListView);

            Scene scene = new Scene(orderHistoryLayout, 400, 300);
            orderHistoryStage.setScene(scene);
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
                profileStage.close();
                if (dashboardStage != null) {
                    dashboardStage.close();
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


      
      public static void updateCustomerImage(int customerId, String imagePath) {
    String query = "UPDATE customers SET customer_image = ? WHERE customer_id = ?";
    try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, imagePath);
        stmt.setInt(2, customerId);
        stmt.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

public static void updateCustomerName(int customerId, String name) {
    String query = "UPDATE customers SET name = ? WHERE customer_id = ?";
    try (Connection conn = Database.connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, name);
        stmt.setInt(2, customerId);
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
                    summary.show(order);

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
