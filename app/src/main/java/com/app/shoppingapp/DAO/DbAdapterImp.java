package com.app.shoppingapp.DAO;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.app.shoppingapp.Models.Category;
import com.app.shoppingapp.Models.Data;

import java.io.IOException;
import java.util.ArrayList;

public class DbAdapterImp implements DbAdapter {
    protected static final String TAG = "DataAdapter";

    private Context mContext;
    private SQLiteDatabase mDb;
    private DbHelper mDbHelper;

    public DbAdapterImp(Context context) {
        this.mContext = context;
        mDbHelper = new DbHelper(mContext);
    }

    @Override
    public DbAdapterImp createDatabase() throws SQLException {
        try {
            mDbHelper.createDataBase();
        } catch (IOException mIOException) {
            Log.e(TAG, mIOException.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
        return this;
    }

    @Override
    public DbAdapterImp open() throws SQLException {
        try {
            mDbHelper.openDataBase();
            mDbHelper.close();
            mDb = mDbHelper.getReadableDatabase();
        } catch (SQLException mSQLException) {
            Log.e(TAG, "open >>" + mSQLException.toString());
            throw mSQLException;
        }
        return this;
    }

    @Override
    public void close() {
        mDbHelper.close();
    }

    @Override
    public ArrayList<Data> findAll() {
        ArrayList<Data> dataList = new ArrayList<>();
        String query = "SELECT * FROM product;";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data();
                data.setId(cursor.getInt(0));
                data.setTitle(cursor.getString(1));
                data.setSubtitle(cursor.getString(2));
                data.setPrice(cursor.getString(3));
                data.setImageURL(cursor.getString(4));
                data.setProductURL(cursor.getString(5));
                data.setDescription(cursor.getString(6));
                data.setCategory(cursor.getInt(7));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        return dataList;
    }

    @Override
    public Integer countProducts() {
        String query = "SELECT COUNT(*) FROM product";
        Cursor cursor = mDb.rawQuery(query, null);
        int size = 0;
        if (cursor.moveToFirst()) {
            size = cursor.getInt(0);
        }
        return size;
    }

    @Override
    public void fillDataListToDb(ArrayList<Data> dataList) {
        if (dataList.size() > 0) {
            for (Data data : dataList) {
                int id = data.getId();
                String title = replace(data.getTitle());
                String subtitle = replace(data.getSubtitle());
                String description = replace(data.getDescription());// Sometimes null pointer
                String price = data.getPrice();
                String imageURL = data.getImageURL();
                String productURL = data.getProductURL(); // Because SQLite uses ' character for seperate TEXT values

                title = "'" + title + "'";
                subtitle = "'" + subtitle + "'";
                price = "'" + price + "'";
                imageURL = "'" + imageURL + "'";
                productURL = "'" + productURL + "'";
                description = "'" + description + "'";
                int category = data.getCategory();

                String query = "INSERT OR REPLACE INTO product VALUES(" + id + "," + title + "," + subtitle + "," + price + "," + imageURL + "," + productURL + "," + description + "," + category + ");";
                mDb.execSQL(query);
            }
        }
    }

    @Override
    public void truncateProductTable() {
        String query = "DELETE FROM product";
        mDb.execSQL(query);
    }

    @Override
    public ArrayList<Data> findAllSaved() {
        ArrayList<Data> dataList = new ArrayList<>();
        String query = "SELECT * FROM saved;";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                Data data = new Data();
                data.setId(cursor.getInt(0));
                data.setTitle(cursor.getString(1));
                data.setSubtitle(cursor.getString(2));
                data.setPrice(cursor.getString(3));
                data.setImageURL(cursor.getString(4));
                data.setProductURL(cursor.getString(5));
                data.setDescription(cursor.getString(6));
                data.setCategory(cursor.getInt(7));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        return dataList;
    }

    @Override
    public void deleteFromSavedById(int id) {
        String query = "DELETE FROM saved WHERE id=" + id + ";";
        mDb.execSQL(query);
    }

    @Override
    public ArrayList<Integer> findAllDiscardedIds() {
        ArrayList<Integer> discardedList = new ArrayList<>();
        String query = "SELECT * FROM discarded";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                discardedList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        return discardedList;
    }

    @Override
    public ArrayList<Integer> findAllSavedIds() {
        ArrayList<Integer> savedList = new ArrayList<>();
        String query = "SELECT id FROM saved";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                savedList.add(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        return savedList;
    }

    @Override
    public void save(Data data) {

        String title = replace(data.getTitle());
        String subtitle = replace(data.getSubtitle());
        String description = replace(data.getDescription());
        String price = data.getPrice();
        String imageURL = data.getImageURL();
        String productURL = data.getProductURL(); // Because SQLite uses ' character for seperate TEXT values

        int id = data.getId();
        title = "'" + title + "'";
        subtitle = "'" + subtitle + "'";
        price = "'" + price + "'";
        imageURL = "'" + imageURL + "'";
        productURL = "'" + productURL + "'";
        description = "'" + description + "'";
        int category = data.getCategory();

        String query = "INSERT OR REPLACE INTO saved VALUES(" + id + "," + title + "," + subtitle + "," + price + "," + imageURL + "," + productURL + "," + description + "," + category + ")";
        mDb.execSQL(query);
    }

    @Override
    public void discard(Data data) {
        int id = data.getId();
        String query = "INSERT INTO discarded VALUES(" + id + ");";
        mDb.execSQL(query);
    }

    @Override
    public ArrayList<Data> findAllExceptDiscardedAndSavedProducts() {
        ArrayList<Integer> notShownList = new ArrayList<>();
        notShownList.addAll(findAllDiscardedIds());
        notShownList.addAll(findAllSavedIds());
        ArrayList<Data> allDatas = findAll();
        for (Data data : allDatas) {
            for (Integer index : notShownList) {
                if (data.getId() == index) {
                    allDatas.remove(data.getId());
                }
            }
        }
        return allDatas;
    }

    @Override
    public ArrayList<Category> findAllCategories() {
        ArrayList<Category> categoryList = new ArrayList<>();
        String query = "SELECT * FROM categories";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                categoryList.add(new Category(cursor.getInt(0), cursor.getString(1), cursor.getInt(2)));
            } while (cursor.moveToNext());
        }
        return categoryList;
    }

    @Override
    public void updateCategoryForCheckState(Category category) {
        String query = "UPDATE categories SET checked=" + category.getIsChecked() + " WHERE id=" + category.getId() + ";";
        mDb.execSQL(query);
    }

    @Override
    public String findProductURLByTitle(String title) {
        String searchTitle = replace(title);
        String query = "SELECT productURL FROM saved WHERE title=" + "'" + searchTitle + "'" + ";";
        Cursor cursor = mDb.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            return cursor.getString(0);
        }
        return "";
    }

