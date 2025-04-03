/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

// OrderItem class (assuming each order has a list of items)
public class OrderItem {
    private int itemId;
    private int quantity;
    private double subtotal;

    // Constructor
    public OrderItem(int itemId, int quantity, double subtotal) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters
    public int getItemId() {
        return itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
