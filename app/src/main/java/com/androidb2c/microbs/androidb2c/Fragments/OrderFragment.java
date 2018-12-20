package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidb2c.microbs.androidb2c.Activities.MainActivity;
import com.androidb2c.microbs.androidb2c.Adapter.OrderAdapter;
import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.Model.CustomerOrder;
import com.androidb2c.microbs.androidb2c.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderFragment extends Fragment {

    private BrokerSQLite db;
    private String customerID = "";
    private List<CustomerOrder> customerOrderList;
    private RecyclerView orderRv;
    private RelativeLayout emptyCartOrderContainer, orderPreviewContainer;
    private Button orderCheckProductsBtn;
    private TextView orderEmptyCartLbl;
    private final int DIALOG_TYPE_LOGIN = 0;
    private final int DIALOG_TYPE_ERROR = 1;
    private FragmentManager fragmentManager;
    private OrderAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.order_fragment_layout, container, false);

        fragmentManager = getFragmentManager();

        if(TextUtils.isEmpty(customerID)){
         showInfoDialog(DIALOG_TYPE_LOGIN);

        }else {

            init(view);
            getCustomerOrdersFromServer();

        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new BrokerSQLite(context);
        db.open();
        Customer customer = db.getCustomer();
        if(customer!= null) {
            customerID = customer.getCustomerID();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }


    private void init(View view){
        orderRv = view.findViewById(R.id.orderRv);
        emptyCartOrderContainer = view.findViewById(R.id.emptyCartOrderContainer);
        orderCheckProductsBtn = view.findViewById(R.id.orderCheckProductsBtn);
        orderEmptyCartLbl = view.findViewById(R.id.orderEmptyCartLbl);
        orderPreviewContainer = view.findViewById(R.id.orderPreviewContainer);
        customerOrderList = new ArrayList<>();


        orderCheckProductsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AllProductsFragment fragment = new AllProductsFragment();
                transaction.replace(R.id.myContainer, fragment);
                transaction.commit();
                ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.main_screen, "Proizvodi");
            }
        });
    }
    private void getCustomerOrdersFromServer(){

        Api  apiInterface = ApiClient.getApiClient().create(Api.class);
        Call<List<CustomerOrder>> call = apiInterface.getCustomerOrders(customerID);
        call.enqueue(new Callback<List<CustomerOrder>>() {
            @Override
            public void onResponse(Call<List<CustomerOrder>> call, Response<List<CustomerOrder>> response) {
                customerOrderList = response.body();

                if(customerOrderList.isEmpty()){
                    //nema ni jedna porudzbina za ovog kupca
                    setEmptyOrderLayout();

                }else{
                    if(customerOrderList.get(0).getOrderStatus() == 99){
                        showInfoDialog(DIALOG_TYPE_ERROR);
                        //puklo je nesto na serveru
                    }else{
                        //sve je ok i ima porudzbina za kupca
                     adapter = new OrderAdapter(getActivity().getApplicationContext(), customerOrderList);
                     orderRv.setAdapter(adapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
                        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
                        orderRv.addItemDecoration(dividerItemDecoration);
                     orderRv.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                     orderRv.setHasFixedSize(true);
                    }

                }


            }

            @Override
            public void onFailure(Call<List<CustomerOrder>> call, Throwable t) {

            }
        });
    }

    private void showInfoDialog(int dialogType){

       final FragmentTransaction transaction = fragmentManager.beginTransaction();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.myDialog);

        builder.setTitle("Pažnja");
        if(dialogType == 0) {
            builder.setMessage("Samo prijavljeni korisnici mogu da pregledaju porudžbine. Prijavite se kako biste videli svoje porudžbine.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LoginFragment fragment = new LoginFragment();

                    transaction.replace(R.id.myContainer, fragment);
                    transaction.commit();
                    ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.profile_screen, "Moj nalog");

                }
            });
        }else {
            builder.setMessage("Došlo je do greške na serveru.Pokušajte ponovo kasnije.");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    AllProductsFragment fragment = new AllProductsFragment();

                    transaction.replace(R.id.myContainer, fragment);
                    transaction.commit();
                    ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.profile_screen, "Proizvodi");

                }
            });
        }

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setEmptyOrderLayout(){
        orderPreviewContainer.setBackgroundColor(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.white));
        orderRv.setVisibility(View.GONE);
        emptyCartOrderContainer.setVisibility(View.VISIBLE);
        orderCheckProductsBtn.setVisibility(View.VISIBLE);
        orderEmptyCartLbl.setText("Nemate aktivnih porudžbina!");
        orderEmptyCartLbl.setVisibility(View.VISIBLE);




    }
}
