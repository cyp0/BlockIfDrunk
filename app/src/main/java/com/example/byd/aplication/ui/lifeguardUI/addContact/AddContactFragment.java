package com.example.byd.aplication.ui.lifeguardUI.addContact;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.byd.R;
import com.example.byd.aplication.models.Contact;
import com.example.byd.aplication.ui.lifeguardUI.lifeguard.LifeguardFragment;
import com.example.byd.databinding.FragmentAddContactBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddContactFragment extends Fragment {
    private Contact contact;
    private ArrayList<Contact> contacts;

    private EditText editTextContact;
    private EditText editTextPhone;
    private BootstrapButton button;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    private DatabaseReference userReference;
    private DatabaseReference emergencyReference;
    private String lifeguardNumber;

    public AddContactFragment() {
        // Required empty public constructor
    }

    public AddContactFragment(ArrayList<Contact> contacts) {
        this.contacts = contacts;
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAddContactBinding binding = FragmentAddContactBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_add_contact, container, false);
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
//
        editTextContact = binding.contactName;
        editTextPhone = binding.contactNumber;
        button = binding.addContact;

        String id = FirebaseAuth.getInstance().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        emergencyReference = userReference.child("emergency");

        emergencyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lifeguardNumber = ((String) snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        contacts = new ArrayList<>();
//        contact = new Contact(contactName, phone);
//        contacts.add(contact);



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contactName = editTextContact.getText().toString();
                String phone = editTextPhone.getText().toString();
                boolean duplicated = false;
                for(int i = 0; i < contacts.size() ; i++ ){
                    if(phone.equals(contacts.get(i).getPhoneNumber())){
                        duplicated = true;
                    }
                }

                if(phone.equals(lifeguardNumber) || phone.equals("+52" + lifeguardNumber)){
                    Snackbar.make(v, R.string.cannotBlock , Snackbar.LENGTH_LONG).show();
                }
                else if(duplicated){
                    Snackbar.make(v, R.string.already_blocked , Snackbar.LENGTH_LONG).show();
//                    try {
//                        Thread.sleep(3000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
                }else {
                    contacts.add(new Contact(contactName, phone));
                    fragmentTransaction.replace(R.id.containerOfFragments, new LifeguardFragment(contacts));
                    fragmentTransaction.commit();
                }

            }
        });


        return binding.getRoot();
    }

}
