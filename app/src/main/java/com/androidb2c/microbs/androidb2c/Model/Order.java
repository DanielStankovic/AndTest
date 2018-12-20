package com.androidb2c.microbs.androidb2c.Model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Order {

    @SerializedName("OrderID")
    private String orderID;
    @SerializedName("CustomerID")
    private String customerID;
    @SerializedName("CustomerEmail")
    private String customerEmail;
    @SerializedName("CustomerName")
    private String customerName;
    @SerializedName("CustomerLastName")
    private String customerLastName;
    @SerializedName("Address")
    private String address;
    @SerializedName("PhoneNum")
    private String phoneNum;
    @SerializedName("City")
    private String city;
    @SerializedName("ZipCode")
    private String zipCode;
    @SerializedName("OrderDetails")
    private String orderDetails;

    public Order(String orderID, String customerID, String customerEmail, String customerName,
                 String customerLastName, String address, String phoneNum, String city,
                 String zipCode, String orderDetails) {
        this.orderID = orderID;
        this.customerID = customerID;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.customerLastName = customerLastName;
        this.address = address;
        this.phoneNum = phoneNum;
        this.city = city;
        this.zipCode = zipCode;
        this.orderDetails = orderDetails;
    }

    public Order() {
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public void setCustomerLastName(String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }
}
