package com.jby.possystem.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;

import com.jby.possystem.home.category.CategoryObject;
import com.jby.possystem.home.sub_category.SubCategoryObject;
import com.jby.possystem.home.sub_category.dialog.CategoryTypeSpinnerObject;
import com.jby.possystem.sharePreference.SharedPreferenceManager;

import java.util.ArrayList;
import java.util.List;

public class CategoryDatabase extends SQLiteOpenHelper {
//    database
    private static final String DATABASE_NAME = "CategoryDatabase";
//    version
    private static final int DATABASE_VERSION = 1;
//    table name
    private static final String TB_CATEGORY = "tb_category";
    private static final String TB_SUB_CATEGORY = "tb_sub_category";
//    table tb_category entity
    private static final String Category_ID = "category_id";
    private static final String Category_Name = "category_name";
    private static final String Created_At = "created_at";
    private static final String Updated_At = "updated_at";
    private static final String Show_Store = "show_store";
    private static final String USER_ID = "user_id";
    private static final String PRIORITY = "priority";
//    table tb_sub_category entity
    private static final String Sub_Category_ID = "sub_category_id";
    private static final String Sub_Category_Name = "sub_category_name";
    private static final String Sub_Category_Price = "sub_category_price";
    private static final String Sub_Category_Quantity = "sub_category_store";

//    create tb_category
    private static final String CREATE_TABLE_CATEGORY = "CREATE TABLE "+ TB_CATEGORY +
        "(" + Category_ID + " INTEGER PRIMARY KEY, " +
        Category_Name+ " Text, "+
        USER_ID+ " Text, "+
        Created_At+ " Text, "+
        Updated_At+ " Text, "+
        Show_Store+ " Text, "+
        PRIORITY+ " Text "+
        ")";

    //    create tb_sub_category
    private static final String CREATE_TABLE_SUB_CATEGORY = "CREATE TABLE "+ TB_SUB_CATEGORY +
            "(" + Sub_Category_ID + " INTEGER PRIMARY KEY, " +
            Category_ID + " Text, "+
            USER_ID+ " Text, "+
            Sub_Category_Name+ " Text, "+
            Sub_Category_Price+ " Text, "+
            Sub_Category_Quantity+ " Text, "+
            Created_At+ " Text, "+
            Updated_At+ " Text, "+
            PRIORITY+ " Text "+
            ")";

    private Context context;

    public CategoryDatabase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_CATEGORY);
        sqLiteDatabase.execSQL(CREATE_TABLE_SUB_CATEGORY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TB_CATEGORY);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TB_SUB_CATEGORY);
    }

    public int createCategory(String categoryName, String showStore) {
        String userID = SharedPreferenceManager.getUserID(context);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        int status = 3;
        String timeStamp = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()));
        String priority = String.valueOf(android.text.format.DateFormat.format("yyMMddHHmmss", new java.util.Date()));

        boolean isCategoryExisted = checkCategoryIsExisted(categoryName, userID);
