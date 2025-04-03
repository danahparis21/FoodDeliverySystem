/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RiderService {

    private Connection connection;

    public RiderService(Connection connection) {
        this.connection = connection;
    }

    public List<Rider> getAllRiders() {
        List<Rider> riders = new ArrayList<>();
        String query = "SELECT r.rider_id, r.name, COUNT(o.order_id) AS assigned_orders "
                     + "FROM riders r "
                     + "LEFT JOIN orders o ON r.rider_id = o.rider_id "
                     + "GROUP BY r.rider_id, r.name "
                     + "ORDER BY r.name";

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                int riderId = resultSet.getInt("rider_id");
                String name = resultSet.getString("name");
                int assignedOrders = resultSet.getInt("assigned_orders");

                // Create a new Rider object and add it to the list
                riders.add(new Rider(riderId, name, assignedOrders));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return riders;
    }
}
