package com.example.byd.aplication.ui.home.startEngine.control;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.example.byd.R;
import com.example.byd.aplication.models.History;
import com.example.byd.aplication.ui.home.startEngine.StartCartFragment;
import com.example.byd.databinding.FragmentControlBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;


public class ControlFragment extends Fragment {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;


    private FusedLocationProviderClient fusedLocationProviderClient;

    private DatabaseReference firebaseDatabase;

    private ImageButton buttonForward, buttonBackward, buttonTurnLeft, buttonTurnRight, buttonStop;
    private Button startCarButton;
    private BootstrapButton disconnectButton;
    private ProgressBar progressBar;
    private TextView textViewBreath;
    private boolean drunk = true;

    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread MyConexionBT;
    // Identificador unico de servicio - SPP UUID
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    // String para la direccion MAC
    private String address = null;

    //-------------------------------------------
    public ControlFragment() {
    }

    public ControlFragment(String address) {
        this.address = address;
    }


    @SuppressLint("HandlerLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentControlBinding binding = FragmentControlBinding.inflate(inflater, container, false);

        progressBar = binding.progressBar2;
        textViewBreath = binding.textViewBreath;

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {

                    //Interacción con los datos de ingreso
                }
            }
        };

        btAdapter = BluetoothAdapter.getDefaultAdapter(); // get Bluetooth adapter
        VerificarEstadoBT();

        fragmentManager = getParentFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        startCarButton = binding.startCartButton2;
        startCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.instructions)
                        .setMessage(R.string.breathAnalysis)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressBar.setVisibility(View.VISIBLE);
                                textViewBreath.setVisibility(View.VISIBLE);
                                MyConexionBT.write("d");
//                //Esperar 15 segundos para checar nuevamente el estado de drunk

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (!drunk) {
                                            Snackbar.make(v, R.string.good_trip, Snackbar.LENGTH_SHORT).show();
                                            startCarButton.setVisibility(View.GONE);
                                            progressBar.setVisibility(View.GONE);
                                            textViewBreath.setVisibility(View.GONE);
                                            buttonForward.setVisibility(View.VISIBLE);
                                            buttonBackward.setVisibility(View.VISIBLE);
                                            buttonStop.setVisibility(View.VISIBLE);
                                            buttonTurnLeft.setVisibility(View.VISIBLE);
                                            buttonTurnRight.setVisibility(View.VISIBLE);
                                            disconnectButton.setVisibility(View.VISIBLE);
                                        } else {
//                    MyConexionBT.write("d");
                                            drunkEvent();
                                        }
                                    }
                                }, 16000L);
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

//                try {
//                    Thread.sleep(16000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                if (!drunk) {
//                    System.out.println("Encendiendo...");
//                    startCarButton.setVisibility(View.GONE);
//                    buttonForward.setVisibility(View.VISIBLE);
//                    buttonBackward.setVisibility(View.VISIBLE);
//                    buttonStop.setVisibility(View.VISIBLE);
//                    buttonTurnLeft.setVisibility(View.VISIBLE);
//                    buttonTurnRight.setVisibility(View.VISIBLE);
//                    disconnectButton.setVisibility(View.VISIBLE);
//                } else {
//                    System.out.println("Esta Borracho");
//                    drunkEvent();
//                }

            }
        });

        buttonForward = binding.imageButtonForward;
        buttonForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("1");
            }
        });

        buttonBackward = binding.imageButtonBackward;
        buttonBackward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("2");
            }
        });

        buttonTurnLeft = binding.imageButtonTurnLeft;
        buttonTurnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("4");
            }
        });

        buttonTurnRight = binding.imageButtonTurnRight;
        buttonTurnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyConexionBT.write("3");
            }
        });

        buttonStop = binding.imageButtonStop;
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyConexionBT.write("5");
            }
        });

        disconnectButton = binding.buttonDisconnect;
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (btSocket != null) {
                    try {
                        btSocket.close();
                    } catch (IOException e) {
                        Toast.makeText(getActivity(), "Error", Toast.LENGTH_SHORT).show();
                        ;
                    }
                }

                fragmentTransaction.replace(R.id.containerOfFragments, new StartCartFragment());
                fragmentTransaction.commit();
            }
        });
        return binding.getRoot();
    }

    private void drunkEvent() {
        //Obtener localizacion del celular

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        final Double[] lat = new Double[1];
        final Double[] lon = new Double[1];
        if (getActivity().getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && getActivity().getApplicationContext().checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        lat[0] = location.getLatitude();
                        lon[0] = location.getLongitude();
                        String id = FirebaseAuth.getInstance().getUid();
                        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("emergency");
                        firebaseDatabase.addValueEventListener(new ValueEventListener() {

                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                System.out.println("***Prueba***");
                                String lifeguardNumber = (String) snapshot.getValue();
                                System.out.println(lat[0]);
                                System.out.println(lon[0]);
                                System.out.println(lifeguardNumber);
                                try {
                                    sendMessage(lat[0], lon[0], lifeguardNumber);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }

                        });
                    }
                }
            });



        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.SEND_SMS}, 1);

        }

        //Obtener el contacto lifeguard del usuario
    }

    private void sendMessage(Double lat, Double lon, String lifeguardNumber) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        addresses = geocoder.getFromLocation(lat, lon, 1);

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

        System.out.println(address);



        String message = (getString(R.string.sms_p1) + address + " " + getString(R.string.sms_p2));
        System.out.println(message);
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("+52" + lifeguardNumber, null, message, null, null);
        Snackbar.make(getView(), R.string.sms_success, Snackbar.LENGTH_LONG).show();

