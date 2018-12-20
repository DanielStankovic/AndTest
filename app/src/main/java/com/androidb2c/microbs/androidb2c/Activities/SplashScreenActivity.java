package com.androidb2c.microbs.androidb2c.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.R;

public class SplashScreenActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 3000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();
    private BrokerSQLite db = new BrokerSQLite(this);
    private TextView welcomeTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        db.open();
        Customer customer = db.getCustomer();
        welcomeTv = findViewById(R.id.welcomeTv);
        if(customer!= null){
            welcomeTv.setText("Dobrodošli nazad,\n" + customer.getFullName());
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }

    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        db.close();
        super.onStop();
    }

    private void launch() {
        if (!isFinishing()) {
            startActivity(new Intent(this, MainActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }

}
