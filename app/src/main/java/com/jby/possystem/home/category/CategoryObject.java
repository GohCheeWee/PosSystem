package com.jby.possystem.home.category;

public class CategoryObject {
    private String categoryName;
    private String categoryStore;
    private String categoryID;

    public CategoryObject(String categoryID, String categoryName, String categoryStore) {
        this.categoryID = categoryID;
        this.categoryName = categoryName;
        this.categoryStore = categoryStore;
}

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryStore() {
        return categoryStore;
    }

    public String getCategoryID() {
        return categoryID;
    }
}
