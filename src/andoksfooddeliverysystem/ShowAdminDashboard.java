package andoksfooddeliverysystem;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import java.sql.*;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date; // ✅ This one!
import java.util.Calendar;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
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
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.mail.MessagingException;
import org.kordamp.ikonli.javafx.FontIcon;



public class ShowAdminDashboard {
    private VBox root;
    private int userID;
    private Label storeStatusLabel = new Label("Loading...");
    private Label ratingsLabel = new Label("Loading...");
    private Button toggleShopButton = new Button("Toggle Shop");
    private Label adminNameLabel = new Label("Admin Name");
    private Label adminEmailLabel = new Label("admin@email.com");
    private Label ordersTodayLabel = new Label("Loading...");
    private Label customersTodayLabel = new Label("Loading...");
    private Label allOrdersLabel = new Label("Loading...");
    private LineChart<String, Number> lineChart;
    
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
    
    public ShowAdminDashboard(int userID) throws MessagingException {
        this.userID = userID;
        createUI();
        loadAdminDetails(userID);
        loadStoreStatus();
        loadTodayStats();
    }
    
   private void createUI() {
        // Inner VBox that holds all sections
        VBox content = new VBox(30);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: " + LIGHT_GRAY + ";");

        // Sections
        HBox welcomeSection = createWelcomeSection();

        Label dashboardHeader = new Label("DASHBOARD OVERVIEW");
        dashboardHeader.setStyle(
            "-fx-font-family: 'Poppins', sans-serif;" +
            "-fx-font-size: 24px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );

        HBox statusCardsSection = createStatusCardsSection();
        HBox adminAndStoreSection = createAdminAndStoreSection();
        VBox performanceSection = createPerformanceSection();

        // Add all UI sections to content VBox
        content.getChildren().addAll(
            welcomeSection,
            dashboardHeader,
            statusCardsSection,
            adminAndStoreSection,
            performanceSection
        );

        // Wrap the entire content in a ScrollPane
        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
            "-fx-background: " + WHITE + ";" +
            "-fx-background-color: " + WHITE + ";" +
            "-fx-padding: 0;" +
            "-fx-background-insets: 0;" +
            "-fx-border-width: 0;"
        );

        // Set scrollPane as the root of the scene or main layout
        root = new VBox(); // Replace or clear previous root
        root.getChildren().add(scrollPane);
    }

    private HBox createWelcomeSection() {
        HBox welcomeBox = new HBox();
        welcomeBox.setAlignment(Pos.CENTER_LEFT);
        welcomeBox.setSpacing(15);
        
        // Current date and greeting
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        String currentDate = dateFormat.format(new Date());
        
        Label dateLabel = new Label(currentDate);
        dateLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";"
        );
        
