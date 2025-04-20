
package andoksfooddeliverysystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;


public class AuditLogs {
    private VBox root;
    private int adminId; // the logged-in admin's ID
    
    // Color palette constants
    private static final String PRIMARY_RED = "#D50000";
    private static final String SECONDARY_YELLOW = "#FFC107";
    private static final String ACCENT_YELLOW = "#FFD54F";
    private static final String BACKGROUND_WHITE = "#FFFFFF";
    private static final String LIGHT_GRAY = "#F5F5F5";
    private static final String TEXT_DARK = "#212121";
    private static final String MEDIUM_GRAY = "#E0E0E0";
      private static final TextField searchField = new TextField();
        private static final ComboBox<String> roleFilter = new ComboBox<>();
           private static Button sortRecent = new Button("Most Recent");
            private static Button sortOldest = new Button("Least Recent");

    public AuditLogs(int adminId) {
        this.adminId = adminId;
        
        // Main container
        root = new VBox(20);
        root.setPadding(new Insets(25));
        root.setPrefSize(1200, 700);
        root.setStyle("-fx-background-color: " + BACKGROUND_WHITE + ";");
        
        // Header section with title
        Label headerLabel = new Label("Audit Logs");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: " + PRIMARY_RED + ";");
        
        // Create panel for search controls
        HBox searchPanel = createSearchPanel();
        
        // Create table view
        VBox tableContainer = createTableView();
        
        // Add components to main layout
        root.getChildren().addAll(headerLabel, searchPanel, tableContainer);
        VBox.setVgrow(tableContainer, Priority.ALWAYS);
    }
    
    private HBox createSearchPanel() {
        HBox searchPanel = new HBox(15);
        searchPanel.setAlignment(Pos.CENTER_LEFT);
        searchPanel.setPadding(new Insets(10, 0, 10, 0));
        searchPanel.setStyle("-fx-background-color: " + LIGHT_GRAY + "; " +
                           "-fx-background-radius: 8px; " +
                           "-fx-padding: 15px;");
        
        // Search field
        VBox searchBox = new VBox(5);
        Label searchLabel = new Label("Search Logs");
        searchLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
      
        searchField.setPromptText("Search by user, action, description...");
        searchField.setPrefHeight(35);
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 20px; -fx-padding: 5px 15px;");
        
        searchBox.getChildren().addAll(searchLabel, searchField);
        
        // Role filter
        VBox filterBox = new VBox(5);
        Label filterLabel = new Label("Filter by Role");
        filterLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
      
        roleFilter.getItems().addAll("All", "Admin", "Customer", "Rider");
        roleFilter.setValue("All");
        roleFilter.setPrefHeight(35);
        roleFilter.setPrefWidth(150);
        roleFilter.setStyle("-fx-background-radius: 4px;");
        
        filterBox.getChildren().addAll(filterLabel, roleFilter);
        
        // Sort buttons
        VBox sortBox = new VBox(5);
        Label sortLabel = new Label("Sort by Date");
        sortLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        HBox sortButtons = new HBox(10);
        
     
        sortRecent.setPrefHeight(35);
        sortRecent.setStyle("-fx-background-color: " + PRIMARY_RED + "; " +
                          "-fx-text-fill: white; " +
                          "-fx-font-weight: bold; " +
                          "-fx-background-radius: 4px;");
        
      
        sortOldest.setPrefHeight(35);
        sortOldest.setStyle("-fx-background-color: " + SECONDARY_YELLOW + "; " +
                          "-fx-text-fill: " + TEXT_DARK + "; " +
                          "-fx-font-weight: bold; " +
                          "-fx-background-radius: 4px;");
        
        sortButtons.getChildren().addAll(sortRecent, sortOldest);
        sortBox.getChildren().addAll(sortLabel, sortButtons);
        
//        // Add export button
//        VBox exportBox = new VBox(5);
//        Label exportLabel = new Label("Export Data");
//        exportLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
//        
//        Button exportButton = new Button("Export to CSV");
//        exportButton.setPrefHeight(35);
//        exportButton.setStyle("-fx-background-color: " + ACCENT_YELLOW + "; " +
//                            "-fx-text-fill: " + TEXT_DARK + "; " +
//                            "-fx-font-weight: bold; " +
//                            "-fx-background-radius: 4px;");
//        
//        exportBox.getChildren().addAll(exportLabel, exportButton);
        
        // Add all components to panel
        searchPanel.getChildren().addAll(searchBox, filterBox, sortBox);
        HBox.setHgrow(searchBox, Priority.ALWAYS);
        
        return searchPanel;
    }
    
