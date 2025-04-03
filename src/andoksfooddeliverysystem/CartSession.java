
package andoksfooddeliverysystem;
import java.util.HashMap;
import java.util.Map;

public class CartSession {
    private static Map<Integer, Integer> cart = new HashMap<>(); // itemID -> quantity
    private static final Map<Integer, String> variationMap = new HashMap<>();
    private static final Map<Integer, String> instructionsMap = new HashMap<>();


    // Add item to cart
//    public static void addToCart(int itemId, int quantity) {
//        cart.put(itemId, cart.getOrDefault(itemId, 0) + quantity);
//    }
    public static void addToCart(int itemId, int quantity, String variation, String instruction) {
        cart.put(itemId, cart.getOrDefault(itemId, 0) + quantity);
        variationMap.put(itemId, variation);
        instructionsMap.put(itemId, instruction);
    }

    
    public static String getItemVariation(int itemId) {
        return variationMap.getOrDefault(itemId, "No variation");
    }

    public static String getItemInstructions(int itemId) {
        return instructionsMap.getOrDefault(itemId, "No instructions");
    }

    // Remove item from cart
    public static void removeFromCart(int itemId) {
        cart.remove(itemId);
    }

    // Get all cart items
    public static Map<Integer, Integer> getCartItems() {
        return cart;
    }
    
        // Get all variations in cart
    public static Map<Integer, String> getVariations() {
        return variationMap;
    }

    // Get all instructions in cart
    public static Map<Integer, String> getInstructions() {
        return instructionsMap;
    }


    // Clear cart after checkout
    public static void clearCart() {
        cart.clear();
        variationMap.clear();
        instructionsMap.clear();
    }

}
