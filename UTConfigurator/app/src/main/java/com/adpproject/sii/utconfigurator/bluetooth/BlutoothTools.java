package com.adpproject.sii.utconfigurator.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * Created by schergui on 26/07/2016.
 */


public class BlutoothTools {
    private static OutputStream outputStream;
    private static InputStream inStream;

    public static void init() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) {
            if (blueAdapter.isEnabled()) {
                Set<BluetoothDevice> bondedDevices = blueAdapter.getBondedDevices();

                if (bondedDevices.size() > 0) {
                    Object[] devices = bondedDevices.toArray();
                    BluetoothDevice device = (BluetoothDevice) devices[0];

                    ParcelUuid[] uuids = device.getUuids();
                    //BluetoothSocket socket = device.createRfcommSocketToServiceRecord(uuids[0].getUuid());

                    Method method = device.getClass().getMethod("createRfcommSocket", int.class);
                    BluetoothSocket socket = (BluetoothSocket) method.invoke(device, 1);

                    socket.connect();


                    outputStream = socket.getOutputStream();
                    inStream = socket.getInputStream();
                }

                Log.e("error", "No appropriate paired devices.");
            } else {
                Log.e("error", "Bluetooth is disabled.");
            }
        }
    }

    public static void write(String s) throws IOException {
        outputStream.write(s.getBytes());
    }

    public static void run() {
        final int BUFFER_SIZE = 1024;
        byte[] buffer = new byte[BUFFER_SIZE];
        int bytes = 0;
        int b = BUFFER_SIZE;

        while (true) {
            try {
                bytes = inStream.read(buffer, bytes, BUFFER_SIZE - bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
