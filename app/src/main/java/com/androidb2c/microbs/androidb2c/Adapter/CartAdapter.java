package com.androidb2c.microbs.androidb2c.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Context context;
    private List<Product> cartProductList;
    private BrokerSQLite db;
    private UpdateTotalSum updateTotalSumInterface;


    public CartAdapter(Context context, List<Product> cartProductList, UpdateTotalSum updateTotalSumInterface) {
        this.context = context;
        this.cartProductList = cartProductList;
        this.updateTotalSumInterface = updateTotalSumInterface;
        db = new BrokerSQLite(context);
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item_layout, parent, false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {

        final Product product = cartProductList.get(position);
        viewHolder.productName.setText(product.getProductName());
        viewHolder.productPrice.setText(String.valueOf(product.getSellingPrice() + " RSD"));
        viewHolder.productDescription.setText(product.getDescription());
        viewHolder.quantity.setText(String.valueOf(product.getQuantity()));
        viewHolder.addQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.open();
                    db.setCartProductQuantity(product.getProductID(), product.getQuantity() + 1);
                    product.setQuantity(product.getQuantity()+1);
                    viewHolder.quantity.setText(String.valueOf(product.getQuantity()));
                    updateTotalSumInterface.onQuantityChanged();
                    db.close();
            }
        });

        viewHolder.subtractQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                db.open();
                if(product.getQuantity() <= 1) {
                    db.setCartProductQuantity(product.getProductID(), 1);
                    product.setQuantity(1);
                    viewHolder.quantity.setText(String.valueOf(product.getQuantity()));
                    updateTotalSumInterface.onQuantityChanged();
                }
                else {
                    db.setCartProductQuantity(product.getProductID(), product.getQuantity() - 1);
                    product.setQuantity(product.getQuantity()-1);
                    viewHolder.quantity.setText(String.valueOf(product.getQuantity()));
                    updateTotalSumInterface.onQuantityChanged();
                }
                db.close();
            }
        });
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
        viewHolder.deleteItemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteItemDialog(v, viewHolder.getAdapterPosition());

            }
        });



    }

    @Override
    public int getItemCount() {
        return cartProductList.size();
    }

    public void removeProduct(int position){
        db.open();
        db.deleteProductFromCart(cartProductList.get(position).getProductID());
        db.close();
        cartProductList.remove(position);
        notifyItemRemoved(position);
        updateTotalSumInterface.onQuantityChanged();
    }

    private void showDeleteItemDialog(View v, final int position){

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getRootView().getContext(), R.style.myDialog);

        builder.setTitle("Pažnja");
        builder.setMessage("Proizvod će biti obrisan iz korpe. Da li želite da nastavite?");
        builder.setPositiveButton("DA", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

               removeProduct(position);

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

    public void restoreProduct(Product product, int position){

        cartProductList.add(position, product);
        notifyItemInserted(position);

        db.open();
        db.insertProductAndQuantity(product.getProductID(), product.getQuantity());
        db.close();
        updateTotalSumInterface.onQuantityChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView productName;
        private TextView productPrice;
        private TextView productDescription;
        private ImageView productImage;
        private TextView addQuantity, subtractQuantity;
        private TextView quantity;
        private ImageView deleteItemBtn;
        public RelativeLayout foregroundView, backgroundView;
    //    private int count = cartProductList.get(getAdapterPosition()).getQuantity();


        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            productName = itemView.findViewById(R.id.orderProductTitle);
            productPrice = itemView.findViewById(R.id.orderProductPriceTv);
            productImage = itemView.findViewById(R.id.orderProductImage);
            productDescription = itemView.findViewById(R.id.orderProdDescTv);
            addQuantity = itemView.findViewById(R.id.addBtn);
            subtractQuantity = itemView.findViewById(R.id.subbBtn);
            quantity = itemView.findViewById(R.id.orderCounterTv);
            foregroundView = itemView.findViewById(R.id.view_foreground);
            backgroundView = itemView.findViewById(R.id.view_background);
            deleteItemBtn = itemView.findViewById(R.id.deleteItemBtn);

//
//            addQuantity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                //    count++;
//                    db.open();
//                  //  db.setCartProductQuantity(cartProductList.get(getAdapterPosition()).getProductID(), count);
//                    db.close();
//                //    quantity.setText(String.valueOf(count));
//                }
//            });
//
//            subtractQuantity.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
////                    if(count <= 1)
////                        count = 1;
////                    else
////                        count--;
//
//                    db.open();
//             //       db.setCartProductQuantity(cartProductList.get(getAdapterPosition()).getProductID(), count);
//                    db.close();
//
//               //     quantity.setText(String.valueOf(count));
//                }
//            });

        }
    }

    public interface UpdateTotalSum{
        void onQuantityChanged();

    }
}
