package andoksfooddeliverysystem;

import java.io.File;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;


public class ShowRiderDashboard {
    private VBox root;
    private int riderId;

    // Brand color constants
    private static final String PRIMARY_RED = "#C81D24";
    private static final String ACCENT_YELLOW = "#FFBA00";
    private static final String WHITE = "#FFFFFF";
    private static final String LIGHT_GRAY = "#F5F5F5";
    private static final String MEDIUM_GRAY = "#E0E0E0";
    private static final String DARK_TEXT = "#2B2B2B";
    private static final String LIGHT_TEXT = "#6C757D";
    private static final String SUCCESS_GREEN = "#28A745";
    private static final String DANGER_RED = "#DC3545";

    private Label storeStatusLabel = new Label("Loading...");
   
    private Label riderNameLabel = new Label();
    private Label riderContactLabel = new Label();
    private Label riderRatingLabel = new Label();
    private Label riderStatusLabel = new Label("Offline");
    private ImageView riderImageView = new ImageView();

    private Label ordersTodayLabel = new Label("Loading...");
    private Label allOrdersLabel = new Label("Loading...");
    private Circle statusCircle; 

    private LineChart<String, Number> lineChart;

    public ShowRiderDashboard(int riderId) {
        this.riderId = riderId;
        createUI();
        loadRiderDetails(riderId);
    }
    