        // Add time updater
        Label timeLabel = new Label();
        timeLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + PRIMARY_RED + ";"
        );
        
        // Update time every second
        Timeline timeline = new Timeline(new KeyFrame(
            Duration.seconds(1),
            event -> {
                SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm:ss a");
                timeLabel.setText(timeFormat.format(new Date()));
            }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Current hour for greeting
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String greeting = (hour < 12) ? "Good Morning" : (hour < 18) ? "Good Afternoon" : "Good Evening";
        
        Label greetingLabel = new Label(greeting + ", Admin");
        greetingLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        welcomeBox.getChildren().addAll(dateLabel, timeLabel, spacer, greetingLabel);
        
        return welcomeBox;
    }
    
    private HBox createStatusCardsSection() {
        HBox cardsContainer = new HBox(20);
        cardsContainer.setAlignment(Pos.CENTER);
        
        // Orders Today Card
        VBox ordersCard = createMetricCard("ORDERS TODAY", ordersTodayLabel, "📊", PRIMARY_RED);
        
        // Customers Today Card
        VBox customersCard = createMetricCard("CUSTOMERS TODAY", customersTodayLabel, "👥", ACCENT_YELLOW);
        
        // All-Time Orders Card
        VBox allOrdersCard = createMetricCard("TOTAL ORDERS", allOrdersLabel, "📈", SUCCESS_GREEN);
        
        cardsContainer.getChildren().addAll(ordersCard, customersCard, allOrdersCard);
        
        return cardsContainer;
    }
    
    private VBox createMetricCard(String title, Label valueLabel, String icon, String accentColor) {
        // Set the style for the value label
        valueLabel.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        // Create title label
        Label titleLabel = new Label(title);
        titleLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";" +
            "-fx-padding: 0 0 5 0;"
        );
        
        // Create icon
        Label iconLabel = new Label(icon);
        iconLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-background-color: " + accentColor + ";" +
            "-fx-background-radius: 50%;" +
            "-fx-min-width: 40px;" +
            "-fx-min-height: 40px;" +
            "-fx-alignment: center;"
        );
        
        // Layout for title and value
        VBox textContent = new VBox(5);
        textContent.getChildren().addAll(titleLabel, valueLabel);
        
        // Container for icon and text content
        HBox contentLayout = new HBox(15);
        contentLayout.setAlignment(Pos.CENTER_LEFT);
        contentLayout.setPadding(new Insets(15));
        contentLayout.getChildren().addAll(iconLabel, textContent);
        
        // Main card container
        VBox card = new VBox(contentLayout);
        card.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
            "-fx-min-width: 280px;" +
            "-fx-min-height: 100px;"
        );
        
        // Animate on hover
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: " + WHITE + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 15, 0, 0, 5);" +
                "-fx-min-width: 280px;" +
                "-fx-min-height: 100px;"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: " + WHITE + ";" +
                "-fx-background-radius: 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
                "-fx-min-width: 280px;" +
                "-fx-min-height: 100px;"
            );
        });
        
        return card;
    }
    
    private HBox createAdminAndStoreSection() {
        HBox container = new HBox(20);
        
        // Admin Profile Card
        VBox profileBox = createAdminProfileCard();
        
        // Store Status Card
        VBox storeBox = createStoreStatusCard();
        
        container.getChildren().addAll(profileBox, storeBox);
        
        return container;
    }
    
    private VBox createAdminProfileCard() {
        // Update styling for admin labels
        adminNameLabel.setStyle(
            "-fx-font-size: 18px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        adminEmailLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + LIGHT_TEXT + ";"
        );
        
        // Avatar placeholder
        Circle avatar = new Circle(40);
        avatar.setFill(Color.web(PRIMARY_RED));
        
        Text avatarText = new Text("A");
        avatarText.setFill(Color.WHITE);
        avatarText.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        
        StackPane avatarPane = new StackPane(avatar, avatarText);
        
        // Admin info layout
        VBox adminInfo = new VBox(5);
        adminInfo.getChildren().addAll(adminNameLabel, adminEmailLabel);
        
        // Row with avatar and admin info
        HBox profileRow = new HBox(15);
        profileRow.setAlignment(Pos.CENTER_LEFT);
        profileRow.getChildren().addAll(avatarPane, adminInfo);
        
//        // Last login info
//        Label lastLoginLabel = new Label("Last Login: Today, 08:45 AM");
//        lastLoginLabel.setStyle(
//            "-fx-font-size: 12px;" +
//            "-fx-text-fill: " + LIGHT_TEXT + ";" +
//            "-fx-padding: 10 0 0 0;"
//        );
        
        // Card container
        VBox profileCard = new VBox(15);
        profileCard.setPadding(new Insets(20));
        profileCard.getChildren().addAll(profileRow);
        profileCard.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
            "-fx-min-width: 350px;"
        );
        
        return profileCard;
    }
    
   
