package com.example.byd.aplication.ui.lifeguardUI.contacts;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.models.BlockedContact;
import com.example.byd.aplication.models.Contact;
import com.example.byd.aplication.service.BackgroundService;
import com.example.byd.aplication.service.Utils;
import com.example.byd.aplication.ui.blockifdrunkUI.phone.PhoneFragment;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.databinding.FragmentAllContactsBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AllContactsFragment extends Fragment {

    ListView contactsList;
    ArrayList<Contact> contactArrayList;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;


    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private String userID, lifeguardNumber;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAllContactsBinding binding = FragmentAllContactsBinding.inflate(inflater, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference lifeguardReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("emergency");

        lifeguardReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                lifeguardNumber = ((String) snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        contactsList = binding.allContactsList;
        contactArrayList = new ArrayList<>();
        if (getActivity().getApplicationContext().checkSelfPermission(Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            showContacts();
        } else {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }
        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showContacts();
            } else {
                Toast.makeText(this.getContext(), "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showContacts() {
        ContentResolver cr = getActivity().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);


        if ((cur != null ? cur.getCount() : 0) > 0) {
            while (cur != null && cur.moveToNext()) {
                String id = cur.getString(
                        cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    while (pCur.moveToNext()) {
                        String phoneNo = pCur.getString(pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contactArrayList.add(new Contact(name, phoneNo));
                        Log.i("Contacts", "Name: " + name);
                        Log.i("Contacts", "Phone Number: " + phoneNo);
                    }
                    pCur.close();
                }
            }
        }
        if (cur != null) {
            cur.close();
        }
        final ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.custom_layout, contactArrayList);
        contactsList.setAdapter(adapter);
        contactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                PopupMenu popupMenu = new PopupMenu(getActivity(), view);
                popupMenu.getMenuInflater().inflate(R.menu.contact_menu, popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        fragmentManager = getParentFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();
                        int id = item.getItemId();
                        switch (id) {
                            case (R.id.callItem):
                                fragmentTransaction.replace(R.id.containerOfFragments, new PhoneFragment(contactArrayList.get(position).getPhoneNumber()));
                                fragmentTransaction.commit();
                                break;
                            case (R.id.blockItem):
//                                ArrayList<Contact> newContacts = (ArrayList<Contact>) snapshot.getValue();
//                                newContacts.add(new Contact(contactArrayList.get(position).getName() , contactArrayList.get(position).getPhoneNumber()));
//                                databaseReference.setValue(newContacts);
                                //Agregar contacto a la lista de bloqueos
                                databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(userID).child("lifeguard").child("blocks").child("contacts");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    ArrayList<Contact> newContacts = new ArrayList<>();
                                    boolean oneTime = true;
                                    boolean isRepeated = false;
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        GenericTypeIndicator<ArrayList<Contact>> t = new GenericTypeIndicator<ArrayList<Contact>>() {
                                        };

                                        if (snapshot.exists()) {


                                            newContacts = snapshot.getValue(t);
                                            if (oneTime) {
                                                Contact contact = new Contact(contactArrayList.get(position).getName(), contactArrayList.get(position).getPhoneNumber().replaceAll("\\s+", ""));

                                                for(int i = 0; i<newContacts.size(); i++){
                                                    if(newContacts.get(i).getPhoneNumber().equals(contact.getPhoneNumber())){
                                                        isRepeated = true;
                                                    }
                                                }

                                                if(isRepeated){
                                                    Snackbar.make(getView(), R.string.already_blocked, Snackbar.LENGTH_SHORT).show();
                                                }
                                                else if(contact.getPhoneNumber().equals(lifeguardNumber) || contact.getPhoneNumber().equals("+52" + lifeguardNumber)){
                                                    Snackbar.make(getView(), R.string.cannotBlock , Snackbar.LENGTH_LONG).show();
                                                }
                                                else {
                                                    newContacts.forEach(x -> System.out.println(x));
                                                    newContacts.add(contact);
                                                    databaseReference.setValue(newContacts);
                                                }

                                                oneTime = false;
                                            }
                                        } else {

                                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                            builder.setTitle(R.string.warning)
                                                    .setMessage(R.string.warning_message)
                                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {
                                                            if (Utils.checkPermission(getActivity())) {

                                                                //Agregar if de permiso de overlay extra
                                                                //If user has Android 10 or superior
                                                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                                                    if (!Settings.canDrawOverlays(getActivity())) {
                                                                        Snackbar.make(getView() , R.string.permission_1 , Snackbar.LENGTH_LONG).show();
                                                                        new Handler().postDelayed(new Runnable() {
                                                                            @Override
                                                                            public void run() {
                                                                                requestOverlayPermission();
                                                                            }
                                                                        }, 2500);
                                                                    } else {
                                                                        Calendar cal = Calendar.getInstance();
                                                                        cal.setTime(new Date());
                                                                        //Esta en GMT
                                                                        cal.add(Calendar.HOUR, 12);
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

                                                                        ArrayList<Contact> contacts = new ArrayList<>();
                                                                        Contact contact = new Contact(contactArrayList.get(position).getName(), contactArrayList.get(position).getPhoneNumber().replaceAll("\\s+", ""));
                                                                        contacts.add(contact);

                                                                        value.put("blocks", new BlockedContact(date, contacts));
                                                                        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                                        databaseReference1.child("Users").child(userID).child("lifeguard").setValue(value);
                                                                        Snackbar.make(getView(), R.string.contactos_bloqueados, Snackbar.LENGTH_SHORT).show();
                                                                        oneTime = false;

                                                                    }
                                                                }else {
                                                                    Calendar cal = Calendar.getInstance();
                                                                    cal.setTime(new Date());
                                                                    //Esta en GMT
                                                                    cal.add(Calendar.HOUR, 12);
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

                                                                    ArrayList<Contact> contacts = new ArrayList<>();
                                                                    Contact contact = new Contact(contactArrayList.get(position).getName(), contactArrayList.get(position).getPhoneNumber().replaceAll("\\s+", ""));
                                                                    contacts.add(contact);

                                                                    value.put("blocks", new BlockedContact(date, contacts));
                                                                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();
                                                                    databaseReference1.child("Users").child(userID).child("lifeguard").setValue(value);
                                                                    Snackbar.make(getView(), R.string.contactos_bloqueados, Snackbar.LENGTH_SHORT).show();
                                                                    oneTime = false;
                                                                }
                                                            } else {

                                                                Snackbar.make(getView() , R.string.permission_2 , Snackbar.LENGTH_LONG).show();
                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        requestUsageAccessPermission();
                                                                    }
                                                                }, 2500);

                                                            }
                                                        }
                                                    })
                                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    });

                                            builder.show();
                                            oneTime = false;
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }


                                });

                                break;
                        }
                        return true;
                    }
                });
            }
        });
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

}
