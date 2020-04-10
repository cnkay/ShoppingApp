package com.app.shoppingapp.Fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.app.shoppingapp.DAO.DbAdapter;
import com.app.shoppingapp.DAO.DbAdapterImp;
import com.app.shoppingapp.Fragments.CategoryRecyclerView.CategoryAdapter;
import com.app.shoppingapp.Models.Category;
import com.app.shoppingapp.R;
import com.app.shoppingapp.databinding.FragmentCategoryBinding;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;


public class CategoryFragment extends Fragment {
    DbAdapter dbAdapter;
    private FragmentCategoryBinding binding;
    private CategoryAdapter categoryAdapter;
    private ArrayList<Category> categoryArrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        binding = FragmentCategoryBinding.inflate(getLayoutInflater(), container, false);
        dbAdapter = new DbAdapterImp(getContext());
        dbAdapter.createDatabase();
        categoryArrayList = findAllCategories();
        createChips(categoryArrayList);
        return binding.getRoot();
    }

    private ArrayList<Category> findAllCategories() {
        dbAdapter.open();
        ArrayList<Category> categoryArrayList = new ArrayList<>(dbAdapter.findAllCategories());
        dbAdapter.close();

        //
        for (Category category : categoryArrayList)
            System.out.println(category.getId() + " " + category.getName());
        //
        return categoryArrayList;
    }

    private void createChips(ArrayList<Category> categoryList) {
        for (Category category : categoryList) {
            Chip chip = (Chip) this.getLayoutInflater().inflate(R.layout.item_chip_category, null, false);
            chip.setText(category.getName());
            int paddingDp = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 10,
                    getResources().getDisplayMetrics()
            );
            chip.setPadding(paddingDp, 0, paddingDp, 0);
            int isChecked = category.getIsChecked();
            if (isChecked == 1)
                chip.setChecked(true);
            else
                chip.setChecked(false);
            chip.setOnCheckedChangeListener((compoundButton, b) -> {
                dbAdapter.open();
                Category checkedCategory = dbAdapter.findCategoryByName(category.getName());
                if (b) {
                    checkedCategory.setIsChecked(1);
                    chip.setChecked(true);
                } else {
                    checkedCategory.setIsChecked(0);
                    chip.setChecked(false);
                }
                dbAdapter.updateCategoryForCheckState(checkedCategory);
                dbAdapter.close();
            });
            binding.chipGroup.addView(chip);
        }

    }
}