    private void createUI() {
        // Profile Section
        riderImageView.setFitWidth(80);
        riderImageView.setFitHeight(80);
        riderImageView.setPreserveRatio(true);
        
        // Apply circular clip to image
        Circle clip = new Circle(40);
        clip.setCenterX(40);
        clip.setCenterY(40);
        riderImageView.setClip(clip);
        
        // Add a border around the image
        StackPane imageContainer = new StackPane();
        Circle imageBorder = new Circle(42);
        imageBorder.setFill(Color.TRANSPARENT);
        imageBorder.setStroke(Color.web(MEDIUM_GRAY));
        imageBorder.setStrokeWidth(2);
        imageContainer.getChildren().addAll(riderImageView, imageBorder);
        
        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(15));
        profileBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        
//        // Style notification button
//        notificationButton.setStyle("-fx-background-color: " + LIGHT_GRAY + "; -fx-text-fill: " + 
//                DARK_TEXT + "; -fx-background-radius: 50%; -fx-min-width: 40px; -fx-min-height: 40px; " +
//                "-fx-max-width: 40px; -fx-max-height: 40px; -fx-padding: 0;");
//     
//          FontIcon bellIcon = new FontIcon(FontAwesomeSolid.BELL);
//        bellIcon.setFill(Color.web(DARK_TEXT));
//        bellIcon.setIconSize(16);
//        notificationButton.setGraphic(bellIcon);
//        
        Label welcomeLabel = new Label("Hi,");
        welcomeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + LIGHT_TEXT + ";");
        
        riderNameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        riderContactLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + LIGHT_TEXT + ";");
        
        VBox riderInfoBox = new VBox(5);
        riderInfoBox.getChildren().addAll(welcomeLabel, riderNameLabel, riderContactLabel);
        
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(imageContainer, riderInfoBox);
        
        // Wrap everything with notification button on the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        HBox profileBoxWrapper = new HBox(10);
        profileBoxWrapper.setAlignment(Pos.CENTER);
        profileBoxWrapper.getChildren().addAll(headerBox, spacer);
        
        // Status indicator styles
        statusCircle = new Circle(6);
        updateStatusCircle("Offline"); // Initialize with offline state
        
        StackPane statusIndicator = new StackPane(statusCircle);
        statusIndicator.setAlignment(Pos.CENTER_LEFT);
        statusIndicator.setPadding(new Insets(0, 5, 0, 0));
        
        Label statusLabel = new Label("Status:");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + LIGHT_TEXT + ";");
        
        riderStatusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.getChildren().addAll(statusLabel, statusIndicator, riderStatusLabel);
        
        Label ratingTitleLabel = new Label("Rating:");
        ratingTitleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + LIGHT_TEXT + ";");
        
        riderRatingLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        // Create rating stars
        HBox starsBox = new HBox(2);
        starsBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox ratingBox = new HBox(10);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        ratingBox.getChildren().addAll(ratingTitleLabel, riderRatingLabel, starsBox);
        
        // Add a nice separator
        Separator separator = new Separator();
        separator.setStyle("-fx-background: " + MEDIUM_GRAY + ";");
        
        profileBox.getChildren().addAll(profileBoxWrapper, separator, statusBox, ratingBox);
        
        // Store Status Section
        VBox storeBox = createStoreStatusSection();
        
        // Stats Section - Today's Performance
        VBox statsBox = createTodayStatsSection();
        
        // Performance Section
        VBox performanceBox = createPerformanceSection(riderId);
        
        
    // Create a ScrollPane and add all sections (profileBox, storeBox, statsBox, performanceBox) to it
    ScrollPane scrollPane = new ScrollPane();
    scrollPane.setContent(new VBox(20, profileBox, storeBox, statsBox, performanceBox)); // Add all sections here
    scrollPane.setFitToWidth(true);
    scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

    // Main Layout
    root = new VBox(20);
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");
    root.getChildren().addAll(scrollPane);  // Add only the ScrollPane to the root
    }
    
    private VBox createStoreStatusSection() {
        Label sectionTitle = new Label("Store Status");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        storeStatusLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + DARK_TEXT + ";");
        
        VBox storeBox = new VBox(10);
        storeBox.setPadding(new Insets(15));
        storeBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        storeBox.getChildren().addAll(sectionTitle, storeStatusLabel);
        
        loadStoreStatus();
        
        return storeBox;
    }
    
    private VBox createTodayStatsSection() {
        Label sectionTitle = new Label("Today's Performance");
        sectionTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        // Create card for orders today
        VBox ordersToday = createStatsCard("Orders Today", ordersTodayLabel, "cart");
        
        // Create card for all orders
        VBox allOrders = createStatsCard("Total Orders", allOrdersLabel, "shopping-bag");
        
//        // Create income card (placeholder)
//        Label incomeLabel = new Label("$0.00");
//        incomeLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
//        VBox income = createStatsCard("Today's Income", incomeLabel, "dollar-sign");
//        
//        // Create performance score card (placeholder)
//        Label scoreLabel = new Label("0%");
//        scoreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
//        VBox performanceScore = createStatsCard("Performance Score", scoreLabel, "chart-line");
//        
        // Arrange cards in 2x2 grid
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(15);
        statsGrid.setVgap(15);
        
        statsGrid.add(ordersToday, 0, 0);
        statsGrid.add(allOrders, 1, 0);
//        statsGrid.add(income, 0, 1);
//        statsGrid.add(performanceScore, 1, 1);
        
        // Set column constraints to make cards equal width
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(50);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(50);
        statsGrid.getColumnConstraints().addAll(col1, col2);
        
        VBox statsBox = new VBox(15);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        statsBox.getChildren().addAll(sectionTitle, statsGrid);
        
        loadTodayStats(riderId);
        
        return statsBox;
    }
    
    private VBox createStatsCard(String title, Label valueLabel, String iconName) {
        // Create icon using FontAwesome
        FontIcon icon = new FontIcon();
        String ikonliCode = "fas-shopping-cart";  // Correct name for cart icon
        icon.setIconLiteral(ikonliCode);
        icon.setIconColor(Color.web(PRIMARY_RED));
        icon.setIconSize(20);
        Circle iconBackground = new Circle(15, Color.web(PRIMARY_RED + "15"));
        StackPane iconContainer = new StackPane(iconBackground, icon);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + LIGHT_TEXT + ";");
        
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: " + WHITE + "; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-border-color: " + MEDIUM_GRAY + "; -fx-border-width: 1;");
        
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getChildren().addAll(iconContainer, titleLabel);
        
        card.getChildren().addAll(header, valueLabel);
        
        return card;
    }
    
       public Node getRoot() {
        return root;
    }
    private VBox createPerformanceSection(int riderId) {
        Label performanceTitle = new Label("Performance Analytics");
        performanceTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        VBox revenueChartPanel = createRevenueChartPanel(riderId);
        
        
        // Create time filter for ratings
        Label filterLabel = new Label("Filter by:");
        filterLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: " + DARK_TEXT + ";");
        
        ComboBox<String> timeRangeComboBox = new ComboBox<>();
        timeRangeComboBox.getItems().addAll("All Time", "Today", "This Week", "This Month");
        timeRangeComboBox.setValue("All Time");
          timeRangeComboBox.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
        
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);
        filterBox.getChildren().addAll(filterLabel, timeRangeComboBox);
        
        
        // Create ratings table
        TableView<Rating> ratingsTable = createRatingsTable();
        
        // Load initial data
        updateRatingsTable(ratingsTable, "Delivery", timeRangeComboBox.getValue(), riderId);
        
        timeRangeComboBox.setOnAction(e ->
            updateRatingsTable(ratingsTable, "Delivery", timeRangeComboBox.getValue(), riderId)
        );
        
        Label ratingsTitle = new Label("Rider Ratings");
        ratingsTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: " + DARK_TEXT + ";");
        
        VBox ratingsSection = new VBox(15);
        ratingsSection.getChildren().addAll(ratingsTitle, filterBox, ratingsTable);
        
        // Layout for the performance section
        VBox performanceBox = new VBox(20);
        performanceBox.setPadding(new Insets(15));
        performanceBox.setStyle("-fx-background-color: " + WHITE + "; -fx-border-radius: 8; " +
                "-fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 2);");
        performanceBox.getChildren().addAll(performanceTitle, revenueChartPanel, ratingsSection);
        
        return performanceBox;
    }
    
    private void updateStatusCircle(String status) {
        if (status.equalsIgnoreCase("Online")) {
            statusCircle.setFill(Color.web(SUCCESS_GREEN));
        } else if (status.equalsIgnoreCase("Busy")) {
            statusCircle.setFill(Color.web(ACCENT_YELLOW));
        } else {
            statusCircle.setFill(Color.web(LIGHT_TEXT));
        }
    }
    
 private void loadRiderDetails(int riderId) {
        String query = "SELECT name, contact_number, imagePath, average_rating, online_status FROM riders WHERE rider_id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, riderId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String name = rs.getString("name");
                    String contact = rs.getString("contact_number");
                    String imagePath = rs.getString("imagePath");
                    double rating = rs.getDouble("average_rating");
                    String isOnline = rs.getString("online_status");
                      updateStatusDisplay(isOnline);
                    
                    // Set UI elements
                    riderNameLabel.setText(name);
                    riderContactLabel.setText(contact);
                    riderRatingLabel.setText(String.format("%.1f ★", rating));
                    
                    // Load image if path exists
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            Image image = new Image(new File(imagePath).toURI().toString());
                            riderImageView.setImage(image);
                        } catch (Exception e) {
                            System.out.println("⚠️ Could not load rider image: " + e.getMessage());
                            // Set default image
                            riderImageView.setImage(new Image(getClass().getResourceAsStream("/images/default_rider.png")));
                        }
                    }
                    
                    // Set status
                    updateStatusDisplay(isOnline);
                    
                    System.out.println("✅ Loaded rider details: " + name);
                } else {
                    System.out.println("⚠️ No rider found with ID " + riderId);
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("❌ Failed to load rider details from database.");
        }
    }
    
  private void updateStatusDisplay(String isOnline) {
    boolean isOnlineBool = isOnline.equalsIgnoreCase("online") || isOnline.equals("1") || isOnline.equalsIgnoreCase("true");

    // Update text
    riderStatusLabel.setText(isOnlineBool ? "Online" : "Offline");
    riderStatusLabel.setStyle("-fx-font-weight: bold; " + 
        (isOnlineBool ? "-fx-text-fill: green;" : "-fx-text-fill: red;"));

    // Update circle color
    statusCircle.setFill(isOnlineBool ? Color.GREEN : Color.RED);
}
  
  
private void loadStoreStatus() {
    String query = "SELECT store_status FROM store WHERE store_id = 1";

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {

        if (rs.next()) {
            String status = rs.getString("store_status");

            if ("Open".equalsIgnoreCase(status)) {
                storeStatusLabel.setText("🟢 Store is Open");
                showInfoMessage("Andok's store is open, you will start receiving orders!");
            } else {
                storeStatusLabel.setText("🔴 Store is Closed");
                showInfoMessage("Andok's is closed right now. All operations are stopped.");
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        storeStatusLabel.setText("❌ Error loading store status");
    }
}
private static void showInfoMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Notification"); // Optional: can also be removed or replaced
    alert.setHeaderText(null);
    alert.setContentText(message);

    // Apply custom styling to the alert
    DialogPane dialogPane = alert.getDialogPane();
    dialogPane.setStyle("-fx-background-color: white;");

    // Apply a default button style (e.g., yellow with dark text)
    dialogPane.getButtonTypes().stream()
        .map(dialogPane::lookupButton)
        .forEach(button -> {
            button.setStyle("-fx-background-color: #FFCC00; -fx-text-fill: #333333;");
        });

    alert.showAndWait();
}



 private void loadTodayStats(int riderId) {
   String todayQuery = """
        SELECT 
            SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed,
            SUM(CASE WHEN status = 'Out for Delivery' THEN 1 ELSE 0 END) AS out_for_delivery,
            SUM(CASE WHEN status = 'Cancelled' THEN 1 ELSE 0 END) AS cancelled
        FROM orders
        WHERE DATE(order_date) = CURDATE() AND rider_id = ?
    """;


    String allTimeQuery = """
        SELECT 
            COUNT(*) AS total_orders
        FROM orders
        WHERE rider_id = ?
    """;

    try (Connection conn = Database.connect()) {
        // Today's stats
        try (PreparedStatement stmt = conn.prepareStatement(todayQuery)) {
            stmt.setInt(1, riderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int completed = rs.getInt("completed");
                    int outForDelivery = rs.getInt("out_for_delivery");

                    int cancelled = rs.getInt("cancelled");

                    ordersTodayLabel.setText("Today's Orders: " +
                            (completed + outForDelivery + cancelled) + " (" +
                            completed + " Completed, " +
                            outForDelivery + " For Delivery, " +
                            cancelled + " Cancelled)");
                }
            }
        }

        // All-time stats
        try (PreparedStatement stmt = conn.prepareStatement(allTimeQuery)) {
            stmt.setInt(1, riderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total_orders");
                    allOrdersLabel.setText("Total Orders by You: " + total);
                }
            }
        }

    } catch (SQLException e) {
        e.printStackTrace();
        ordersTodayLabel.setText("❌ Error loading today's stats");
        allOrdersLabel.setText("❌ Error loading total stats");
    }
}
 
 


