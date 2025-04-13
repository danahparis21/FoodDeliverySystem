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

    // Constructor
    public Order(int orderId, double totalPrice, String orderDate,
            String street, String barangay, List<DetailedOrderItem> orderItems, 
            String contactNumber, String orderStatus, String imagePath) {
        this.orderId = orderId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.street = street;
        this.barangay = barangay;
        this.orderItems = orderItems;
         this.contactNumber = contactNumber;
        this.orderStatus = orderStatus;
        this.imagePath = imagePath;
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
}

    
    

