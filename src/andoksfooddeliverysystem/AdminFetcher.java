
package andoksfooddeliverysystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class AdminFetcher {
    public static int getAdminIdFromUserId(int userId) {
        String query = "SELECT admin_id FROM admins WHERE user_id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("admin_id"); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 
    }
}
