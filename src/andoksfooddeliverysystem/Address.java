/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

public class Address {
    private String street;
    private String barangay;
    private String addressType;
    private boolean isDefault;

    public Address(String street, String barangay, String addressType, boolean isDefault) {
        this.street = street;
        this.barangay = barangay;
        this.addressType = addressType;
        this.isDefault = isDefault;
    }

    public String getStreet() {
        return street;
    }

    public String getBarangay() {
        return barangay;
    }

    public String getAddressType() {
        return addressType;
    }

    public boolean isDefault() {
        return isDefault;
    }

    @Override
    public String toString() {
        return street + ", " + barangay + " (" + addressType + ")";
    }
}

