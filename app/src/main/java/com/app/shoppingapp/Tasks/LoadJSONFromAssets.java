package com.app.shoppingapp.Tasks;

import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoadJSONFromAssets {
    public static String load(Context context){
        String json;
        try {
            InputStream is = context.getAssets().open("products.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
