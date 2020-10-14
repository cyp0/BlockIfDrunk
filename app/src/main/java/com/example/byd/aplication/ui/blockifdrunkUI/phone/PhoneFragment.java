package com.example.byd.aplication.ui.blockifdrunkUI.phone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.models.Contact;
import com.example.byd.databinding.FragmentPhoneBinding;
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
public class PhoneFragment extends Fragment {

    private static final int REQUEST_CALL = 1;
    private EditText editTextNumber;
    private Button phoneButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public PhoneFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone, container, false);
        FragmentPhoneBinding fragmentPhoneBinding = FragmentPhoneBinding.inflate(inflater, container, false);

        firebaseAuth = FirebaseAuth.getInstance();


        editTextNumber = fragmentPhoneBinding.editTextNumber;
        phoneButton = fragmentPhoneBinding.buttonCall;
        phoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makePhoneCall();
            }
        });
        return fragmentPhoneBinding.getRoot();

    }

    private void makePhoneCall() {
        //Talvez sea necesario agregar un trim al final
        final String number = editTextNumber.getText().toString().trim();


        if (number.length() > 0) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            }

            final ArrayList<Contact> contacts = new ArrayList<>();

            String id = firebaseAuth.getCurrentUser().getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("lifeguard").child("Contacts");

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {


                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isBlocked = false;
                    for (DataSnapshot contactSnapshot : snapshot.getChildren()) {
                        Contact contact = contactSnapshot.getValue(Contact.class);
//                        contacts.add(contact);
                        if(contact.getPhoneNumber().equals(number)){
                            isBlocked = true;
                        }
                    }

                    if(isBlocked){
                        Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.containerOfFragments), "Numero bloqueado", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }else{
                        String dial = "tel:" + number;
                        startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

//            boolean duplicated = false;
//            System.out.println("******************************************************************************************");
//            System.out.println(contacts.size());
//            for(int i = 0; i < contacts.size() ; i++ ){
//                if(number.equals(contacts.get(i).getPhoneNumber())){
//                    duplicated = true;
//                    System.out.println(number);
//                    System.out.println(contacts.get(i).getPhoneNumber());
//                }
//            }
//            System.out.println(duplicated);
//
//            if (duplicated) {
//                Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.containerOfFragments), "Numero bloqueado", Snackbar.LENGTH_LONG);
//                snackbar.show();
//            } else {
//                String dial = "tel:" + number;
//                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
//            }
        } else {
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.containerOfFragments), "Introduce un numero", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CALL) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                makePhoneCall();
            } else {
                Toast.makeText(this.getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
