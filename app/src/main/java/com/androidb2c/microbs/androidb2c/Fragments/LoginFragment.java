package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidb2c.microbs.androidb2c.Activities.MainActivity;
import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.ApplicationConnectionStatus;
import com.androidb2c.microbs.androidb2c.Utility.Utility;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment implements View.OnClickListener {

    private Button registerBtn, loginBtn, forgotPassBtn;
    private FragmentManager fragmentManager2;
    private FragmentTransaction fragmentTransaction2;
    private EditText emailEt, passwordEt;
    private Api apiInterface;
    private BrokerSQLite db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_fragment_layout, container, false);
        init(view);

         fragmentManager2 = getFragmentManager();
         fragmentTransaction2 = fragmentManager2.beginTransaction();

        ((MainActivity)getActivity()).setTitle("Prijava");
        return view;
    }

    @Override
    public void onAttach(Context context) {
         db = new BrokerSQLite(context);
         db.open();

        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }

    private void init(View view){
        emailEt = view.findViewById(R.id.emailEtLogin);
        passwordEt = view.findViewById(R.id.passwordEtLogin);
        loginBtn = view.findViewById(R.id.loginBtn);
        registerBtn = view.findViewById(R.id.registerBtn);
        forgotPassBtn = view.findViewById(R.id.forgotPassBtn);


        loginBtn.setOnClickListener(this);
        registerBtn.setOnClickListener(this);
        forgotPassBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.registerBtn: {

                RegisterFragment fragment = new RegisterFragment();
                fragmentTransaction2.addToBackStack("registerFragment");
                fragmentTransaction2.replace(R.id.myContainer, fragment);
                fragmentTransaction2.commit();

                break;
            }

            case R.id.loginBtn: {

                if(!Utility.isValidEmail(emailEt.getText().toString()) || TextUtils.isEmpty(emailEt.getText().toString())){
                    emailEt.setError(getString(R.string.error_message));
                }
                else if(TextUtils.isEmpty(passwordEt.getText().toString())){
                    passwordEt.setError(getString(R.string.error_message));
                } else{

                    if(ApplicationConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline())
                    loginCustomer();
                    else{
                      Utility.showNoInternetDialog(getActivity());
                    }

                }

                break;
            }

            case R.id.forgotPassBtn: {

                ForgotPasswordFragment fragment = new ForgotPasswordFragment();
                fragmentTransaction2.addToBackStack("forgotPassFragment");
                fragmentTransaction2.replace(R.id.myContainer, fragment);
                fragmentTransaction2.commit();

                break;
            }

        }
    }

    private void loginCustomer(){

        Customer customer  = new Customer();
        customer.setEmail(emailEt.getText().toString());
        customer.setPassword(passwordEt.getText().toString());
        apiInterface = ApiClient.getApiClient().create(Api.class);
        Call<List<Customer>> call = apiInterface.loginCustomer(customer);

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                Customer customer = response.body().get(0);

                if(customer.getCustomerID() != null){

                    db.customer_I(customer);

                    //EventBus salje customera svim subscriberima. U ovom slucaju, subscriber je main activity
                    //zato sto joj treba customer da postavi sliku i ime u navigation drawer.
                    EventBus.getDefault().post(customer);



                Toast.makeText(getActivity().getApplicationContext(), customer.getResponse(), Toast.LENGTH_SHORT).show();
                AllProductsFragment fragment = new AllProductsFragment();
                fragmentTransaction2.replace(R.id.myContainer, fragment);
                fragmentTransaction2.commit();
                ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.main_screen, "Proizvodi");
                } else{
                    Toast.makeText(getActivity().getApplicationContext(), customer.getResponse(), Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {

                Toast.makeText(getActivity().getApplicationContext(), "Došlo je do greške. Proverite internet konekciju.", Toast.LENGTH_SHORT).show();
            }
        });

    }
}
