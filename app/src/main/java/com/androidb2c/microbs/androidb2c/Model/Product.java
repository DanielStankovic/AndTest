package com.androidb2c.microbs.androidb2c.Model;

import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("ProductID")
    private int ProductID;

    @SerializedName("ProductCode")
    private String ProductCode;

    @SerializedName("ProductName")
    private String ProductName;

    @SerializedName("SellingPrice")
    private double SellingPrice;

    @SerializedName("Description")
    private String Description;

    @SerializedName("CategoryID")
    private int CategoryID;

    @SerializedName("SubcategoryID")
    private int SubcategoryID;

    @SerializedName("Discount")
    private double Discount;


    @SerializedName("IsSpecial")
    private boolean IsSpecial;

    @SerializedName("ProductImageName")
    private String ProductImage;


    private boolean isAddedToFav;

    @SerializedName("Quantity")
    private int quantity;

    private boolean isAddedToCart;



    public Product(int productID, String productCode, String productName, double sellingPrice,
                   String description, int categoryID, int subcategoryID, double discount,
                   boolean isSpecial, String productImage) {
        ProductID = productID;
        ProductCode = productCode;
        ProductName = productName;
        SellingPrice = sellingPrice;
        Description = description;
        CategoryID = categoryID;
        SubcategoryID = subcategoryID;
        Discount = discount;
        IsSpecial = isSpecial;
        ProductImage = productImage;
    }

    public Product() {
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public double getSellingPrice() {
        return SellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        SellingPrice = sellingPrice;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getCategoryID() {
        return CategoryID;
    }

    public void setCategoryID(int categoryID) {
        CategoryID = categoryID;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

    public boolean isSpecial() {
        return IsSpecial;
    }

    public void setSpecial(boolean special) {
        IsSpecial = special;
    }

    public String getProductImage() {
        return ProductImage;
    }

    public void setProductImage(String productImage) {
        ProductImage = productImage;
    }

    public int getSubcategoryID() {
        return SubcategoryID;
    }

    public void setSubcategoryID(int subcategoryID) {
        SubcategoryID = subcategoryID;
    }

        public boolean isAddedToFav() {
        return isAddedToFav;
    }

    public void setAddedToFav(boolean addedToFav) {
        isAddedToFav = addedToFav;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public boolean isAddedToCart() {
        return isAddedToCart;
    }

    public void setAddedToCart(boolean addedToCart) {
        isAddedToCart = addedToCart;
    }
}
