/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import java.time.LocalDateTime;

public class Notification {
    int notificationId;
    String message;
    LocalDateTime timestamp;  // Add this field

    public Notification(int id, String msg) {
        this.notificationId = id;
        this.message = msg;
        this.timestamp = LocalDateTime.now();  // Set to current time
    }
    
    public String getMessage() {
    return message;
}
        // Setters
    public void setMessage(String msg) {
        this.message = msg;
    }
    // Add getter
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
