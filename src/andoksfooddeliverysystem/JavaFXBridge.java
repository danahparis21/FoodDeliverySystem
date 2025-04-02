/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

// Class to handle the address updates from JavaScript

import javafx.application.Platform;
import javafx.scene.control.TextField;
// 1. Create the bridge class FIRST (before loading the page)
public class JavaFXBridge {
    private final TextField streetField;
    private final TextField cityField;
    private final TextField postalCodeField;
    private final TextField countryField;
    
    public JavaFXBridge(TextField streetField, TextField cityField, 
                       TextField postalCodeField, TextField countryField) {
        this.streetField = streetField;
        this.cityField = cityField;
        this.postalCodeField = postalCodeField;
        this.countryField = countryField;
    }
    
    public void updateAddress(String street, String city, String postalCode, String country) {
        Platform.runLater(() -> {
            System.out.println("Updating address fields:");
            System.out.println("Street: " + street);
            System.out.println("City: " + city);
            System.out.println("Postal: " + postalCode);
            System.out.println("Country: " + country);
            
            streetField.setText(street != null ? street : "");
            cityField.setText(city != null ? city : "");
            postalCodeField.setText(postalCode != null ? postalCode : "");
            countryField.setText(country != null ? country : "");
        });
    }
}