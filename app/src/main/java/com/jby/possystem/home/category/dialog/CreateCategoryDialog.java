package com.jby.possystem.home.category.dialog;


import android.app.Dialog;

import android.content.Context;
import android.graphics.Color;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;

import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.possystem.R;
import com.jby.possystem.database.CategoryDatabase;
import java.util.Objects;


public class CreateCategoryDialog extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    View rootView;
    TextView createCategoryDialogTitle, createCategoryDialogCancelButton, createCategoryDialogSaveButton;
    EditText createCategoryDialogName;
    SwitchCompat createCategoryDialogShowStore;
    String status;// determine whether is edit or create
    private CategoryDatabase categoryDatabase;// crud purpose
    private String showStore = "hide";
    private CreateCategoryDialogCallBack createCategoryDialogCallBack;
    private String initializeCategoryName, initializeShowStore; //determine wheather user change the name or not
    private String selectedID;

    public CreateCategoryDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main_category_dialog, container);
        Objects.requireNonNull(getDialog().getWindow()).getAttributes().windowAnimations = R.style.DialogAnimation;//animation purpose
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
        createCategoryDialogName.requestFocus();
        showKeyBoard();
    }

    private void objectInitialize() {
        createCategoryDialogTitle = rootView.findViewById(R.id.activity_main_category_dialog_label_dialog);
        createCategoryDialogCancelButton = rootView.findViewById(R.id.activity_main_category_dialog_button_cancel);
        createCategoryDialogSaveButton = rootView.findViewById(R.id.activity_main_category_dialog_button_confirm);

        createCategoryDialogName = rootView.findViewById(R.id.activity_main_category_dialog_category_name);
        createCategoryDialogShowStore = rootView.findViewById(R.id.activity_main_category_dialog_show_store);

        categoryDatabase = new CategoryDatabase(getActivity());
        createCategoryDialogCallBack = (CreateCategoryDialogCallBack)getActivity();
    }

    public void objectSetting(){
        Bundle bundle = getArguments();
        if(bundle != null){
            status = bundle.getString("status");
            assert status != null;

            if(status.equals("create"))
                createCategoryDialogTitle.setText(R.string.activity_main_category_dialog_label_create_category);
            else{
                createCategoryDialogTitle.setText(R.string.activity_main_category_dialog_label_edit_category);

                initializeCategoryName = bundle.getString("category_name");
                initializeShowStore = bundle.getString("category_show_store");
                showStore = initializeShowStore;
                selectedID = bundle.getString("category_id");

                assert initializeShowStore != null;
                initializeSetup(initializeCategoryName, initializeShowStore);
            }
        }

        createCategoryDialogSaveButton.setOnClickListener(this);
        createCategoryDialogCancelButton.setOnClickListener(this);
        createCategoryDialogShowStore.setOnCheckedChangeListener(this);
    }
/*-------------------------------------------------------------------create category purpose--------------------------------*/
    public void createCategory(String categoryName){
        int createStatus = categoryDatabase.createCategory(categoryName, showStore);
        switch (createStatus){
            case 1:
                Toast.makeText(getActivity(), "Create Successful!", Toast.LENGTH_SHORT).show();
                //when created succesful then get back the category id for getting the sub category
                String categoryID = categoryDatabase.getCategoryId(categoryName);

                createCategoryDialogCallBack.getCategoryList();
                createCategoryDialogCallBack.getSubCategoryList(-1);
                dismiss();
                break;
            case 2:
                Toast.makeText(getActivity(), "This category already existed!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(), "Failed to store this record!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public void checkingInput(){
        String categoryName = createCategoryDialogName.getText().toString().trim();
        if(!categoryName.equals(""))
            createCategory(categoryName);
        else
            Toast.makeText(getActivity(), "Category name can't be blank!", Toast.LENGTH_SHORT).show();
    }
    /*--------------------------------------------------------------end of create purpose----------------------------------------------------------*/

    /*------------------------------------------------------------update purpuse----------------------------------------------------------------------*/

    private void initializeSetup(String categoryName, String showStore){
        createCategoryDialogName.setText(categoryName);

        if(showStore.equals("show")){
            createCategoryDialogShowStore.setChecked(true);
        }
        else
            createCategoryDialogShowStore.setChecked(false);
    }

    private void checkingUpdateInput(){
        String categoryName = createCategoryDialogName.getText().toString().trim();
        if(!initializeCategoryName.equals(categoryName) || !showStore.equals(initializeShowStore)){

            updateCategory(categoryName, showStore);
        }
        else
        {
            dismiss();
        }
    }

    private void updateCategory(String categoryName, String showStore){
        int updateStatus = categoryDatabase.updateCategory(categoryName, selectedID, showStore);
        switch (updateStatus){
            case 1:
                Toast.makeText(getActivity(), "Update Successful!", Toast.LENGTH_SHORT).show();
                createCategoryDialogCallBack.updateCategoryList(categoryName, showStore);
                createCategoryDialogCallBack.getSubCategoryList(-1);
                dismiss();
                break;
            case 2:
                Toast.makeText(getActivity(), "This category already existed!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(), "Failed to update this record!", Toast.LENGTH_SHORT).show();
                break;
        }
    }


    /*--------------------------------------------------------------------end of update purpose -------------------------------------------*/

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.activity_main_category_dialog_button_confirm:
                if(status.equals("create"))
                    checkingInput();
                else
                    checkingUpdateInput();
                break;
            case R.id.activity_main_category_dialog_button_cancel:
                dismiss();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()){
            case R.id.activity_main_category_dialog_show_store:
                if (b)
                    showStore = "show";
                else
                    showStore = "hide";
                break;
        }
    }

    @Override
    public void dismiss() {
        closeKeyBoard();
        super.dismiss();
    }

    public void showKeyBoard(){
        final InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
    }

    public void closeKeyBoard(){
        final InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm != null)
            imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    public interface CreateCategoryDialogCallBack{
        void updateCategoryList(String newCategoryName, String showStore);
        void getCategoryList();
        void getSubCategoryList(int categoryPosition);
    }

}