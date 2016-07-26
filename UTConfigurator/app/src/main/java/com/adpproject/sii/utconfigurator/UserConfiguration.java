package com.adpproject.sii.utconfigurator;

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by schergui on 26/07/2016.
 */
public class UserConfiguration implements Parcelable {
    public static final Creator<UserConfiguration> CREATOR = new Creator<UserConfiguration>() {
        @Override
        public UserConfiguration createFromParcel(Parcel in) {
            return new UserConfiguration(in);
        }

        @Override
        public UserConfiguration[] newArray(int size) {
            return new UserConfiguration[size];
        }
    };
    private BluetoothDevice bluetoothDevice;
    private String location;

    protected UserConfiguration(Parcel in) {
        bluetoothDevice = in.readParcelable(BluetoothDevice.class.getClassLoader());
        location = in.readString();
    }

    public UserConfiguration(BluetoothDevice device) {
        this.bluetoothDevice = device;
        this.location = "<unspecified location>";
    }

    public UserConfiguration(BluetoothDevice device, String location) {
        this.bluetoothDevice = device;
        this.location = location;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(bluetoothDevice, i);
        parcel.writeString(location);
    }
}
