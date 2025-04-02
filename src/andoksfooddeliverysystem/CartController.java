/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package andoksfooddeliverysystem;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.Map;

public class CartController {
    @FXML private TableView<CartItem> cartTable;
    @FXML private TableColumn<CartItem, Integer> itemIdColumn;
    @FXML private TableColumn<CartItem, Integer> quantityColumn;

    public void initialize() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadCartItems();
    }

    private void loadCartItems() {
        ObservableList<CartItem> cartList = FXCollections.observableArrayList();
        for (Map.Entry<Integer, Integer> entry : CartSession.getCartItems().entrySet()) {
            cartList.add(new CartItem(entry.getKey(), entry.getValue()));
        }
        cartTable.setItems(cartList);
    }
}
