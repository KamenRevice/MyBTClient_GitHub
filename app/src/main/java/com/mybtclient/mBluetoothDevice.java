package com.mybtclient;

import java.io.Serializable;

/**
 * 蓝牙 设备类
 *
 * Created by QQ1778257558
 * on 2020-02-09
 */
public class mBluetoothDevice implements Serializable {
    private String name;
    private String address;

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
}
