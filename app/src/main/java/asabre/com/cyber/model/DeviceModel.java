package asabre.com.cyber.model;

import android.bluetooth.BluetoothDevice;

public class DeviceModel {
    String name;
    String address;
    BluetoothDevice mDevice;

    public DeviceModel(String name, String address, BluetoothDevice device) {
        this.name = name;
        this.address = address;
        mDevice = device;
    }

    public DeviceModel(String name, String address) {
        this.name = name;
        this.address = address;
        mDevice =  null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BluetoothDevice getDevice() {
        return mDevice;
    }

    public void setDevice(BluetoothDevice device) {
        mDevice = device;
    }
}
