/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderFetcher {
    public static List<Order> fetchOrders() {
        List<Order> orders = new ArrayList<>();
        
        // SQL query to fetch orders and their associated address information
        String orderQuery = "SELECT o.*, a.street, b.barangay_name, a.contact_number " +
                            "FROM orders o " +
                            "JOIN addresses a ON o.address_id = a.address_id " +
                            "JOIN barangay b ON a.barangay_id = b.barangay_id"; // Fetch order and address details
        
        // SQL query to fetch items for a specific order
        String itemQuery = "SELECT oi.*, i.* \n" +
                "FROM order_items oi\n" +
                "JOIN menu_items i ON oi.item_id = i.item_id\n" +
                "WHERE oi.order_id = ?"; 

        try (Connection conn = Database.connect();
             Statement stmt = conn.createStatement();
             ResultSet orderResultSet = stmt.executeQuery(orderQuery)) {

            while (orderResultSet.next()) {
                int orderId = orderResultSet.getInt("order_id");
                double totalPrice = orderResultSet.getDouble("total_price");
                String orderDate = orderResultSet.getString("order_date");
                String street = orderResultSet.getString("street");
                String barangay = orderResultSet.getString("barangay_name"); // barangay name from barangays table
                String contactNumber = orderResultSet.getString("contact_number");
                String orderStatus = orderResultSet.getString("status");
                String imagePath = orderResultSet.getString("proof_of_delivery_image_path");
                String orderType = orderResultSet.getString("order_type");
                String paymentMethod = orderResultSet.getString("payment_method");
                String paymentStatus = orderResultSet.getString("payment_status");
                String pickupTime = orderResultSet.getString("pickup_time");
                String proofOfPaymentImage = orderResultSet.getString("payment_proof_path");


                // Now fetch the items for this order
                List<DetailedOrderItem> items = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(itemQuery)) {
                    ps.setInt(1, orderId);
                    ResultSet itemResultSet = ps.executeQuery();

                    while (itemResultSet.next()) {
                        int itemId = itemResultSet.getInt("item_id");
                        String itemName = itemResultSet.getString("name");
                        int quantity = itemResultSet.getInt("quantity");
                        double subtotal = itemResultSet.getDouble("subtotal");
                        String variation = itemResultSet.getString("variation");
                        String instructions = itemResultSet.getString("instructions");
                        items.add(new DetailedOrderItem(itemId, itemName, quantity, totalPrice, subtotal, variation, instructions));
                    }
                }

                // Create the Order object with the actual address details
                orders.add(new Order(orderId, totalPrice, orderDate, street, barangay, items, contactNumber, orderStatus, imagePath, orderType, paymentMethod, 
                        paymentStatus, pickupTime, proofOfPaymentImage));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }
    public static List<Order> fetchOrdersByRider(int riderId) {
    List<Order> orders = new ArrayList<>();
    String query = "SELECT SELECT o.*, a.street, b.barangay_name, a.contact_number " +
                   "FROM orders o " +
                   "JOIN addresses a ON o.address_id = a.address_id " +
                   "JOIN barangay b ON a.barangay_id = b.barangay_id " +
                   "WHERE o.rider_id = ?"; // Fetch orders for a specific rider

    // SQL query to fetch items for a specific order
    String itemQuery = "SELECT oi.*, i.* FROM order_items oi " +
                       "JOIN menu_items i ON oi.item_id = i.item_id " +
                       "WHERE oi.order_id = ?"; 

    try (Connection conn = Database.connect();
         PreparedStatement stmt = conn.prepareStatement(query)) {

        stmt.setInt(1, riderId);
        ResultSet orderResultSet = stmt.executeQuery();

        while (orderResultSet.next()) {
            int orderId = orderResultSet.getInt("order_id");
            double totalPrice = orderResultSet.getDouble("total_price");
            String orderDate = orderResultSet.getString("order_date");
            String street = orderResultSet.getString("street");
            String barangay = orderResultSet.getString("barangay_name");
            String contactNumber = orderResultSet.getString("contact_number");
            String orderStatus = orderResultSet.getString("status");
            String imagePath = orderResultSet.getString("proof_of_delivery_image_path");
            String orderType = orderResultSet.getString("order_type");
            String paymentMethod = orderResultSet.getString("payment_method");
            String paymentStatus = orderResultSet.getString("payment_status");
            String pickupTime = orderResultSet.getString("pickup_time");
             String proofOfPaymentImage = orderResultSet.getString("payment_proof_path");


             
            // Fetch items for this order
            List<DetailedOrderItem> orderItems = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(itemQuery)) {
                ps.setInt(1, orderId);
                ResultSet itemResultSet = ps.executeQuery();

                while (itemResultSet.next()) {
                    int itemId = itemResultSet.getInt("item_id");
                    String itemName = itemResultSet.getString("name");
                    int quantity = itemResultSet.getInt("quantity");
                    double subtotal = itemResultSet.getDouble("subtotal");
                    String variation = itemResultSet.getString("variation");
                    String instructions = itemResultSet.getString("instructions");

                    orderItems.add(new DetailedOrderItem(itemId, itemName, quantity, totalPrice, subtotal, variation, instructions));
                }
            }

            // Create the Order object with the address details and order items
            Order order = new Order(orderId, totalPrice, orderDate, street, barangay, orderItems, contactNumber, 
                    orderStatus, imagePath, orderType, paymentMethod, paymentStatus, pickupTime, proofOfPaymentImage);

            // Add order to the list
            orders.add(order);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return orders;
}
}
