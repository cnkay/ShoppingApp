package com.app.shoppingapp.Fragments.CategoryRecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shoppingapp.DAO.DbAdapter;
import com.app.shoppingapp.DAO.DbAdapterImp;
import com.app.shoppingapp.Models.Category;
import com.app.shoppingapp.R;
import com.google.android.material.checkbox.MaterialCheckBox;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private ArrayList<Category> categoryArrayList;
    DbAdapter dbAdapter;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public MaterialCheckBox checkBox;
        public CardView cardView;

        public CategoryViewHolder(@NonNull View view) {
            super(view);
            checkBox = view.findViewById(R.id.checkBox);
            cardView = view.findViewById(R.id.cardView);
        }
    }

    public CategoryAdapter(ArrayList<Category> categoryArrayList) {
        this.categoryArrayList = categoryArrayList;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent,
                                                 int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_recyclerview_item, parent, false);
        dbAdapter = new DbAdapterImp(view.getContext());
        dbAdapter.createDatabase();

        return new CategoryViewHolder(view);
    }

    boolean onBind;

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        boolean checkedState = false;
        Category category = categoryArrayList.get(position);
        if (category.getIsChecked() == 1)
            checkedState = true;
        holder.checkBox.setText(category.getName());
        onBind = true;
        holder.checkBox.setChecked(checkedState);
        onBind = false;
        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (!onBind) {
                Category checkedCategory = categoryArrayList.get(position);
                if (b)
                    category.setIsChecked(1);
                else
                    category.setIsChecked(0);
                Log.d("C.Adap::itemChecked()", "Checked ID : " + category.getId() + " Checked Name : " + category.getName());
                dbAdapter.open();
                dbAdapter.updateCategoryForCheckState(checkedCategory);
                dbAdapter.close();
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (categoryArrayList == null)
            return 0;
        else
            return categoryArrayList.size();
    }
}

