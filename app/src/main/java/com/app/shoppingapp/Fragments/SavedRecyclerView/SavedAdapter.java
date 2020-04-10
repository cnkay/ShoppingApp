package com.app.shoppingapp.Fragments.SavedRecyclerView;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.shoppingapp.DAO.DbAdapter;
import com.app.shoppingapp.DAO.DbAdapterImp;
import com.app.shoppingapp.Models.Data;
import com.app.shoppingapp.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.SavedViewHolder> {
    DbAdapter dbAdapter;
    private ArrayList<Data> dataList;
    Context context;
    private OnSavedRecyclerViewItemClickListener clickListener;

    public SavedAdapter(ArrayList<Data> dataList) {
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public SavedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.saved_recyclerview_item, parent, false);
        dbAdapter = new DbAdapterImp(view.getContext());
        dbAdapter.createDatabase();
        context = view.getContext();
        return new SavedViewHolder(view);
    }

    public void setOnItemClickListener(OnSavedRecyclerViewItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public void onBindViewHolder(@NonNull SavedViewHolder holder, int position) {
        //  Bind your data here
        Glide
                .with(context)
                .load(dataList.get(position).getImageURL())
                //.override(300, 200)
                .into(holder.ivUser);
        holder.tvName.setText(dataList.get(position).getTitle());
        holder.tvDescription.setText(dataList.get(position).getPrice());
        // holder with glide
        holder.viewF.setOnClickListener((View v)
                -> {
            String url = dataList.get(position).getProductURL(); // Open browser intent
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            v.getContext().startActivity(i);
            // clickListener.onSavedRecyclerViewItemClicked(position, holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        if (dataList == null)
            return 0;
        else
            return dataList.size();

    }

    public static class SavedViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUser;
        TextView tvName, tvDescription;
        RelativeLayout viewF, viewB;

        public SavedViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUser = itemView.findViewById(R.id.ivUser);
            tvName = itemView.findViewById(R.id.tvName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            viewF = itemView.findViewById(R.id.r1);
            viewB = itemView.findViewById(R.id.view_background);
        }
    }
    // Remove and restore products

    public void removeProductFromSaved(int position) {
        Data data = dataList.get(position);
        dbAdapter.open();
        dbAdapter.deleteFromSavedById(data.getId());
        dbAdapter.discard(data);
        dbAdapter.close();
        dataList.remove(position);
        notifyItemRemoved(position); // Refreshes recyclerview
        notifyDataSetChanged();
    }

    public void restoreProduct(Data product, int position) {
        dataList.add(position, product);
        dbAdapter.open();
        dbAdapter.save(product);
        dbAdapter.close();
        notifyItemInserted(position);
        notifyDataSetChanged();
    }
}
