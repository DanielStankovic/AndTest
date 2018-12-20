package com.androidb2c.microbs.androidb2c.Data;

import com.androidb2c.microbs.androidb2c.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    //MBS LOKALNA ADRESA
    public static final String BASE_URL =  "http://89.216.113.44:8261/";

    //KRONOST PRODUKCIJA






    private static final String METHOD_NAME = "B2C.svc/";
    private static final String FULL_ADDRESS = BASE_URL+METHOD_NAME;
    public static Retrofit retrofit;

    public static Retrofit getApiClient(){

        //Okhttp client
        OkHttpClient.Builder builder = new OkHttpClient.Builder();


        //Interceptor - used to see the data being sent and recieved
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        if(BuildConfig.DEBUG) {
            builder.addInterceptor(interceptor);
        }

        if(retrofit == null){

            retrofit = new Retrofit.Builder().baseUrl(FULL_ADDRESS).addConverterFactory(GsonConverterFactory.create())
                    .client(builder.build()).build();
        }
        return retrofit;

    }
}