//        if existed
        if(isCategoryExisted){
            status = 2;
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(Category_Name, categoryName);
            contentValues.put(USER_ID, userID);
            contentValues.put(Show_Store, showStore);
            contentValues.put(Created_At, timeStamp);
            contentValues.put(PRIORITY, priority);
//            store new record
            result = db.insert(TB_CATEGORY, null, contentValues);
//            if success
            if(result != -1)
                status = 1;

        }
        return status;

    }

    public ArrayList<CategoryObject> fetchAllCategory() {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<CategoryObject> results = new ArrayList<>();
        String userID = SharedPreferenceManager.getUserID(context);

        String sql = "SELECT "+ Category_ID + "," + Category_Name + "," + Show_Store +
                " FROM " + TB_CATEGORY +
                " WHERE " + USER_ID + "= ?" +
                " ORDER BY "+ PRIORITY + " DESC";

        Cursor crs = db.rawQuery(sql,  new String[] {userID});

        while (crs.moveToNext()) {
            results.add(new CategoryObject(crs.getString(crs.getColumnIndex(Category_ID)),
                    crs.getString(crs.getColumnIndex(Category_Name)),
                    crs.getString(crs.getColumnIndex(Show_Store))));
        }
        db.close();
        crs.close();
        return results;
    }

    public String getCategoryId(String category_Name) {
        SQLiteDatabase db = this.getReadableDatabase();
        String userID = SharedPreferenceManager.getUserID(context);
        String categoryID = null;

        String sql = "SELECT "+ Category_ID +
                " FROM " + TB_CATEGORY +
                " WHERE " + USER_ID + "= ? AND " + Category_Name + " =? " +
                " ORDER BY "+ PRIORITY + " DESC";

        Cursor crs = db.rawQuery(sql,  new String[] {userID, category_Name});

        while (crs.moveToNext()) {
            categoryID = crs.getString(crs.getColumnIndex(Category_ID));
        }
        db.close();
        crs.close();
        return categoryID;
    }

    public int updateCategory(String categoryName, String categoryID, String showStore){
        SQLiteDatabase db = this.getWritableDatabase();
        String userID = SharedPreferenceManager.getUserID(context);
        long result = -1;
        int status = 3;
        String timeStamp = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()));

        boolean isCategoryExisted = checkCategoryIsExistedForUpdatePurpose(categoryName, userID, categoryID);
        if(isCategoryExisted){
            status = 2;
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(Category_Name, categoryName);
            contentValues.put(Show_Store, showStore);
            contentValues.put(Updated_At, timeStamp);

            result = db.update(TB_CATEGORY, contentValues, Category_ID + "=?" , new String[] { categoryID });
            if(result != -1)
                status = 1;
        }
        return status;
    }

    public boolean deleteCategory(String category_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        boolean status = false;

        result = db.delete(TB_CATEGORY,Category_ID + "= ?", new String[] { category_ID });
        if(result != -1){
            status = true;
        }
        return status;

    }

    private boolean checkCategoryIsExisted(String categoryName, String userID){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT"+ " "+ Category_ID +" FROM " + TB_CATEGORY + " WHERE " + Category_Name + "=? AND "+ USER_ID + "=?" ;
        Cursor cursor = db.rawQuery(sql, new String[] { categoryName,  userID});
        boolean status = false;

        if(cursor.getCount() > 0)
            status = true;
        cursor.close();
        return status;
    }

    private boolean checkCategoryIsExistedForUpdatePurpose(String categoryName, String userID, String categoryID){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT"+ " "+ Category_ID +" FROM " + TB_CATEGORY + " WHERE " + Category_Name + "=? AND "+ USER_ID + "=? AND " + Category_ID + "<>?" ;
        Cursor cursor = db.rawQuery(sql, new String[] { categoryName,  userID, categoryID});
        boolean status = false;

        if(cursor.getCount() > 0)
            status = true;
        cursor.close();
        return status;
    }

    /*------------------------------------------------------------------------------------sub category purpose-------------------------------------------*/
    public int createSubCategory(String subCategoryName, String price, String quantity, String category_ID) {
        String userID = SharedPreferenceManager.getUserID(context);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        int status = 3;
        String timeStamp = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()));
        String priority = String.valueOf(android.text.format.DateFormat.format("yyMMddHHmmss", new java.util.Date()));

        boolean isSubCategoryExisted = checkSubCategoryIsExisted(userID, subCategoryName);
