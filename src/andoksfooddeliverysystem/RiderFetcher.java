/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


public class RiderFetcher {
    public static int getRiderIdFromUserId(int userId) {
        String query = "SELECT rider_id FROM riders WHERE user_id = ?";
        
        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {
             
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("rider_id"); // Return the corresponding rider_id
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Return -1 if no rider_id is found
    }
}
