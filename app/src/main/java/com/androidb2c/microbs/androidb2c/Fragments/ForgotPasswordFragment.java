package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.ApplicationConnectionStatus;
import com.androidb2c.microbs.androidb2c.Utility.Utility;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordFragment extends Fragment implements View.OnClickListener {

    private EditText emailEt, authCodeEt;
    private Button sendCodeBtn, applyCodeBtn;
    private Api apiInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.forgot_password_layout, container, false);
        init(view);

        showInfoDialog();


        return view;
    }

    private void init(View view){
        emailEt = view.findViewById(R.id.emailEtForgotPass);
        authCodeEt = view.findViewById(R.id.authTokenForgotPass);
        sendCodeBtn = view.findViewById(R.id.forgotPassSendBtn);
        applyCodeBtn = view.findViewById(R.id.forgotPassApplyBtn);
    }

    private void showInfoDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.myDialog);

        builder.setTitle("Obaveštenje");
        builder.setMessage("Unesite email koji ste koristili pri registraciji i kliknite na \"Pošalji kod\" kako biste dobili kod za resetovanje lozinke.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.forgotPassSendBtn: {

                if(!Utility.isValidEmail(emailEt.getText().toString()) || TextUtils.isEmpty(emailEt.getText().toString())){
                    emailEt.setError(getString(R.string.error_message));
                }else{

                    if(ApplicationConnectionStatus.getInstance(getActivity().getApplicationContext()).isOnline())
                        sendDataToServer(emailEt.getText().toString());
                    else
                        Utility.showNoInternetDialog(getActivity());


                }
                break;
            }

            default:
                break;
        }
    }

    private void sendDataToServer(String email){

        Customer customer  = new Customer();
        customer.setEmail(email);

        apiInterface = ApiClient.getApiClient().create(Api.class);
        Call<List<Customer>> call = apiInterface.getAuthCode(customer);

        call.enqueue(new Callback<List<Customer>>() {
            @Override
            public void onResponse(Call<List<Customer>> call, Response<List<Customer>> response) {
                Customer customer = response.body().get(0);

            }

            @Override
            public void onFailure(Call<List<Customer>> call, Throwable t) {

                Toast.makeText(getActivity().getApplicationContext(), "Došlo je do greške na serveru. Pokušajte ponovo kasnije.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
