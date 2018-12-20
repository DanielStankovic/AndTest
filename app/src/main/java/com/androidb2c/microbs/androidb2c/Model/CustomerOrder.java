package com.androidb2c.microbs.androidb2c.Model;

import com.google.gson.annotations.SerializedName;

public class CustomerOrder {

    @SerializedName("OrderID")
    private String orderID;
    @SerializedName("OrderStatus")
    private int orderStatus;
    @SerializedName("SumTotal")
    private double sumTotal;

    public CustomerOrder(String orderID, int orderStatus, double sumTotal) {
        this.orderID = orderID;
        this.orderStatus = orderStatus;
        this.sumTotal = sumTotal;
    }

    public CustomerOrder() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public double getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(double sumTotal) {
        this.sumTotal = sumTotal;
    }
}
