/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

public class Rider {
    private int riderId;
    private String name;
    private int assignedOrders;

    // Constructor
    public Rider(int riderId, String name, int assignedOrders) {
        this.riderId = riderId;
        this.name = name;
        this.assignedOrders = assignedOrders;
    }

    // Getters
    public int getRiderId() {
        return riderId;
    }

    public String getName() {
        return name;
    }

    public int getAssignedOrders() {
        return assignedOrders;
    }

    // Setters (optional if you want to modify these later)
    public void setRiderId(int riderId) {
        this.riderId = riderId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAssignedOrders(int assignedOrders) {
        this.assignedOrders = assignedOrders;
    }

    // Override toString() method to display rider info
    @Override
    public String toString() {
        return name + " - Pending Assigned Orders: " + assignedOrders;
    }
}

