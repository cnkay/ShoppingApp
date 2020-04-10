package com.app.shoppingapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.app.shoppingapp.DAO.DbAdapter;
import com.app.shoppingapp.DAO.DbAdapterImp;
import com.app.shoppingapp.Models.Data;
import com.app.shoppingapp.Models.Item;
import com.app.shoppingapp.Tasks.CheckInternetConnectionTask;
import com.app.shoppingapp.Tasks.FetchResponse;
import com.app.shoppingapp.Tasks.ScrapeTask;
import com.app.shoppingapp.databinding.ActivitySplashBinding;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends AppCompatActivity implements FetchResponse {
    DbAdapter dbAdapter;
    Context context;
    private ActivitySplashBinding binding; // New feature in Gradle
    ArrayList<Data> dataList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        //Animations

        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); // Add this
        fadeIn.setDuration(1000);

        AnimationSet animation = new AnimationSet(false); // Change to false
        animation.addAnimation(fadeIn);

        binding.ellipseView.setAnimation(animation);
        binding.cartView.setAnimation(animation);
        binding.txtSplash.setAnimation(animation);
        dbAdapter = new DbAdapterImp(getApplicationContext());
        context = getApplicationContext();
        new CheckInternetConnectionTask(internet -> checkInternet(internet)); // https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    }

    private void checkInternet(boolean status) {
        if (status) {
            binding.progressBar.setVisibility(View.VISIBLE);
            new ScrapeTask(this, new WeakReference<>(context)).execute();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(getResources().getString(R.string.str_no_internet));
            builder.setNeutralButton(getResources().getString(R.string.ok), (DialogInterface dialogInterface, int i) -> {
                finish();
            });
            builder.show();
        }

    }

    @Override
    public void processScrapeTaskFinish(ArrayList<Data> dataList) {
        this.dataList = dataList;
        dbAdapter.createDatabase();
        dbAdapter.open();
        int count = dbAdapter.countProducts();
        if (count > 0) {
            if (dataList.size() != count) {
                // JSON file updated so truncate table
                dbAdapter.truncateProductTable();
                dbAdapter.fillDataListToDb(dataList);
            }

        } else {
            dbAdapter.truncateProductTable();
            dbAdapter.fillDataListToDb(dataList);
        }
        dbAdapter.close();
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            binding.progressBar.setVisibility(View.GONE);
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_up); // Activity animation
            finish();
        }, 1000);

    }

    @Override
    public void processDataListFinish(List<Item> dataList) {

    }
}

