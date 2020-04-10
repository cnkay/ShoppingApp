package com.app.shoppingapp.Tasks;


import com.app.shoppingapp.Models.Data;
import com.app.shoppingapp.Models.Item;

import java.util.ArrayList;
import java.util.List;

public interface FetchResponse { // This interface for returning data from AsyncTask
    void processScrapeTaskFinish(ArrayList<Data> dataList);
    void processDataListFinish(List<Item> dataList);
}
