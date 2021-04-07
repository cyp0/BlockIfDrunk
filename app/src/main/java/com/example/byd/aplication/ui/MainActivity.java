package com.example.byd.aplication.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.byd.R;
import com.example.byd.aplication.LoginActivity;
import com.example.byd.aplication.ui.blockifdrunkUI.block.ContactsFragment;
import com.example.byd.aplication.ui.blockifdrunkUI.history.HistoryFragment;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.aplication.ui.home.user.UserFragment;
import com.example.byd.aplication.ui.lifeguardUI.contacts.AllContactsFragment;
import com.example.byd.aplication.ui.lifeguardUI.lifeguard.LifeguardFragment;
import com.example.byd.aplication.ui.blockifdrunkUI.phone.PhoneFragment;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

@SuppressWarnings("deprecation")
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    androidx.appcompat.widget.Toolbar toolbar;
    NavigationView navigationView;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer);
        navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);
        TextView textViewName = navigationView.getHeaderView(0).findViewById(R.id.textView3);
        textViewName.setText(firebaseAuth.getCurrentUser().getDisplayName());

        TextView textViewEmail = navigationView.getHeaderView(0).findViewById(R.id.textViewPhone);
        textViewEmail.setText(firebaseAuth.getCurrentUser().getEmail());
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
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if(getCurrentFocus() == null){

        }
        else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
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
            case (R.id.contactsItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new AllContactsFragment());
                fragmentTransaction.commit();
                break;
            case (R.id.phoneItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new PhoneFragment());
                fragmentTransaction.commit();
                break;
            case (R.id.historialItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new HistoryFragment());
                fragmentTransaction.commit();
                break;
            case (R.id.contactsBlockedItem):
                fragmentTransaction.replace(R.id.containerOfFragments, new ContactsFragment());
                fragmentTransaction.commit();
                break;

            case (R.id.bluetoothItem):
                Intent intentOpenBluetoothSettings = new Intent();
                intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                startActivity(intentOpenBluetoothSettings);
                break;
            case (R.id.languageItem):
                Locale myLocale;
                if(item.getTitle().equals("Cambiar a Español")){
                    myLocale = new Locale("es");
                }else {
                    myLocale = new Locale("en");
                }
                Resources res = getResources();
                DisplayMetrics dm = res.getDisplayMetrics();
                Configuration conf = res.getConfiguration();
                conf.locale = myLocale;
                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(this, MainActivity.class);
                finish();
                startActivity(refresh);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.search_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}


