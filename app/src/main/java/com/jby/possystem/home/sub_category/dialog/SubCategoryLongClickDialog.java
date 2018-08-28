package com.jby.possystem.home.sub_category.dialog;


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


public class SubCategoryLongClickDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    private TextView subCategoryLongClickDialogEdit, subCategoryLongClickDialogDelete;
    private TextView subCategoryLongClickDialogCancel;
    private CategoryDatabase subCategoryDatabase;
    private SubCategoryLongClickDialogCallBack subCategoryLongClickDialogCallBack;
    private String subCategoryID;
    ;
    public SubCategoryLongClickDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main_sub_category_long_click_dialog, container);
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
        subCategoryLongClickDialogDelete = rootView.findViewById(R.id.activity_main_sub_category_long_click_dialog_delete);
        subCategoryLongClickDialogEdit = rootView.findViewById(R.id.activity_main_sub_category_long_click_dialog_edit);
        subCategoryLongClickDialogCancel = rootView.findViewById(R.id.activity_main_sub_category_long_click_dialog_cancel);

        subCategoryDatabase = new CategoryDatabase(getActivity());
        subCategoryLongClickDialogCallBack = (SubCategoryLongClickDialogCallBack)getActivity();
    }

    public void objectSetting(){
        Bundle bundle = getArguments();
        if(bundle != null){
            subCategoryID = bundle.getString("sub_category_id");
        }
        subCategoryLongClickDialogEdit.setOnClickListener(this);
        subCategoryLongClickDialogDelete.setOnClickListener(this);
        subCategoryLongClickDialogCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_main_sub_category_long_click_dialog_edit:
                subCategoryLongClickDialogCallBack.openSubCategoryDialog(1);
                dismiss();
                break;
            case R.id.activity_main_sub_category_long_click_dialog_delete:
                deleteDialog();
                break;
            case R.id.activity_main_sub_category_long_click_dialog_cancel:
                dismiss();
                break;
        }
    }

    public void deleteDialog(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Warning");
        builder.setMessage("Are you sure that you want to delete this item?");
        builder.setCancelable(true);

        builder.setPositiveButton(
                "Confirm",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        deleteSubCategory();
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

    private void deleteSubCategory(){
        boolean delelteStatus = subCategoryDatabase.deleteSubCategory(subCategoryID);
        if(delelteStatus){
            subCategoryLongClickDialogCallBack.getSubCategoryList(-1);
            Toast.makeText(getActivity(), "Delete Successfully!", Toast.LENGTH_SHORT).show();
            dismiss();
        }
        else
            Toast.makeText(getActivity(), "Failed to delete this record!", Toast.LENGTH_SHORT).show();
    }

    public interface SubCategoryLongClickDialogCallBack{
        void openSubCategoryDialog(int status);
        void getSubCategoryList(int categoryPosition);
    }
}