package andoksfooddeliverysystem;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javax.mail.MessagingException;

public class User {
    
    private int userID;
    private String role;

    // Constructor
    public User(int userID, String role) {
        this.userID = userID;
        this.role = role;
    }

    // Getters
    public int getUserID() {
        return userID;
    }

    public String getRole() {
        return role;
    }

        public static boolean signUp(String name, String email, String password) {
            
             if (emailExists(email)) {
                System.out.println("Email already exists. Please use a different email.");
                return false;
            }
             
            String userSql = "INSERT INTO Users (full_name, email, password, role) VALUES (?, ?, ?, 'Customer')";
            String customerSql = "INSERT INTO Customers (user_id, name, email, password) VALUES (?, ?, ?, ?)";

            try (Connection conn = Database.connect();
                 PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
                 PreparedStatement customerStmt = conn.prepareStatement(customerSql)) {

                // Insert into Users
                userStmt.setString(1, name);
                userStmt.setString(2, email);
                userStmt.setString(3, hashPassword(password));
                int userRows = userStmt.executeUpdate();

                // Get the generated user_id
                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Insert into Customers
                    customerStmt.setInt(1, userId);
                    customerStmt.setString(2, name);
                    customerStmt.setString(3, email);
                     customerStmt.setString(4, password);
                    int customerRows = customerStmt.executeUpdate();

                    return customerRows > 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }
        
   public static boolean emailExists(String email) {
    String sql = "{CALL CheckEmailExists(?)}"; // Stored procedure call

    try (Connection conn = Database.connect();
         CallableStatement stmt = conn.prepareCall(sql)) {

        stmt.setString(1, email);  // Set the email input
        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("email_count") > 0; // Use alias from stored procedure
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}




    
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

   public static User login(String name, String password, Stage currentStage) throws MessagingException {
    String query = "{CALL UserLogin(?, ?)}"; // Call the stored procedure

    try (Connection conn = Database.connect();
         CallableStatement stmt = conn.prepareCall(query)) {

        stmt.setString(1, name);             // Set full_name parameter
        stmt.setString(2, hashPassword(password)); // Set hashed password parameter

        ResultSet rs = stmt.executeQuery();   // Execute stored procedure
        if (rs.next()) {
            int userID = rs.getInt("user_id");  
            String role = rs.getString("role").trim().toLowerCase();

            User user = new User(userID, role); // Create User object

            // Open appropriate dashboard based on the role
            if (role.equals("admin")) {
                new AdminDashboard(userID).start(new Stage());
            } else if (role.equals("customer")) {
                new CustomerDashboard(userID).start(new Stage()); 
            }
            else if (role.equals("rider")) {
                // Fetch the rider_id using the user_id
                String getRiderIdSql = "SELECT rider_id FROM riders WHERE user_id = ?";
                try (PreparedStatement riderStmt = conn.prepareStatement(getRiderIdSql)) {
                    riderStmt.setInt(1, userID);
                    ResultSet riderRs = riderStmt.executeQuery();
                    if (riderRs.next()) {
                        int riderId = riderRs.getInt("rider_id");

                        // Update rider's online_status to "Online"
                        String updateStatusSql = "UPDATE riders SET online_status = 'Online', last_modified_by = ? WHERE rider_id = ?";
                        try (PreparedStatement updateStmt = conn.prepareStatement(updateStatusSql)) {
                            updateStmt.setInt(1, userID);     // Set the modifier (the user who modified the status)
                            updateStmt.setInt(2, riderId);    // Set the rider whose status is being changed
                            updateStmt.executeUpdate();       // Execute the update
                        } catch (SQLException e) {
                            e.printStackTrace();
                            // You can log the error or notify the user if needed
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // You can log the error or notify the user if needed
                }

                new RiderDashboard(userID).start(new Stage());
            }

            currentStage.close(); // Close the login window
            return user; // Return the created user object
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return null; // Return null if login fails
}





}
