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
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


import com.androidb2c.microbs.androidb2c.Activities.MainActivity;
import com.androidb2c.microbs.androidb2c.Adapter.ExpadableListCartAdapter;
import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.Model.Order;
import com.androidb2c.microbs.androidb2c.Model.OrderDetails;
import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.Utility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderPrepareFragment extends Fragment {
    private EditText prepOrderCustNameEt, prepOrderCustLastNameEt, prepOrderCustEmailEt, prepOrderAddressEt, prepOrderPhoneEt,
            prepOrderZipCodeEt, prepOrderCityEt;
    private CheckBox prepOrderTermOfOrderCb;
    private TextView prepOrderTermOfOrderTv, prepareOrderTotalTv;
    private Button sendOrderBtn;
    private ExpandableListView expandableListViewCart;
    private ArrayList<Product> orderPrepareProductList;
    private ArrayList<String> groupListTitle;
    private Customer loggedCustomer;
    private ExpadableListCartAdapter adapter;
    private ScrollView containerView;
    private Api apiInterface;
    private BrokerSQLite db;
    String orderID;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_prepare_fragment_layout, container, false);

        init(view);

        return view;


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

    private void init(View view){

        prepOrderCustNameEt = view.findViewById(R.id.prepOrderCustNameEt);
        prepOrderCustLastNameEt = view.findViewById(R.id.prepOrderCustLastNameEt);
        prepOrderCustEmailEt = view.findViewById(R.id.prepOrderCustEmailEt);
        prepOrderAddressEt = view.findViewById(R.id.prepOrderAddressEt);
        prepOrderPhoneEt = view.findViewById(R.id.prepOrderPhoneEt);
        prepOrderZipCodeEt = view.findViewById(R.id.prepOrderZipCodeEt);
        prepOrderCityEt = view.findViewById(R.id.prepOrderCityEt);
        prepOrderTermOfOrderCb = view.findViewById(R.id.prepOrderTermOfOrderCb);
        prepOrderTermOfOrderTv = view.findViewById(R.id.prepOrderTermOfOrderTv);
        expandableListViewCart = view.findViewById(R.id.expandableListViewCart);
        containerView = view.findViewById(R.id.scrollView1);
        prepareOrderTotalTv = view.findViewById(R.id.prepareOrderTotalTv);
        sendOrderBtn = view.findViewById(R.id.sendOrderBtn);

        orderPrepareProductList = new ArrayList<>();
        groupListTitle = new ArrayList<>();
        groupListTitle.add(getResources().getString(R.string.cart_content));

        Bundle bundle = getArguments();
        String prodListJson = bundle.getString("prodList");
        String customerJson = bundle.getString("loggedCustomer");
        orderPrepareProductList = new Gson().fromJson(prodListJson, new TypeToken<List<Product>>(){}.getType());
        loggedCustomer = new Gson().fromJson(customerJson, Customer.class);
        setLoggedCustomer(loggedCustomer);
        adapter = new ExpadableListCartAdapter(getActivity().getApplicationContext(), orderPrepareProductList, groupListTitle);
        expandableListViewCart.setAdapter(adapter);

        prepOrderTermOfOrderTv.setText(Html.fromHtml("Prihvatam <u>uslove prodaje</u>"));
        setSumTotal();


        prepOrderTermOfOrderCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                prepOrderTermOfOrderCb.setError(null);
            }
        });

        expandableListViewCart.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                containerView.setVisibility(View.GONE);
            }
        });

        expandableListViewCart.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {
                containerView.setVisibility(View.VISIBLE);
            }
        });

        sendOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkInputInfo())

               showConfirmationDialog();
            }
        });

    }

    private void setLoggedCustomer(Customer customer){

        String[] customerName = customer.getFullName().trim().split(" ");
        if(customerName.length >= 2) {
            prepOrderCustNameEt.setText(customerName[0]);
            prepOrderCustLastNameEt.setText(customerName[1]);
        }else{
            prepOrderCustNameEt.setText(customer.getFullName());
        }
        prepOrderCustEmailEt.setText(customer.getEmail());
        prepOrderAddressEt.setText(customer.getAddress());
        prepOrderPhoneEt.setText(customer.getPhoneNum());
        prepOrderCityEt.setText(customer.getCity());
    }

    private void setSumTotal(){
        double totalSum = 0;

        for(Product product : orderPrepareProductList){
            totalSum = totalSum + (product.getSellingPrice() * product.getQuantity());
        }
        prepareOrderTotalTv.setText("UKUPNO:\n" + String.format("%.2f", totalSum) + " RSD");
    }

    private void sendOrderToServer(){
        orderID = Utility.generateOrderID(loggedCustomer.getCustomerID());
        ArrayList<OrderDetails> orderDetailsArrayList = getOrderDetails(orderPrepareProductList, orderID);
        String orderDetailsList = new Gson().toJson(orderDetailsArrayList);
        Order order = new Order(orderID, loggedCustomer.getCustomerID(), prepOrderCustEmailEt.getText().toString(),
                prepOrderCustNameEt.getText().toString(),
                prepOrderCustLastNameEt.getText().toString(),
                prepOrderAddressEt.getText().toString(),
                prepOrderPhoneEt.getText().toString(),
                prepOrderCityEt.getText().toString(),
                prepOrderZipCodeEt.getText().toString(),
                orderDetailsList);
        apiInterface = ApiClient.getApiClient().create(Api.class);
        Call<List<String>> call = apiInterface.sendOrder(order);
        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                String responseString = "";
                responseString = response.body().get(0);
                if(TextUtils.equals(responseString, "success")){
                    showOrderResponseInfoDialog("Uspešno ste naručili proizvode. Očekujte email sa potvrdom porudžbine.");
                    db.deleteAllFromCart();
                } else if (TextUtils.equals(responseString, "") || TextUtils.equals(responseString, "failed")){
                    showOrderResponseInfoDialog("Došlo je do greške. Pokušajte ponovo kasnije.");
                }

            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                showOrderResponseInfoDialog("Došlo je do greške. Pokušajte ponovo kasnije.");
            }
        });


    }

    private boolean checkInputInfo(){
        if(!Utility.isValidEmail(prepOrderCustEmailEt.getText().toString()) || TextUtils.isEmpty(prepOrderCustEmailEt.getText().toString())){
            prepOrderCustEmailEt.setError(getString(R.string.error_message));
        }else if(!TextUtils.equals(prepOrderCustEmailEt.getText().toString(), loggedCustomer.getEmail())){
            prepOrderCustEmailEt.setError("Uneta email adresa je različita od korisničke adrese.");
        }else if(TextUtils.isEmpty(prepOrderCustNameEt.getText().toString())){
            prepOrderCustNameEt.setError(getString(R.string.error_message));
        }else if(TextUtils.isEmpty(prepOrderCustLastNameEt.getText().toString())){
            prepOrderCustLastNameEt.setError(getString(R.string.error_message));
        }else if(TextUtils.isEmpty(prepOrderAddressEt.getText().toString())){
            prepOrderAddressEt.setError(getString(R.string.error_message));
        }else if(TextUtils.isEmpty(prepOrderPhoneEt.getText().toString())){
            prepOrderPhoneEt.setError(getString(R.string.error_message));
        }else if(TextUtils.isEmpty(prepOrderZipCodeEt.getText().toString()) || prepOrderZipCodeEt.getText().toString().length() != 5 ){
            prepOrderZipCodeEt.setError(getString(R.string.error_message));
        }else if(TextUtils.isEmpty(prepOrderCityEt.getText().toString())){
            prepOrderCityEt.setError(getString(R.string.error_message));
        } else if(!prepOrderTermOfOrderCb.isChecked()){

            prepOrderTermOfOrderCb.setError("Morate prihvatiti uslove prodaje.");

        }else{
            return true;
        }
        return false;
    }

    private ArrayList<OrderDetails> getOrderDetails(ArrayList<Product> productList, String orderID){
        ArrayList<OrderDetails> list = new ArrayList<>();

        for(Product product : productList){
        OrderDetails orderDetails = new OrderDetails(orderID, product.getProductID(), product.getQuantity(), product.getSellingPrice(), product.getDiscount());
        list.add(orderDetails);
        }

        return list;
    }

    private void showConfirmationDialog(){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.myDialog);

        builder.setTitle("Pažnja");
        builder.setMessage("Da li ste sigurni da želite da izvršite porudbinu u iznosu od " + prepareOrderTotalTv.getText().toString());
        builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               sendOrderToServer();
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
    }

    private void showOrderResponseInfoDialog(String orderResponseInfo){

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.myDialog);

        builder.setTitle("Pažnja");
        builder.setMessage(orderResponseInfo);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AllProductsFragment fragment = new AllProductsFragment();
                transaction.replace(R.id.myContainer, fragment);
                transaction.commit();
                ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.main_screen, "Proizvodi");
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


}