public VBox createRevenueChartPanel(int riderId) {
    // Create axes and chart
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Time");
    yAxis.setLabel("Revenue (₱)");

    lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Total Earnings Over Time");
    lineChart.setAnimated(false); // Disable animations for better performance
    lineChart.setCreateSymbols(true); // Ensure data points are visible
    lineChart.setLegendVisible(true);
    lineChart.setHorizontalGridLinesVisible(true);
    lineChart.setVerticalGridLinesVisible(true);

    xAxis.setTickLabelsVisible(true);
    xAxis.setTickMarkVisible(true);
    yAxis.setTickLabelsVisible(true);
    yAxis.setTickMarkVisible(true);

    lineChart.setStyle(
    "-fx-background-color: transparent;" + // This can also be a light color if needed
    "-fx-border-color: #ccc;" +            // Optional: make borders more visible
    "-fx-legend-visible: true;"            // Ensure legend is visible
);
lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: white;");
xAxis.lookup(".axis-label").setStyle("-fx-text-fill: black;");
yAxis.lookup(".axis-label").setStyle("-fx-text-fill: black;");

xAxis.setStyle("-fx-tick-label-fill: black;");
yAxis.setStyle("-fx-tick-label-fill: black;");

    // ✨ Set preferred size (you can adjust height as needed)
    lineChart.setPrefHeight(300);
    lineChart.setMinHeight(300);
    lineChart.setPrefWidth(800);
    lineChart.setMinWidth(600);

    // Optional styling (border for visibility during debugging)
    // lineChart.setStyle("-fx-border-color: red;");

    // Time range selector
    ComboBox<String> timeRangeComboBox = new ComboBox<>();
    timeRangeComboBox.getItems().addAll("Today", "Daily", "Monthly", "Yearly");
    timeRangeComboBox.setValue("Today");
    timeRangeComboBox.setOnAction(e -> updateRevenueChart(timeRangeComboBox.getValue(), riderId));
    timeRangeComboBox.setStyle("-fx-font-size: 14px; -fx-pref-width: 150px;");
      timeRangeComboBox.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
    // Control panel
    HBox controlPanel = new HBox(10, new Label("Time Range:"), timeRangeComboBox);
    controlPanel.setAlignment(Pos.CENTER_LEFT);
    controlPanel.setPadding(new Insets(10));

    // Chart container
    VBox chartContainer = new VBox(10, controlPanel, lineChart);
    chartContainer.setPadding(new Insets(15));

    // Allow chart to grow with window
    VBox.setVgrow(lineChart, Priority.ALWAYS);

    // Initialize chart
    updateRevenueChart("Today", riderId);

    Platform.runLater(() -> {
    Node chartBackground = lineChart.lookup(".chart-plot-background");
    if (chartBackground != null) {
        chartBackground.setStyle("-fx-background-color: white;");
    }

    // Force grid line colors
    Node horizontalGridLines = lineChart.lookup(".chart-horizontal-grid-lines");
    if (horizontalGridLines != null) {
        horizontalGridLines.setStyle("-fx-stroke: #ccc;");
    }

    Node verticalGridLines = lineChart.lookup(".chart-vertical-grid-lines");
    if (verticalGridLines != null) {
        verticalGridLines.setStyle("-fx-stroke: #ccc;");
    }
});


    return chartContainer;
}



