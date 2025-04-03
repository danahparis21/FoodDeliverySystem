/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

public class DetailedOrderItem {
    private int itemId;
     private String itemName;
    private int quantity;
    private double price;
      private double subTotal;
    private String variation;
    private String instructions;
   

    // Constructor
    public DetailedOrderItem(int itemId, String itemName, int quantity, double price, double subTotal,String variation, String instructions) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.subTotal = subTotal;
        this.variation = variation;
        this.instructions = instructions;
    }

    // Getters
    public int getItemId() {
        return itemId;
    }

     public String getItemName() {
        return itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
    
     public double getSubtotal() {
        return subTotal;
    }

    public String getVariation() {
        return variation;
    }

    public String getInstructions() {
        return instructions;
    }
}
