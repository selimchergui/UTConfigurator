package com.adpproject.sii.utconfigurator.bluetooth;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by mac on 20/06/16.
 */
public class BluetoothThread implements Runnable {

    final byte delimiter = 33;
    int readBufferPosition = 0;
    private String btMsg;
    private BluetoothSocket mmSocket;
    private BluetoothDevice mmDevice;
    private Handler handler;
    private TextView myLabel;


    public BluetoothThread(String btMsg, BluetoothSocket mmSocket, BluetoothDevice mmDevice, Handler handler, TextView myLabel) {
        this.btMsg = btMsg;
        this.mmSocket = mmSocket;
        this.mmDevice = mmDevice;
        this.handler = handler;
        this.myLabel = myLabel;
    }


    public void run() {

        BluetoothTools.sendBtMsg(btMsg, mmDevice, mmSocket);
        while (!Thread.currentThread().isInterrupted()) {
            int bytesAvailable;
            boolean workDone = false;

            try {


                final InputStream mmInputStream;
                mmInputStream = mmSocket.getInputStream();
                bytesAvailable = mmInputStream.available();
                if (bytesAvailable > 0) {

                    byte[] packetBytes = new byte[bytesAvailable];

                    byte[] readBuffer = new byte[1024];
                    mmInputStream.read(packetBytes);

                    for (int i = 0; i < bytesAvailable; i++) {
                        byte b = packetBytes[i];
                        if (b == delimiter) {
                            byte[] encodedBytes = new byte[readBufferPosition];
                            System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                            final String data = new String(encodedBytes, "US-ASCII");
                            readBufferPosition = 0;

                            //The variable data now contains our full command
                            handler.post(new Runnable() {
                                public void run() {
                                    myLabel.setText(data);
                                }
                            });

                            workDone = true;
                            break;


                        } else {
                            readBuffer[readBufferPosition++] = b;
                        }
                    }

                    if (workDone == true) {
                        mmSocket.close();
                        break;
                    }

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}
