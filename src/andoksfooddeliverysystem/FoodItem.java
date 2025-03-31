/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

/**
 *
 * @author 63945
 */
 public class FoodItem {
        private int id;
        private String name;
        private double price;
        private int stock;
        private int categoryId;
        private String description;
        private String imagePath; // NEW!

    public FoodItem(int id, String name, double price, int stock, int categoryId, String description, String imagePath) {
         this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
         this.categoryId = categoryId;
        this.description = description;
        this.imagePath = imagePath;
    }
    // ✅ Add a getter for ID
    public int getId() {
        return id;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getStock() { return stock; }
     public int getCategoryId() {  // Change from getCategory() to getCategoryId()
        return categoryId;
    }
    public String getDescription() { return description; }
    public String getImagePath() { return imagePath; }
}