    private VBox createTableView() {
        VBox tableContainer = new VBox(10);
        tableContainer.setPadding(new Insets(0));
        tableContainer.setStyle("-fx-background-color: transparent;");
        
        // Table Header
        Label tableTitle = new Label("System Activity Records");
        tableTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + TEXT_DARK + ";");
        
        // Create TableView
        TableView<AuditEntry> tableView = new TableView<>();
        tableView.setPrefWidth(1150);
        tableView.setStyle("-fx-background-color: white; " +
                         "-fx-border-color: " + MEDIUM_GRAY + "; " +
                         "-fx-border-radius: 4px;");
        
        // Define columns with specific widths
        TableColumn<AuditEntry, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));
        logIdCol.setPrefWidth(70);
        
        TableColumn<AuditEntry, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));
        userIdCol.setPrefWidth(70);
        
        TableColumn<AuditEntry, String> userNameCol = new TableColumn<>("User Name");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        userNameCol.setPrefWidth(120);
        
        TableColumn<AuditEntry, String> userRoleCol = new TableColumn<>("User Role");
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("userRole"));
        userRoleCol.setPrefWidth(90);
        
        // Style user role column with colors
        userRoleCol.setCellFactory(column -> new TableCell<AuditEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    if (item.equalsIgnoreCase("admin")) {
                        setStyle("-fx-text-fill: " + PRIMARY_RED + "; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("rider")) {
                        setStyle("-fx-text-fill: " + SECONDARY_YELLOW + "; -fx-font-weight: bold;");
                    } else if (item.equalsIgnoreCase("customer")) {
                        setStyle("-fx-text-fill: #4CAF50; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: " + TEXT_DARK + ";");
                    }
                }
            }
        });
        
        TableColumn<AuditEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));
        actionCol.setPrefWidth(100);
        
        // Style action column
        actionCol.setCellFactory(column -> new TableCell<AuditEntry, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    if (item.contains("DELETE") || item.contains("REMOVE")) {
                        setStyle("-fx-text-fill: " + PRIMARY_RED + ";");
                    } else if (item.contains("CREATE") || item.contains("ADD")) {
                        setStyle("-fx-text-fill: #4CAF50;");
                    } else if (item.contains("UPDATE") || item.contains("MODIFY")) {
                        setStyle("-fx-text-fill: " + SECONDARY_YELLOW + ";");
                    } else {
                        setStyle("-fx-text-fill: " + TEXT_DARK + ";");
                    }
                }
            }
        });
        
        TableColumn<AuditEntry, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));
        descCol.setPrefWidth(200);
        
        TableColumn<AuditEntry, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));
        timestampCol.setPrefWidth(140);
        
        TableColumn<AuditEntry, String> oldValueCol = new TableColumn<>("Old Value");
        oldValueCol.setCellValueFactory(new PropertyValueFactory<>("oldValue"));
        oldValueCol.setPrefWidth(175);
        
        TableColumn<AuditEntry, String> newValueCol = new TableColumn<>("New Value");
        newValueCol.setCellValueFactory(new PropertyValueFactory<>("newValue"));
        newValueCol.setPrefWidth(175);
        
        tableView.getColumns().addAll(
            logIdCol, userIdCol, userNameCol, userRoleCol,
            actionCol, descCol, timestampCol, oldValueCol, newValueCol
        );
        
         /// === ObservableList and FilteredList Setup ===
        ObservableList<AuditEntry> auditData = FXCollections.observableArrayList();
        FilteredList<AuditEntry> filteredData = new FilteredList<>(auditData, p -> true);
        tableView.setItems(filteredData);

    
        // === SortedList Setup (single instance only!) ===
        SortedList<AuditEntry> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData); // Set this once only!

        // === Search Field Listener ===
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(filteredData, newValue, roleFilter.getValue());
        });

        roleFilter.setOnAction(e -> {
            updateFilter(filteredData, searchField.getText(), roleFilter.getValue());
        });

        // === Sort Buttons ===
       

        sortRecent.setOnAction(e -> {
         sortedData.comparatorProperty().unbind();
        sortedData.setComparator((entry1, entry2) -> entry2.getTimestamp().compareTo(entry1.getTimestamp()));
        System.out.println("Button Sort recent is clicked");
    });

    sortOldest.setOnAction(e -> {
        sortedData.comparatorProperty().unbind();
        sortedData.setComparator((entry1, entry2) -> entry1.getTimestamp().compareTo(entry2.getTimestamp()));
    });


        // === Load data ===
        loadAuditData(auditData);


        // Status bar for showing count information
        HBox statusBar = new HBox(10);
        statusBar.setAlignment(Pos.CENTER_LEFT);
        statusBar.setPadding(new Insets(5, 0, 0, 0));
        
        Label recordCountLabel = new Label("Total Records: " + auditData.size());
        recordCountLabel.setStyle("-fx-text-fill: " + TEXT_DARK + ";");
        
        statusBar.getChildren().add(recordCountLabel);
        
        // Add table and status bar to container
        tableContainer.getChildren().addAll(tableTitle, tableView, statusBar);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        
        return tableContainer;
    }
    
    private void loadAuditData(ObservableList<AuditEntry> auditData) {
        try (Connection conn = Database.connect()) {
            String sql = "SELECT * FROM audit_logs";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int logId = rs.getInt("log_id");
                int userId = rs.getInt("user_id");
                String userName = rs.getString("user_name");
                String userRole = rs.getString("user_role");
                String action = rs.getString("action");
                String description = rs.getString("description");
                String timestamp = rs.getString("timestamp");
                String oldValue = rs.getString("old_value");
                String newValue = rs.getString("new_value");
                
                auditData.add(new AuditEntry(logId, userId, userName, userRole, action, description, timestamp, oldValue, newValue));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Database Error", "Could not load audit logs data.");
        }
    }
    
    private void updateFilter(FilteredList<AuditEntry> filteredData, String searchText, String role) {
        filteredData.setPredicate(entry -> {
    // If search text is empty and role is "All", show everything
        if ((searchText == null || searchText.isEmpty()) && 
            (role == null || role.equalsIgnoreCase("All"))) {
            return true;
        }

        boolean matchesSearch = true;
        boolean matchesRole = true;

        // Filter by search text
        if (searchText != null && !searchText.isEmpty()) {
            String lowerCaseFilter = searchText.toLowerCase();

            matchesSearch =
                (entry.getUserName() != null && entry.getUserName().toLowerCase().contains(lowerCaseFilter)) ||
                (entry.getUserRole() != null && entry.getUserRole().toLowerCase().contains(lowerCaseFilter)) ||
                (entry.getAction() != null && entry.getAction().toLowerCase().contains(lowerCaseFilter)) ||
                (entry.getDescription() != null && entry.getDescription().toLowerCase().contains(lowerCaseFilter)) ||
                String.valueOf(entry.getLogId()).contains(lowerCaseFilter) ||
                String.valueOf(entry.getUserId()).contains(lowerCaseFilter);
        }

        // Filter by role
        if (role != null && !role.equalsIgnoreCase("All")) {
            matchesRole = entry.getUserRole() != null && entry.getUserRole().equalsIgnoreCase(role);
        }

        return matchesSearch && matchesRole;
    });
        }
    