private VBox createStoreStatusCard() {
    // Section title
    Label storeTitle = new Label("STORE STATUS");
    storeTitle.setStyle(
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + LIGHT_TEXT + ";"
    );

    // Status indicator
    storeStatusLabel.setStyle(
        "-fx-font-size: 18px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + SUCCESS_GREEN + ";"
    );
    
    // Add ratings label (will be populated in loadStoreStatus method)
    Label ratingsLabel = new Label();
    ratingsLabel.setStyle(
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + DARK_TEXT + ";"
    );
    
    // Better toggle button
    toggleShopButton.setStyle(
        "-fx-background-color: " + PRIMARY_RED + ";" +
        "-fx-text-fill: " + WHITE + ";" +
        "-fx-font-weight: bold;" +
        "-fx-background-radius: 5px;" +
        "-fx-padding: 10px 20px;" +
        "-fx-cursor: hand;"
    );
    
    // Hover effect
    toggleShopButton.setOnMouseEntered(e -> {
        toggleShopButton.setStyle(
            "-fx-background-color: #A01118;" + // Darker red
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-cursor: hand;"
        );
    });
    
    toggleShopButton.setOnMouseExited(e -> {
        toggleShopButton.setStyle(
            "-fx-background-color: " + PRIMARY_RED + ";" +
            "-fx-text-fill: " + WHITE + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-cursor: hand;"
        );
    });
    
    // Keep the original action
    toggleShopButton.setOnAction(e -> {
        try {
            toggleShop();
        } catch (MessagingException ex) {
            Logger.getLogger(ShowAdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
        }
    });
    
    // Opening hours info
    VBox hoursInfo = new VBox(5);
    hoursInfo.setPadding(new Insets(10, 0, 0, 0));
    
    Label businessHoursLabel = new Label("BUSINESS HOURS");
    businessHoursLabel.setStyle(
        "-fx-font-size: 12px;" +
        "-fx-font-weight: bold;" +
        "-fx-text-fill: " + LIGHT_TEXT + ";"
    );
    
    Label weekdaysLabel = new Label("Mon-Sun: 8:00 AM - 8:00 PM");
    weekdaysLabel.setStyle(
        "-fx-font-size: 12px;" +
        "-fx-text-fill: " + DARK_TEXT + ";"
    );
    
    hoursInfo.getChildren().addAll(
        businessHoursLabel,
        weekdaysLabel
    );
    
    // Combine into store card layout
    VBox storeCardContent = new VBox(15);
    storeCardContent.getChildren().addAll(
        storeTitle,
        storeStatusLabel,
        ratingsLabel,  // Add the ratings label here
        toggleShopButton,
        hoursInfo
    );
    
    // Card container
    VBox storeCard = new VBox(storeCardContent);
    storeCard.setPadding(new Insets(20));
    storeCard.setStyle(
        "-fx-background-color: " + WHITE + ";" +
        "-fx-background-radius: 10px;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);" +
        "-fx-min-width: 350px;"
    );
    
    // Make ratingsLabel accessible to class methods
    this.ratingsLabel = ratingsLabel;
//    
//    // Load store status and ratings
//    try {
//        loadStoreStatus();
//    } catch (MessagingException ex) {
//        Logger.getLogger(ShowAdminDashboard.class.getName()).log(Level.SEVERE, null, ex);
//    }
    
    return storeCard;
}
    
    private VBox createPerformanceSection() {
        // Section header
        Label performanceTitle = new Label("PERFORMANCE ANALYTICS");
        performanceTitle.setStyle(
            "-fx-font-size: 20px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";" +
            "-fx-padding: 0 0 10 0;"
        );
        
        // Generate Report Button
        Button generateReportButton = new Button("Generate Report");
        generateReportButton.setGraphic(new Label("📊"));
        generateReportButton.setGraphicTextGap(10);
        generateReportButton.setStyle(
            "-fx-background-color: " + ACCENT_YELLOW + ";" +
            "-fx-text-fill: " + DARK_TEXT + ";" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 5px;" +
            "-fx-padding: 10px 20px;" +
            "-fx-cursor: hand;"
        );
        
        // Hover effect
        generateReportButton.setOnMouseEntered(e -> {
            generateReportButton.setStyle(
                "-fx-background-color: #E6A700;" + // Darker yellow
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-cursor: hand;"
            );
        });
        
        generateReportButton.setOnMouseExited(e -> {
            generateReportButton.setStyle(
                "-fx-background-color: " + ACCENT_YELLOW + ";" +
                "-fx-text-fill: " + DARK_TEXT + ";" +
                "-fx-font-weight: bold;" +
                "-fx-background-radius: 5px;" +
                "-fx-padding: 10px 20px;" +
                "-fx-cursor: hand;"
            );
        });
        
        // Keep original action
        generateReportButton.setOnAction(e -> {
            ReportGenerator reportGenerator = new ReportGenerator();
            reportGenerator.generateReport();
        });
        
        // Header and button in a row
        HBox headerRow = new HBox(generateReportButton);
        headerRow.setAlignment(Pos.CENTER_RIGHT);
        
        // Create styled sections
        VBox pieCharts = stylePieChartsPanel(createPieChartsPanel());
        VBox revenueChartPanel = styleChartPanel(createRevenueChartPanel(), "REVENUE TRENDS");
        
        // Category and Rider charts in split pane with styling
        VBox menuCategoryChart = styleChartPanel(createMenuCategoryChartPanel(), "MENU CATEGORY PERFORMANCE");
        VBox riderOrdersChart = styleChartPanel(createRiderOrdersChartPanel(), "RIDER ORDER DISTRIBUTION");
        
        // Create a styled container for the split pane
        HBox chartsContainer = new HBox(20);
        chartsContainer.getChildren().addAll(menuCategoryChart, riderOrdersChart);
        
        // Style rider dashboard
        VBox riderDashboard = styleGenericPanel(createRiderPerformanceDashboard(), "RIDER PERFORMANCE");
        
        // Style customer table
        VBox customerTableBox = new VBox(10);
        ComboBox<String> customerTableFilter = new ComboBox<>();
        customerTableFilter.getItems().addAll("Today", "This Week", "This Month", "All Time");
        customerTableFilter.setValue("All Time");
        customerTableFilter.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
        
        GridPane customerTable = createLoyalCustomerTable("All Time");
        customerTableFilter.setOnAction(e -> {
            String selected = customerTableFilter.getValue();
            customerTableBox.getChildren().set(1, createLoyalCustomerTable(selected));
        });
        
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label filterLabel = new Label("Filter Period:");
        filterLabel.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        filterContainer.getChildren().addAll(filterLabel, customerTableFilter);
        customerTableBox.getChildren().addAll(filterContainer, customerTable);
        
        VBox loyalCustomersPanel = styleGenericPanel(customerTableBox, "LOYAL CUSTOMERS");
        
        // Style ratings viewer
        VBox ratingsView = styleGenericPanel(createRatingsViewer(), "CUSTOMER RATINGS");
        
        // Main layout
        VBox performanceBox = new VBox(20);
        performanceBox.setPadding(new Insets(20, 0, 30, 0));
        performanceBox.getChildren().addAll(
            performanceTitle,
            headerRow,
            pieCharts,
            revenueChartPanel,
            chartsContainer,
            riderDashboard,
            loyalCustomersPanel,
            ratingsView
        );
        
        return performanceBox;
    }
    
  
    
    // Helper method to style chart panels
    private VBox styleChartPanel(VBox original, String title) {
        // Apply styling to the panel
        VBox styledPanel = new VBox(15);
        styledPanel.getChildren().addAll(original.getChildren());
        styledPanel.setPadding(new Insets(20));
        styledPanel.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        
        // Add section title
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        styledPanel.getChildren().add(0, sectionTitle);
        
        return styledPanel;
    }
    
   public VBox createRevenueChartPanel() {
    // Create axes and chart with modern styling
    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();
    xAxis.setLabel("Time");
    yAxis.setLabel("Revenue (₱)");
    
    // Create line chart with modern styling
    lineChart = new LineChart<>(xAxis, yAxis);
    lineChart.setTitle("Total Revenue Over Time");
    lineChart.setAnimated(true); // Enable animations
    lineChart.setCreateSymbols(true);
    lineChart.setLegendVisible(true);
    
    // Apply modern styling to the chart
    String chartStyle = 
        "-fx-background-color: transparent;" +
        "-fx-plot-background-color: rgba(250, 250, 252, 0.8);" +
        "-fx-horizontal-grid-lines-visible: true;" +
        "-fx-horizontal-zero-line-visible: true;" +
        "-fx-vertical-grid-lines-visible: false;";
    lineChart.setStyle(chartStyle);
    
    // Style the symbols and lines
    lineChart.lookupAll(".chart-series-line").forEach(node -> 
        node.setStyle("-fx-stroke-width: 2.5px;")
    );
    lineChart.lookupAll(".chart-line-symbol").forEach(node -> 
        node.setStyle("-fx-background-radius: 5px; -fx-padding: 5px;")
    );
    
    // Create modern time range selector with gradient
    ComboBox<String> timeRangeComboBox = new ComboBox<>();
    timeRangeComboBox.getItems().addAll("Today", "Daily", "Weekly", "Monthly", "Yearly");
    timeRangeComboBox.setValue("Today");
    timeRangeComboBox.setStyle(
        "-fx-background-color: linear-gradient(to bottom, #ffffff, #f5f7fa);" +
        "-fx-background-radius: 6px;" +
        "-fx-border-color: #e2e8f0;" +
        "-fx-border-radius: 6px;" +
        "-fx-padding: 7px;" +
        "-fx-font-size: 13px;"
    );
    
    // Add animation transition when changing time ranges
    timeRangeComboBox.setOnAction(e -> {
        String selectedRange = timeRangeComboBox.getValue();
        
        // Create fade transition for smooth data change
        FadeTransition fadeOut = new FadeTransition(Duration.millis(250), lineChart);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.3);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(350), lineChart);
        fadeIn.setFromValue(0.3);
        fadeIn.setToValue(1.0);
        
        // Run the animations in sequence
        fadeOut.setOnFinished(event -> {
            updateRevenueChart(selectedRange);
            fadeIn.play();
        });
        
        fadeOut.play();
    });
    
    // Create a refresh button with animation
    Button refreshButton = new Button("↻");
    refreshButton.setStyle(
        "-fx-background-color: #4299e1;" +
        "-fx-text-fill: white;" +
        "-fx-background-radius: 50%;" +
        "-fx-min-width: 32px;" +
        "-fx-min-height: 32px;" +
        "-fx-font-size: 14px;" +
        "-fx-font-weight: bold;"
    );
    
    // Add refresh animation
    refreshButton.setOnAction(e -> {
        RotateTransition rotateAnim = new RotateTransition(Duration.millis(750), refreshButton);
        rotateAnim.setByAngle(360);
        rotateAnim.setCycleCount(1);
        rotateAnim.setInterpolator(Interpolator.EASE_BOTH);
        rotateAnim.play();
        
        // Refresh data with animation
        updateRevenueChart(timeRangeComboBox.getValue());
    });
    
