package com.app.shoppingapp.Tasks;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class CheckInternetConnectionTask extends AsyncTask<Void,Void,Boolean> { // Checks internet connection

    private Consumer mConsumer;
    public interface Consumer { void accept(Boolean internet); }

    public CheckInternetConnectionTask(Consumer consumer) { mConsumer = consumer; execute(); }

    @Override protected Boolean doInBackground(Void... voids) { try {
        Socket sock = new Socket();
        sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
        sock.close();
        return true;
    } catch (IOException e) { return false; } }

    @Override protected void onPostExecute(Boolean internet) { mConsumer.accept(internet); }
}
