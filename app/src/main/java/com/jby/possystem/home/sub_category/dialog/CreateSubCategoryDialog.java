package com.jby.possystem.home.sub_category.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jby.possystem.R;
import com.jby.possystem.database.CategoryDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreateSubCategoryDialog extends DialogFragment implements View.OnClickListener {

    View rootView;
    TextView createSubCategoryLabel;
    EditText createSubCategoryName, createSubCategoryPrice, createSubCategoryQuantity;
    TextView createSubCategoryConfirm, createSubCategoryCancelButton, createSubCategoryAddNewButton;
    AppCompatSpinner createSubCategoryType;
    List<CategoryTypeSpinnerObject> categoryTypeSpinnerObjectList;
    LinearLayout createSubCategoryButtonLayout, createSubCategoryMainLayout;
    String subCategoryID, subCategoryName, price, quantity, status;

    private CategoryDatabase categoryDatabase;
    private String categoryID;

    private boolean somethingChange = false;

    public CreateSubCategoryDialogCallBack createSubCategoryDialogCallBack;

    public CreateSubCategoryDialog() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogStyle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_main_sub_category_dialog, container);
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
        createSubCategoryName.requestFocus();
        showKeyBoard();
    }

    @Override
    public void dismiss() {
        closeKeyBoard();
        super.dismiss();
    }

    private void objectInitialize() {
        createSubCategoryLabel = rootView.findViewById(R.id.activity_main_sub_category_dialog_label_dialog);

        createSubCategoryName = rootView.findViewById(R.id.activity_main_sub_category_dialog_sub_category_name);
        createSubCategoryPrice = rootView.findViewById(R.id.activity_main_sub_category_dialog_price);
        createSubCategoryQuantity = rootView.findViewById(R.id.activity_main_sub_category_dialog_quantity);

        createSubCategoryConfirm = rootView.findViewById(R.id.activity_main_sub_category_dialog_button_confirm);
        createSubCategoryCancelButton = rootView.findViewById(R.id.activity_main_sub_category_dialog_button_cancel);
        createSubCategoryAddNewButton = rootView.findViewById(R.id.activity_main_sub_category_dialog_button_add_new);

        createSubCategoryType = rootView.findViewById(R.id.activity_main_sub_category_dialog_type);

        createSubCategoryButtonLayout = rootView.findViewById(R.id.activity_main_sub_category_dialog_button_layout);
        createSubCategoryMainLayout = rootView.findViewById(R.id.activity_main_sub_category_dialog_main_layout);

        categoryDatabase = new CategoryDatabase(getActivity());
        createSubCategoryDialogCallBack = (CreateSubCategoryDialogCallBack)getActivity();
    }

    private void objectSetting() {
//        bundle setting
        Bundle bundle = getArguments();
        if(bundle != null){
            status = bundle.getString("status");
            categoryID = bundle.getString("category_id");
            assert status != null;

//           checking whether create or update action
            if(status.equals("create")){
                createSubCategoryLabel.setText(R.string.activity_main_category_dialog_label_create_sub_category);
            }
            else{
                Log.d("haha","Position:: update");
                createSubCategoryLabel.setText(R.string.activity_main_category_dialog_label_edit_sub_category);
                createSubCategoryAddNewButton.setVisibility(View.GONE);
                // configure the button style
                createSubCategoryConfirm.setBackground(getResources().getDrawable(R.drawable.custom_button_with_color_tifany_blue_and_ripple_effect));
                createSubCategoryConfirm.setTextColor(getResources().getColor(R.color.white));
                // store current value into variable
                subCategoryID = bundle.getString("sub_category_id");
                subCategoryName = bundle.getString("sub_category_name");
                price = bundle.getString("price");
                quantity = bundle.getString("quantity");
                //set up initial view
                setUpdateInitialView(subCategoryName, price, quantity);
            }
            setupSpinner();
        }

        createSubCategoryQuantity.setText("0");

        createSubCategoryConfirm.setOnClickListener(this);
        createSubCategoryCancelButton.setOnClickListener(this);
        createSubCategoryAddNewButton.setOnClickListener(this);
        calcualateHeight();
    }

    private void setUpdateInitialView(String subCategoryName, String price, String quantity) {
        createSubCategoryName.setText(subCategoryName);
        createSubCategoryPrice.setText(price);
        createSubCategoryQuantity.setText(quantity);
    }

    private void calcualateHeight(){
        ViewTreeObserver viewTreeObserver = createSubCategoryMainLayout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {

                    createSubCategoryMainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int parentLayoutHeight = createSubCategoryMainLayout.getHeight();
                    int parentLayoutWidth = createSubCategoryMainLayout.getWidth();

                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)createSubCategoryButtonLayout.getLayoutParams();
                    layoutParams.height = parentLayoutHeight;
                    layoutParams.width = parentLayoutWidth/20;

                    createSubCategoryButtonLayout.setLayoutParams(layoutParams);

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.activity_main_sub_category_dialog_button_cancel:
//                if make something changes then refresh the sub category list when closing the dialog
                if(somethingChange)
                    createSubCategoryDialogCallBack.getSubCategoryList(-1);
                dismiss();
                break;
            case R.id.activity_main_sub_category_dialog_button_confirm:
                if(status.equals("create"))
                    checkingCreateInput(true);
                else
                    checkingUpdateInput();
                break;
            case R.id.activity_main_sub_category_dialog_button_add_new:
                checkingCreateInput(false);
                break;
        }
    }

    private void checkingCreateInput(boolean closeDialog){
        String subCategoryName = createSubCategoryName.getText().toString().trim();
        String subCategoryPrice = createSubCategoryPrice.getText().toString().trim();
        String subCategoryQuantity = createSubCategoryQuantity.getText().toString().trim();
        if(subCategoryQuantity.equals(""))
            subCategoryQuantity = "0";

        if(!subCategoryName.equals("") && !subCategoryName.equals("")){
            createSubCategory(subCategoryName, subCategoryPrice, subCategoryQuantity, closeDialog);
        }
        else
            Toast.makeText(getActivity(), "Name and price is required!", Toast.LENGTH_SHORT).show();
    }

