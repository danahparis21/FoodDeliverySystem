
package andoksfooddeliverysystem;

import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OrderHistory {

    private VBox root;
    private int adminId; // the logged-in admin's ID
     private static final String MEDIUM_GRAY = "#E0E0E0";
     private static final String LIGHT_GRAY = "#F5F5F5";
    

    public OrderHistory(int adminId) {
        this.adminId = adminId;

        // Root container setup with modern styling
        root = new VBox(10);
        root.setPadding(new Insets(15));
        root.setPrefSize(1000, 700); // Larger size for better visibility
        root.setStyle("-fx-background-color: #f8f8f8;");

        // Add header with title
        Label headerLabel = new Label("Order History");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #D32F2F;"); // Red text
        
        // ====== SEARCH + SORT + FILTER CONTROLS ======
        // Search field with styling
        TextField searchField = new TextField();
        searchField.setPromptText("Search by order ID, payment method, or status...");
        searchField.setPrefWidth(600);
        searchField.setStyle("-fx-background-radius: 20px; -fx-padding: 5px 15px;");
        
        // Sort dropdown
        ComboBox<String> sortBy = new ComboBox<>();
        sortBy.getItems().addAll("Order # Ascending", "Order # Descending", "Most Recent", "Oldest");
        sortBy.setValue("Order # Ascending");
        sortBy.setStyle("-fx-background-radius: 4px;");
        // Status filter
        ComboBox<String> statusFilter = new ComboBox<>();
        statusFilter.getItems().addAll("All", "Pending", "Out for delivery", "Completed", "Cancelled", "Ready for Pick-up");
        statusFilter.setValue("All");
        statusFilter.setStyle("-fx-background-radius: 4px;");
        
        // Labels for filters
        Label sortLabel = new Label("Sort by:");
        sortLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        
        // Organize controls in HBox with improved spacing and alignment
        HBox filterControls = new HBox(10);
        filterControls.setAlignment(Pos.CENTER_LEFT);
        filterControls.setPadding(new Insets(10, 0, 10, 0));
        filterControls.getChildren().addAll(
            searchField, 
            new Separator(Orientation.VERTICAL), 
            statusLabel, statusFilter, 
            new Separator(Orientation.VERTICAL), 
            sortLabel, sortBy
        );
        
        // Add container styling
        filterControls.setStyle("-fx-background-color: " + LIGHT_GRAY + "; " +
                           "-fx-background-radius: 8px; " +
                           "-fx-padding: 15px;");
       
        // TABLEVIEW SETUP
        TableView<OrderHistoryFetcher> tableView = new TableView<>();
        tableView.setStyle("-fx-border-color: #FFC107; -fx-border-radius: 5px;");
        
        // Custom style for table header
        tableView.setStyle("-fx-background-color: white; " +
                         "-fx-border-color: " + MEDIUM_GRAY + "; " +
                         "-fx-border-radius: 4px;");
        
        TableColumn<OrderHistoryFetcher, Integer> orderIdCol = new TableColumn<>("Order ID");
        orderIdCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderIdCol.setPrefWidth(70);

        TableColumn<OrderHistoryFetcher, String> customerIdCol = new TableColumn<>("Customer ID");
        customerIdCol.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        customerIdCol.setPrefWidth(150);

        TableColumn<OrderHistoryFetcher, Double> totalPriceCol = new TableColumn<>("Total Price");
        totalPriceCol.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        totalPriceCol.setPrefWidth(90);
        
        // Add $ formatting to price column
        totalPriceCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", price));
                }
            }
        });

        TableColumn<OrderHistoryFetcher, String> paymentMethodCol = new TableColumn<>("Payment Method");
        paymentMethodCol.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        paymentMethodCol.setPrefWidth(120);

        TableColumn<OrderHistoryFetcher, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(130);
        
        // Colored status indicators
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(status);
                    
                    String textColor = switch(status.toLowerCase()) {
                        case "completed" -> "-fx-text-fill: green;";
                        case "pending" -> "-fx-text-fill: #FFC107;"; // yellow
                        case "cancelled" -> "-fx-text-fill: #D32F2F;"; // red
                        case "out for delivery" -> "-fx-text-fill: blue;";
                        case "ready for pick-up" -> "-fx-text-fill: purple;";
                        default -> "-fx-text-fill: black;";
                    };
                    
                    setStyle("-fx-font-weight: bold; " + textColor);
                }
            }
        });

        TableColumn<OrderHistoryFetcher, Timestamp> orderDateCol = new TableColumn<>("Order Date");
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        orderDateCol.setPrefWidth(150);
        
        // Format date nicely
        orderDateCol.setCellFactory(col -> new TableCell<>() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            
            @Override
            protected void updateItem(Timestamp date, boolean empty) {
                super.updateItem(date, empty);
                if (empty || date == null) {
                    setText(null);
                } else {
                    setText(dateFormat.format(date));
                }
            }
        });

        TableColumn<OrderHistoryFetcher, Integer> riderIdCol = new TableColumn<>("Rider ID");
        riderIdCol.setCellValueFactory(new PropertyValueFactory<>("riderId"));
        riderIdCol.setPrefWidth(70);

        TableColumn<OrderHistoryFetcher, String> proofCol = new TableColumn<>("Proof of Delivery");
        proofCol.setCellValueFactory(new PropertyValueFactory<>("proofOfDelivery"));
        proofCol.setPrefWidth(120);

        TableColumn<OrderHistoryFetcher, String> paymentStatusCol = new TableColumn<>("Payment Status");
        paymentStatusCol.setCellValueFactory(new PropertyValueFactory<>("paymentStatus"));
        paymentStatusCol.setPrefWidth(110);

        TableColumn<OrderHistoryFetcher, String> orderTypeCol = new TableColumn<>("Order Type");
        orderTypeCol.setCellValueFactory(new PropertyValueFactory<>("orderType"));
        orderTypeCol.setPrefWidth(100);

        TableColumn<OrderHistoryFetcher, String> pickupTimeCol = new TableColumn<>("Pickup Time");
        pickupTimeCol.setCellValueFactory(new PropertyValueFactory<>("pickupTime"));
        pickupTimeCol.setPrefWidth(100);
        
        
        
        tableView.getColumns().addAll(
            orderIdCol, customerIdCol, totalPriceCol, paymentMethodCol,
            statusCol, orderDateCol, riderIdCol, proofCol, paymentStatusCol, 
            orderTypeCol, pickupTimeCol
        );
        
        // Set table to use full space
        VBox.setVgrow(tableView, Priority.ALWAYS);

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

        // Load data from database
        try (Connection conn = Database.connect()) {
            String sql = "SELECT * FROM orders";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                OrderHistoryFetcher order = new OrderHistoryFetcher(
                    rs.getInt("order_id"),
                    rs.getDouble("total_price"),
                    rs.getTimestamp("order_date"),
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
        
        // Add footer with summary info
        HBox footerBar = new HBox(15);
        footerBar.setAlignment(Pos.CENTER_RIGHT);
        footerBar.setPadding(new Insets(10));
        footerBar.setStyle("-fx-background-color: white; -fx-border-color: #FFC107; -fx-border-radius: 5px;");
        
        Label totalOrdersLabel = new Label("Total Orders: " + orderData.size());
        totalOrdersLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        
        // Calculate total revenue
        double totalRevenue = orderData.stream()
            .mapToDouble(OrderHistoryFetcher::getTotalPrice)
            .sum();
            
        Label revenueLabel = new Label(String.format("Total Revenue: ₱%.2f", totalRevenue));
        revenueLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #D32F2F;");
        
        footerBar.getChildren().addAll(totalOrdersLabel, revenueLabel);
        
        // Stack components vertically in main container
        root.getChildren().addAll(headerLabel, filterControls, tableView, footerBar);
    }

    public VBox getRoot() {
        return root;
    }
 

 public static class OrderHistoryFetcher {
    private int orderId;
    private double totalPrice;
     private Timestamp orderDate;

    private String paymentMethod;
    private String status;
    private int riderId;
    private String proofOfDelivery;
    private String paymentStatus;
    private String orderType;
    private String pickupTime;
     private String orderStatus;
      private int customerId;

    public OrderHistoryFetcher(int orderId, double totalPrice, Timestamp orderDate, String paymentMethod,
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

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
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




