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

    ListView listViewPairedDevices;

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
        listViewPairedDevices = fragmentDevicesBinding.listViewDevices;
        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
//        View view = inflater.inflate(R.layout.fragment_devices, container, false);
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

    // Configura un (on-click) para la lista
    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView av, View v, int arg2, long arg3) {

            // Obtener la dirección MAC del dispositivo, que son los últimos 17 caracteres en la vista
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            System.out.println(address);

            // Realiza un intent para iniciar la siguiente actividad
            // mientras toma un EXTRA_DEVICE_ADDRESS que es la dirección MAC.
            if(address.equals("45:54:14:04:7B:07")) {
                fragmentTransaction.replace(R.id.containerOfFragments, new ControlFragment(address), "devices").addToBackStack("devices");
                fragmentTransaction.commit();
            }else {
                Snackbar.make(getView(), "No elegiste el carro", Snackbar.LENGTH_SHORT).show();
            }
        }
    };

    private void VerificarEstadoBT() {
        // Comprueba que el dispositivo tiene Bluetooth y que está encendido.
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(getActivity(), "El dispositivo no soporta Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (mBtAdapter.isEnabled()) {
                Log.d(TAG, "...Bluetooth Activado...");
            } else {
                //Solicita al usuario que active Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}
