package com.app.shoppingapp.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shoppingapp.DAO.DbAdapter;
import com.app.shoppingapp.DAO.DbAdapterImp;
import com.app.shoppingapp.Fragments.SavedRecyclerView.CallbackProductTouch;
import com.app.shoppingapp.Fragments.SavedRecyclerView.ProductTouchHelperCallback;
import com.app.shoppingapp.Fragments.SavedRecyclerView.SavedAdapter;
import com.app.shoppingapp.Models.Data;
import com.app.shoppingapp.databinding.FragmentSavedBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;


public class SavedFragment extends Fragment implements CallbackProductTouch {
    private SavedAdapter recyclerViewAdapter;
    private ArrayList<Data> dataList;
    private FragmentSavedBinding binding;
    private DbAdapter dbAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSavedBinding.inflate(getLayoutInflater(), container, false);
        dbAdapter = new DbAdapterImp(getContext());
        dbAdapter.open();
        dataList = dbAdapter.findAllSaved();
        dbAdapter.close();
        configureRecyclerView();
        return binding.getRoot();
    }

    private void configureRecyclerView() {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAdapter = new SavedAdapter(dataList);
        binding.recyclerView.setAdapter(recyclerViewAdapter);
        ItemTouchHelper.Callback callback = new ProductTouchHelperCallback(this);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.recyclerView);
    }

    @Override
    public void itemTouchOnMove(int oldPosition, int newPosition) {
        Data data = dataList.get(oldPosition);
        dataList.remove(oldPosition);
        dataList.add(newPosition, data);
        recyclerViewAdapter.notifyItemMoved(oldPosition, newPosition);
        recyclerViewAdapter.notifyItemChanged(newPosition);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int position) {
        // Delete and also show undo section
        String title = dataList.get(viewHolder.getAdapterPosition()).getTitle();

        // Backup of removed item for undo
        final Data deletedProduct = dataList.get(viewHolder.getAdapterPosition());
        final int deletedIndex = viewHolder.getAdapterPosition();

        // Remove the product from recyclerview
        recyclerViewAdapter.removeProductFromSaved(viewHolder.getAdapterPosition());
        // Showing snackbar for undo

        Snackbar snackbar = Snackbar.make(binding.layout, title + " removed", Snackbar.LENGTH_LONG);

        snackbar.setAction("UNDO", (View v)
                -> recyclerViewAdapter.restoreProduct(deletedProduct, deletedIndex));

        snackbar.setActionTextColor(Color.GREEN);
        snackbar.show();
    }
}
