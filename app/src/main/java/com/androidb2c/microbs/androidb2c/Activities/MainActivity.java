package com.androidb2c.microbs.androidb2c.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Fragments.AboutUsFragment;
import com.androidb2c.microbs.androidb2c.Fragments.CartFragment;
import com.androidb2c.microbs.androidb2c.Fragments.CategoryFragment;
import com.androidb2c.microbs.androidb2c.Fragments.AllProductsFragment;
import com.androidb2c.microbs.androidb2c.Fragments.NoInternetFragment;
import com.androidb2c.microbs.androidb2c.Fragments.OrderFragment;
import com.androidb2c.microbs.androidb2c.Fragments.LoginFragment;
import com.androidb2c.microbs.androidb2c.Fragments.ProfileFragment;
import com.androidb2c.microbs.androidb2c.Fragments.WishlistFragment;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.ApplicationConnectionStatus;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private FragmentManager fragmentManager;
    private ImageView headerImage;
    private TextView headerNameTv, headerEmailTv;
    private Fragment fragment;
    private BrokerSQLite db = new BrokerSQLite(this);
    private Customer loggedCustomer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        db.open();
        init();
        loggedCustomer = db.getCustomer();
        if(loggedCustomer != null) {
            setLoggedCustomer(loggedCustomer);
        }
        if (savedInstanceState == null) {
            //PROVERA DA LI IMA INTERNATA
            if (ApplicationConnectionStatus.getInstance(getApplicationContext()).isOnline()){

                if (fragment == null) {

                    fragment = new AllProductsFragment();
                    fragmentManager.beginTransaction()
                            .add(R.id.myContainer, fragment)
                            .commit();
                    setTitle("Početna");
                    navigationView.setCheckedItem(R.id.main_screen);
                }

        } else{
                fragment = new NoInternetFragment();
              fragmentManager.beginTransaction()
                      .add(R.id.myContainer, fragment)
                      .commit();
              setTitle("Internet Problem");
              navigationView.setCheckedItem(R.id.main_screen);
            }
        }

    }

    @Subscribe
    public void onEvent(Customer customer){
        loggedCustomer = customer;
        setLoggedCustomer(customer);
    }

    @Subscribe
    public void onEvent(Integer i){
       logoutCustomer();
    }



    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        db.close();
        EventBus.getDefault().unregister(this);
    }

    private void init() {
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);

        View hView = navigationView.getHeaderView(0);
        headerImage = hView.findViewById(R.id.headerImageView);

        headerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToProfileScreenOrLogin();
            }
        });
        headerNameTv = hView.findViewById(R.id.headerNameTv);
        headerEmailTv = hView.findViewById(R.id.headerEmailTv);

        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        fragment = fragmentManager.findFragmentById(R.id.myContainer);
    }

    private void setLoggedCustomer(Customer loggedCustomer){

        Glide.with(this).load(ApiClient.BASE_URL+ "ProfileImages/" + loggedCustomer.getProfileImageName()).apply(
                new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .error(R.drawable.profile_image_placeholder)
                        .circleCropTransform())
                .into(headerImage);
        headerNameTv.setText(loggedCustomer.getFullName());
        headerEmailTv.setText(loggedCustomer.getEmail());
    }


    private void logoutCustomer(){
        Glide.with(this).load(R.drawable.profile_image_placeholder).apply(
                new RequestOptions()
                        .placeholder(R.drawable.loading_image)
                        .circleCropTransform())
                .into(headerImage);
        headerNameTv.setText(getString(R.string.user_name_logged_out));
        headerEmailTv.setText(getString(R.string.user_email_logged_out));
        loggedCustomer = null;

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        if(ApplicationConnectionStatus.getInstance(getApplicationContext()).isOnline()) {

            switch (menuItem.getItemId()) {
                case R.id.main_screen:

                    fragment = new AllProductsFragment();


                    break;
                case R.id.category_screen:

                    fragment = new CategoryFragment();

                    break;

                case R.id.cart_screen:
                    fragment = new CartFragment();

                    break;

                case R.id.orders_screen:
                    fragment = new OrderFragment();
                    break;

                case R.id.wishlist_screen:
                    fragment = new WishlistFragment();
                    break;

                case R.id.profile_screen:

                    if (loggedCustomer != null) {
                        //fragment za logovanog korisnika

                        EventBus.getDefault().post(loggedCustomer);
                        fragment = new ProfileFragment();

                    } else {
                        fragment = new LoginFragment();
                    }

                    break;

                case R.id.about_us_screen:
                    fragment = new AboutUsFragment();

                    break;


            }

            fragmentManager.beginTransaction()

                    .replace(R.id.myContainer, fragment)
                    .commit();
            menuItem.setChecked(true);
            setTitle(menuItem.getTitle());
            drawerLayout.closeDrawers();
            return true;
        } else{
           showNoInternetFragment();
            return true;
        }



    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            int count = getSupportFragmentManager().getBackStackEntryCount();
            if(count == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.myDialog);

                builder.setTitle("Pažnja");
                builder.setMessage("Da li želite da napustite aplikaciju?");
                builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                builder.setNegativeButton("NE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void setNavDrawerAndTitle(int id, String title){


        navigationView.setCheckedItem(id);
        setTitle(title);


    }

    private void goToProfileScreenOrLogin(){
        if(ApplicationConnectionStatus.getInstance(getApplicationContext()).isOnline()) {

            if (loggedCustomer != null) {
                //fragment za logovanog korisnika

                EventBus.getDefault().post(loggedCustomer);
                fragment = new ProfileFragment();


            } else {
                fragment = new LoginFragment();

            }

            fragmentManager.beginTransaction().replace(R.id.myContainer, fragment)
                    .commit();
            setNavDrawerAndTitle(R.id.profile_screen, "Moj nalog");
            drawerLayout.closeDrawers();
        } else{
            showNoInternetFragment();
        }

    }

    private void showNoInternetFragment(){
        fragment = new NoInternetFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.myContainer, fragment)
                .commit();
        setTitle("Internet Problem");
        navigationView.setCheckedItem(R.id.main_screen);
        drawerLayout.closeDrawers();
    }
}
