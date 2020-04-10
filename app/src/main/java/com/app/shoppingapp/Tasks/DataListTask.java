package com.app.shoppingapp.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.app.shoppingapp.Models.Item;
import com.app.shoppingapp.Models.JSONEntity;
import com.google.gson.Gson;

import java.lang.ref.WeakReference;
import java.util.List;

public class DataListTask extends AsyncTask<Void, Void, List<Item>> {
    private WeakReference<Context> weakContext;
    private FetchResponse delegate = null;

    public DataListTask(FetchResponse delegate, WeakReference<Context> weakContext) {
        this.delegate = delegate;
        this.weakContext = weakContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(List<Item> items) {
        delegate.processDataListFinish(items);
    }

    @Override
    protected List<Item> doInBackground(Void... voids) {
        String json = LoadJSONFromAssets.load(weakContext.get());
        Gson gson = new Gson();
        JSONEntity entity = gson.fromJson(json, JSONEntity.class);
        List<Item> items = entity.getItems();
        return items;
    }
}