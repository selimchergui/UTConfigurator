package com.adpproject.sii.utconfigurator;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

/**
 * Created by mac on 21/06/16.
 */
public class BtDevicesActivity extends Activity {

    static HandleSearch handleSearch;
    ListView listViewPaired;
    ListView listViewDetected;
    ArrayList<String> arrayListpaired;
    Button buttonSearch;
    ArrayAdapter<String> adapter, detectedAdapter;
    BluetoothDevice bdDevice;
    BluetoothClass bdClass;
    Intent theIntent;

    ArrayList<BluetoothDevice> arrayListPairedBluetoothDevices;
    //    ListItemClickedonPaired listItemClickedonPaired;
    BluetoothAdapter bluetoothAdapter = null;
    ArrayList<BluetoothDevice> arrayListBluetoothDevices = null;
    ListItemClicked listItemClicked;
    private ButtonClicked clicked;
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Message msg = Message.obtain();
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {


                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (arrayListBluetoothDevices.size() < 1) // this checks if the size of bluetooth device is 0,then add the
                {                                           // device to the arraylist.
                    detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                    arrayListBluetoothDevices.add(device);
                    detectedAdapter.notifyDataSetChanged();
                } else {
                    boolean flag = true;    // flag to indicate that particular device is already in the arlist or not
                    for (int i = 0; i < arrayListBluetoothDevices.size(); i++) {
                        if (device.getAddress().equals(arrayListBluetoothDevices.get(i).getAddress())) {
                            flag = false;
                        }
                    }
                    if (flag == true) {
                        detectedAdapter.add(device.getName() + "\n" + device.getAddress());
                        arrayListBluetoothDevices.add(device);
                        detectedAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_devices);
        listViewDetected = (ListView) findViewById(R.id.listViewDetected);
        buttonSearch = (Button) findViewById(R.id.refresh_btn);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        clicked = new ButtonClicked();
        handleSearch = new HandleSearch();
        arrayListPairedBluetoothDevices = new ArrayList<BluetoothDevice>();

        theIntent = new Intent();
        /*
         * the above declaration is just for getting the paired bluetooth devices;
         * this helps in the removing the bond between paired devices.
         */
        arrayListBluetoothDevices = new ArrayList<BluetoothDevice>();
        detectedAdapter = new ArrayAdapter<String>(BtDevicesActivity.this, R.layout.bt_list_item);
        listViewDetected.setAdapter(detectedAdapter);
        listItemClicked = new ListItemClicked();
        detectedAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        buttonSearch.setOnClickListener(clicked);
        listViewDetected.setOnItemClickListener(listItemClicked);

    }

    private void callThread() {
        new Thread(){
            public void run() {
                Boolean isBonded = false;
                try {
                    isBonded = createBond(bdDevice);
                    if(isBonded)
                    {
                        arrayListpaired.add(bdDevice.getName()+"\n"+bdDevice.getAddress());
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }//connect(bdDevice);
                Log.i("Log", "The bond is created: "+isBonded);
            }
        }.start();
    }
    private Boolean connect(BluetoothDevice bdDevice) {
        Boolean bool = false;
        try {
            Log.i("Log", "service method is called ");
            Class cl = Class.forName("android.bluetooth.BluetoothDevice");
            Class[] par = {};
            Method method = cl.getMethod("createBond", par);
            Object[] args = {};
            bool = (Boolean) method.invoke(bdDevice);//, args);// this invoke creates the detected devices paired.
        } catch (Exception e) {
            Log.i("Log", "Inside catch of serviceFromDevice Method");
            e.printStackTrace();
        }
        return bool.booleanValue();
    }


    public boolean removeBond(BluetoothDevice btDevice)
            throws Exception {
        Class btClass = Class.forName("android.bluetooth.BluetoothDevice");
        Method removeBondMethod = btClass.getMethod("removeBond");
        Boolean returnValue = (Boolean) removeBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }


    public boolean createBond(BluetoothDevice btDevice)
            throws Exception {
        Class class1 = Class.forName("android.bluetooth.BluetoothDevice");
        Method createBondMethod = class1.getMethod("createBond");
        Boolean returnValue = (Boolean) createBondMethod.invoke(btDevice);
        return returnValue.booleanValue();
    }

    private void startSearching() {
        Log.i("Log", "in the start searching method");
        IntentFilter intentFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        BtDevicesActivity.this.registerReceiver(myReceiver, intentFilter);
        bluetoothAdapter.startDiscovery();
    }



    private void getPairedDevices() {
           Set<BluetoothDevice> pairedDevice = bluetoothAdapter.getBondedDevices();
           if(pairedDevice.size()>0)
           {
               for(BluetoothDevice device : pairedDevice)
               {
                   arrayListpaired.add(device.getName()+"\n"+device.getAddress());
                   arrayListPairedBluetoothDevices.add(device);
               }
           }
           adapter.notifyDataSetChanged();
       }


    class ListItemClicked implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // TODO Auto-generated method stub
            bdDevice = arrayListBluetoothDevices.get(position);
//            bdClass = arrayListBluetoothDevices.get(position);
            Log.i("Log", "The device : " + bdDevice.toString());
            Boolean isBonded = false;
            theIntent.putExtra("chosenDevice", new UserConfiguration(bdDevice));

            try {
                isBonded = createBond(bdDevice);
                if (isBonded) {
                    arrayListpaired.add(bdDevice.getName() + "\n" + bdDevice.getAddress());
                    adapter.notifyDataSetChanged();
                    getPairedDevices();
                    adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            //      connect(bdDevice);
            Log.i("Log", "The bond is created: " + isBonded);

            setResult(RESULT_OK, theIntent);

            finish();
        }
    }


    class ButtonClicked implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            arrayListBluetoothDevices.clear();
            startSearching();
        }
    }


    class HandleSearch extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 111:

                    break;

                default:
                    break;
            }
        }
    }
}