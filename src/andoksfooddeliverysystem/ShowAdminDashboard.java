package andoksfooddeliverysystem;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;



public class ShowAdminDashboard {
    private VBox root;
    private int userID;

    private Label storeStatusLabel = new Label("Loading...");
    private Button toggleShopButton = new Button("Toggle Shop");

    private Label adminNameLabel = new Label("Admin Name");
    private Label adminEmailLabel = new Label("admin@email.com");

    private Label ordersTodayLabel = new Label("Loading...");
    private Label customersTodayLabel = new Label("Loading...");
    
    private Label allOrdersLabel = new Label("Loading...");

    private LineChart<String, Number> lineChart;

    public ShowAdminDashboard(int userID) {
        this.userID = userID;
        createUI();
        loadAdminDetails(userID);
        loadStoreStatus();
        loadTodayStats();
    }

    private void createUI() {
    // Profile Section
    VBox profileBox = new VBox(10, adminNameLabel, adminEmailLabel);
    profileBox.setPadding(new Insets(10));
    profileBox.setStyle("-fx-border-color: gray; -fx-border-width: 1;");

    // Store Section
    VBox storeBox = new VBox(10, storeStatusLabel, toggleShopButton);
    storeBox.setPadding(new Insets(10));
    toggleShopButton.setOnAction(e -> toggleShop());

    // Stats Section
    VBox statsBox = new VBox(10, ordersTodayLabel, customersTodayLabel, allOrdersLabel);
    statsBox.setPadding(new Insets(10));

        // Performance Section (to add now)
    VBox performanceBox = createPerformanceSection();

    // Wrap the performanceBox in a ScrollPane for scrolling ability
    ScrollPane performanceScrollPane = new ScrollPane(performanceBox);
    performanceScrollPane.setFitToWidth(true); // Ensures it fills the width

    // Main Layout
    root = new VBox(20, profileBox, storeBox, statsBox, performanceScrollPane);
    root.setPadding(new Insets(20));
}

    public Node getRoot() {
        return root;
    }

  private void loadAdminDetails(int userId) {
    String query = "SELECT full_name, email FROM users WHERE user_id = ?";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, userId);

        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                String name = rs.getString("full_name");
                String email = rs.getString("email");

                adminNameLabel.setText("Admin: " + name);
                adminEmailLabel.setText(email);

                System.out.println("✅ Loaded admin details: " + name + ", " + email);
            } else {
                System.out.println("⚠️ No admin found with userID " + userId);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        System.out.println("❌ Failed to load admin details from database.");
    }
}


  private void loadStoreStatus() {
    String query = "SELECT store_status FROM store WHERE store_id = 1";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            String status = rs.getString("store_status");
            storeStatusLabel.setText("Store is currently: " + status);
            toggleShopButton.setText(status.equalsIgnoreCase("Open") ? "Close Shop" : "Open Shop");

            System.out.println("✅ Store status loaded: " + status);

            // If store is closed at login, show dialog
            if (status.equalsIgnoreCase("Close")) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Store is Closed");
                alert.setHeaderText("Store is closed.");
                alert.setContentText("Would you like to open and start operations?");

                ButtonType openButton = new ButtonType("Open Store");
                ButtonType laterButton = new ButtonType("Later", ButtonBar.ButtonData.CANCEL_CLOSE);

                alert.getButtonTypes().setAll(openButton, laterButton);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.isPresent() && result.get() == openButton) {
                    updateStoreStatus("Open");
                    showInfo("Andok's is now OPEN! You will start receiving orders!");
                }
            }

        } else {
            System.out.println("⚠️ No store data found!");
        }

    } catch (SQLException e) {
        e.printStackTrace();
        storeStatusLabel.setText("❌ Error loading store status");
    }
}


    private void toggleShop() {
        String currentStatus = storeStatusLabel.getText().contains("Open") ? "Open" : "Close";
        String newStatus = currentStatus.equals("Open") ? "Close" : "Open";

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Action");

        if (newStatus.equals("Close")) {
            confirm.setHeaderText("Are you sure you want to CLOSE the store?");
            confirm.setContentText("All orders today will be cancelled.");
        } else {
            confirm.setHeaderText("Open the store?");
            confirm.setContentText("You will start receiving orders.");
        }

        ButtonType yes = new ButtonType("Yes");
        ButtonType later = new ButtonType("Later", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirm.getButtonTypes().setAll(yes, later);

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == yes) {
            updateStoreStatus(newStatus);
            if (newStatus.equals("Open")) {
                showInfo("✅ Andok's is now OPEN! You will start receiving orders!");
            } else {
                showInfo("⚠️ Andok's is now CLOSED! All orders today will be cancelled.");
            }
        }
    }
