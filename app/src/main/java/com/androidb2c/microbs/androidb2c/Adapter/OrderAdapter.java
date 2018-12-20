package com.androidb2c.microbs.androidb2c.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidb2c.microbs.androidb2c.Model.CustomerOrder;
import com.androidb2c.microbs.androidb2c.R;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Context context;
    private List<CustomerOrder> customerOrderList;

    public OrderAdapter(){

    }

    public OrderAdapter(Context context, List<CustomerOrder> customerOrderList){
        this.context = context;
        this.customerOrderList = customerOrderList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_preview_item, parent, false);
       return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        CustomerOrder order = customerOrderList.get(position);

        holder.orderPreviewOrderNoTv.setText(order.getOrderID());
        holder.orderPreviewSumTotal.setText(order.getSumTotal() + " RSD");
        if(order.getOrderStatus() == 1){
            holder.orderPreviewOrderStatusTv.setText("U pripremi");
            holder.orderPreviewOrderStatusTv.setTextColor(ContextCompat.getColor(context, R.color.text_orange));
        } else if(order.getOrderStatus() == 2){
            holder.orderPreviewOrderStatusTv.setText("Poslato");
            holder.orderPreviewOrderStatusTv.setTextColor(ContextCompat.getColor(context, R.color.text_yellow));
        }


    }

    @Override
    public int getItemCount() {
        return customerOrderList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView orderPreviewOrderNoTv, orderPreviewSumTotal, orderPreviewOrderStatusTv;

        public ViewHolder(View itemView) {
            super(itemView);
            orderPreviewOrderNoTv = itemView.findViewById(R.id.orderPreviewOrderNoTv);
            orderPreviewSumTotal = itemView.findViewById(R.id.orderPreviewSumTotal);
            orderPreviewOrderStatusTv = itemView.findViewById(R.id.orderPreviewOrderStatusTv);

        }
    }
}
