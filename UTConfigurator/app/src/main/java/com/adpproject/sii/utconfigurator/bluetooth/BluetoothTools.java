package com.adpproject.sii.utconfigurator.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by schergui on 26/07/2016.
 */


public class BluetoothTools {
    private static OutputStream outputStream;
    private static InputStream inStream;

    public static void init(BluetoothDevice chosenDevice) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        BluetoothAdapter blueAdapter = BluetoothAdapter.getDefaultAdapter();
        if (blueAdapter != null) if (blueAdapter.isEnabled()) {

            Method method = chosenDevice.getClass().getMethod("createRfcommSocket", int.class);
            BluetoothSocket socket = (BluetoothSocket) method.invoke(chosenDevice, 1);

            socket.connect();
            outputStream = socket.getOutputStream();
            //inStream = socket.getInputStream(); //at this stage we don't need to receive data from raspberry pi //Selim//


            Log.e("error", "No appropriate paired devices.");
        } else {
            Log.e("error", "Bluetooth is disabled.");
        }
    }

    public static void write(String s) throws IOException {
        outputStream.write(s.getBytes());
        outputStream.flush();
    }

    public static void disconnect(BluetoothDevice chosenDevice) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = chosenDevice.getClass().getMethod("createRfcommSocket", int.class);
        BluetoothSocket socket = (BluetoothSocket) method.invoke(chosenDevice, 1);

        socket.close();
    }

// run() is not used in this case //Selim//
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