private void updateStoreStatus(String newStatus) {
    String query = "UPDATE store SET store_status = ?, last_modified_by = ? WHERE store_id = 1";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setString(1, newStatus);
        stmt.setInt(2, userID);
        int updated = stmt.executeUpdate();

        if (updated > 0) {
            storeStatusLabel.setText("Store is currently: " + newStatus);
            toggleShopButton.setText(newStatus.equals("Open") ? "Close Shop" : "Open Shop");
            System.out.println("✅ Store status updated to: " + newStatus);

            // 👇 Cancel all pending orders if store is closed
            if (newStatus.equalsIgnoreCase("Close")) {
                System.out.println("🧪 newStatus value: " + newStatus);

                String cancelQuery = "UPDATE orders SET status = 'Cancelled', last_modified_by = ? WHERE status = 'Pending'";
                try (PreparedStatement cancelStmt = conn.prepareStatement(cancelQuery)) {
                    cancelStmt.setInt(1, userID);
                    int cancelled = cancelStmt.executeUpdate();
                    System.out.println("🛑 Pending orders cancelled: " + cancelled);
                }
            }

        } else {
            System.out.println("❌ Failed to update store status.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    private void showInfo(String message) {
    Alert info = new Alert(Alert.AlertType.INFORMATION);
    info.setTitle("Notification");
    info.setHeaderText(null);
    info.setContentText(message);
    info.showAndWait();
}

    
private void loadTodayStats() {
    // Today's stats
   String todayQuery = "SELECT * FROM today_order_stats";


    // All-time stats
    String allTimeQuery = "SELECT * FROM all_time_order_stats";


    try (Connection conn = Database.connect()) {
        // Today's orders
        try (PreparedStatement stmt = conn.prepareStatement(todayQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total_orders");
                int completed = rs.getInt("completed");
                int pending = rs.getInt("pending");
                int cancelled = rs.getInt("cancelled");
                int customers = rs.getInt("unique_customers");
                int delivery = rs.getInt("delivery_orders");
                int pickup = rs.getInt("pickup_orders");

                ordersTodayLabel.setText("Orders Today: " + total + " (" +
                        completed + " Completed, " +
                        pending + " Pending, " +
                        cancelled + " Cancelled)");

                customersTodayLabel.setText("Customers Today: " + customers +
                        "\nDelivery orders: " + delivery +
                        "\nPickUp orders: " + pickup);
            }
        }

        // All-time orders
        try (PreparedStatement stmt = conn.prepareStatement(allTimeQuery);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                int total = rs.getInt("total_orders");
                int completed = rs.getInt("completed");
                int pending = rs.getInt("pending");
                int cancelled = rs.getInt("cancelled");

                allOrdersLabel.setText("All Orders: " + total + " (" +
                        completed + " Completed, " +
                        pending + " Pending, " +
                        cancelled + " Cancelled)");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        ordersTodayLabel.setText("❌ Failed to load orders");
        customersTodayLabel.setText("");
        allOrdersLabel.setText("❌ Failed to load all-time orders");
    }
}

private VBox createPerformanceSection() {
    // Title for Performance
    Label performanceTitle = new Label("Performance Section");
    performanceTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    // Generate Report Button
   Button generateReportButton = new Button("Generate Report");
        generateReportButton.setOnAction(e -> {
            // When the button is clicked, generate the report
            ReportGenerator reportGenerator = new ReportGenerator(); // Create instance of ReportGenerator
            reportGenerator.generateReport(); // Call the method to generate the report
        });


    
    VBox pieCharts = createPieChartsPanel();


    // 1. Get the complete chart panel (which now includes both chart and controls)
        VBox revenueChartPanel = createRevenueChartPanel();

   
    // In your main dashboard setup:
    VBox menuCategoryChart = createMenuCategoryChartPanel();
    VBox riderOrdersChart = createRiderOrdersChartPanel();

    // Add to your layout (example with SplitPane)
    SplitPane chartsPane = new SplitPane();
    chartsPane.getItems().addAll(menuCategoryChart, riderOrdersChart);
    chartsPane.setDividerPositions(0.5); // 50-50 split

        // In your main dashboard:
    VBox riderDashboard = createRiderPerformanceDashboard();


    ComboBox<String> customerTableFilter = new ComboBox<>();
    customerTableFilter.getItems().addAll("Today", "This Week", "This Month", "All Time");
    customerTableFilter.setValue("All Time");

    VBox customerTableBox = new VBox(10);
    GridPane customerTable = createLoyalCustomerTable("All Time");

    customerTableFilter.setOnAction(e -> {
        String selected = customerTableFilter.getValue();
        customerTableBox.getChildren().set(1, createLoyalCustomerTable(selected));
    });

    customerTableBox.getChildren().addAll(customerTableFilter, customerTable);
    
    //RATINGS 
        VBox ratingsView = createRatingsViewer();
    
    // Layout for the performance section
    VBox performanceBox = new VBox(10, performanceTitle, generateReportButton, pieCharts, revenueChartPanel, 
            chartsPane, riderDashboard, customerTableBox, ratingsView);
    performanceBox.setPadding(new Insets(10));
    
    return performanceBox;
}

private ObservableList<PieChart.Data> getMostOrderedItemsData() {
    ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
   String sql = "SELECT * FROM menu_item_sales_view";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            data.add(new PieChart.Data(rs.getString("name"), rs.getInt("total_quantity")));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}

private ObservableList<PieChart.Data> getPaymentMethodData() {
    ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
    String sql = "SELECT * FROM payment_method_count_view";

    try (Connection conn =  Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            data.add(new PieChart.Data(rs.getString("payment_method"), rs.getInt("count")));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}

private ObservableList<PieChart.Data> getBusiestTimeOfDayData() {
    ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
   String sql = "SELECT * FROM hourly_order_count_view";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            String label = String.format("%02d:00", rs.getInt("hour"));
            data.add(new PieChart.Data(label, rs.getInt("count")));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return data;
}
private VBox createPieChartsPanel() {
    VBox box = new VBox(20);
    box.setPadding(new Insets(10));
    
    // Create the charts
    PieChart itemChart = new PieChart(getMostOrderedItemsData());
    PieChart paymentChart = new PieChart(getPaymentMethodData());
    PieChart timeChart = new PieChart(getBusiestTimeOfDayData());
    
    // Force the exact same config for all charts
    for (PieChart chart : new PieChart[] {itemChart, paymentChart, timeChart}) {
        // Remove all sizing attributes from charts themselves
        chart.setMinSize(0, 0);
        chart.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        chart.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        chart.setLegendVisible(false);
        chart.setLabelsVisible(true);
        chart.setAnimated(false);
    }
    
    // Set titles
    itemChart.setTitle("Most Ordered Items");
    paymentChart.setTitle("Payment Methods");
    timeChart.setTitle("Busiest Time of Day (Today)");
    
    // Process labels for each chart
    processLabels(itemChart, true); // Truncate names for item chart
    processLabels(paymentChart, false);
    processLabels(timeChart, false);
    
    // Create a grid layout with precise constraints
    GridPane grid = new GridPane();
    grid.setAlignment(Pos.CENTER);
    grid.setHgap(20);
    grid.setVgap(20);
    
    // Create completely equal-sized column constraints
    ColumnConstraints col1 = new ColumnConstraints();
    col1.setPercentWidth(33.33);
    col1.setHalignment(HPos.CENTER);
    col1.setHgrow(Priority.ALWAYS);
    
    ColumnConstraints col2 = new ColumnConstraints();
    col2.setPercentWidth(33.33);
    col2.setHalignment(HPos.CENTER);
    col2.setHgrow(Priority.ALWAYS);
    
    ColumnConstraints col3 = new ColumnConstraints();
    col3.setPercentWidth(33.33);
    col3.setHalignment(HPos.CENTER);
    col3.setHgrow(Priority.ALWAYS);
    
    grid.getColumnConstraints().addAll(col1, col2, col3);
    
    // Create equal-sized wrappers for each chart that strictly enforce dimensions
    StackPane itemWrapper = createChartWrapper(itemChart, 400);
    StackPane paymentWrapper = createChartWrapper(paymentChart, 400);
    StackPane timeWrapper = createChartWrapper(timeChart, 400);
    
    // Add charts to grid cells
    grid.add(itemWrapper, 0, 0);
    grid.add(paymentWrapper, 1, 0);
    grid.add(timeWrapper, 2, 0);
    
    box.getChildren().add(grid);
    return box;
}

// Create a wrapper with fixed size and clip to ensure uniform appearance
private StackPane createChartWrapper(PieChart chart, double size) {
    StackPane wrapper = new StackPane(chart);
    wrapper.setMinSize(size, size);
    wrapper.setPrefSize(size, size);
    wrapper.setMaxSize(size, size);
    
    // Use clip to enforce exact dimensions
    Rectangle clip = new Rectangle(size, size);
    wrapper.setClip(clip);
    
    // Add a visible border to debug size issues
    wrapper.setStyle("-fx-border-color: lightgray; -fx-border-width: 1px;");
    
    return wrapper;
}

// Process labels for chart data
private void processLabels(PieChart chart, boolean truncateNames) {
    for (PieChart.Data data : chart.getData()) {
        String name = data.getName();
        
        // Truncate names if needed
        if (truncateNames && name.length() > 10) {
            name = name.substring(0, 7) + "...";
        }
        
        // Set the visible label
        final String displayName = name;
        data.setName(displayName + " - " + (int)data.getPieValue());
        
        // Add tooltip that shows full data
        Tooltip tooltip = new Tooltip(displayName + ": " + (int)data.getPieValue() + " orders");
        Tooltip.install(data.getNode(), tooltip);
        
        // Add hover effect
        final Node node = data.getNode();
        node.setOnMouseEntered(e -> {
            node.setEffect(new Glow(0.5));
            node.setScaleX(1.05);
            node.setScaleY(1.05);
        });
        node.setOnMouseExited(e -> {
            node.setEffect(null);
            node.setScaleX(1);
            node.setScaleY(1);
        });
    }
}
public VBox createRevenueChartPanel() {
    // Create axes and chart
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Time");
    yAxis.setLabel("Revenue (₱)");
    
    lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Total Revenue Over Time");
    lineChart.setAnimated(false); // Disable animations for better performance
    lineChart.setCreateSymbols(true); // Ensure data points are visible
    lineChart.setLegendVisible(true);

    // Create time range selector
    ComboBox<String> timeRangeComboBox = new ComboBox<>();
    timeRangeComboBox.getItems().addAll("Today", "Weekly", "Monthly", "Yearly");
    timeRangeComboBox.setValue("Today"); // Default selection
    timeRangeComboBox.setOnAction(e -> updateRevenueChart(timeRangeComboBox.getValue()));
    
    // Add some styling to the combo box
    timeRangeComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 150px;");
    
    // Create a control panel
    HBox controlPanel = new HBox(10, new Label("Time Range:"), timeRangeComboBox);
    controlPanel.setAlignment(Pos.CENTER_LEFT);
    controlPanel.setPadding(new Insets(10));
    
    // Combine everything in a VBox
    VBox chartContainer = new VBox(10, controlPanel, lineChart);
    chartContainer.setPadding(new Insets(15));
    
    // Initialize chart data
    updateRevenueChart("Today");
    
    return chartContainer;
}
private void updateRevenueChart(String range) {
    String query = buildRevenueQuery(range);
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Revenue - " + range);

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        while (rs.next()) {
            String timeLabel = formatTimeLabel(range, rs.getString(1));
            double revenue = rs.getDouble(2);
            series.getData().add(new XYChart.Data<>(timeLabel, revenue));
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return;
    }

    lineChart.getData().clear();
    lineChart.getData().add(series);
    
    // Apply CSS styling to make lines more visible
    for (XYChart.Series<String, Number> s : lineChart.getData()) {
        for (XYChart.Data<String, Number> d : s.getData()) {
            Node line = d.getNode();
            if (line != null) {
                line.setStyle("-fx-stroke-width: 2px; -fx-stroke: #2A5058;");
            }
        }
    }
}

private String formatTimeLabel(String range, String rawValue) {
    switch (range) {
        case "Today":
            return "Today";
        case "Weekly":
            return "Week " + rawValue;
        case "Monthly":
            // Convert month number to month name
            try {
                int month = Integer.parseInt(rawValue);
                return new DateFormatSymbols().getMonths()[month-1];
            } catch (Exception e) {
                return rawValue;
            }
        case "Yearly":
            return rawValue;
        default:
            return rawValue;
    }
}private String buildRevenueQuery(String range) {
    switch (range) {
        case "Today":
            return "SELECT * FROM revenue_today";
        case "Weekly":
            return "SELECT * FROM revenue_weekly";
        case "Monthly":
            return "SELECT * FROM revenue_monthly";
        case "Yearly":
            return "SELECT * FROM revenue_yearly";
        default:
            return buildRevenueQuery("Today");
    }
}

private VBox createMenuCategoryChartPanel() {
    // Create axes
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Menu Category");
    yAxis.setLabel("Number of Items");
    yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "", "")); // Remove number formatting

    // Create chart
    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Menu Items per Category");
    barChart.setLegendVisible(false);
    barChart.setAnimated(false); // Disable animation for better performance
    barChart.setCategoryGap(20); // Space between bars

    // Create refresh button
    Button refreshButton = new Button("Refresh Data");
    refreshButton.setOnAction(e -> updateMenuCategoryChart(barChart));
    
    // Style components
    refreshButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 120px;");
    barChart.setStyle("-fx-font-size: 14px;");
    barChart.setPrefSize(600, 400);

    // Create control panel
    HBox controlPanel = new HBox(10, refreshButton);
    controlPanel.setAlignment(Pos.CENTER_LEFT);
    controlPanel.setPadding(new Insets(10));

    // Create container
    VBox chartContainer = new VBox(10, controlPanel, barChart);
    chartContainer.setPadding(new Insets(15));

    // Load initial data
    updateMenuCategoryChart(barChart);

    return chartContainer;
}

private void updateMenuCategoryChart(BarChart<String, Number> barChart) {
    // SQL query to count items per category
    String query = "SELECT * FROM category_item_count";

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Menu Items");

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        // Clear previous data
        barChart.getData().clear();

        // Add data from database
        while (rs.next()) {
            String category = rs.getString("category_name");
            int count = rs.getInt("item_count");
            XYChart.Data<String, Number> data = new XYChart.Data<>(category, count);
            series.getData().add(data);
        }

        barChart.getData().add(series);

        // Apply custom styling to bars
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                // Different color for each bar
                int index = series.getData().indexOf(data);
                String color = getCategoryColor(index);
                node.setStyle("-fx-bar-fill: " + color + ";");
                
                // Add tooltip
                Tooltip tooltip = new Tooltip(
                    String.format("%s: %d items", data.getXValue(), data.getYValue().intValue())
                );
                Tooltip.install(node, tooltip);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        // Fallback to sample data if query fails
        loadSampleData(barChart);
    }
}

private String getCategoryColor(int index) {
    // Color palette for categories
    String[] colors = {
        "#3498db", "#2ecc71", "#e74c3c", "#f39c12", "#9b59b6",
        "#1abc9c", "#d35400", "#34495e", "#16a085", "#c0392b"
    };
    return colors[index % colors.length];
}

private void loadSampleData(BarChart<String, Number> barChart) {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Menu Items (Sample Data)");
    
    series.getData().add(new XYChart.Data<>("Affordable Meals", 15));
    series.getData().add(new XYChart.Data<>("Drinks", 8));
    series.getData().add(new XYChart.Data<>("Desserts", 5));
    
    barChart.getData().add(series);
}

private VBox createRiderOrdersChartPanel() {
    // Create axes
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Rider");
    yAxis.setLabel("Number of Orders");
    yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis, "", ""));

    // Create chart
    BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
    barChart.setTitle("Orders per Rider");
    barChart.setLegendVisible(false);
    barChart.setAnimated(false);
    barChart.setCategoryGap(20);
    barChart.setStyle("-fx-font-size: 14px;");
    barChart.setPrefSize(600, 400);

    // Create time period selector
    ComboBox<String> timePeriodCombo = new ComboBox<>();
    timePeriodCombo.getItems().addAll("All Time", "This Year", "This Month", "This Week", "Today");
    timePeriodCombo.setValue("This Month");
    timePeriodCombo.setOnAction(e -> updateRiderOrdersChart(barChart, timePeriodCombo.getValue()));

    // Add status filter
    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("All Orders", "Completed Only");
    statusCombo.setValue("Completed Only");
    statusCombo.setOnAction(e -> updateRiderOrdersChart(barChart, timePeriodCombo.getValue()));

    // Style components
    timePeriodCombo.setStyle("-fx-font-size: 14px; -fx-pref-width: 150px;");
    statusCombo.setStyle("-fx-font-size: 14px; -fx-pref-width: 150px;");

    // Create control panel
    HBox controlPanel = new HBox(10, 
        new Label("Time Period:"), timePeriodCombo,
        new Label("Status:"), statusCombo
    );
    controlPanel.setAlignment(Pos.CENTER_LEFT);
    controlPanel.setPadding(new Insets(10));

    // Create container
    VBox chartContainer = new VBox(10, controlPanel, barChart);
    chartContainer.setPadding(new Insets(15));

    // Load initial data
    updateRiderOrdersChart(barChart, "This Month");

    return chartContainer;
}