//    // Add info label for revenue stats
//    Label revenueStatsLabel = new Label("Total: ₱0.00");
//    revenueStatsLabel.setStyle(
//        "-fx-font-size: 14px;" +
//        "-fx-font-weight: bold;" +
//        "-fx-text-fill: #2d3748;"
//    );
    
    // Create control panel with modern layout
    HBox controlPanel = new HBox(15);
    controlPanel.setAlignment(Pos.CENTER_LEFT);
    controlPanel.setPadding(new Insets(5, 10, 15, 10));
    
    // Left side: range selector
    HBox rangeSelector = new HBox(8, new Label("Time Range:"), timeRangeComboBox);
    rangeSelector.setAlignment(Pos.CENTER_LEFT);
    
    // Right side: stats and refresh button
    HBox statsAndRefresh = new HBox(15, refreshButton);
    statsAndRefresh.setAlignment(Pos.CENTER_RIGHT);
    
    // Add spacing to push elements to opposite sides
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    
    // Combine all in control panel
    controlPanel.getChildren().addAll(rangeSelector, spacer, statsAndRefresh);
    
    // Add subtle gradient background to the entire panel
    VBox chartContainer = new VBox(5, controlPanel, lineChart);
    chartContainer.setPadding(new Insets(15));
    chartContainer.setStyle(
        "-fx-background-color: linear-gradient(to bottom right, #ffffff, #f8fafc);" +
        "-fx-background-radius: 12px;" +
        "-fx-border-color: #e2e8f0;" +
        "-fx-border-radius: 12px;" +
        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.07), 15, 0, 0, 5);"
    );
    
    // Update chart data with enter animation
    Platform.runLater(() -> {
        updateRevenueChart("Today");
        
        // Add enter animation
        ScaleTransition scaleIn = new ScaleTransition(Duration.millis(400), lineChart);
        scaleIn.setFromX(0.94);
        scaleIn.setFromY(0.94);
        scaleIn.setToX(1);
        scaleIn.setToY(1);
        scaleIn.setInterpolator(Interpolator.EASE_OUT);
        
        FadeTransition fadeIn = new FadeTransition(Duration.millis(450), lineChart);
        fadeIn.setFromValue(0.2);
        fadeIn.setToValue(1.0);
        
        ParallelTransition enterAnimation = new ParallelTransition(scaleIn, fadeIn);
        enterAnimation.play();
    });
    
    return chartContainer;
}
    
    // Helper method for generic panel styling
    private VBox styleGenericPanel(Node content, String title) {
        // Create styled container
        VBox styledPanel = new VBox(15);
        
        // Add section title
        Label sectionTitle = new Label(title);
        sectionTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";"
        );
        
        styledPanel.getChildren().addAll(sectionTitle, content);
        styledPanel.setPadding(new Insets(20));
        styledPanel.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        
        return styledPanel;
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


 // Update your loadStoreStatus method to also fetch and display ratings
