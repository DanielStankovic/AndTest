package com.androidb2c.microbs.androidb2c.Model;

import com.google.gson.annotations.SerializedName;

public class Customer {

    @SerializedName("CustomerID")
    private String customerID;

    @SerializedName("Email")
    private String email;

    @SerializedName("FullName")
    private String fullName;

    @SerializedName("Password")
    private String password;

    @SerializedName("Address")
    private String address;

    @SerializedName("PhoneNum")
    private String phoneNum;

    @SerializedName("City")
    private String city;

    @SerializedName("ImageBytes")
    private String profileImageString;

    @SerializedName("ProfileImageName")
    private String profileImageName;

    @SerializedName("Response")
    private String response;




    public Customer(String customerID, String email, String fullName, String password, String address, String phoneNum, String city, String profileImageString) {
       this.customerID = customerID;
        this.email = email;
        this.fullName = fullName;
        this.password = password;
        this.address = address;
        this.phoneNum = phoneNum;
        this.city = city;
        this.profileImageString = profileImageString;
    }

    public Customer() {
    }

    public String getEmail() {
        return email;
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getProfileImageString() {
        return profileImageString;
    }

    public void setProfileImageString(String profileImageString) {
        this.profileImageString = profileImageString;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getProfileImageName() {
        return profileImageName;
    }

    public void setProfileImageName(String profileImageName) {
        this.profileImageName = profileImageName;
    }
}
