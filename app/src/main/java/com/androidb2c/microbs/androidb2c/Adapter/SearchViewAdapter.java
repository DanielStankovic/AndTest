package com.androidb2c.microbs.androidb2c.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidb2c.microbs.androidb2c.Data.ApiClient;
import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

public class SearchViewAdapter extends RecyclerView.Adapter<SearchViewAdapter.ViewHolder> {

private List<Product> productList;
private Context context;


    public SearchViewAdapter(List<Product> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    public SearchViewAdapter() {
    }

    @NonNull
    @Override
    public SearchViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_view_item, parent, false);;

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewAdapter.ViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.productName.setText(product.getProductName());
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
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView productName;
        private ImageView productImage;
        public ViewHolder(View itemView) {
            super(itemView);

            productName = itemView.findViewById(R.id.searchViewProdName);
            productImage = itemView.findViewById(R.id.searchViewImg);
        }


    }
}