//        if existed
        if(isSubCategoryExisted){
            status = 2;
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(Sub_Category_Name, subCategoryName);
            contentValues.put(Category_ID, category_ID);
            contentValues.put(USER_ID, userID);
            contentValues.put(Sub_Category_Price, price);
            contentValues.put(Sub_Category_Quantity, quantity);
            contentValues.put(Created_At, timeStamp);
            contentValues.put(PRIORITY, priority);
//            store new record
            result = db.insert(TB_SUB_CATEGORY, null, contentValues);
//            if success
            if(result != -1)
                status = 1;

        }
        return status;

    }

    public ArrayList<SubCategoryObject> fetchAllSubCategory(String categoryID) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<SubCategoryObject> results = new ArrayList<>();

        String sql = "SELECT "+ Sub_Category_ID + "," + Sub_Category_Name + "," + Sub_Category_Price +
                "," + Sub_Category_Quantity +
                " FROM " + TB_SUB_CATEGORY +
                " WHERE " + Category_ID + "= ?" +
                " ORDER BY "+ PRIORITY + " DESC";

        Cursor crs = db.rawQuery(sql,  new String[] {categoryID});

        while (crs.moveToNext()) {
            results.add(new SubCategoryObject(crs.getString(crs.getColumnIndex(Sub_Category_ID)),
                    crs.getString(crs.getColumnIndex(Sub_Category_Name)),
                    crs.getString(crs.getColumnIndex(Sub_Category_Quantity)),
                    crs.getString(crs.getColumnIndex(Sub_Category_Price)),
                    false));
        }
        db.close();
        crs.close();
        return results;
    }

    public int updateSubCategory(String subCategoryName, String subCategoryID, String sub_CategoryPrice, String subCategoryQuantity, String categoryID){
        String userID = SharedPreferenceManager.getUserID(context);
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        int status = 3;
        String timeStamp = String.valueOf(android.text.format.DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()));

        boolean isSubCategoryExisted = checkSubCategoryIsExistedForUpdatePurpose(userID, subCategoryName, subCategoryID);
        if(isSubCategoryExisted){
            status = 2;
        }
        else{
            ContentValues contentValues = new ContentValues();
            contentValues.put(Sub_Category_Name, subCategoryName);
            contentValues.put(Sub_Category_Price, sub_CategoryPrice);
            contentValues.put(Sub_Category_Quantity, subCategoryQuantity);
            contentValues.put(Category_ID, categoryID);
            contentValues.put(Updated_At, timeStamp);

            result = db.update(TB_SUB_CATEGORY, contentValues, Sub_Category_ID + "=?" , new String[] { subCategoryID });
            if(result != -1)
                status = 1;
        }
        return status;
    }

    public boolean deleteSubCategory(String subCategoryID){
        SQLiteDatabase db = this.getWritableDatabase();
        long result = -1;
        boolean status = false;

        result = db.delete(TB_SUB_CATEGORY,Sub_Category_ID + "= ?", new String[] { subCategoryID });
        if(result != -1){
            status = true;
        }
        return status;

    }

    private boolean  checkSubCategoryIsExisted(String userID, String subCategoryName){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT"+ " "+ Sub_Category_ID + " FROM " + TB_SUB_CATEGORY + " WHERE " + Sub_Category_Name + "=? AND "+ USER_ID + "=?" ;
        Cursor cursor = db.rawQuery(sql, new String[] { subCategoryName,  userID});
        boolean status = false;

        if(cursor.getCount() > 0)
            status = true;
        cursor.close();
        return status;
    }

    private boolean  checkSubCategoryIsExistedForUpdatePurpose(String userID, String subCategoryName, String subCategoryID){
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT"+ " "+ Sub_Category_ID + " FROM " + TB_SUB_CATEGORY + " WHERE " + Sub_Category_Name + "=? AND "+ USER_ID + "=? AND " +
                Sub_Category_ID + "<>?";
        Cursor cursor = db.rawQuery(sql, new String[] { subCategoryName,  userID, subCategoryID});
        boolean status = false;

        if(cursor.getCount() > 0)
            status = true;
        cursor.close();
        return status;
    }

    public List<CategoryTypeSpinnerObject> getCategoryTypeSpinnerItem() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<CategoryTypeSpinnerObject> results = new ArrayList<>();
        String userID = SharedPreferenceManager.getUserID(context);

        String sql = "SELECT "+ Category_ID + "," + Category_Name +
                " FROM " + TB_CATEGORY +
                " WHERE " + USER_ID + "= ?" +
                " ORDER BY "+ PRIORITY + " DESC";

        Cursor crs = db.rawQuery(sql,  new String[] {userID});

        while (crs.moveToNext()) {
            results.add(new CategoryTypeSpinnerObject(crs.getString(crs.getColumnIndex(Category_ID)),
                    crs.getString(crs.getColumnIndex(Category_Name))));
        }
        db.close();
        crs.close();
        return results;
    }

}