//    private void exportToCSV(ObservableList<AuditEntry> data) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Save Audit Log Data");
//        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
//        fileChooser.setInitialFileName("audit_logs.csv");
//        
//        File file = fileChooser.showSaveDialog(null);
//        
//        if (file != null) {
//            try (PrintWriter writer = new PrintWriter(file)) {
//                // Write header
//                writer.println("Log ID,User ID,User Name,User Role,Action,Description,Timestamp,Old Value,New Value");
//                
//                // Write data
//                for (AuditEntry entry : data) {
//                    writer.println(
//                        entry.getLogId() + "," +
//                        entry.getUserId() + "," +
//                        "\"" + entry.getUserName() + "\"," +
//                        "\"" + entry.getUserRole() + "\"," +
//                        "\"" + entry.getAction() + "\"," +
//                        "\"" + entry.getDescription() + "\"," +
//                        "\"" + entry.getTimestamp() + "\"," +
//                        "\"" + entry.getOldValue() + "\"," +
//                        "\"" + entry.getNewValue() + "\""
//                    );
//                }
//                
//                showInfo("Export Successful", "Audit log data has been exported to:\n" + file.getAbsolutePath());
//                
//            } catch (Exception e) {
//                e.printStackTrace();
//                showError("Export Failed", "Could not export data to CSV.");
//            }
//        }
//    }
//    
    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    

    public VBox getRoot() {
        return root;
    }