private void updateRiderOrdersChart(BarChart<String, Number> barChart, String timePeriod) {
    updateRiderOrdersChart(barChart, timePeriod, true); // Default to completed orders
}

private void updateRiderOrdersChart(BarChart<String, Number> barChart, String timePeriod, boolean completedOnly) {
    String query = buildRiderOrdersQuery(timePeriod, completedOnly);

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Orders");

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        barChart.getData().clear();

        while (rs.next()) {
            String riderName = rs.getString("rider_name");
            int orderCount = rs.getInt("order_count");
            XYChart.Data<String, Number> data = new XYChart.Data<>(riderName, orderCount);
            series.getData().add(data);
        }

        barChart.getData().add(series);

        // Style bars with tooltips
        int colorIndex = 0;
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                node.setStyle("-fx-bar-fill: " + getRiderColor(colorIndex++) + ";");
                
                Tooltip tooltip = new Tooltip(
                    String.format("%s\n%d orders", data.getXValue(), data.getYValue().intValue())
                );
                Tooltip.install(node, tooltip);
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load rider order data");
        loadSampleRiderData(barChart);
    }
}private String buildRiderOrdersQuery(String timePeriod, boolean completedOnly) {
    StringBuilder query = new StringBuilder();
       query.append("SELECT name AS rider_name, COUNT(o.order_id) AS order_count ")
         .append("FROM orders o ")
         .append("INNER JOIN riders r ON o.rider_id = r.rider_id ");
    
    // Add status filter if needed
    if (completedOnly) {
        query.append("WHERE o.status = 'Completed' ");
    } else {
        query.append("WHERE 1=1 ");
    }
    
    // Add time period filter
    switch (timePeriod) {
        case "Today":
            query.append("AND DATE(o.order_date) = CURRENT_DATE() ");
            break;
        case "This Week":
            query.append("AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) ")
                 .append("AND WEEK(o.order_date) = WEEK(CURRENT_DATE()) ");
            break;
        case "This Month":
            query.append("AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) ")
                 .append("AND MONTH(o.order_date) = MONTH(CURRENT_DATE()) ");
            break;
        case "This Year":
            query.append("AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) ");
            break;
        // "All Time" needs no additional conditions
    }
    
    // Complete query
    query.append("GROUP BY r.name ")
         .append("ORDER BY order_count DESC");
    
    return query.toString();
}



