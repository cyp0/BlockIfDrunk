package com.example.byd.aplication.ui.home.startEngine.devicesList;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.ui.home.startEngine.control.ControlFragment;
import com.example.byd.databinding.FragmentDevicesBinding;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevicesFragment extends Fragment {

    public DevicesFragment() {
        // Required empty public constructor
    }

    private Button button;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        FragmentDevicesBinding fragmentDevicesBinding = FragmentDevicesBinding.inflate(inflater, container, false);
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        button = fragmentDevicesBinding.devicesButton;
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentTransaction.replace(R.id.containerOfFragments, new ControlFragment(), "devices").addToBackStack("devices");
                fragmentTransaction.commit();
            }
        });
        return fragmentDevicesBinding.getRoot();
    }

}
