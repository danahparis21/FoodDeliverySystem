/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {
    public static Customer getCustomerByUserId(int userId) {
        Customer customer = null;
        String query = "SELECT * FROM customers WHERE user_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int customerId = rs.getInt("customer_id");
                String name = rs.getString("name");
                String email = rs.getString("email");

                // 🔁 Load all addresses for this customer
                List<Address> addresses = getAddressesByCustomerId(customerId);
                String customerImage = rs.getString("customer_image");

                // ✅ Create Customer object
                customer = new Customer(customerId, name, email, userId, addresses, customerImage);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customer;
    }

    // 💡 Helper method to fetch multiple addresses for a customer
    private static List<Address> getAddressesByCustomerId(int customerId) {
        List<Address> addresses = new ArrayList<>();
        String query = "SELECT a.address_id, a.street, b.barangay_name, a.address_type, a.contact_number, a.is_default " +
                       "FROM addresses a " +
                       "JOIN barangay b ON a.barangay_id = b.barangay_id " +
                       "WHERE a.customer_id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, customerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int addressId = rs.getInt("address_id");
                String street = rs.getString("street");
                String barangay = rs.getString("barangay_name");
                String addressType = rs.getString("address_type");
                String contactNumber = rs.getString("contact_number");
                boolean isDefault = rs.getBoolean("is_default");

                Address address = new Address(addressId, street, barangay, addressType, isDefault, contactNumber);
                addresses.add(address);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return addresses;
    }
}

