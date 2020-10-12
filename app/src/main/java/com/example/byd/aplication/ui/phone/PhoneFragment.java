package com.example.byd.aplication.ui.phone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.databinding.FragmentPhoneBinding;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhoneFragment extends Fragment {

    private static final int REQUEST_CALL = 1;
    private EditText editTextNumber;
    private Button phoneButton;
    public PhoneFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone, container, false);
        FragmentPhoneBinding fragmentPhoneBinding = FragmentPhoneBinding.inflate(inflater, container, false);
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
        String number = editTextNumber.getText().toString().trim();

        if(number.length() > 0){
            if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.CALL_PHONE} , REQUEST_CALL);
            }
            if(number.equals("8110261226")){
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.containerOfFragments), "Numero bloqueado", Snackbar.LENGTH_LONG );
            snackbar.show();
            }
            else {
                String dial = "tel:" + number;
                startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }
        }else {
            Toast.makeText(getActivity(), "Enter phone number", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(getActivity().findViewById(R.id.containerOfFragments), "Introduce un numero", Snackbar.LENGTH_LONG );
            snackbar.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
       if(requestCode == REQUEST_CALL){
           if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
               makePhoneCall();
           }else {
               Toast.makeText(this.getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
           }
       }
    }
}
