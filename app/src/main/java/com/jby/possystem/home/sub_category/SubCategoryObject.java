package com.jby.possystem.home.sub_category;

public class SubCategoryObject {
    private String subCategoryID;
    private String subCategoryName;
    private String subCategoryQuantity;
    private String subCategoryPrice;
    private boolean subCategoryIsLast;


    public SubCategoryObject(String subCategoryID, String subCategoryName, String subCategoryQuantity, String subCategoryPrice, boolean subCategoryIsLast) {
        this.subCategoryID = subCategoryID;
        this.subCategoryName = subCategoryName;
        this.subCategoryQuantity = subCategoryQuantity;
        this.subCategoryPrice = subCategoryPrice;
        this.subCategoryIsLast = subCategoryIsLast;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getSubCategoryQuantity() {
        return subCategoryQuantity;
    }

    public String getSubCategoryPrice() {
        return subCategoryPrice;
    }

    public boolean isSubCategoryIsLast() {
        return subCategoryIsLast;
    }

    public String getSubCategoryID() {
        return subCategoryID;
    }
}
