package com.androidb2c.microbs.androidb2c.Model;

public class CartProduct {

    private int productID;
    private int quantity;

    public CartProduct(int productID, int quantity) {
        this.productID = productID;
        this.quantity = quantity;
    }

    public CartProduct() {
    }

    public int getProductID() {
        return productID;
    }

    public void setProductID(int productID) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