private void loadStoreStatus() throws MessagingException {
    String query = "SELECT store_status, average_rating, total_reviews FROM store WHERE store_id = 1";
    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query);
         ResultSet rs = stmt.executeQuery()) {
        
        if (rs.next()) {
            // Load store status
            String status = rs.getString("store_status");
            storeStatusLabel.setText("Store is currently: " + status);
            toggleShopButton.setText(status.equalsIgnoreCase("Open") ? "Close Shop" : "Open Shop");
            System.out.println("✅ Store status loaded: " + status);
            
            // Load and display ratings
            double averageRating = rs.getDouble("average_rating");
            int totalReviews = rs.getInt("total_reviews");
            
            // Format with one decimal place
            String formattedRating = String.format("%.1f", averageRating);
            ratingsLabel.setText("⭐ " + formattedRating + " (" + totalReviews + ")");
            System.out.println("✅ Store ratings loaded: " + formattedRating + " from " + totalReviews + " reviews");
            
            // If store is closed at login, show custom dialog
            if (status.equalsIgnoreCase("Close")) {
                createModernAlert(status);
            }
        } else {
            System.out.println("⚠️ No store data found!");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        storeStatusLabel.setText("❌ Error loading store status");
        ratingsLabel.setText("❌ Error loading ratings");
    }
}

