package com.androidb2c.microbs.androidb2c.Fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;


import com.androidb2c.microbs.androidb2c.Activities.MainActivity;
import com.androidb2c.microbs.androidb2c.Adapter.ProductAdapter;
import com.androidb2c.microbs.androidb2c.Adapter.SearchViewAdapter;
import com.androidb2c.microbs.androidb2c.Data.Api;
import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;
import com.androidb2c.microbs.androidb2c.Utility.BadgeDrawable;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.miguelcatalan.materialsearchview.SearchAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllProductsFragment extends Fragment  implements ProductAdapter.CartCounterInterface {


    private LayerDrawable icon;
    private int counter = 0;
    private RecyclerView productsRecyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private Api apiInterface;
    private BrokerSQLite db;
    private SearchView searchView;
   private AutoCompleteTextView searchAutoComplete;
   private ArrayList<String> listOfSuggestions;
   private ArrayAdapter<String> suggestionAdapter;



    private List<Integer> listOfIDs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.all_products_fragment_layout, container, false);
        setHasOptionsMenu(true);
        init(view);

        getProductsFromServer();


        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        db = new BrokerSQLite(context);
        db.open();
    counter = db.getNumberOfArticlesInCart();
        listOfIDs = new ArrayList<>();
        listOfIDs = db.getCartProductIDs();
        listOfSuggestions = db.getSuggestionsFromDb();
        suggestionAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, listOfSuggestions.toArray(new String[listOfSuggestions.size()]));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        db.close();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_screen_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);

        MenuItem itemCart = menu.findItem(R.id.cart);


         searchView = (SearchView) menu.findItem(R.id.menu_search_bar).getActionView();
         searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchAutoComplete.setThreshold(0);

        searchAutoComplete.setAdapter(suggestionAdapter);
        searchAutoComplete.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
             String filterText = (String)adapterView.getItemAtPosition(i);
             searchAutoComplete.setText(filterText);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                db.insertSuggestion(query.toLowerCase().trim());

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                    filter(newText.toLowerCase().trim());

                return false;
            }
        });





        itemCart.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                CartFragment fragment = new CartFragment();

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();

                transaction.replace(R.id.myContainer, fragment);
                transaction.commit();
                ((MainActivity)getActivity()).setNavDrawerAndTitle(R.id.cart_screen, "Korpa");
                return true;
            }
        });


         icon = (LayerDrawable) itemCart.getIcon();
        setBadgeCount(getActivity().getApplicationContext(), icon, String.valueOf(counter));
    }


    public static void setBadgeCount(Context context, LayerDrawable icon, String count) {

        BadgeDrawable badge;

        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.badge, badge);
    }

    private void init(View view){

        productsRecyclerView = view.findViewById(R.id.productsRecycleView);



        productList = new ArrayList<>();
        //OVDE DA DOBIJEM ARIKLE
        adapter = new ProductAdapter(getActivity().getApplicationContext(), productList, this);
        productsRecyclerView.setLayoutManager(new GridLayoutManager(getActivity().getApplicationContext(), 2));
        productsRecyclerView.setHasFixedSize(true);



    }



    private void getProductsFromServer(){
        apiInterface = ApiClient.getApiClient().create(Api.class);
        Call<List<Product>> call = apiInterface.getProducts();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {

                productList = response.body();
                for (int i = 0; i < productList.size() ; i++) {
            for(Integer id : listOfIDs){
                if(productList.get(i).getProductID() == (int)id){
                    productList.get(i).setAddedToCart(true);
                }
            }
        }
                adapter = new ProductAdapter(getActivity().getApplicationContext(), productList, AllProductsFragment.this);
                productsRecyclerView.setAdapter(adapter);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {

                Toast.makeText(getActivity().getApplicationContext(), "Došlo je do problema", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onProductAdded() {
        counter++;
        setBadgeCount(getActivity().getApplicationContext(), icon, String.valueOf(counter));
    }

    @Override
    public void onProductRemoved() {
        counter--;
        setBadgeCount(getActivity().getApplicationContext(), icon, String.valueOf(counter));
    }


    private void filter(String text) {

        List<Product> newList = new ArrayList<>();

        for (Product model : productList) {

            if (model.getProductName().toLowerCase().contains(text.toLowerCase())) {
                newList.add(model);
            }
        }
        adapter.filterList(newList);
    }
}
