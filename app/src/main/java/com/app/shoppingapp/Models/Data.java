package com.app.shoppingapp.Models;

import android.os.Parcel;
import android.os.Parcelable;

public class Data implements Parcelable {
    private int id;
    private String title;
    private String subtitle;
    private String price;
    private String imageURL;
    private String productURL;
    private String description;
    private int category;

    public Data() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getProductURL() {
        return productURL;
    }

    public void setProductURL(String productURL) {
        this.productURL = productURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Data{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", subtitle='" + subtitle + '\'' +
                ", price='" + price + '\'' +
                ", imageURL='" + imageURL + '\'' +
                ", productURL='" + productURL + '\'' +
                ", description='" + description + '\'' +
                ", category=" + category +
                '}';
    }

    protected Data(Parcel in) {
        id = in.readInt();
        title = in.readString();
        subtitle = in.readString();
        price = in.readString();
        imageURL = in.readString();
        productURL = in.readString();
        description = in.readString();
        category = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(price);
        dest.writeString(imageURL);
        dest.writeString(productURL);
        dest.writeString(description);
        dest.writeInt(category);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Data> CREATOR = new Parcelable.Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };
}