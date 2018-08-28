package com.jby.possystem.home;


import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.facebook.stetho.Stetho;
import com.jby.possystem.R;
import com.jby.possystem.database.CategoryDatabase;
import com.jby.possystem.home.category.CategoryAdapter;
import com.jby.possystem.home.category.CategoryObject;
import com.jby.possystem.home.category.dialog.CategoryLongClickDialog;
import com.jby.possystem.home.category.dialog.CreateCategoryDialog;
import com.jby.possystem.home.sub_category.SubCategoryAdapter;
import com.jby.possystem.home.sub_category.SubCategoryObject;
import com.jby.possystem.home.sub_category.dialog.CreateSubCategoryDialog;
import com.jby.possystem.home.sub_category.dialog.SubCategoryLongClickDialog;
import com.jby.possystem.others.CustomDividerItemDecoration;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,
        CreateCategoryDialog.CreateCategoryDialogCallBack, CategoryAdapter.CategoryAdapterCallBack, CategoryLongClickDialog.CategoryLongClickDialogCallBack,
        SubCategoryAdapter.SubCategoryAdapterCallBack, CreateSubCategoryDialog.CreateSubCategoryDialogCallBack,
        SubCategoryLongClickDialog.SubCategoryLongClickDialogCallBack
{

//    left hand side
//    right hand side
    RecyclerView mainActivityCategoryListView;
    CategoryAdapter categoryAdapter;
    ArrayList<CategoryObject> categoryObjectArrayList;
    LinearLayoutManager categoryListViewLayoutManager;
    DividerItemDecoration categoryListViewDivider;
    ImageView categoryCreateButton;
    private int categoryLongClickPosition;
    private int categoryFocusPosition = 0;

    RecyclerView mainActivitySubCategoryListView;
    SubCategoryAdapter subCategoryAdapter;
    ArrayList<SubCategoryObject> subCategoryObjectArrayList;
    GridLayoutManager subCategoryListViewLayoutManager;
    DividerItemDecoration subCategoryListViewDivider;
//    sqlite
    private CategoryDatabase categoryDatabase;
//    dialog
    private DialogFragment dialogFragment;
    private FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        objectInitialize();
        objectSetting();
    }

    private void objectInitialize() {
//        for debug purpose
        Stetho.initializeWithDefaults(this);
        fm = getSupportFragmentManager();
        //    left hand side
        //    right hand side

    /*-----------------------------------------------------category setting-----------------------------------------------------------*/
        categoryDatabase = new CategoryDatabase(this);
        mainActivityCategoryListView = findViewById(R.id.activity_main_right_hand_category_list_view);
        categoryCreateButton = findViewById(R.id.activity_main_right_hand_part_create_category_button);
        categoryObjectArrayList = new ArrayList<>();

        categoryListViewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false); //arrangement
        categoryListViewDivider = new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL); //divider
        categoryListViewDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_view_divider));
        /*-----------------------------------------------------end category setting-----------------------------------------------------------*/

        /*-----------------------------------------------------sub category setting-----------------------------------------------------------*/
        mainActivitySubCategoryListView = findViewById(R.id.activity_main_right_hand_sub_category_list_view);
        subCategoryObjectArrayList = new ArrayList<>();

        subCategoryListViewLayoutManager  = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
//        subCategoryListViewDivider.setDrawable(ContextCompat.getDrawable(this, R.drawable.recycler_view_divider));
        /*-----------------------------------------------------end sub category setting-----------------------------------------------------------*/

    }

    private void objectSetting() {
        //    left hand side
        //    right hand side
        mainActivityCategoryListView.setLayoutManager(categoryListViewLayoutManager);
        mainActivityCategoryListView.addItemDecoration(new CustomDividerItemDecoration(this, null));
        categoryCreateButton.setOnClickListener(this);

        mainActivitySubCategoryListView.setLayoutManager(subCategoryListViewLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();
        getCategoryList();
        setUpInitialSubCategoryList();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_main_right_hand_part_create_category_button:
                openCategoryDialog(1);
                break;
        }
    }
