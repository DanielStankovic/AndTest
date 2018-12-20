package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.greenrobot.eventbus.EventBus;


public class ProfileFragment extends Fragment implements View.OnClickListener {

    private EditText customerEmail, customerName, customerAddress, customerPhone, customerCity;
    private ImageView profileImageView;
    private Button logoutBtn;
    private BrokerSQLite db;
    private Customer customer;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment_layout, container, false);
        init(view);

        customer = db.getCustomer();
        setCustomerInfo(customer);
        return view;
    }

    private void init(View view){
        customerEmail = view.findViewById(R.id.profileEmailEt);
        customerName = view.findViewById(R.id.profileFullNameEt);
        customerAddress = view.findViewById(R.id.profileAddressEt);
        customerPhone = view.findViewById(R.id.profilePhoneEt);
        customerCity = view.findViewById(R.id.profileCityEt);
        profileImageView = view.findViewById(R.id.profileImageLogged);
        logoutBtn = view.findViewById(R.id.logoutButton);

        logoutBtn.setOnClickListener(this);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new BrokerSQLite(context);
        db.open();


    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();

    }



    @Override
    public void onClick(View v) {

        switch (v.getId()){


            case R.id.logoutButton:

                //ovde ide logout customera.

                EventBus.getDefault().post(1);
                db.customer_D();

                LoginFragment fragment = new LoginFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.myContainer, fragment);
                transaction.commit();

                break;
        }
    }

    private void setCustomerInfo(Customer customer){
        customerEmail.setText(customer.getEmail());
        customerName.setText(customer.getFullName());
        customerAddress.setText(customer.getAddress());
        customerPhone.setText(customer.getPhoneNum());
        customerCity.setText(customer.getCity());
        Glide.with(this).load(ApiClient.BASE_URL+ "ProfileImages/" +customer.getProfileImageName()).apply(
                new RequestOptions().placeholder(R.drawable.loading_image).
                        circleCropTransform())
                .into(profileImageView);

    }
}
