package com.adpproject.sii.utconfigurator;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.adpproject.sii.utconfigurator.bluetooth.BluetoothTools;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

/**
 * Created by mac on 17/06/16.
 */
public class MainFragment extends Fragment {

    BluetoothDevice mDevice = null;
    private EditText utNameText;
    private EditText locationText;
    private BluetoothSocket mSocket;
    private TextView myLabel = null;
    private Button goButton = null;
    private Button searchButton = null;
    private Handler handler = null;
    private UserConfiguration theChosenDevice;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        utNameText = (EditText) view.findViewById(R.id.input_ut_name);
        locationText = (EditText) view.findViewById(R.id.input_location);
        myLabel = (TextView) view.findViewById(R.id.infoResult);
        goButton = (Button) view.findViewById(R.id.go_btn);
        searchButton = (Button) view.findViewById(R.id.find_btn);
        handler = new Handler();


        goButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //(new Thread(new BluetoothThread("LeMessage", mSocket, mDevice, handler, myLabel))).start();

                try {
                    BluetoothTools.init(theChosenDevice.getBluetoothDevice());
                    BluetoothTools.write("UT name:" + utNameText.getText() + "\nLocation :" + locationText.getText());
//                    BluetoothTools.run();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


                Toast.makeText(getActivity(), "Pushed configuration" + "\nUT Name :" + utNameText.getText().toString() + "\nLocation :" + locationText.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });


        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Activity btDevicesActivity=new BtDevicesActivity();
                Intent btActivity = new Intent(getActivity(), BtDevicesActivity.class);
                //btDevicesActivity.setArguments(getActivity().getIntent().getExtras());
                startActivityForResult(btActivity, 113);


            }
        });


        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //register local BT adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //check to see if there is BT on the Android device at all


//        IntentFilter filter = new IntentFilter();
//        filter.addAction(BluetoothDevice.ACTION_FOUND);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
//        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
//        getActivity().registerReceiver(mReceiver, filter);
//        mBluetoothAdapter.startDiscovery();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals("raspberrypi-0")) //Note, you will need to change this to match the name of your device
                {
                    Log.e("Aquarium", device.getName());
                    mDevice = device;
                    break;
                }
            }
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {

        } catch (NullPointerException e) {
        }


    }

  /*  private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action))
            {
            //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Toast.makeText (getActivity(),"Found device " + device.getName(), Toast.LENGTH_SHORT).show();
                }
        }
    };
*/

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
//            Log.d("selim", data.getStringExtra("utName"));
            Bundle b = data.getExtras();
            theChosenDevice = b.getParcelable("chosenDevice");
            utNameText.setText(theChosenDevice.getBluetoothDevice().getName());
            locationText.setText(theChosenDevice.getLocation());
            // do something with B's return values
        }
    }


}
