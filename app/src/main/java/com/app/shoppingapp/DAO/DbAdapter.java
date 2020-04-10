package com.app.shoppingapp.DAO;

import com.app.shoppingapp.Models.Category;
import com.app.shoppingapp.Models.Data;

import java.util.ArrayList;

public interface DbAdapter {
    DbAdapterImp createDatabase();

    DbAdapterImp open();

    void close();

    ArrayList<Data> findAll();

    ArrayList<Data> findAllExceptDiscardedAndSavedProducts();

    Integer countProducts();

    void truncateProductTable();

    void fillDataListToDb(ArrayList<Data> dataList);

    void save(Data data);

    void discard(Data data);

    ArrayList<Integer> findAllSavedAndDiscardedIds();

    void deleteFromSavedById(int id);

    ArrayList<Data> findAllSaved();

    ArrayList<Integer> findAllDiscardedIds();

    ArrayList<Integer> findAllSavedIds();

    ArrayList<Category> findAllCategories();

    String findProductURLByTitle(String title);

    Category findCategoryByName(String name);

    void updateCategoryForCheckState(Category category);

    ArrayList<Data> getProductsForShopping();
}
