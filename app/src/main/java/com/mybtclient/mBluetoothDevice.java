package com.mybtclient;

import android.bluetooth.BluetoothDevice;

import java.io.Serializable;

/**
 * Created by QQ1778257558
 * on 2020-02-09
 */
public class mBluetoothDevice implements Serializable {
    private BluetoothDevice device;

    public mBluetoothDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }
}
