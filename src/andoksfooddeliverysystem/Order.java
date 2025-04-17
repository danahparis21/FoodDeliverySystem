/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import java.util.List;

public class Order {
    private int orderId;
    private double totalPrice;
    private String orderDate;
    private String street;
    private String barangay;
    private List<DetailedOrderItem> orderItems;
        private String contactNumber;
        private String orderStatus;
        private String imagePath;
        private String orderType;
        private String paymentMethod;
        private String paymentStatus;
        private String pickupTime;
        private String proofOfPaymentImage;
        private String customerName;

    // Constructor
    public Order(int orderId, double totalPrice, String orderDate,
            String street, String barangay, List<DetailedOrderItem> orderItems, 
            String contactNumber, String orderStatus, String imagePath, String orderType, String paymentMethod,
            String paymentStatus, String pickupTime, String proofOfPaymentImage, String customerName) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.street = street;
        this.barangay = barangay;
        this.orderItems = orderItems;
         this.contactNumber = contactNumber;
        this.orderStatus = orderStatus;
        this.imagePath = imagePath;
        this.orderType = orderType;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.pickupTime = pickupTime;
        this.proofOfPaymentImage = proofOfPaymentImage;
        this.customerName = customerName;
    }

    // Getters
    public int getOrderId() {
        return orderId;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public String getStreet() {
        return street;
    }

    public String getBarangay() {
        return barangay;
    }

    public List<DetailedOrderItem> getOrderItems() {
        return orderItems;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public String getOrderStatus() {
        return orderStatus;
    }
    
     public String getProofOfDeliveryImagePath() {
        return imagePath;
    }
     
      public String getOrderType() {
        return orderType;
    }
      
      public String getPaymentMethod() {
        return paymentMethod;
    }
      
       
        public String getPaymentStatus() {
        return paymentStatus;
    }
      
     public String getPickupTime() {
        return pickupTime;
    }
      
     
      
    public String getProofOfPaymentImagePath() {
        return proofOfPaymentImage;
    }
    
    public String getCustomerName() {
    return customerName;
}
        // Setters
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public void setBarangay(String barangay) {
        this.barangay = barangay;
    }

    public void setOrderItems(List<DetailedOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public void setOrderStatus(String orderStatus) {
         this.orderStatus = orderStatus;

        }
    
    public void setCustomerName(String customerName) {
    this.customerName = customerName;
}
}

    
    