private void createSubCategory(String subCategoryName, String subCategoryPrice, String subCategoryQuantity, boolean closeDialog){
        int createSubCategory = categoryDatabase.createSubCategory(subCategoryName, subCategoryPrice, subCategoryQuantity, categoryID);
        switch (createSubCategory){
            case 1:
                Toast.makeText(getActivity(), "Item is created successful!", Toast.LENGTH_SHORT).show();
                if(closeDialog){
                    createSubCategoryDialogCallBack.getSubCategoryList(-1);
                    dismiss();
                }
                else
                    resetInput();

                somethingChange = true;
                break;
            case 2:
                Toast.makeText(getActivity(), "Item is already created!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(), "Failed to create this item!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void setupSpinner(){
        categoryTypeSpinnerObjectList = new ArrayList<>();
        categoryTypeSpinnerObjectList = categoryDatabase.getCategoryTypeSpinnerItem();

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, categoryTypeSpinnerObjectList);
        adapter.setDropDownViewResource(R.layout.activity_main_custom_spinner_item);
        createSubCategoryType.setAdapter(adapter);

        for(int i = 0 ; i<categoryTypeSpinnerObjectList.size(); i++){
            if(categoryTypeSpinnerObjectList.get(i).getCategoryID().equals(categoryID)){
                createSubCategoryType.setSelection(i);//set as default
                i = categoryTypeSpinnerObjectList.size();//finish the looping
            }
        }
    }
    private void checkingUpdateInput(){
        int position = createSubCategoryType.getSelectedItemPosition();

        String name = createSubCategoryName.getText().toString().trim();
        String subCategoryPrice = createSubCategoryPrice.getText().toString().trim();
        String subCategoryQuantity = createSubCategoryQuantity.getText().toString().trim();
        String categoryId = categoryTypeSpinnerObjectList.get(position).getCategoryID();

        if(!name.equals(subCategoryName) || !subCategoryPrice.equals(price) || !subCategoryQuantity.equals(quantity) || !categoryId.equals(categoryID)){
            updateSubCategory(name, subCategoryPrice, subCategoryQuantity, categoryId);
        }
        else{
            dismiss();
        }
    }

    private void updateSubCategory(String subCategoryName, String price, String quantity, String categoryID){
        int updateStatus = categoryDatabase.updateSubCategory(subCategoryName, subCategoryID, price, quantity, categoryID);
        switch (updateStatus){
            case 1:
                Toast.makeText(getActivity(), "Updated Successfully!", Toast.LENGTH_SHORT).show();
                createSubCategoryDialogCallBack.getSubCategoryList(-1);
                closeKeyBoard();
                break;
            case 2:
                Toast.makeText(getActivity(), "Item is already created!", Toast.LENGTH_SHORT).show();
                break;
            case 3:
                Toast.makeText(getActivity(), "Failed to update this item!", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void resetInput(){
        createSubCategoryName.setText("");
        createSubCategoryPrice.setText("");
        createSubCategoryName.requestFocus();
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

    public interface CreateSubCategoryDialogCallBack{
        void getSubCategoryList(int categoryPosition);
    }
}
