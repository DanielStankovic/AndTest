package com.androidb2c.microbs.androidb2c.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Data.BrokerSQLite;
import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private List<Product> productList;
    private CartCounterInterface cartCounterInterface;
    private BrokerSQLite db;

    private List<Integer> listOfIDs;

    public ProductAdapter(Context context, List<Product> productList, CartCounterInterface cartCounterInterface) {
        this.context = context;
        this.productList = productList;
        this.cartCounterInterface = cartCounterInterface;
        db = new BrokerSQLite(context);
//        db.open();
//        listOfIDs = new ArrayList<>();
//        listOfIDs = db.getCartProductIDs();
//        db.close();
//        for (int i = 0; i < productList.size() ; i++) {
//            for(Integer id : listOfIDs){
//                if(productList.get(i).getProductID() == (int)id){
//                    productList.get(i).setAddedToCart(true);
//                }
//            }
//        }


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        Product product = productList.get(position);

        if(product.isAddedToCart()){
            viewHolder.addToCartBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.added_to_cart_btn));
        }else {
            viewHolder.addToCartBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.add_to_cart_btn));
        }

        viewHolder.productName.setText(product.getProductName());
        viewHolder.productPrice.setText(String.valueOf(product.getSellingPrice() + " RSD"));
        if (product.getDiscount() != 0) {
            viewHolder.productDiscount.setText(String.valueOf(product.getDiscount()));
        } else {
            viewHolder.productDiscount.setVisibility(View.INVISIBLE);
        }

        if (product.isAddedToFav()) {
            viewHolder.addToFavBtn.setImageResource(R.drawable.baseline_favorite_24);
        } else {
            viewHolder.addToFavBtn.setImageResource(R.drawable.baseline_favorite_border_24);
        }



        Glide
                .with(context)
                .load(ApiClient.BASE_URL + "ProductImages/" + product.getProductImage())
                .apply(
                        new RequestOptions()
                                .placeholder(R.drawable.loading_image)
                                .error(R.drawable.no_image)
                                .fallback(R.drawable.no_image)
                )
                .transition(DrawableTransitionOptions.withCrossFade(500))
                .into(viewHolder.productImage);
    }


    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productName;
        private TextView productPrice;
        private TextView productDiscount;
        private ImageView addToFavBtn;
        private RelativeLayout addToCartBtn;
        private ImageView productImage;



        public ViewHolder(@NonNull final View itemView, Context ctx) {
            super(itemView);

            context = ctx;
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDiscount = itemView.findViewById(R.id.productDiscount);
            addToFavBtn = itemView.findViewById(R.id.add_to_fav_btn);
            addToCartBtn = itemView.findViewById(R.id.add_to_cart_btn);
            productImage = itemView.findViewById(R.id.productImageView);


          // final BrokerSQLite db = new BrokerSQLite(context);


            addToCartBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    db.open();
                    final int productID = productList.get(getAdapterPosition()).getProductID();
                    if (db.checkIfProductExistsInCart(productID)) {

                        Snackbar snackbar = Snackbar.make(itemView, "Ovaj proizvod vec postoji u korpi!", Snackbar.LENGTH_LONG);
                        snackbar.show();

                    } else {

                        db.addProductIDToCart(productID);
                       //Ovde povecati counter za Cart
                        addToCartBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.added_to_cart_btn));
                        cartCounterInterface.onProductAdded();
                        productList.get(getAdapterPosition()).setAddedToCart(true);
                        Snackbar snackbar = Snackbar.make(itemView, "Proizvod dodat u korpu!", Snackbar.LENGTH_LONG)
                                .setAction("PONIŠTI", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            db.open();
                                            db.removeProductFromCart(productID);
                                            cartCounterInterface.onProductRemoved();
                                            productList.get(getAdapterPosition()).setAddedToCart(false);
                                            Snackbar snackbar1 = Snackbar.make(itemView, "Proizvod je izbačen iz korpe!", Snackbar.LENGTH_LONG);
                                            snackbar1.show();
                                            addToCartBtn.setBackground(ContextCompat.getDrawable(context, R.drawable.add_to_cart_btn));
                                            db.close();
                                        }catch (Exception ex){

                                        }
                                    }
                                });

                        snackbar.show();
                    }
                    db.close();
                }

            });
        }
    }

    public interface CartCounterInterface{

        void onProductAdded();
        void onProductRemoved();

    }

    public void filterList (List<Product> list){

        productList = list;
        notifyDataSetChanged();
    }
}