private String getRiderColor(int index) {
    // Professional color palette
    String[] colors = {
        "#4285F4", "#34A853", "#FBBC05", "#EA4335", "#673AB7",
        "#FF5722", "#009688", "#795548", "#607D8B", "#9C27B0"
    };
    return colors[index % colors.length];
}

private void loadSampleRiderData(BarChart<String, Number> barChart) {
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Orders (Sample Data)");
    
    series.getData().add(new XYChart.Data<>("John Rider", 18));
    series.getData().add(new XYChart.Data<>("Maria Courier", 12));
    series.getData().add(new XYChart.Data<>("Alex Deliver", 9));
    series.getData().add(new XYChart.Data<>("Sam Transport", 6));
    
    barChart.getData().add(series);
}

private void showAlert(Alert.AlertType type, String title, String message) {
    Platform.runLater(() -> {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    });
}

public VBox createRiderPerformanceDashboard() {
    // Create main container
    VBox dashboard = new VBox(20);
    dashboard.setPadding(new Insets(20));
    dashboard.setStyle("-fx-background-color: #f5f5f5;");

    // Create title
    Label title = new Label("Rider Performance Overview");
    title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #333;");

    // Create the performance table (using GridPane for better control)
    GridPane performanceTable = createRiderPerformanceTable();

    // Add components to dashboard
    dashboard.getChildren().addAll(title, performanceTable);

    // Load data
    loadRiderPerformanceData(performanceTable);

    return dashboard;
}

