package com.jby.possystem.home.category.dialog;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.possystem.R;
import com.jby.possystem.database.CategoryDatabase;

import java.util.Objects;


public class CategoryLongClickDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    private TextView categoryLongClickDialogEdit, categoryLongClickDialogDelete;
    private TextView categoryLongClickDialogCancel;
    private CategoryDatabase categoryDatabase;
    private CategoryLongClickDialogCallBack categoryLongClickDialogCallBack;
    private String categoryID;
    ;
    public CategoryLongClickDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main_category_long_click_dialog, container);
        objectInitialize();
        objectSetting();
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog d = getDialog();
        if (d != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;

            Objects.requireNonNull(d.getWindow()).setLayout(width, height);
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    private void objectInitialize() {
        categoryLongClickDialogDelete = rootView.findViewById(R.id.activity_main_category_long_click_dialog_delete);
        categoryLongClickDialogEdit = rootView.findViewById(R.id.activity_main_category_long_click_dialog_edit);
        categoryLongClickDialogCancel = rootView.findViewById(R.id.activity_main_category_long_click_dialog_cancel);

        categoryDatabase = new CategoryDatabase(getActivity());
        categoryLongClickDialogCallBack = (CategoryLongClickDialogCallBack)getActivity();
    }

    public void objectSetting(){
        Bundle bundle = getArguments();
        if(bundle != null){
            categoryID = bundle.getString("category_id");
        }
        categoryLongClickDialogEdit.setOnClickListener(this);
        categoryLongClickDialogDelete.setOnClickListener(this);
        categoryLongClickDialogCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_main_category_long_click_dialog_edit:
                dismiss();
                categoryLongClickDialogCallBack.openCategoryDialog(2);
                break;
            case R.id.activity_main_category_long_click_dialog_delete:
                deleteDialog();
                break;
            case R.id.activity_main_category_long_click_dialog_cancel:
                dismiss();
                break;
        }
    }

    public void deleteDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning");
        builder.setMessage("Are you sure that you want to delete this category?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteCategory();
                        dialog.cancel();
                    }
                });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void deleteCategory(){
        boolean delelteStatus = categoryDatabase.deleteCategory(categoryID);
        if(delelteStatus){
            categoryLongClickDialogCallBack.getCategoryList();
            categoryLongClickDialogCallBack.getSubCategoryList(0);
            Toast.makeText(getActivity(), "Delete Successfully!", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        else
            Toast.makeText(getActivity(), "Failed to delete this record!", Toast.LENGTH_SHORT).show();

    }

    public interface CategoryLongClickDialogCallBack{
        void openCategoryDialog(int status);
        void getCategoryList();
        void getSubCategoryList(int categoryPosition);
    }
}