private void updateRevenueChart(String range, int riderId) {
    String query = buildRevenueQuery(range);
    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Revenue - " + range);
    
try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, riderId); // Rider ID placeholder
        ResultSet rs = stmt.executeQuery();

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
        case "Daily":
            return "Day " + rawValue;
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
}
private String buildRevenueQuery(String range) {
  switch (range) {
        case "Today":
            return "SELECT * FROM rider_earnings_today_view WHERE rider_id = ?;";
        case "Daily":
            return "SELECT * FROM rider_earnings_weekly_view WHERE rider_id = ?;";
        case "Monthly":
            return "SELECT * FROM rider_earnings_monthly_view WHERE rider_id = ?;";
        case "Yearly":
            return "SELECT * FROM rider_earnings_yearly_view WHERE rider_id = ?;";
        default:
            return buildRevenueQuery("Today");
    }
}


private TableView<Rating> createRatingsTable() {
    TableView<Rating> table = new TableView<>();
    
    // Order ID column
    TableColumn<Rating, Integer> orderCol = new TableColumn<>("Order ID");
    orderCol.setCellValueFactory(new PropertyValueFactory<>("orderId"));
    
   
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
        orderCol,
        deliveryRatingCol, deliveryReviewCol, dateCol
    );
    
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    return table;
}
// Update this method to accept 'riderId' as an additional argument
private void updateRatingsTable(TableView<Rating> table, String ratingType, String timePeriod, int riderId) {
    String query = buildRatingsQuery(ratingType, timePeriod, riderId);

    ObservableList<Rating> ratings = FXCollections.observableArrayList();

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, riderId); // Set the rider ID parameter
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            ratings.add(new Rating(
                rs.getInt("rating_id"),
                rs.getInt("order_id"),
                0, // Food Rating (if not used)
                "", // Food Review (if not used)
                rs.getInt("delivery_rating"),
                rs.getString("delivery_review"),
                rs.getTimestamp("rating_date")
            ));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    table.setItems(ratings); // Set the filtered ratings data
}


private String buildRatingsQuery(String ratingType, String timePeriod, int riderId) {
    StringBuilder query = new StringBuilder("""
        SELECT r.rating_id, r.order_id, 
               r.delivery_rating, r.delivery_review, r.rating_date
        FROM ratings r
        JOIN orders o ON r.order_id = o.order_id
        WHERE o.rider_id = ?
    """);
    
    
     // Time filtering
    if (!timePeriod.equals("All Time")) {
        query.append(" AND ");
        switch (timePeriod) {
            case "Today" -> query.append("DATE(rating_date) = CURDATE()");
            case "This Week" -> query.append("YEARWEEK(rating_date) = YEARWEEK(CURDATE())");
            case "This Month" -> query.append("YEAR(rating_date) = YEAR(CURDATE()) AND MONTH(rating_date) = MONTH(CURDATE())");
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