// private void updateFilter(FilteredList<AuditEntry> filteredData, String searchText, String role) {
//    filteredData.setPredicate(entry -> {
//        // Handle empty search case: if no search text, allow all entries to pass
//        boolean matchesSearch = true;
//        if (searchText != null && !searchText.isEmpty()) {
//            // Prepare search text for comparison
//            String filter = searchText.toLowerCase();
//
//            // Check logId and userId, convert them to String for comparison
//            matchesSearch = String.valueOf(entry.getLogId()).contains(filter)
//                || String.valueOf(entry.getUserId()).contains(filter);
//
//            // Check userName and userRole, null-safe check before calling toLowerCase()
//            matchesSearch = matchesSearch
//                || (entry.getUserName() != null && entry.getUserName().toLowerCase().contains(filter))
//                || (entry.getUserRole() != null && entry.getUserRole().toLowerCase().contains(filter));
//
//            // Check action, description, timestamp, oldValue, and newValue with null-safe checks
//            matchesSearch = matchesSearch
//                || (entry.getAction() != null && entry.getAction().toLowerCase().contains(filter))
//                || (entry.getDescription() != null && entry.getDescription().toLowerCase().contains(filter))
//                || (entry.getTimestamp() != null && entry.getTimestamp().toLowerCase().contains(filter))
//                || (entry.getOldValue() != null && entry.getOldValue().toLowerCase().contains(filter))
//                || (entry.getNewValue() != null && entry.getNewValue().toLowerCase().contains(filter));
//        }
//
//        // If role filter is applied, check if entry's role matches
//        if (role != null && !role.equals("All")) {
//            return matchesSearch && (entry.getUserRole() != null && entry.getUserRole().equalsIgnoreCase(role));
//        }
//
//        // If no role filter, just return search result
//        return matchesSearch;
//    });
//}

    
      // ===== Inner Class for Audit Entry =====
    public static class AuditEntry {
        private int logId;
        private int userId;
        private String userName;
        private String userRole;
        private String action;
        private String description;
        private String timestamp;
        private String oldValue;
        private String newValue;

        public AuditEntry(int logId, int userId, String userName, String userRole,
                          String action, String description, String timestamp,
                          String oldValue, String newValue) {
            this.logId = logId;
            this.userId = userId;
            this.userName = userName;
            this.userRole = userRole;
            this.action = action;
            this.description = description;
            this.timestamp = timestamp;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        // Getters
        public int getLogId() { return logId; }
        public int getUserId() { return userId; }
        public String getUserName() { return userName; }
        public String getUserRole() { return userRole; }
        public String getAction() { return action; }
        public String getDescription() { return description; }
        public String getTimestamp() { return timestamp; }
        public String getOldValue() { return oldValue; }
        public String getNewValue() { return newValue; }
    }
}
