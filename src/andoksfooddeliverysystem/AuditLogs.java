
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
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class AuditLogs {

    private VBox root;
    private int adminId; // the logged-in admin's ID

    public AuditLogs(int adminId) {
        this.adminId = adminId;

        root = new VBox(20); // Adjusted spacing
        root.setPadding(new Insets(20));
        root.setPrefSize(600, 400); // Set preferred size

          //===
     // ====== SEARCH + SORT + FILTER CONTROLS ======
        TextField searchField = new TextField();
        searchField.setPromptText("Search...");

        Button sortRecent = new Button("Most Recent");
        Button sortOldest = new Button("Least Recent");

        ComboBox<String> roleFilter = new ComboBox<>();
        roleFilter.getItems().addAll("All", "Admin", "Customer", "Rider");
        roleFilter.setValue("All");

        HBox topBar = new HBox(10, searchField, roleFilter, sortRecent, sortOldest);
        topBar.setPadding(new Insets(10));

        // ====== TABLEVIEW SETUP ======
        TableView<AuditEntry> tableView = new TableView<>();
        tableView.setPrefWidth(1000);


        TableColumn<AuditEntry, Integer> logIdCol = new TableColumn<>("Log ID");
        logIdCol.setCellValueFactory(new PropertyValueFactory<>("logId"));

        TableColumn<AuditEntry, Integer> userIdCol = new TableColumn<>("User ID");
        userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

        TableColumn<AuditEntry, String> userNameCol = new TableColumn<>("User Name");
        userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

        TableColumn<AuditEntry, String> userRoleCol = new TableColumn<>("User Role");
        userRoleCol.setCellValueFactory(new PropertyValueFactory<>("userRole"));

        TableColumn<AuditEntry, String> actionCol = new TableColumn<>("Action");
        actionCol.setCellValueFactory(new PropertyValueFactory<>("action"));

        TableColumn<AuditEntry, String> descCol = new TableColumn<>("Description");
        descCol.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<AuditEntry, String> timestampCol = new TableColumn<>("Timestamp");
        timestampCol.setCellValueFactory(new PropertyValueFactory<>("timestamp"));

        TableColumn<AuditEntry, String> oldValueCol = new TableColumn<>("Old Value");
        oldValueCol.setCellValueFactory(new PropertyValueFactory<>("oldValue"));

        TableColumn<AuditEntry, String> newValueCol = new TableColumn<>("New Value");
        newValueCol.setCellValueFactory(new PropertyValueFactory<>("newValue"));

        tableView.getColumns().addAll(
            logIdCol, userIdCol, userNameCol, userRoleCol,
            actionCol, descCol, timestampCol, oldValueCol, newValueCol
        );

                /// === ObservableList and FilteredList Setup ===
        ObservableList<AuditEntry> auditData = FXCollections.observableArrayList();
        FilteredList<AuditEntry> filteredData = new FilteredList<>(auditData, p -> true);
        tableView.setItems(filteredData);

        // === Search Field Listener ===
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilter(filteredData, newValue, roleFilter.getValue());  // Apply both search and role filter
        });

        roleFilter.setOnAction(e -> {
            updateFilter(filteredData, searchField.getText(), roleFilter.getValue()); // Apply role filter with search
        });
       
        // Sort by Most Recent
        sortRecent.setOnAction(e -> {
            SortedList<AuditEntry> sortedData = new SortedList<>(filteredData);
            sortedData.setComparator((entry1, entry2) -> {
                // Compare timestamps in descending order for Most Recent first
                return entry2.getTimestamp().compareTo(entry1.getTimestamp());
            });
            tableView.setItems(sortedData);
        });

        // Sort by Least Recent
        sortOldest.setOnAction(e -> {
            SortedList<AuditEntry> sortedData = new SortedList<>(filteredData);
            sortedData.setComparator((entry1, entry2) -> {
                // Compare timestamps in ascending order for Least Recent first
                return entry1.getTimestamp().compareTo(entry2.getTimestamp());
            });
            tableView.setItems(sortedData);
        });

        // Apply both filters (search and role) and then sort
        SortedList<AuditEntry> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableView.comparatorProperty());
        tableView.setItems(sortedData);  // ✅ Sorted AND filtered

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
 private void updateFilter(FilteredList<AuditEntry> filteredData, String searchText, String role) {
    filteredData.setPredicate(entry -> {
        // Handle empty search case: if no search text, allow all entries to pass
        boolean matchesSearch = true;
        if (searchText != null && !searchText.isEmpty()) {
            // Prepare search text for comparison
            String filter = searchText.toLowerCase();

            // Check logId and userId, convert them to String for comparison
            matchesSearch = String.valueOf(entry.getLogId()).contains(filter)
                || String.valueOf(entry.getUserId()).contains(filter);

            // Check userName and userRole, null-safe check before calling toLowerCase()
            matchesSearch = matchesSearch
                || (entry.getUserName() != null && entry.getUserName().toLowerCase().contains(filter))
                || (entry.getUserRole() != null && entry.getUserRole().toLowerCase().contains(filter));

            // Check action, description, timestamp, oldValue, and newValue with null-safe checks
            matchesSearch = matchesSearch
                || (entry.getAction() != null && entry.getAction().toLowerCase().contains(filter))
                || (entry.getDescription() != null && entry.getDescription().toLowerCase().contains(filter))
                || (entry.getTimestamp() != null && entry.getTimestamp().toLowerCase().contains(filter))
                || (entry.getOldValue() != null && entry.getOldValue().toLowerCase().contains(filter))
                || (entry.getNewValue() != null && entry.getNewValue().toLowerCase().contains(filter));
        }

        // If role filter is applied, check if entry's role matches
        if (role != null && !role.equals("all")) {
            return matchesSearch && (entry.getUserRole() != null && entry.getUserRole().equalsIgnoreCase(role));
        }

        // If no role filter, just return search result
        return matchesSearch;
    });
}

    
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
