package com.example.byd.aplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.LoginActivity;
import com.example.byd.aplication.ui.blockifdrunkUI.history.HistoryFragment;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.aplication.ui.home.user.UserFragment;
import com.example.byd.aplication.ui.lifeguardUI.lifeguard.LifeguardFragment;
import com.example.byd.aplication.ui.lifeguardUI.message.ChatFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.open, R.string.close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        actionBarDrawerToggle.syncState();

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.containerOfFragments, new StartCartFragment());
        fragmentTransaction.commit();

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        drawerLayout.closeDrawer(GravityCompat.START);

        int id = item.getItemId();
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        switch (id){
            case (R.id.homeItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new StartCartFragment());
                fragmentTransaction.commit();
                break;
            case (R.id.userItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new UserFragment());
                fragmentTransaction.commit();
                break;
            case (R.id.lifeguardItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new LifeguardFragment());
                fragmentTransaction.commit();
                break;
            case (R.id.historialItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new HistoryFragment());
                fragmentTransaction.commit();
                break;

            case (R.id.bluetoothItem):
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intentOpenBluetoothSettings);
                break;
            case (R.id.logoutItem):
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

        return false;

        }


    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return super.onSupportNavigateUp();
    }


}


