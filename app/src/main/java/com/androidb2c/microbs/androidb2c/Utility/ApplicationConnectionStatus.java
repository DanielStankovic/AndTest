package com.androidb2c.microbs.androidb2c.Utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ApplicationConnectionStatus {

    static Context context;
    /**
     * We use this class to determine if the application has been connected to either WIFI Or Mobile
     * Network, before we make any network request to the server.
     * <p>
     * The class uses two permission - INTERNET and ACCESS NETWORK STATE, to determine the user's
     * connection stats
     */

    private static ApplicationConnectionStatus instance = new ApplicationConnectionStatus();
    ConnectivityManager connectivityManager;
    NetworkInfo wifiInfo, mobileInfo;
    boolean connected = false;

    public static ApplicationConnectionStatus getInstance(Context ctx) {
        context = ctx.getApplicationContext();
        return instance;
    }

    public boolean isOnline() {
        try {
            connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            connected = networkInfo != null && networkInfo.isAvailable() &&
                    networkInfo.isConnected();
            return connected;

        } catch (Exception e) {

        }
        return connected;
    }
}
