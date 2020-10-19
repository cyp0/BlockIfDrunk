package com.example.byd.aplication.ui.lifeguardUI.contacts;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.provider.ContactsContract;
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
import com.example.byd.aplication.models.Contact;
import com.example.byd.aplication.ui.blockifdrunkUI.phone.PhoneFragment;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.databinding.FragmentAllContactsBinding;

import java.util.ArrayList;


public class AllContactsFragment extends Fragment {

    ListView contactsList;
    ArrayList<Contact> contactArrayList;

    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentAllContactsBinding binding = FragmentAllContactsBinding.inflate(inflater, container, false);
        contactsList = binding.allContactsList;
        contactArrayList = new ArrayList<>();
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
        if(cur!=null){
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
                                //Agregar contacto a la lista de bloqueos


                                break;
                        }
                        return true;
                    }
                });
            }
        });
        return binding.getRoot();
    }
}
