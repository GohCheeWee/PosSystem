package com.jby.possystem.home.sub_category;

import android.app.Activity;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jby.possystem.R;
import java.util.ArrayList;
import java.util.Locale;

public class SubCategoryAdapter extends RecyclerView.Adapter<SubCategoryAdapter.MyViewHolder> {

    private ArrayList<SubCategoryObject> subCategoryObjectArrayList;
    private Activity context;
    private SubCategoryAdapterCallBack subCategoryAdapterCallBack;
    private String showStore = "false";
    private int currentFocusPosition = 0;


    public SubCategoryAdapter(ArrayList<SubCategoryObject> subCategoryObjectArrayList, Activity context, SubCategoryAdapterCallBack subCategoryAdapterCallBack,
                              String showStore) {
        this.subCategoryObjectArrayList = subCategoryObjectArrayList;
        this.context = context;
        this.subCategoryAdapterCallBack = subCategoryAdapterCallBack;
        this.showStore = showStore;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_sub_category_list_view_item, parent, false);
        // This code is used to get the screen dimensions of the user's device
        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = context.getWindowManager().getDefaultDisplay();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)itemView.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels/7;

        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        if(displayMetrics.heightPixels > 1200)
            layoutParams.height = displayMetrics.heightPixels/7;
        else
            layoutParams.height = displayMetrics.heightPixels/5;


//        int width = displayMetrics.widthPixels;
//        int height = displayMetrics.heightPixels;

        // Set the ViewHolder width to be a third of the screen size, and height to wrap content
//        itemView.setLayoutParams(new RecyclerView.LayoutParams(width/8, RecyclerView.LayoutParams.WRAP_CONTENT));
        itemView.setLayoutParams(layoutParams);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        SubCategoryObject object = getItem(position);

        if(object.isSubCategoryIsLast()){
            holder.add.setVisibility(View.VISIBLE);
            holder.mainLayout.setVisibility(View.GONE);

            holder.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    subCategoryAdapterCallBack.openSubCategoryDialog(-1);
                }
            });
        }
        else{
            holder.add.setVisibility(View.GONE);
            holder.mainLayout.setVisibility(View.VISIBLE);

            holder.foodName.setText(object.getSubCategoryName());
            holder.quantity.setText(object.getSubCategoryQuantity());

            double price = Double.parseDouble(object.getSubCategoryPrice());
            holder.price.setText(decimalSetting(price));

            if(showStore.equals("hide")){
                holder.quantity.setVisibility(View.INVISIBLE);
                holder.price.setVisibility(View.INVISIBLE);

            }
            else{
                holder.quantity.setVisibility(View.VISIBLE);
                holder.price.setVisibility(View.VISIBLE);
            }

            holder.mainLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

            holder.mainLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    subCategoryAdapterCallBack.openSubCategoryLongClickDialog(position);
                    currentFocusPosition = position;
                    return false;
                }
            });
        }
    }

    private String decimalSetting(double value) {
        String value1 = "0";
        if (value == (double) Math.round(value)) {
            if (value / 1000000000 > 1.0) {
                value1 = String.format(Locale.getDefault(),"%.1f G", value / 1000000000);
            } else if (value / 1000000 > 1.0) {
                value1 = String.format(Locale.getDefault(),"%.1f M", value / 1000000);
            } else if (value / 1000 > 1.0) {
                value1 = String.format(Locale.getDefault(),"%.1f K", value / 1000);
            } else {
                value1 = String.format(Locale.getDefault(),"%.1f", value);
                }
            } else {
                value1 = String.format(Locale.getDefault(),"%.2f", value);
            }
            return value1;
    }

    @Override
    public int getItemCount(){
        return subCategoryObjectArrayList.size();
    }

    public int getCurrentFocusPosition(){
        return currentFocusPosition;
    }

    public interface SubCategoryAdapterCallBack{
        void openSubCategoryDialog(int position);
        void openSubCategoryLongClickDialog(int position);
    }


    private SubCategoryObject getItem(int i) {
        return subCategoryObjectArrayList.get(i);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView foodName, quantity, price;
        ImageView add;
        LinearLayout mainLayout, parentLayout;

        private MyViewHolder(View view) {
            super(view);
            foodName = view.findViewById(R.id.activity_main_sub_category_list_view_item_name);
            quantity = view.findViewById(R.id.activity_main_sub_category_list_view_item_quantity);
            price = view.findViewById(R.id.activity_main_sub_category_list_view_item_price);
            add = view.findViewById(R.id.activity_main_sub_category_list_view_item_add_button);
            mainLayout = view.findViewById(R.id.activity_main_sub_category_list_view_item_main_layout);
        }
    }
}
