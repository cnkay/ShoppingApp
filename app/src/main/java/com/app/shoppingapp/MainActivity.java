package com.app.shoppingapp;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.app.shoppingapp.Fragments.CategoryFragment;
import com.app.shoppingapp.Fragments.SavedFragment;
import com.app.shoppingapp.Fragments.ShoppingFragment;
import com.app.shoppingapp.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private Context context;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        context = getApplicationContext();
        Bundle data = getIntent().getExtras();
        configureBottomNavigationView();
        startsWithFirstFragment(data);
    }

    private void startsWithFirstFragment(Bundle data) {
        ShoppingFragment firstFragment = new ShoppingFragment();
        firstFragment.setArguments(data);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, firstFragment)
                .commit();
    }

    private void configureBottomNavigationView() {
        binding.bottomNavigation.setSelectedItemId(R.id.navigation_shopping);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(this::onNavigationItemSelected);
    }

    private boolean loadFragment(Fragment fragment) {
        // Switching fragment
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_category:
                fragment = new CategoryFragment();
                break;
            case R.id.navigation_shopping:
                fragment = new ShoppingFragment();
                break;
            case R.id.navigation_saved:
                fragment = new SavedFragment();
                break;
        }
        return loadFragment(fragment);
    }

}