    @Override
    public ArrayList<Data> getProductsForShopping() {
        ArrayList<Data> dataList = new ArrayList<>();
        String query = "SELECT * FROM product WHERE category IN (SELECT id FROM categories WHERE checked=1);";
        Cursor cursor = mDb.rawQuery(query, null);
        ArrayList<Integer> savedAndDiscardedIds = new ArrayList<>();
        savedAndDiscardedIds.addAll(findAllSavedAndDiscardedIds());
        if (cursor.moveToFirst()) {
            do {
                if (savedAndDiscardedIds.contains(cursor.getInt(0)))
                    continue;
                Data data = new Data();
                data.setId(cursor.getInt(0));
                data.setTitle(cursor.getString(1));
                data.setSubtitle(cursor.getString(2));
                data.setPrice(cursor.getString(3));
                data.setImageURL(cursor.getString(4));
                data.setProductURL(cursor.getString(5));
                data.setDescription(cursor.getString(6));
                data.setCategory(cursor.getInt(7));
                dataList.add(data);
            } while (cursor.moveToNext());
        }
        return dataList;
    }

    @Override
    public ArrayList<Integer> findAllSavedAndDiscardedIds() {
        ArrayList<Integer> savedAndDiscardedIds = new ArrayList<>();
        savedAndDiscardedIds.addAll(findAllDiscardedIds());
        savedAndDiscardedIds.addAll(findAllSavedIds());
        return savedAndDiscardedIds;
    }

    public String replace(String str) {
        return str.replace("'", "''");
    }

    @Override
    public Category findCategoryByName(String name) {
        name = replace(name);
        String query = "SELECT * FROM categories WHERE name=" + "'" + name + "'" + ";";
        Cursor cursor = mDb.rawQuery(query, null);
        Category category = new Category();
        if (cursor.moveToFirst()) {
            category.setId(cursor.getInt(0));
            category.setName(cursor.getString(1));
            category.setIsChecked(cursor.getInt(2));
        }
        return category;
    }
}


