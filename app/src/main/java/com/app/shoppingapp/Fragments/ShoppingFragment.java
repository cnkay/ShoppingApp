package com.app.shoppingapp.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.app.shoppingapp.DAO.DbAdapter;
import com.app.shoppingapp.DAO.DbAdapterImp;
import com.app.shoppingapp.Models.Data;
import com.app.shoppingapp.Models.Item;
import com.app.shoppingapp.R;
import com.app.shoppingapp.Tasks.CheckInternetConnectionTask;
import com.app.shoppingapp.Tasks.DataListTask;
import com.app.shoppingapp.Tasks.FetchResponse;
import com.app.shoppingapp.Tasks.ScrapeTask;
import com.app.shoppingapp.databinding.FragmentShoppingBinding;
import com.bumptech.glide.Glide;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment implements FetchResponse {
    public static ViewHolder viewHolder;
    public static SwipeCardsAdapter swipeCardsAdapter;
    private FragmentShoppingBinding binding;
    private View v;
    private int count = 0;
    private Context context;
    private DbAdapter dbAdapter;
    ArrayList<Data> dataList = new ArrayList<>();
    ArrayList<Integer> savedOrDiscardedIdsList = new ArrayList<>();
    // SwipeRefreshLayout pullToRefresh;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentShoppingBinding.inflate(getLayoutInflater(), container, false);
        context = getContext();
        dbAdapter = new DbAdapterImp(context);
        dbAdapter.open();
        count = dbAdapter.countProducts();
        // Use this for debug
        // dataList = dbAdapter.findAll();
        // Use this for production
        dataList = dbAdapter.getProductsForShopping(); // Filtered products (Filtered by discarded,saved and selected category)

        dbAdapter.close();


        createSwipe(dataList);
        createPullToRefresh();
        return binding.getRoot();
    }

    private void checkInternet(boolean status) {
        if (status) {
            new DataListTask(this,new WeakReference<>(context)).execute();
        } else {
            Toast.makeText(context, getResources().getString(R.string.str_no_internet), Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void processDataListFinish(List<Item> dataListFromWebsite) {
        if (dataListFromWebsite.size() == count) {
            binding.pullToRefresh.setRefreshing(false);
            Toast.makeText(context, getResources().getString(R.string.str_no_product), Toast.LENGTH_LONG).show();
        }
        if (dataListFromWebsite.size() > count) {
            new ScrapeTask(this,new WeakReference<>(context)).execute();
        }
    }

    @Override
    public void processScrapeTaskFinish(ArrayList<Data> dataListFromWebsite) {
        dbAdapter.open();
        dbAdapter.createDatabase();
        dbAdapter.truncateProductTable();
        dbAdapter.fillDataListToDb(dataListFromWebsite);
        dbAdapter.close();
        dataList.clear();
        dataList.addAll(dataListFromWebsite);
        swipeCardsAdapter.notifyDataSetChanged();
        binding.pullToRefresh.setRefreshing(false);
        Toast.makeText(context, getResources().getString(R.string.str_new_product), Toast.LENGTH_LONG).show();
    }
    private void createPullToRefresh(){
        binding.pullToRefresh.setVisibility(View.INVISIBLE);
        binding.pullToRefresh.setOnRefreshListener(() -> {
            new CheckInternetConnectionTask(internet -> checkInternet(internet));
        });
    }
    private void createSwipe(ArrayList<Data> dataList) {
        swipeCardsAdapter = new SwipeCardsAdapter(dataList, context);
        binding.flingContainer.setAdapter(swipeCardsAdapter);
        binding.flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Data data = dataList.get(0);
                dbAdapter.open();
                dbAdapter.discard(data);
                dbAdapter.close();
                Log.d("onLeftCardExit", "DISCARDED"); // Discard
                dataList.remove(0);
                swipeCardsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Data data = dataList.get(0);
                dbAdapter.open();
                dbAdapter.save(data);
                dbAdapter.close();
                Log.d("onRightCardExit", "SAVED"); // Save
                dataList.remove(0);
                swipeCardsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter==0) {
                    binding.pullToRefresh.setVisibility(View.VISIBLE);
                    Log.d("onAdapterAboutToEmpty", "Triggered");
                }
                else
                    binding.pullToRefresh.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = binding.flingContainer.getSelectedView();
                view.findViewById(R.id.background).setAlpha(0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });

        // Optionally add an OnItemClickListener
        binding.flingContainer.setOnItemClickListener((int itemPosition, Object dataObject) -> {
            View view = binding.flingContainer.getSelectedView();
            view.findViewById(R.id.background).setAlpha(0);
            swipeCardsAdapter.notifyDataSetChanged();
            String url = dataList.get(itemPosition).getProductURL(); // Open browser intent
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        });

    }

    private static class ViewHolder {
        private FrameLayout background;
        private TextView textTitle;
        private TextView textPrice;
        private TextView textQuantity;
        private TextView textDescription;
        private ImageView cardImage;


    }

    class SwipeCardsAdapter extends BaseAdapter {


        private ArrayList<Data> dataList;
        public Context context;

        private SwipeCardsAdapter(ArrayList<Data> apps, Context context) {
            this.dataList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;

            if (rowView == null) {

                LayoutInflater inflater = getLayoutInflater();
                rowView = inflater.inflate(R.layout.card_item, parent, false);
                // Configure view holder
                viewHolder = new ViewHolder();
                viewHolder.textTitle = rowView.findViewById(R.id.textTitle);
                viewHolder.textPrice = rowView.findViewById(R.id.textPrice);
                viewHolder.textQuantity = rowView.findViewById(R.id.textQuantity);
                viewHolder.textDescription = rowView.findViewById(R.id.textDescription);
                viewHolder.background = rowView.findViewById(R.id.background);
                viewHolder.cardImage = rowView.findViewById(R.id.cardImage);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.textTitle.setText(dataList.get(position).getTitle()); // getDescription()
            viewHolder.textQuantity.setText(dataList.get(position).getSubtitle());
            viewHolder.textPrice.setText(dataList.get(position).getPrice());
            viewHolder.textDescription.setText(dataList.get(position).getDescription());
            Glide
                    .with(context)
                    .load(dataList.get(position).getImageURL())
                    .override(300, 200)
                    .into(viewHolder.cardImage);
            // Glide for get image file from links which ends like .png/.jpg etc.
            return rowView;
        }
    }

}