private GridPane createRiderPerformanceTable() {
    GridPane table = new GridPane();
    table.setHgap(15);
    table.setVgap(10);
    table.setPadding(new Insets(15));
    table.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: #ddd;");

    // Column headers
    addTableHeader(table, "Rider Name", 0);
    addTableHeader(table, "Rating", 1);
    addTableHeader(table, "Total Orders", 2);
    addTableHeader(table, "Total Earnings", 3);
    addTableHeader(table, "Status", 4);

    return table;
}

private void addTableHeader(GridPane table, String text, int column) {
    Label header = new Label(text);
    header.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #555;");
    header.setPadding(new Insets(0, 0, 5, 0));
    table.add(header, column, 0);
}

private void loadRiderPerformanceData(GridPane table) {
    String query = "SELECT rider_id, name, average_rating, total_reviews, order_count, total_earnings, status " +
                   "FROM rider_performance_view " +
                   "ORDER BY order_count DESC";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        int row = 1; // Start after headers
        while (rs.next()) {
            String riderName = rs.getString("name");
            double rating = rs.getDouble("average_rating");
            int reviews = rs.getInt("total_reviews");
            int orderCount = rs.getInt("order_count");
            double earnings = rs.getDouble("total_earnings");
            String status = rs.getString("status");

            // Add data to table
            addTableRow(table, riderName, rating, reviews, orderCount, earnings, status, row);
            row++;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        loadSampleRiderData(table);
    }
}

