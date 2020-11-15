package com.example.byd.aplication.ui.home.startEngine.devicesList;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.byd.R;
import com.example.byd.aplication.ui.MainActivity;
import com.example.byd.aplication.ui.home.startEngine.control.ControlFragment;
import com.example.byd.databinding.FragmentDevicesBinding;
import com.google.android.material.snackbar.Snackbar;

import java.util.Set;


public class DevicesFragment extends Fragment {


    private static final String TAG = "DevicesFragment";

    private ListView listViewPairedDevices;
    private ProgressBar progressBar;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";


    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter mPairedDevicesArrayAdapter;


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
        progressBar = fragmentDevicesBinding.progressBarDevices;
        listViewPairedDevices = fragmentDevicesBinding.listViewDevices;

//        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        return fragmentDevicesBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        VerificarEstadoBT();
        // Inicializa la array que contendra la lista de los dispositivos bluetooth vinculados
        mPairedDevicesArrayAdapter = new ArrayAdapter(getActivity(), R.layout.custom_layout);

        // Presenta los dispositivos vinculados en el ListView
        listViewPairedDevices.setAdapter(mPairedDevicesArrayAdapter);
        listViewPairedDevices.setOnItemClickListener(mDeviceClickListener);
        // Obtiene el adaptador local Bluetooth adapter
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();



            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

            // Adiciona un dispositivos emparejado al array
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            }

        //---------------------------------------------------------------------
    }


    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {

        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            System.out.println(address);


            if(address.equals("45:54:14:04:7B:07")) {
                fragmentManager = getParentFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.containerOfFragments, new ControlFragment(address), "devices").addToBackStack("devices");
                fragmentTransaction.commit();
            }else {
                Snackbar.make(getView(), R.string.wrong_pick, Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    private void VerificarEstadoBT() {
        // Comprueba que el dispositivo tiene Bluetooth y que está encendido.
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getActivity(), R.string.bluetooth_unsupported, Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, getString(R.string.bt_activated));
            } else {
                //Solicita al usuario que active Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
