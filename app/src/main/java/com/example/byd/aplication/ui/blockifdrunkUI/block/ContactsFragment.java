package com.example.byd.aplication.ui.blockifdrunkUI.block;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.byd.R;
import com.example.byd.aplication.models.Contact;
import com.example.byd.databinding.FragmentContactsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ContactsFragment extends Fragment {

    private ListView listView;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public ContactsFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentContactsBinding binding = FragmentContactsBinding.inflate(inflater, container, false);
        listView = binding.listViewBlockedContacts;
        listView.setDivider(null);
        listView.setDividerHeight(0);
        final ArrayList<Contact> contactArrayList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        String id = firebaseAuth.getCurrentUser().getUid();
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, contactArrayList);


        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("lifeguard").child("Contacts");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Contact> contacts = new ArrayList<Contact>();
                for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                    Contact contact = contactSnapshot.getValue(Contact.class);
                    contactArrayList.add(contact);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        listView.setAdapter(adapter);


        return binding.getRoot();
    }
}