private void addTableRow(GridPane table, String name, double rating, int reviews, 
                        int orders, double earnings, String status, int row) {
    // Rider Name
    Label nameLabel = new Label(name);
    nameLabel.setStyle("-fx-font-size: 14px;");
    table.add(nameLabel, 0, row);

    // Rating (with star visualization)
    HBox ratingBox = new HBox(5);
    ratingBox.setAlignment(Pos.CENTER_LEFT);
    
    Label ratingLabel = new Label(String.format("%.1f", rating));
    ratingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    
    // Star icon (using Unicode star character)
    Text star = new Text("★");
    star.setStyle("-fx-fill: #FFD700; -fx-font-size: 16px;");
    
    Label reviewsLabel = new Label("(" + reviews + ")");
    reviewsLabel.setStyle("-fx-text-fill: #777; -fx-font-size: 12px;");
    
    ratingBox.getChildren().addAll(star, ratingLabel, reviewsLabel);
    table.add(ratingBox, 1, row);

    // Order Count
    Label ordersLabel = new Label(String.valueOf(orders));
    ordersLabel.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER;");
    table.add(ordersLabel, 2, row);

    // Earnings (formatted as currency)
    Label earningsLabel = new Label(String.format("₱%,.2f", earnings));
    earningsLabel.setStyle("-fx-font-size: 14px; -fx-alignment: CENTER_RIGHT;");
    table.add(earningsLabel, 3, row);

    // Status (with color coding)
    Label statusLabel = new Label(status);
    statusLabel.setStyle(getStatusStyle(status));
    table.add(statusLabel, 4, row);
}

private String getStatusStyle(String status) {
    switch (status.toLowerCase()) {
        case "available":
            return "-fx-text-fill: #2ecc71; -fx-font-weight: bold; -fx-font-size: 14px;";
        case "offline":
            return "-fx-text-fill: #95a5a6; -fx-font-weight: bold; -fx-font-size: 14px;";
        case "on delivery":
            return "-fx-text-fill: #3498db; -fx-font-weight: bold; -fx-font-size: 14px;";
        default:
            return "-fx-font-size: 14px;";
    }
}

