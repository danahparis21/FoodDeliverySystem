/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

public class Card {
    private String cardNumber;
    private String cardholderName;
    private boolean isDummyCard = false;

    public Card(String cardNumber, String cardholderName, boolean isDummyCard) {
        this.cardNumber = cardNumber;
        this.cardholderName = cardholderName;
        this.isDummyCard = isDummyCard;
    }
    
    public boolean isDummyCard() {
    return isDummyCard;
}

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCardholderName() {
        return cardholderName;
    }

    @Override
 
    public String toString() {
        if (isDummyCard) {
            return "➕ Add New Card";
        }
        return cardholderName + " (**** **** " + getLast4Digits(cardNumber) + ")";
    }
    
     private static String getLast4Digits(String cardNumber) {
        if (cardNumber.length() >= 4) {
            return cardNumber.substring(cardNumber.length() - 4);
        }
        return cardNumber;
    }

}
