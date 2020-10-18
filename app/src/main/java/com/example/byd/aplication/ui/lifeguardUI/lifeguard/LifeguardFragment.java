package com.example.byd.aplication.ui.lifeguardUI.lifeguard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.byd.R;
import com.example.byd.aplication.models.BlockedContact;
import com.example.byd.aplication.models.Contact;
import com.example.byd.aplication.service.BackgroundService;
import com.example.byd.aplication.service.Utils;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.aplication.ui.home.startEngine.devicesList.DevicesFragment;
import com.example.byd.aplication.ui.lifeguardUI.addContact.AddContactFragment;
import com.example.byd.databinding.LifeguardFragmentBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shawnlin.numberpicker.NumberPicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifeguardFragment extends Fragment {

    private static final int REQUEST_PACKAGE_USAGE_STATS = 2;
    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE= 3;

    private View view;
    private ListView contactList;
    private NumberPicker numberPicker;
    private ArrayList<Contact> contacts;
    private Contact contact;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private FirebaseAuth firebaseAuth;

    //Database
    DatabaseReference databaseReference;
    //Firebase storage
    private FirebaseStorage storage;
    private StorageReference storageReference;

    public LifeguardFragment() {
    }

    public LifeguardFragment(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    public ArrayList<Contact> getContacts() {
        return contacts;
    }

    public void setContacts(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }

    @SuppressLint("WrongConstant")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LifeguardFragmentBinding binding = LifeguardFragmentBinding.inflate(inflater, container, false);
        view = inflater.inflate(R.layout.lifeguard_fragment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Number Picker
        numberPicker = binding.numberPicker;

        contactList = binding.contactList;
        if (contacts == null) {
            contacts = new ArrayList<>();
        }

        if (contact != null) {
            contacts.add(contact);
        }

        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.containerOfFragments, new AddContactFragment(contacts), "lifeguard").addToBackStack("lifeguard");
                fragmentTransaction.commit();
            }
        });

        binding.confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Setting permissions
                if (Utils.checkPermission(getActivity())) {

                    //Agregar if de permiso de overlay extra
                    //If user has Android 10 or superior
                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        if (!Settings.canDrawOverlays(getActivity())) {
                            Snackbar.make(getView() , "Necesitas dar permisos a la app para acceder a esta funcion" , Snackbar.LENGTH_LONG).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    requestOverlayPermission();
                                }
                            }, 2500);
                        } else {
                           addToFirebase();
                        }
                    }else {
                       addToFirebase();
                    }
                } else {
                    Snackbar.make(getView() , "Necesitas dar permisos a la app para acceder a esta funcion" , Snackbar.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            requestUsageAccessPermission();
                        }
                    }, 2500);

                }
            }
        });

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.custom_layout, contacts);
        contactList.setAdapter(adapter);
        return binding.getRoot();
    }

    private void requestUsageAccessPermission() {

//        Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS, Uri.parse("package:" +
//                this.getActivity().getPackageName()));
//        startActivityForResult(intent, REQUEST_PACKAGE_USAGE_STATS);
        startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

    }

    private void requestOverlayPermission() {
        startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
    }

    private void addToFirebase(){
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        //Esta en GMT
        cal.add(Calendar.MINUTE, numberPicker.getValue());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
        String date = dateFormat.format(cal.getTime());
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String id = firebaseAuth.getCurrentUser().getUid();
        //Service
        //Add if, if a service already exists
        Intent intent = new Intent(getActivity(),BackgroundService.class);
        getActivity().stopService(intent);
        intent.putExtra("Date" , date);
        getActivity().startService(intent);
        //Firebase
        Map<String, Object> value = new HashMap<>();
        value.put("blocks", new BlockedContact(date, contacts));
        databaseReference.child("Users").child(id).child("lifeguard").setValue(value);
        Snackbar.make(getView(), "Contactos bloqueados", Snackbar.LENGTH_SHORT).show();
        fragmentTransaction.replace(R.id.containerOfFragments, new StartCartFragment());
        fragmentTransaction.commit();
    }
}
