
package andoksfooddeliverysystem;

import java.util.List;


public class Customer {
    private int customerId;
    private String name;
    private String email;
    private int userId;
    private List<Address> addresses; // 💡 Multiple addresses
    private String customerImage; // New field for image path

    // Constructor
    public Customer(int customerId, String name, String email, int userId, List<Address> addresses,
            String customerImage) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.userId = userId;
        this.addresses = addresses;
         this.customerImage = customerImage;
    }

    // Getters
    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getUserId() {
        return userId;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

     public String getCustomerImage() {
        return customerImage;
    }

  
    // Setters
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }
      public void setCustomerImage(String customerImage) {
        this.customerImage = customerImage;
    }

    // Helper to get the default address
    public Address getDefaultAddress() {
        if (addresses == null) return null;
        return addresses.stream()
                .filter(Address::isDefault)
                .findFirst()
                .orElse(null);
    }
}


