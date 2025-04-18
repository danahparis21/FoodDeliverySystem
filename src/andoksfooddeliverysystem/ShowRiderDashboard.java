package andoksfooddeliverysystem;

import java.io.File;
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



public class ShowRiderDashboard {
    private VBox root;
    private int riderId;

    private Label storeStatusLabel = new Label("Loading...");
    private Button notificationButton = new Button("Notification");
    

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
      //  loadStoreStatus();
       // loadTodayStats();
    }
    
    
    private void createUI() {
  // Profile Section
        riderImageView.setFitWidth(80);
        riderImageView.setFitHeight(80);
        riderImageView.setPreserveRatio(true);
        riderImageView.setStyle("-fx-border-radius: 40; -fx-border-color: #ddd;");
        
        VBox profileBox = new VBox(10);
        profileBox.setPadding(new Insets(15));
        profileBox.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        
        notificationButton.setText("\uD83D\uDD14"); // Unicode bell icon 🛎️
        notificationButton.setStyle("-fx-font-size: 16px; -fx-background-color: transparent;");
        
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().addAll(riderImageView, new VBox(5, 
            new Label("Hi,"), 
            riderNameLabel,
            riderContactLabel
        ));
        // --- WRAP EVERYTHING WITH NOTIFICATION BUTTON ON THE LEFT ---
        HBox profileBoxWrapper = new HBox(10);
        profileBoxWrapper.setPadding(new Insets(10));
        profileBoxWrapper.getChildren().addAll( headerBox, notificationButton);

        
         statusCircle = new Circle(5);
        StackPane statusIndicator = new StackPane(statusCircle);
        statusIndicator.setAlignment(Pos.CENTER_LEFT);
        statusIndicator.setPadding(new Insets(0, 5, 0, 0));
        
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.getChildren().addAll(
            new Label("Status:"),
            statusIndicator,
            riderStatusLabel
        );
        
        HBox ratingBox = new HBox(10);
        ratingBox.setAlignment(Pos.CENTER_LEFT);
        ratingBox.getChildren().addAll(
            new Label("Rating:"),
            riderRatingLabel
        );
        
      

        
        // Style elements
        riderNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        riderStatusLabel.setStyle("-fx-font-weight: bold;");
        
          
        profileBox.getChildren().addAll(profileBoxWrapper, new Separator(), statusBox, ratingBox);
        
        
    // Store Section
    VBox storeBox = new VBox(10, storeStatusLabel);
    storeBox.setPadding(new Insets(10));
    //notifcationButton.setOnAction(e -> notification());

    // Stats Section
    VBox statsBox = new VBox(10, ordersTodayLabel, allOrdersLabel);
    statsBox.setPadding(new Insets(10));
    loadStoreStatus();
    loadTodayStats(riderId);

     //Performance Section (to add now)
    VBox performanceBox = createPerformanceSection(riderId);

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
private void showInfoMessage(String message) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Store Status");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.show();
}



 private void loadTodayStats(int riderId) {
    String todayQuery = """
        SELECT 
            SUM(CASE WHEN status = 'Completed' THEN 1 ELSE 0 END) AS completed,
            SUM(CASE WHEN status = 'Pending' THEN 1 ELSE 0 END) AS pending,
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
                    int pending = rs.getInt("pending");
                    int cancelled = rs.getInt("cancelled");

                    ordersTodayLabel.setText("Today's Orders: " +
                            (completed + pending + cancelled) + " (" +
                            completed + " Completed, " +
                            pending + " Pending, " +
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
 
 
private VBox createPerformanceSection(int riderId) {
    Label performanceTitle = new Label("Performance Section");
    performanceTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

    VBox revenueChartPanel = createRevenueChartPanel(riderId);
   
    
    ComboBox<String> timeRangeComboBox = new ComboBox<>();
    timeRangeComboBox.getItems().addAll("All Time", "Today", "This Week", "This Month");
    timeRangeComboBox.setValue("All Time");

    TableView<Rating> ratingsTable = createRatingsTable();
    
    // Load initial data
    updateRatingsTable(ratingsTable, "Delivery", timeRangeComboBox.getValue(), riderId);

    timeRangeComboBox.setOnAction(e ->
        updateRatingsTable(ratingsTable, "Delivery", timeRangeComboBox.getValue(), riderId)
    );

    VBox ratingsSection = new VBox(10,
        new Label("Rider Ratings"),
        new HBox(10, new Label("Filter:"), timeRangeComboBox),
        ratingsTable
    );
    ratingsSection.setPadding(new Insets(15));

    // Layout for the performance section
    VBox performanceBox = new VBox(10, performanceTitle, revenueChartPanel,ratingsSection);
    performanceBox.setPadding(new Insets(10));
    
    return performanceBox;
}


public VBox createRevenueChartPanel(int riderId) {
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
    timeRangeComboBox.setValue("Today");
    
    timeRangeComboBox.setOnAction(e -> updateRevenueChart(timeRangeComboBox.getValue(), riderId));

    
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
    updateRevenueChart("Today", riderId);
    
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
}
private String buildRevenueQuery(String range) {
  switch (range) {
        case "Today":
            return "SELECT * FROM rider_earnings_today_view WHERE rider_id = ?;";
        case "Weekly":
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
