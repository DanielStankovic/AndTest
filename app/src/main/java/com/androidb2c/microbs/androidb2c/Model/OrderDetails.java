package com.androidb2c.microbs.androidb2c.Model;

import com.google.gson.annotations.SerializedName;

public class OrderDetails {

    @SerializedName("OrderID")
    private String orderID;
    @SerializedName("ProductID")
    private int productID;
    @SerializedName("Quantity")
    private int quantity;
    @SerializedName("Price")
    private double price;
    @SerializedName("Discount")
    private double discount;

    public OrderDetails(String orderID, int productID, int quantity, double price, double discount) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.price = price;
        this.discount = discount;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public OrderDetails() {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