/**
 * Creates a modern styled red and white alert dialog
 */
private void createModernAlert(String status) throws MessagingException {
    // Create custom dialog
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Store Status");
    
    // Set dialog icon (optional - you can replace with your own icon)
    Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    stage.getIcons().add(new Image(getClass().getResourceAsStream("/icons/store_open.png")));
    
    // Create custom header with icon and text
    HBox header = new HBox(10);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(10, 10, 10, 10));
    header.setStyle("-fx-background-color: #C81D24;");
    
    // Create icon for alert
    FontIcon icon = new FontIcon();
    icon.setIconLiteral("fas-store-slash");
    icon.setIconSize(32);
    icon.setIconColor(Color.WHITE);
    
    // Create header text
    Label headerLabel = new Label("STORE IS CLOSED");
    headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
    
    header.getChildren().addAll(icon, headerLabel);
    
    // Create content area
    VBox content = new VBox(15);
    content.setPadding(new Insets(20, 20, 10, 20));
    content.setStyle("-fx-background-color: white;");
    
    Label messageLabel = new Label("The store is currently closed. Would you like to open and start operations?");
    messageLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
    messageLabel.setWrapText(true);
    
    content.getChildren().add(messageLabel);
    
    // Combine header and content
    VBox dialogContent = new VBox();
    dialogContent.getChildren().addAll(header, content);
    
    // Set up dialog pane
    DialogPane dialogPane = dialog.getDialogPane();
    dialogPane.setContent(dialogContent);
    dialogPane.getStylesheets().add(getClass().getResource("/styles/modern_dialog.css").toExternalForm());
    
    // Add buttons
    ButtonType openButton = new ButtonType("OPEN STORE", ButtonBar.ButtonData.OK_DONE);
    ButtonType laterButton = new ButtonType("LATER", ButtonBar.ButtonData.CANCEL_CLOSE);
    dialogPane.getButtonTypes().addAll(openButton, laterButton);
    
    // Style buttons
    Button openBtn = (Button) dialogPane.lookupButton(openButton);
    openBtn.setStyle("-fx-background-color: #C81D24; -fx-text-fill: white; -fx-font-weight: bold;");
    
    Button laterBtn = (Button) dialogPane.lookupButton(laterButton);
    laterBtn.setStyle("-fx-background-color: #f8f8f8; -fx-text-fill: #333333;");
    
    // Handle result
    Optional<ButtonType> result = dialog.showAndWait();
    if (result.isPresent() && result.get() == openButton) {
        updateStoreStatus("Open");
        showInfo("Andok's is now OPEN! You will start receiving orders!");
    }
}


    private void toggleShop() throws MessagingException {
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
private void updateStoreStatus(String newStatus) throws MessagingException {
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

            if (newStatus.equalsIgnoreCase("Close")) {
    // 👇 Cancel all pending orders placed today
    String cancelQuery = "UPDATE orders SET status = 'Cancelled', last_modified_by = ? " +
                         "WHERE status = 'Pending' AND DATE(order_date) = CURDATE()";
    try (PreparedStatement cancelStmt = conn.prepareStatement(cancelQuery)) {
        cancelStmt.setInt(1, userID);
        int cancelled = cancelStmt.executeUpdate();
        System.out.println("🛑 Today's pending orders cancelled: " + cancelled);
    }

    // 👇 Fetch customers who placed orders today
    String fetchCustomers = """
        SELECT DISTINCT c.customer_id, c.email 
        FROM orders o 
        JOIN customers c ON o.customer_id = c.customer_id 
        WHERE DATE(o.order_date) = CURDATE()
    """;

    try (PreparedStatement customerStmt = conn.prepareStatement(fetchCustomers);
         ResultSet rs = customerStmt.executeQuery()) {

        while (rs.next()) {
            int customerId = rs.getInt("customer_id");
            String email = rs.getString("email");

             // 🔁 Reuse the stored procedure instead of raw INSERT
            String callNotifProc = "{CALL InsertNotification(?, ?, ?, ?)}";
            try (CallableStatement notifStmt = conn.prepareCall(callNotifProc)) {
                notifStmt.setInt(1, customerId);
                notifStmt.setString(2, "Andok's is closed. Unfortunately, we are closed, all orders are cancelled and online payments are refunded. Thanks for your patience!");
                notifStmt.setString(3, "store_close");
                notifStmt.setInt(4, userID);
                notifStmt.executeUpdate();
            }


            // Send email using existing class
            SendEmail.sendEmail(email, "Andok's is Closed - Orders Cancelled",
                """
                Hi there,

                We're sorry, but Andok's is currently closed for the day. 
                All orders placed today have been cancelled and any online payments will be refunded.

                We appreciate your understanding and patience. We’ll be back soon!

                Love,  
                The Andok’s Team ❤️
                """);
        }
    }
}

            // 👇 Notify all customers if store is opened
           if (newStatus.equalsIgnoreCase("Open")) {
        String fetchCustomers = "SELECT customer_id, email FROM customers";
        try (PreparedStatement customerStmt = conn.prepareStatement(fetchCustomers);
             ResultSet rs = customerStmt.executeQuery()) {

        while (rs.next()) {
            int customerId = rs.getInt("customer_id");
            String email = rs.getString("email");

            // ✅ Call the reusable procedure
            String callNotifProc = "{CALL InsertNotification(?, ?, ?, ?)}";
            try (CallableStatement notifStmt = conn.prepareCall(callNotifProc)) {
                notifStmt.setInt(1, customerId);
                notifStmt.setString(2, "Andok's is now OPEN! Start browsing and placing your orders now!");
                notifStmt.setString(3, "store_open");
                notifStmt.setInt(4, userID); // who triggered the open
                notifStmt.executeUpdate();
            }

            // 💌 Optional: send email
//            SendEmail.sendEmail(
//                email,
//                "Andok's is Now Open!",
//                """
//                Hi there!
//
//                Andok's is officially open! 🎉
//                Place your order now while it's hot!
//
//                Love, Andok's Team
//                """
//            );
        }

    } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

        } else {
            System.out.println("❌ Failed to update store status.");
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


 private static void showInfo(String message) {
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
  // Helper method to style pie charts panel
    public static VBox stylePieChartsPanel(VBox original) {
        // Apply styling to the panel
        VBox styledPanel = new VBox(15);
        styledPanel.getChildren().addAll(original.getChildren());
        styledPanel.setPadding(new Insets(20));
        styledPanel.setAlignment(Pos.CENTER); // Center everything
        styledPanel.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-background-radius: 10px;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);"
        );
        
        // Add section title
        Label sectionTitle = new Label("KEY METRICS DISTRIBUTION");
        sectionTitle.setStyle(
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: " + DARK_TEXT + ";" +
            "-fx-padding: 0 0 10 0;"
        );
        
        styledPanel.getChildren().add(0, sectionTitle);
        
        return styledPanel;
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
}private String buildRevenueQuery(String range) {
    switch (range) {
        case "Today":
            return "SELECT * FROM revenue_today";
        case "Daily":
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
//    refreshButton.setStyle("-fx-font-size: 14px; -fx-pref-width: 120px;");
     refreshButton.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
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
    timePeriodCombo.getItems().addAll("All Time", "This Year", "This Month", "Today");
    timePeriodCombo.setValue("This Month");
     timePeriodCombo.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
    timePeriodCombo.setOnAction(e -> updateRiderOrdersChart(barChart, timePeriodCombo.getValue()));

    // Add status filter
    ComboBox<String> statusCombo = new ComboBox<>();
    statusCombo.getItems().addAll("All Orders", "Completed Only");
    statusCombo.setValue("Completed Only");
    statusCombo.setOnAction(e -> updateRiderOrdersChart(barChart, timePeriodCombo.getValue()));
     statusCombo.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );


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
    String query = "SELECT rider_id, name, average_rating, total_reviews, order_count, total_earnings, online_status " +
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
            String status = rs.getString("online_status");

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
        case "online":
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
    ratingTypeFilter.getItems().addAll(
      "All Ratings", "5 Stars", "4 Stars", "3 Stars", "2 Stars", "1 Star"
  );
    ratingTypeFilter.setValue("All Ratings");
     ratingTypeFilter.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
    
    ComboBox<String> timeFilter = new ComboBox<>();
    timeFilter.getItems().addAll("All Time", "Today", "This Week", "This Month");
    timeFilter.setValue("All Time");
      timeFilter.setStyle(
            "-fx-background-color: " + WHITE + ";" +
            "-fx-border-color: " + MEDIUM_GRAY + ";" +
            "-fx-border-radius: 3px;" +
            "-fx-padding: 5px;"
        );
    
    filterBox.getChildren().addAll(
        new Label("Type:"), ratingTypeFilter,
        new Label("From:"), timeFilter
    );
    
    // Create table for ratings
    TableView<Rating> ratingsTable = createRatingsTable();
    
    // Load initial data
    updateRatingsTable(ratingsTable, "All Ratings", "All Time");
    
   
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
    
    // Add rating amount filter
    if (!ratingType.equals("All Ratings")) {
        // Extract the number from "5 Stars" -> 5
        int starValue = Integer.parseInt(ratingType.substring(0, 1));
        query.append(" AND (food_rating = ").append(starValue)
             .append(" OR delivery_rating = ").append(starValue).append(")");
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