private void loadSampleRiderData(GridPane table) {
    addTableRow(table, "John Rider", 4.8, 42, 18, 12500.50, "Available", 1);
    addTableRow(table, "Maria Courier", 4.9, 35, 15, 11200.00, "On Delivery", 2);
    addTableRow(table, "Alex Deliver", 4.5, 28, 12, 9800.75, "Offline", 3);
    addTableRow(table, "Sam Transport", 4.2, 15, 8, 7200.00, "Available", 4);
}
private GridPane createLoyalCustomerTable(String timePeriod) {
    GridPane table = new GridPane();
    table.setHgap(15);
    table.setVgap(10);
    table.setPadding(new Insets(15));
    table.setStyle("-fx-background-color: white; -fx-border-radius: 5; -fx-border-color: #ddd;");

    // Column headers
    addTableHeader(table, "Customer Name", 0);
    addTableHeader(table, "Email", 1);
    addTableHeader(table, "Total Orders", 2);
    addTableHeader(table, "Total Spent", 3);

    String query = buildLoyalCustomerTableQuery(timePeriod);
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        int row = 1;
        while (rs.next()) {
            String name = rs.getString("customer_name");
            String email = rs.getString("email");
            int orderCount = rs.getInt("order_count");
            double totalSpent = rs.getDouble("total_spent");

            table.add(new Label(name), 0, row);
            table.add(new Label(email != null ? email : "N/A"), 1, row);
            table.add(new Label(String.valueOf(orderCount)), 2, row);
            table.add(new Label("₱" + String.format("%.2f", totalSpent)), 3, row);

            row++;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return table;
}
private String buildLoyalCustomerTableQuery(String timePeriod) {
    String baseQuery =
        "SELECT c.name AS customer_name, c.email, COUNT(o.order_id) AS order_count, " +
        "SUM(o.total_price) AS total_spent " +
        "FROM orders o " +
        "JOIN customers c ON o.customer_id = c.customer_id ";

    switch (timePeriod) {
        case "Today":
            baseQuery += "WHERE DATE(o.order_date) = CURRENT_DATE() ";
            break;
        case "This Week":
            baseQuery += "WHERE WEEK(o.order_date) = WEEK(CURRENT_DATE()) " +
                         "AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) ";
            break;
        case "This Month":
            baseQuery += "WHERE MONTH(o.order_date) = MONTH(CURRENT_DATE()) " +
                         "AND YEAR(o.order_date) = YEAR(CURRENT_DATE()) ";
            break;
        // "All Time" = no filter
    }

    baseQuery += "GROUP BY c.name, c.email ORDER BY order_count DESC";
    return baseQuery;
}

private VBox createRatingsViewer() {
    // Title for Ratings section
    Label ratingsTitle = new Label("Customer Ratings");
    ratingsTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
    
    // Create filter controls
    HBox filterBox = new HBox(10);
    filterBox.setAlignment(Pos.CENTER_LEFT);
    
    ComboBox<String> ratingTypeFilter = new ComboBox<>();
    ratingTypeFilter.getItems().addAll("All Ratings", "Food Only", "Delivery Only");
    ratingTypeFilter.setValue("All Ratings");
    
    ComboBox<String> timeFilter = new ComboBox<>();
    timeFilter.getItems().addAll("All Time", "Today", "This Week", "This Month");
    timeFilter.setValue("All Time");
    
    filterBox.getChildren().addAll(
        new Label("Show:"), ratingTypeFilter,
        new Label("From:"), timeFilter
    );
    
    // Create table for ratings
    TableView<Rating> ratingsTable = createRatingsTable();
    
    // Load initial data
    updateRatingsTable(ratingsTable, "All Ratings", "All Time");
    
    // Set up filter actions
    ratingTypeFilter.setOnAction(e -> updateRatingsTable(
        ratingsTable, 
        ratingTypeFilter.getValue(), 
        timeFilter.getValue()
    ));
    
    timeFilter.setOnAction(e -> updateRatingsTable(
        ratingsTable, 
        ratingTypeFilter.getValue(), 
        timeFilter.getValue()
    ));
    
    // Put table in scrollable container
    ScrollPane scrollPane = new ScrollPane(ratingsTable);
    scrollPane.setFitToWidth(true);
    scrollPane.setPrefHeight(300);
    
    // Create container
    VBox ratingsBox = new VBox(10, ratingsTitle, filterBox, scrollPane);
    ratingsBox.setPadding(new Insets(10));
    ratingsBox.setStyle("-fx-background-color: white; -fx-border-radius: 5;");
    
    return ratingsBox;
}

private TableView<Rating> createRatingsTable() {
    TableView<Rating> table = new TableView<>();
    
    // Order ID column
    TableColumn<Rating, Integer> orderCol = new TableColumn<>("Order ID");
    orderCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
    
    // Food Rating column (with stars)
    TableColumn<Rating, Integer> foodRatingCol = new TableColumn<>("Food Rating");
    foodRatingCol.setCellValueFactory(new PropertyValueFactory<>("foodRating"));
    foodRatingCol.setCellFactory(col -> new TableCell<Rating, Integer>() {
        @Override
        protected void updateItem(Integer rating, boolean empty) {
            super.updateItem(rating, empty);
            if (empty || rating == null) {
                setGraphic(null);
            } else {
                HBox stars = new HBox(2);
                for (int i = 0; i < 5; i++) {
                    Text star = new Text(i < rating ? "★" : "☆");
                    star.setFill(i < rating ? Color.GOLD : Color.GRAY);
                    star.setStyle("-fx-font-size: 16px;");
                    stars.getChildren().add(star);
                }
                setGraphic(stars);
            }
        }
    });
    
    // Food Review column
    TableColumn<Rating, String> foodReviewCol = new TableColumn<>("Food Review");
    foodReviewCol.setCellValueFactory(new PropertyValueFactory<>("foodReview"));
    foodReviewCol.setPrefWidth(200);
    
    // Delivery Rating column (with stars)
    TableColumn<Rating, Integer> deliveryRatingCol = new TableColumn<>("Delivery Rating");
    deliveryRatingCol.setCellValueFactory(new PropertyValueFactory<>("deliveryRating"));
    deliveryRatingCol.setCellFactory(col -> new TableCell<Rating, Integer>() {
        @Override
        protected void updateItem(Integer rating, boolean empty) {
            super.updateItem(rating, empty);
            if (empty || rating == null) {
                setGraphic(null);
            } else {
                HBox stars = new HBox(2);
                for (int i = 0; i < 5; i++) {
                    Text star = new Text(i < rating ? "★" : "☆");
                    star.setFill(i < rating ? Color.GOLD : Color.GRAY);
                    star.setStyle("-fx-font-size: 16px;");
                    stars.getChildren().add(star);
                }
                setGraphic(stars);
            }
        }
    });
    
    // Delivery Review column
    TableColumn<Rating, String> deliveryReviewCol = new TableColumn<>("Delivery Review");
    deliveryReviewCol.setCellValueFactory(new PropertyValueFactory<>("deliveryReview"));
    deliveryReviewCol.setPrefWidth(200);
    
    // Date column
    TableColumn<Rating, String> dateCol = new TableColumn<>("Date");
    dateCol.setCellValueFactory(new PropertyValueFactory<>("formattedDate"));
    
    table.getColumns().addAll(
        orderCol, foodRatingCol, foodReviewCol, 
        deliveryRatingCol, deliveryReviewCol, dateCol
    );
    
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    return table;
}

private void updateRatingsTable(TableView<Rating> table, String ratingType, String timePeriod) {
    String query = buildRatingsQuery(ratingType, timePeriod);
    
    ObservableList<Rating> ratings = FXCollections.observableArrayList();
    
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        
        while (rs.next()) {
            ratings.add(new Rating(
                rs.getInt("rating_id"),
                rs.getInt("order_id"),
                rs.getInt("food_rating"),
                rs.getString("food_review"),
                rs.getInt("delivery_rating"),
                rs.getString("delivery_review"),
                rs.getTimestamp("rating_date")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    
    table.setItems(ratings);
}

private String buildRatingsQuery(String ratingType, String timePeriod) {
    StringBuilder query = new StringBuilder("""
        SELECT rating_id, order_id, food_rating, food_review, 
               delivery_rating, delivery_review, rating_date
        FROM ratings
        WHERE 1=1
    """);
    
    // Add rating type filter
    if (!ratingType.equals("All Ratings")) {
        if (ratingType.equals("Food Only")) {
            query.append(" AND food_rating > 0");
        } else if (ratingType.equals("Delivery Only")) {
            query.append(" AND delivery_rating > 0");
        }
    }
    
    // Add time period filter
    if (!timePeriod.equals("All Time")) {
        query.append(" AND ");
        switch (timePeriod) {
            case "Today":
                query.append("DATE(rating_date) = CURDATE()");
                break;
            case "This Week":
                query.append("YEARWEEK(rating_date) = YEARWEEK(CURDATE())");
                break;
            case "This Month":
                query.append("YEAR(rating_date) = YEAR(CURDATE()) AND MONTH(rating_date) = MONTH(CURDATE())");
                break;
        }
    }
    
    query.append(" ORDER BY rating_date DESC");
    return query.toString();
}


// Rating model class
public class Rating {
    private final IntegerProperty ratingId;
    private final IntegerProperty orderId;
    private final IntegerProperty foodRating;
    private final StringProperty foodReview;
    private final IntegerProperty deliveryRating;
    private final StringProperty deliveryReview;
    private final ObjectProperty<Timestamp> ratingDate;
    
    public Rating(int ratingId, int orderId, int foodRating, String foodReview, 
                 int deliveryRating, String deliveryReview, Timestamp ratingDate) {
        this.ratingId = new SimpleIntegerProperty(ratingId);
        this.orderId = new SimpleIntegerProperty(orderId);
        this.foodRating = new SimpleIntegerProperty(foodRating);
        this.foodReview = new SimpleStringProperty(foodReview);
        this.deliveryRating = new SimpleIntegerProperty(deliveryRating);
        this.deliveryReview = new SimpleStringProperty(deliveryReview);
        this.ratingDate = new SimpleObjectProperty<>(ratingDate);
    }
    
    // Getters and property methods
    public int getOrderId() { return orderId.get(); }
    public int getFoodRating() { return foodRating.get(); }
    public String getFoodReview() { return foodReview.get(); }
    public int getDeliveryRating() { return deliveryRating.get(); }
    public String getDeliveryReview() { return deliveryReview.get(); }
    public String getFormattedDate() { 
        return new SimpleDateFormat("MMM d, yyyy h:mm a").format(ratingDate.get()); 
    }
    
    // Property getters
    public IntegerProperty orderIdProperty() { return orderId; }
    public IntegerProperty foodRatingProperty() { return foodRating; }
    public StringProperty foodReviewProperty() { return foodReview; }
    public IntegerProperty deliveryRatingProperty() { return deliveryRating; }
    public StringProperty deliveryReviewProperty() { return deliveryReview; }
    public ObjectProperty<Timestamp> ratingDateProperty() { return ratingDate; }
}


}







