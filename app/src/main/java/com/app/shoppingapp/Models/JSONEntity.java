package com.app.shoppingapp.Models;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class JSONEntity {

    @SerializedName("items")
    @Expose
    private List<Item> items = null;

    /**
     * No args constructor for use in serialization
     *
     */
    public JSONEntity() {
    }

    /**
     *
     * @param items
     */
    public JSONEntity(List<Item> items) {
        super();
        this.items = items;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

}
