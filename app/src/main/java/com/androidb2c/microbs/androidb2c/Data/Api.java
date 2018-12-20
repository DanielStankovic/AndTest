package com.androidb2c.microbs.androidb2c.Data;

import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.Model.CustomerOrder;
import com.androidb2c.microbs.androidb2c.Model.Order;
import com.androidb2c.microbs.androidb2c.Model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface Api {


    @GET("GetAllProducts")
    Call<List<Product>> getProducts();

    @POST("RegisterCustomer")
    Call<List<Customer>> registerCustomer(@Body Customer customer);

    @POST("LoginCustomer")
    Call<List<Customer>> loginCustomer(@Body Customer customer);

    @POST("GetCartProducts")
    Call<List<Product>> getCartProducts(@Body List<Integer> productIDs);

    @POST("GetAuthCode")
    Call<List<Customer>> getAuthCode(@Body Customer customer);

    @POST("SendOrder")
    Call<List<String>> sendOrder(@Body Order order);

    @POST("GetCustomerOrders")
    Call<List<CustomerOrder>> getCustomerOrders(@Body String orderID);
}
