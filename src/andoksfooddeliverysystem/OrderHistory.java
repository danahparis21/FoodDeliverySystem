
package andoksfooddeliverysystem;

import java.security.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class OrderHistory {

    private VBox root;
    private int adminId; // the logged-in admin's ID

    public OrderHistory(int adminId) {
        this.adminId = adminId;

        root = new VBox(20); // Adjusted spacing
        root.setPadding(new Insets(20));
        root.setPrefSize(600, 400); // Set preferred size

          //===
     // ====== SEARCH + SORT + FILTER CONTROLS ======
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        ComboBox<String> sortBy = new ComboBox<>();
              sortBy.getItems().addAll("Order # Ascending", "Order # Descending", "Most Recent", "Oldest");
              sortBy.setValue("Order # Ascending");

        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Out for delivery", "Completed", "Cancelled", "Ready for Pick-up");
        statusFilter.setValue("All");


         HBox topBar = new HBox(10, searchField, statusFilter, sortBy);
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setPadding(new Insets(10));

          // TABLEVIEW SETUP
        TableView<OrderHistoryFetcher> tableView = new TableView<>();

        TableColumn<OrderHistoryFetcher, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));

        TableColumn<OrderHistoryFetcher, String> customerIdCol = new TableColumn<>("Customer Name");
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));

        TableColumn<OrderHistoryFetcher, Double> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        TableColumn<OrderHistoryFetcher, String> paymentMethodCol = new TableColumn<>("Payment Method");
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));

        TableColumn<OrderHistoryFetcher, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<OrderHistoryFetcher, Timestamp> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));

        TableColumn<OrderHistoryFetcher, Integer> riderIdCol = new TableColumn<>("Rider ID");
        riderIdCol.setCellValueFactory(new PropertyValueFactory<>("riderId"));

        TableColumn<OrderHistoryFetcher, String> proofCol = new TableColumn<>("Proof of Delivery");
        proofCol.setCellValueFactory(new PropertyValueFactory<>("proofOfDelivery"));

        TableColumn<OrderHistoryFetcher, String> paymentStatusCol = new TableColumn<>("Payment Status");
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));

        TableColumn<OrderHistoryFetcher, String> orderTypeCol = new TableColumn<>("Order Type");
        orderTypeCol.setCellValueFactory(new PropertyValueFactory<>("orderType"));

        TableColumn<OrderHistoryFetcher, String> pickupTimeCol = new TableColumn<>("Pickup Time");
        pickupTimeCol.setCellValueFactory(new PropertyValueFactory<>("pickupTime"));

        tableView.getColumns().addAll(orderIdCol, customerIdCol, totalPriceCol, paymentMethodCol,
            statusCol, orderDateCol, riderIdCol, proofCol, paymentStatusCol, orderTypeCol, pickupTimeCol);

        // Load Data
        ObservableList<OrderHistoryFetcher> orderData = FXCollections.observableArrayList();
     

        // === Search Field Listener ===
         // Filter & Sort
        FilteredList<OrderHistoryFetcher> filteredData = new FilteredList<>(orderData, p -> true);

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(order -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String lower = newVal.toLowerCase();
                return String.valueOf(order.getOrderId()).contains(lower)
                    || order.getPaymentMethod().toLowerCase().contains(lower)
                    || order.getOrderStatus().toLowerCase().contains(lower);
            });
        });

        statusFilter.setOnAction(e -> {
            String selectedStatus = statusFilter.getValue();
            filteredData.setPredicate(order -> {
                if ("All".equals(selectedStatus)) return true;
                return order.getOrderStatus().equalsIgnoreCase(selectedStatus);
            });
        });

        sortBy.setOnAction(e -> {
            Comparator<OrderHistoryFetcher> comparator = switch (sortBy.getValue()) {
                case "Order # Ascending" -> Comparator.comparing(OrderHistoryFetcher::getOrderId);
                case "Order # Descending" -> Comparator.comparing(OrderHistoryFetcher::getOrderId).reversed();
                case "Most Recent" -> Comparator.comparing(OrderHistoryFetcher::getOrderDate).reversed();
                case "Oldest" -> Comparator.comparing(OrderHistoryFetcher::getOrderDate);
                default -> null;
            };
            if (comparator != null) {
                SortedList<OrderHistoryFetcher> sortedData = new SortedList<>(filteredData);
                sortedData.setComparator(comparator);
                tableView.setItems(sortedData);
            }
        });

        // Default sort binding
        SortedList<OrderHistoryFetcher> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);

      

        try (Connection conn = Database.connect()) {
            String sql = "SELECT * FROM orders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                  OrderHistoryFetcher order = new OrderHistoryFetcher(
                rs.getInt("order_id"),
                rs.getDouble("total_price"),
                rs.getString("order_date"),
                rs.getString("payment_method"),
                rs.getString("status"),
                rs.getInt("rider_id"),
                rs.getString("proof_of_delivery_image_path"),
                rs.getString("payment_status"),
                rs.getString("order_type"),
                rs.getString("pickup_time"),
                          rs.getString("status"),
                          rs.getInt("customer_id")
            );
                orderData.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
           VBox searchTableView = new VBox(10); // VBox to stack search bar and table with spacing
    searchTableView.getChildren().addAll(topBar, tableView);

    // Add formPane and tableView to mainPane
    
   
    
         // ✅ Organize layout
    HBox mainPane = new HBox(20);
    mainPane.getChildren().addAll( searchTableView);
    root.getChildren().add(mainPane);
    root.setStyle("-fx-background-color: #f4f4f4;");

    }

    public VBox getRoot() {
        return root;
    }
 

 public static class OrderHistoryFetcher {
    private int orderId;
    private double totalPrice;
    private String orderDate;
    private String paymentMethod;
    private String status;
    private int riderId;
    private String proofOfDelivery;
    private String paymentStatus;
    private String orderType;
    private String pickupTime;
     private String orderStatus;
      private int customerId;

    public OrderHistoryFetcher(int orderId, double totalPrice, String orderDate, String paymentMethod,
                        String status, int riderId, String proofOfDelivery,
                        String paymentStatus, String orderType, String pickupTime, String orderStatus, int customerId) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.paymentMethod = paymentMethod;
        this.status = status;
        this.riderId = riderId;
        this.proofOfDelivery = proofOfDelivery;
        this.paymentStatus = paymentStatus;
        this.orderType = orderType;
        this.pickupTime = pickupTime;
        this.orderStatus = orderStatus;
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRiderId() {
        return riderId;
    }

    public void setRiderId(int riderId) {
        this.riderId = riderId;
    }

    public String getProofOfDelivery() {
        return proofOfDelivery;
    }

    public void setProofOfDelivery(String proofOfDelivery) {
        this.proofOfDelivery = proofOfDelivery;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(String pickupTime) {
        this.pickupTime = pickupTime;
    }
    
        public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
    
        public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
 }
}




