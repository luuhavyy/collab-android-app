package models;

import java.io.Serializable;

public class CartItem implements Serializable {
    private String productid;
    private int quantity;
    private double price;
    private boolean isSelected;

    public CartItem() {
    }

    public CartItem(String productid, int quantity, double price, boolean isSelected) {
        this.productid = productid;
        this.quantity = quantity;
        this.price = price;
        this.isSelected = isSelected;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
