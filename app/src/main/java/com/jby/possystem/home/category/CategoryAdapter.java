package com.jby.possystem.home.category;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.possystem.R;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {

    private ArrayList<CategoryObject> categoryObjectArrayList;
    private Activity context;
    private CategoryAdapterCallBack categoryAdapterCallBack;
    private SparseBooleanArray selectedItem;
    private int currentFocusPosition = 0;


    public CategoryAdapter(ArrayList<CategoryObject> categoryObjectArrayList, Activity context, CategoryAdapterCallBack categoryAdapterCallBack) {
        this.categoryObjectArrayList = categoryObjectArrayList;
        this.context = context;
        this.categoryAdapterCallBack = categoryAdapterCallBack;
        selectedItem = new  SparseBooleanArray();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_main_category_list_view_item, parent, false);
        // This code is used to get the screen dimensions of the user's device
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams)itemView.getLayoutParams();
        layoutParams.width = displayMetrics.widthPixels/8;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;;
//        int width = displayMetrics.widthPixels;
//        int height = displayMetrics.heightPixels;

        // Set the ViewHolder width to be a third of the screen size, and height to wrap content
//        itemView.setLayoutParams(new RecyclerView.LayoutParams(width/8, RecyclerView.LayoutParams.WRAP_CONTENT));
        itemView.setLayoutParams(layoutParams);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        CategoryObject object = getItem(position);
        holder.categoryName.setText(object.getCategoryName());
        holder.categoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectView(position);
                currentFocusPosition = position;
                categoryAdapterCallBack.getSubCategoryList(-1);
            }
        });

        holder.categoryName.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                categoryAdapterCallBack.openCategoryLongClickDialog(position);
                selectView(position);
                currentFocusPosition = position;
                return false;
            }
        });
        clickEffect(position, holder);
    }

    @Override
    public int getItemCount(){
        return categoryObjectArrayList.size();
    }

    public SparseBooleanArray getSelectedItem(){
        return selectedItem;
    }

    // Item checked on selection
    public void selectView(int position) {
        selectedItem.put(position, true);
        notifyDataSetChanged();
    }

    public int getCurrentFocusPosition(){
        return currentFocusPosition;
    }

    private void clickEffect(int position, MyViewHolder holder){
        if(selectedItem.get(position)){
            holder.categoryName.setBackgroundColor(ContextCompat.getColor(context, R.color.tiffany_blue));
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.white));
            selectedItem.clear();//if found selected position then clear the array
        }
        else{
            holder.categoryName.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
            holder.categoryName.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

    }

    public interface CategoryAdapterCallBack{
        void openCategoryLongClickDialog(int position);
        void getSubCategoryList(int categoryPosition);
    }

    private CategoryObject getItem(int i) {
        return categoryObjectArrayList.get(i);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
            TextView categoryName;

        private MyViewHolder(View view) {
            super(view);
            categoryName = view.findViewById(R.id.category_name);
        }
    }
}
