package com.androidb2c.microbs.androidb2c.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidb2c.microbs.androidb2c.Model.Product;
import com.androidb2c.microbs.androidb2c.R;

import java.util.ArrayList;

public class ExpadableListCartAdapter extends BaseExpandableListAdapter {

    Context context;
    private ArrayList<String> groupTitleList;
    private ArrayList<Product> productList;

    public ExpadableListCartAdapter(Context context, ArrayList<Product> productList, ArrayList<String> groupTitleList) {
        this.groupTitleList = groupTitleList;
        this.context = context;
        this.productList = productList;
    }


    @Override
    public int getGroupCount() {
        return groupTitleList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return productList.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupTitleList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return productList.get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String title = (String)getGroup(groupPosition);
        if(convertView == null){

            convertView  = LayoutInflater.from(context).inflate(R.layout.expandable_list_cart_group, null);

        }
        ImageView listIndicator = convertView.findViewById(R.id.listIndicator);
        TextView textViewTitle = convertView.findViewById(R.id.listTitle);
        textViewTitle.setText(title);
        if(isExpanded){
            listIndicator.setImageResource(R.drawable.baseline_keyboard_arrow_up_24);
        }else{
            listIndicator.setImageResource(R.drawable.baseline_keyboard_arrow_down_24);
        }
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
      Product product = (Product) getChild(groupPosition, childPosition);
        if(convertView == null){

            convertView  = LayoutInflater.from(context).inflate(R.layout.expandable_list_cart_item, null);

        }
        TextView productName = convertView.findViewById(R.id.expndListProductName);
        TextView productQnt= convertView.findViewById(R.id.expndListProductQnt);
        TextView productPrice= convertView.findViewById(R.id.expndListProductPrice);
        productName.setText(product.getProductName());
        productQnt.setText("Količina: " + product.getQuantity());
        productPrice.setText("Cena: " + product.getQuantity() * product.getSellingPrice());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
