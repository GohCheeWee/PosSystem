package com.jby.possystem.home.sub_category.dialog;

import java.io.Serializable;

public class CategoryTypeSpinnerObject{
    private String categoryID;
    private String categoryName;

    public CategoryTypeSpinnerObject(String categoryID, String categoryName) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    @Override
    public String toString() {
        return categoryName;
    }
}