//        StringBuilder sbValue = new StringBuilder(sbMethod());
//        PlacesTask placesTask = new PlacesTask();
//        placesTask.execute(sbValue.toString());
        addToHistory(address, lifeguardNumber);
        fragmentTransaction.replace(R.id.containerOfFragments, new StartCartFragment());
        fragmentTransaction.commit();

    }

//    public StringBuilder sbMethod() {
//
//        //use your current location here
//        double mLatitude = 25.6030777;
//        double mLongitude = -100.2618924;
//
//        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
//        sb.append("location=" + mLatitude + "," + mLongitude);
//        sb.append("&radius=5000");
//        sb.append("&types=" + "restaurant");
//        sb.append("&sensor=true");
//        sb.append("&key=AIzaSyAcYrrhBn-0XzbdgB3Fnt-x5UNEpv-pdyM");
//
//        Log.d("Map", "api: " + sb.toString());
//
//        return sb;
//    }
//
//    private class PlacesTask extends AsyncTask<String, Integer, String> {
//
//        String data = null;
//
//        // Invoked by execute() method of this object
//        @Override
//        protected String doInBackground(String... url) {
//            try {
//                data = downloadUrl(url[0]);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        // Executed after the complete execution of doInBackground() method
//        @Override
//        protected void onPostExecute(String result) {
//            ParserTask parserTask = new ParserTask();
//
//            // Start parsing the Google places in JSON format
//            // Invokes the "doInBackground()" method of the class ParserTask
//            parserTask.execute(result);
//        }
//    }
//
//    private String downloadUrl(String strUrl) throws IOException {
//        String data = "";
//        InputStream iStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(strUrl);
//
//            // Creating an http connection to communicate with url
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            // Connecting to url
//            urlConnection.connect();
//
//            // Reading data from url
//            iStream = urlConnection.getInputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//            data = sb.toString();
//
//            br.close();
//
//        } catch (Exception e) {
//            Log.d("Exception while downloading url", e.toString());
//        } finally {
//            iStream.close();
//            urlConnection.disconnect();
//        }
//        return data;
//    }
//
//    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {
//
//        JSONObject jObject;
//
//        // Invoked by execute() method of this object
//        @Override
//        protected List<HashMap<String, String>> doInBackground(String... jsonData) {
//
//            List<HashMap<String, String>> places = null;
//            Place_JSON placeJson = new Place_JSON();
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//
//                places = placeJson.parse(jObject);
//
//            } catch (Exception e) {
//                Log.d("Exception", e.toString());
//            }
//            return places;
//        }
//
//        // Executed after the complete execution of doInBackground() method
//        @Override
//        protected void onPostExecute(List<HashMap<String, String>> list) {
//
//            Log.d("Map", "list size: " + list.size());
//            // Clears all the existing markers;
////            mGoogleMap.clear();
//
//            for (int i = 0; i < list.size(); i++) {
//
//                // Creating a marker
////                MarkerOptions markerOptions = new MarkerOptions();
//
//                // Getting a place from the places list
//                HashMap<String, String> hmPlace = list.get(i);
//
//
//                // Getting latitude of the place
//                double lat = Double.parseDouble(hmPlace.get("lat"));
//
//                // Getting longitude of the place
//                double lng = Double.parseDouble(hmPlace.get("lng"));
//
//                // Getting name
//                String name = hmPlace.get("place_name");
//                System.out.println("Nombre Restaurante: " + name);
//                Log.d("Map", "place: " + name);
//
//                // Getting vicinity
////                String vicinity = hmPlace.get("vicinity");
////
////                LatLng latLng = new LatLng(lat, lng);
////
////                // Setting the position for the marker
////                markerOptions.position(latLng);
////
////                markerOptions.title(name + " : " + vicinity);
////
////                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
////
////                // Placing a marker on the touched position
////                Marker m = mGoogleMap.addMarker(markerOptions);
//
//            }
//        }
//    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        //crea un conexion de salida segura para el dispositivo usando el servicio UUID
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }

    //Esto provoca que se tarde en abrir el fragment en devices fragment
    @Override
    public void onResume() {
        super.onResume();
//ESTO PUEDE DAR ERROR, CAMBIAR A MI VERSION
//        Intent intent = getIntent();
//        address = intent.getStringExtra(DispositivosVinculados.EXTRA_DEVICE_ADDRESS);
        //Setea la direccion MAC
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getActivity(), R.string.socket_failed, Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
            }
        }
        MyConexionBT = new ConnectedThread(btSocket);
        MyConexionBT.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        try { // Cuando se sale de la aplicación esta parte permite que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {
        }
    }

    //Comprueba que el dispositivo Bluetooth
    //está disponible y solicita que se active si está desactivado
    private void VerificarEstadoBT() {

        if (btAdapter == null) {
            Toast.makeText(getActivity(), R.string.bluetooth_unsupported, Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //Crea la clase que permite crear el evento de conexion
    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] byte_in = new byte[1];
            // Se mantiene en modo escucha para determinar el ingreso de datos
            while (true) {
                try {
                    mmInStream.read(byte_in);
                    char ch = (char) byte_in[0];
                    System.out.println(ch);
                    if (ch == 's') {
//                        Toast.makeText(getActivity(), "Esta Borracho", Toast.LENGTH_SHORT).show();
                        drunk = true;
                        System.out.println("Esta Borracho");

                    }
                    if (ch == 'n') {
                        drunk = false;
                        System.out.println("No esta Borracho " + drunk);
                    }
                    bluetoothIn.obtainMessage(handlerState, ch).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        //Envio de trama
        public void write(String input) {
            try {
                mmOutStream.write(input.getBytes());
            } catch (IOException e) {

                Toast.makeText(getActivity(), R.string.connection_failed, Toast.LENGTH_LONG).show();

                fragmentTransaction.replace(R.id.containerOfFragments, new StartCartFragment());
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                drunkEvent();
            } else {
                Toast.makeText(this.getContext(), R.string.permission_denied, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void addToHistory(String ubication, String lifeguard){
        String id = FirebaseAuth.getInstance().getUid();


        firebaseDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("history").child("records");
        firebaseDatabase.addValueEventListener(new ValueEventListener() {
            ArrayList<History> newHistory = new ArrayList<>();
            boolean oneTime = true;
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GenericTypeIndicator<ArrayList<History>> t = new GenericTypeIndicator<ArrayList<History>>() {
                };
                if(oneTime) {
                    Map<String, Object> value = new HashMap<>();
                    Date date = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    ArrayList<History> histories = new ArrayList<>();


                    if(snapshot.getValue(t) != null) {
                        newHistory = snapshot.getValue(t);
                        newHistory.add((new History(formatter.format(date), ubication, lifeguard)));
                        firebaseDatabase.setValue(newHistory);
                        System.out.println("sdjadjsakld");
                    }else {
                        histories.add(new History(formatter.format(date), ubication, lifeguard));
                        value.put("records", histories);
                        FirebaseDatabase.getInstance().getReference().child("Users").child(id).child("history").setValue(value);
                    }

                    oneTime = false;
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }
}
