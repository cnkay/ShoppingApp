package com.app.shoppingapp.Tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.app.shoppingapp.Models.Data;
import com.app.shoppingapp.Models.Item;
import com.app.shoppingapp.Models.JSONEntity;
import com.google.gson.Gson;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ScrapeTask extends AsyncTask<Void, Void, ArrayList<Data>> {
    private WeakReference<Context> weakContext;
    private StringBuffer buffer;
    private ArrayList<Data> dataList;
    private FetchResponse delegate = null;

    //CSS Selector array for scrape product description from amazon website
    private String[] descriptionCSSselectors = {"div#productDescription>p", "div#productDescription", "div#aplus>div>div:nth-of-type(2)>div>div>p"};

    public ScrapeTask(FetchResponse delegate, WeakReference<Context> weakContext) {
        this.delegate = delegate;
        this.weakContext = weakContext;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(ArrayList<Data> dataList) { // Return datalist to FetchResponse
        delegate.processScrapeTaskFinish(dataList);
    }

    @Override
    protected ArrayList<Data> doInBackground(Void... voids) {
        dataList = new ArrayList<>();
        buffer = new StringBuffer();
        StringBuilder sb = new StringBuilder();
        String json = LoadJSONFromAssets.load(weakContext.get());
        Gson gson = new Gson();
        JSONEntity entity = gson.fromJson(json, JSONEntity.class);
        List<Item> items = entity.getItems();
        for (Item item : items) {
            Data productData = new Data();
            productData.setId(item.getId());
            productData.setTitle(checkStartOfTheString(item.getTitle()));
            productData.setSubtitle(checkStartOfTheString(item.getSubtitle()));
            productData.setPrice(checkStartOfTheString(item.getPrice()));
            productData.setImageURL(checkURL(checkStartOfTheString(item.getImage())));
            productData.setProductURL(checkURL(checkStartOfTheString(item.getUrl())));
            productData.setCategory(item.getCategory());
            try {
                String URL = productData.getProductURL();
                Document doc = Jsoup.connect(URL).get(); // Jsoup scrapes data from amazon
                Elements description = new Elements();   // For more information https://jsoup.org/
                for (String selector : descriptionCSSselectors) {
                    description = doc.select(selector);
                    if (description.hasText()) {
                        break;
                    }
                }
                String strDescription = description.html();
                if (strDescription.length() > 0) {
                    productData.setDescription(Jsoup.parse(strDescription).text());
                } else
                    productData.setDescription("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            dataList.add(productData);
        }
        for (Data data : dataList) {
            System.out.println(data.toString());
        }
        return dataList;
    }

    private String checkStartOfTheString(String str) { // This functions checks blank characters
        if (str.length() == 0) {
            return str;
        }
        char start = str.charAt(0); //https://stackoverflow.com/questions/17597157/android-url-openstream-not-working// Some products in url, like [ Colgate...]
        String newString = "";
        if (start == ' ') {
            newString = str.substring(1);
        } else {
            newString = str;
        }

        return newString;
    }

    private String checkURL(String str) {
        String newString = "";
        if (!(str.startsWith("http://")) && !(str.startsWith("https://"))) // Validation for product url (Some URL's starts like www.blahblah)
            newString = "http://" + str;
        else
            return str;
        return newString;
    }
}
