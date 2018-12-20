package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidb2c.microbs.androidb2c.Activities.MainActivity;
import com.androidb2c.microbs.androidb2c.Adapter.CartAdapter;
import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.CartProduct;
import com.androidb2c.microbs.androidb2c.Model.Customer;
import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.RecyclerItemTouchHelper;
import com.androidb2c.microbs.androidb2c.Utility.RecyclerItemTouchHelperListener;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class CartFragment extends Fragment implements CartAdapter.UpdateTotalSum, RecyclerItemTouchHelperListener {

    private RecyclerView cartRecyclerView;
    private CartAdapter adapter;
    private List<Product> productList;
    private Api apiInterface;
    private BrokerSQLite db;
    private List<Integer> listOfIDs;
    private TextView totalSumTv;
    private Button orderBtn, cartCheckProductsBtn;
    private List<CartProduct> listOfCartProducts;
    private RelativeLayout cartContainer, emptyCartContainer, relativeLayout1;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.cart_fragment_layout, container, false);

        init(view);

        getProductsFromServer();
        ((MainActivity)getActivity()).setTitle("Korpa");

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new BrokerSQLite(context);
        db.open();
        listOfIDs = db.getCartProductIDs();
        listOfCartProducts = db.getCartProducts();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }

    private void init(View view){
      final  FragmentManager fragmentManager = getFragmentManager();


    cartRecyclerView = view.findViewById(R.id.cartRv);
    totalSumTv = view.findViewById(R.id.totalTv);
    orderBtn = view.findViewById(R.id.orderBtn);
    cartContainer = view.findViewById(R.id.cartContainer);
    emptyCartContainer = view.findViewById(R.id.emptyCartContainer);
    relativeLayout1 = view.findViewById(R.id.relativeLayout1);
    cartCheckProductsBtn = view.findViewById(R.id.cartCheckProductsBtn);

    setEmptyCartLayout();

    orderBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           Customer loggedCustomer = db.getCustomer();
            if(loggedCustomer != null) {
                //NASTAVI DO PORUCIVANJA

                Bundle bundle = new Bundle();
                String productListJson = new Gson().toJson(productList);
                String customerJson = new Gson().toJson(loggedCustomer);
                bundle.putString("prodList",productListJson);
                bundle.putString("loggedCustomer",customerJson);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            OrderPrepareFragment orderPrepareFragment = new OrderPrepareFragment();
            orderPrepareFragment.setArguments(bundle);
                fragmentTransaction.addToBackStack("cartFragment");
                ((MainActivity)getActivity()).setTitle("Podaci za dostavu");
                fragmentTransaction.replace(R.id.myContainer, orderPrepareFragment);
                fragmentTransaction.commit();


            } else{

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.myDialog);

                builder.setTitle("Pažnja");
                builder.setMessage("Samo prijavljeni korisnici mogu da poručuju. Da li želite da se prijavite?");
                builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LoginFragment fragment = new LoginFragment();

                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.myContainer, fragment);
                        transaction.commit();
                        ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.profile_screen, "Moj nalog");
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
        }
    });

    cartCheckProductsBtn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            AllProductsFragment fragment = new AllProductsFragment();
            transaction.replace(R.id.myContainer, fragment);
            transaction.commit();
            ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.main_screen, "Proizvodi");
        }
    });


    productList = new ArrayList<>();
    adapter = new CartAdapter(getActivity().getApplicationContext(), productList, this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divider));
        cartRecyclerView.addItemDecoration(dividerItemDecoration);
    cartRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
    cartRecyclerView.setHasFixedSize(true);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);

        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(cartRecyclerView);
    }

    private void getProductsFromServer(){

        apiInterface = ApiClient.getApiClient().create(Api.class);
        Call<List<Product>> call = apiInterface.getCartProducts(listOfIDs);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                productList = response.body();
//                for (int){
//                    for(CartProduct cartProduct : listOfCartProducts){
//
//
//                        if(product.getProductID() == cartProduct.getProductID()){
//                            product.setQuantity(cartProduct.getQuantity());
//                        }
//                    }
//                }

                for (int i = 0; i < productList.size() ; i++) {
                    for(CartProduct cartProduct : listOfCartProducts){
                        if(productList.get(i).getProductID() == cartProduct.getProductID() ){
                            productList.get(i).setQuantity(cartProduct.getQuantity());
                        }
                    }

                }
                adapter = new CartAdapter(getActivity().getApplicationContext(), productList, CartFragment.this);
                cartRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
                setSumTotal();
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

            }
        });

    }

    public void setSumTotal(){
        double totalSum = 0;

        for(Product product : productList){
            totalSum = totalSum + (product.getSellingPrice() * product.getQuantity());
        }
        totalSumTv.setText("UKUPNO:\n" + String.format("%.2f", totalSum) + " RSD");
    }

    @Override
    public void onQuantityChanged() {
        setSumTotal();
    }



    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof CartAdapter.ViewHolder){
            String name = productList.get(viewHolder.getAdapterPosition()).getProductName();

            final Product deletedProduct = productList.get(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();
                adapter.removeProduct(viewHolder.getAdapterPosition());

            Snackbar snackbar = Snackbar.make(cartContainer, name + " je obrisan!", Snackbar.LENGTH_LONG);
            snackbar.setAction("PONIŠTI", new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                        adapter.restoreProduct(deletedProduct, deleteIndex);
                }
            });
            snackbar.show();
        }
    }

    private void setEmptyCartLayout(){
        if(listOfIDs.isEmpty()){
            emptyCartContainer.setVisibility(View.VISIBLE);
            cartRecyclerView.setVisibility(View.GONE);
            relativeLayout1.setVisibility(View.GONE);
        }else {
            emptyCartContainer.setVisibility(View.GONE);
            cartRecyclerView.setVisibility(View.VISIBLE);
            relativeLayout1.setVisibility(View.VISIBLE);
        }

    }
}