/*--------------------------------------------------------------------------------category purpose------------------------------------------------*/
    @Override
    public void updateCategoryList(String newCategoryName, String showStore) {
        String categoryID = categoryObjectArrayList.get(categoryLongClickPosition).getCategoryID();
        categoryObjectArrayList.set(categoryLongClickPosition, new CategoryObject(categoryID, newCategoryName, showStore));
        categoryAdapter.selectView(categoryLongClickPosition);
    }

    @Override
    public void getCategoryList(){
        categoryObjectArrayList = categoryDatabase.fetchAllCategory();
//        setup category list view
        categoryAdapter = new CategoryAdapter(categoryObjectArrayList, this, this);
        mainActivityCategoryListView.setAdapter(categoryAdapter);
        categoryAdapter.selectView(categoryFocusPosition);
    }

    public void openCategoryDialog(int status){
        dialogFragment = new CreateCategoryDialog();
        Bundle bundle = new Bundle();
        if(status == 1)
            bundle.putString("status", "create");
        else{
            bundle.putString("status", "update");
            bundle.putString("category_name", categoryObjectArrayList.get(categoryLongClickPosition).getCategoryName());
            bundle.putString("category_show_store", categoryObjectArrayList.get(categoryLongClickPosition).getCategoryStore());
            bundle.putString("category_id", categoryObjectArrayList.get(categoryLongClickPosition).getCategoryID());
        }


        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    public void openCategoryLongClickDialog(int clickPosition){
        categoryLongClickPosition = clickPosition;
        dialogFragment = new CategoryLongClickDialog();
        Bundle bundle = new Bundle();

        bundle.putString("category_id", categoryObjectArrayList.get(clickPosition).getCategoryID());
        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }
    /*--------------------------------------------------------end of category-------------------------------------------------------------------------*/

    /*---------------------------------------------------------sub category purpose---------------------------------------------------------------------*/
    private void setUpInitialSubCategoryList(){
        String showStore = "hide";

        if(categoryObjectArrayList.size() > 0){
            String categoryID = categoryObjectArrayList.get(0).getCategoryID();
            showStore = categoryObjectArrayList.get(0).getCategoryStore();
            subCategoryObjectArrayList = categoryDatabase.fetchAllSubCategory(categoryID);
            addCreateButton();
        }
//        setup subcategory list
        subCategoryAdapter = new SubCategoryAdapter(subCategoryObjectArrayList, this, this, showStore);
        mainActivitySubCategoryListView.setAdapter(subCategoryAdapter);

    }

    public void getSubCategoryList(int newPosition){
        String categoryID, showStore;
        if(newPosition == -1){
            // create && update action
            categoryID = categoryObjectArrayList.get(categoryAdapter.getCurrentFocusPosition()).getCategoryID();
            showStore = categoryObjectArrayList.get(categoryAdapter.getCurrentFocusPosition()).getCategoryStore();
        }
        else{
            //delete action
            categoryID = categoryObjectArrayList.get(newPosition).getCategoryID();
            showStore = categoryObjectArrayList.get(newPosition).getCategoryStore();
        }

        subCategoryObjectArrayList = categoryDatabase.fetchAllSubCategory(categoryID);
        //        setup subcategory list
        subCategoryAdapter = new SubCategoryAdapter(subCategoryObjectArrayList, this, this, showStore);
        mainActivitySubCategoryListView.setAdapter(subCategoryAdapter);
        addCreateButton();
    }

    public void openSubCategoryDialog(int position){
        String categoryID = categoryObjectArrayList.get(categoryAdapter.getCurrentFocusPosition()).getCategoryID();
        Bundle bundle = new Bundle();
        dialogFragment = new CreateSubCategoryDialog();
        if(position == -1){
            bundle.putString("status", "create");
        }
        else{
            bundle.putString("status", "update");
            bundle.putString("sub_category_id", subCategoryObjectArrayList.get(subCategoryAdapter.getCurrentFocusPosition()).getSubCategoryID());
            bundle.putString("sub_category_name", subCategoryObjectArrayList.get(subCategoryAdapter.getCurrentFocusPosition()).getSubCategoryName());
            bundle.putString("price", subCategoryObjectArrayList.get(subCategoryAdapter.getCurrentFocusPosition()).getSubCategoryPrice());
            bundle.putString("quantity", subCategoryObjectArrayList.get(subCategoryAdapter.getCurrentFocusPosition()).getSubCategoryQuantity());
        }
        bundle.putString("category_id", categoryID);

        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    public void openSubCategoryLongClickDialog(int position){
        String subCategoryID = subCategoryObjectArrayList.get(position).getSubCategoryID();
        dialogFragment = new SubCategoryLongClickDialog();

        Bundle bundle = new Bundle();
        bundle.putString("sub_category_id", subCategoryID);

        dialogFragment.setArguments(bundle);
        dialogFragment.show(fm, "");
    }

    private void addCreateButton(){
        subCategoryObjectArrayList.add(new SubCategoryObject("","", "","", true));
    }

    /*---------------------------------------------------------end of sub category-----------------------------------------------------------------------*/
}
