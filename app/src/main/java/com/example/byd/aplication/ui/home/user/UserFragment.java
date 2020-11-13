package com.example.byd.aplication.ui.home.user;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.byd.R;
import com.example.byd.aplication.LoginActivity;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.databinding.UserFragmentBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class UserFragment extends Fragment {

    private EditText name;
    private EditText emergencyNumber;
    private EditText address1;
    private EditText address2;
    private BootstrapButton saveButton;
    private Button deleteButton;

    private DatabaseReference userReference;
    private DatabaseReference nameReference;
    private DatabaseReference emergencyReference;
    private DatabaseReference address1Reference;
    private DatabaseReference address2Reference;

    private FirebaseAuth firebaseAuth;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    public static UserFragment newInstance() {
        return new UserFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        UserFragmentBinding binding = UserFragmentBinding.inflate(inflater, container, false);
        name = binding.newUserName;
        emergencyNumber = binding.editTextPhoneLifeguard;
        address1 = binding.editTextColonia;
        address2 = binding.editTextCalle;

        String id = FirebaseAuth.getInstance().getUid();
        userReference = FirebaseDatabase.getInstance().getReference().child("Users").child(id);
        nameReference = userReference.child("nombre");
        emergencyReference = userReference.child("emergency");
        address1Reference = userReference.child("colonia");
        address2Reference = userReference.child("calleYNumero");

        nameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText((String) snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        emergencyReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                emergencyNumber.setText(((String) snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        address1Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                address1.setText(((String) snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        address2Reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                address2.setText(((String) snapshot.getValue()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        saveButton = binding.saveButton;
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String nombre = name.getText().toString().trim();
                final String emergencyPhone = emergencyNumber.getText().toString().trim();
                final String colonia = address1.getText().toString().trim();
                final String calleYNumero = address2.getText().toString().trim();

                Map<String, Object> value = new HashMap<>();
                value.put("nombre", nombre);
                value.put("emergency", emergencyPhone);
                value.put("colonia", colonia);
                value.put("calleYNumero", calleYNumero);
                userReference.setValue(value);
                Snackbar.make(getView(), "Guardado" , Snackbar.LENGTH_SHORT).show();
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerOfFragments, new StartCartFragment());
                fragmentTransaction.commit();

            }
        });

//        deleteButton = binding.deleteAccountButton;
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                user.delete();
//                getActivity().startActivity(new Intent(getActivity(),LoginActivity.class));
//            }
//        });
        return binding.getRoot();
    }


}
