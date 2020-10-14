package com.example.byd.aplication.ui.lifeguardUI.lifeguard;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.byd.R;
import com.example.byd.aplication.models.Contact;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LifeguardFragment extends Fragment {


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

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        //Number Picker
        numberPicker = binding.numberPicker;
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(24);
        numberPicker.setOrientation(NumberPicker.HORIZONTAL);
        
        contactList = binding.contactList;
        contactList.setDivider(null);
        contactList.setDividerHeight(0);
        if(contacts == null) {
            contacts = new ArrayList<>();
        }

        if(contact != null){
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
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String id = firebaseAuth.getCurrentUser().getUid();
                Map<String, Object> value = new HashMap<>();
                value.put("Contacts" , contacts);
                databaseReference.child("Users").child(id).child("lifeguard").setValue(value);
                Snackbar.make(v, "YEAH" , Snackbar.LENGTH_SHORT).show();

            }
        });

        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, contacts);
        contactList.setAdapter(adapter);
        return binding.getRoot();
    }


}
