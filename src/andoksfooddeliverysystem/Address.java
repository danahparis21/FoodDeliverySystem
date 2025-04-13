/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

public class Address {
    private int addressId;
    private String street;
    private String barangay;
    private String addressType;
    private String contactNumber;
    private boolean isDefault;

    public Address(int addressId, String street, String barangay, String addressType, boolean isDefault, String contactNumber) {
        this.addressId = addressId;
        this.street = street;
        this.barangay = barangay;
        this.addressType = addressType;
        this.isDefault = isDefault;
        this.contactNumber = contactNumber;
    }

    // Getter for addressId
    public int getAddressId() {
        return addressId;
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
    
    public String getContactNumber() {
        return contactNumber;
    }

    @Override
    public String toString() {
        return street + ", " + barangay + " (" + addressType + ")";
    }
}

