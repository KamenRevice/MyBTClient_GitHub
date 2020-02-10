package com.mybtclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;

import java.io.IOException;
import java.util.UUID;

/**
 * 客户端连接线程
 * Created by QQ1778257558
 * on 2020-02-09
 */
public class ClientThread extends Thread {

    public static String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private final BluetoothAdapter bluetoothAdapter;
    private final BluetoothSocket bluetoothSocket;
    private final Handler mHandler;
    private ConnectedThread mConnectedThread;
    //private static volatile ClientThread instance;
    private Context context;

    /**
     * @param
     * @brief ClientThread构造方法
     * @date 2020-02-11 下午 2:08
     */
    public ClientThread(Context context, BluetoothAdapter adapter, BluetoothDevice device, Handler handler) {
        this.context = context;
        this.bluetoothAdapter = adapter;
        this.mHandler = handler;

        BluetoothSocket tmp = null;
        try {
            //创建客户端socket
            tmp = device.createRfcommSocketToServiceRecord(UUID.fromString(UUID_STRING));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothSocket = tmp;
    }


    /**
     * @param
     * @brief synchronized方法开启单线程
     * @date 2020-02-11 下午 2:06
     */
    /*public synchronized static ClientThread getInstance(Context context, BluetoothDevice bluetoothDevice) {
        if (instance == null) {
            synchronized (ClientThread.class) {
                if (instance == null) {
                    if (bluetoothDevice == null) {
                        instance = null;
                    } else {
                        instance = new ClientThread(context, bluetoothDevice);
                    }
                }
            }
        }
        return instance;
    }*/

    @Override
    public void run() {
        super.run();
        //关闭设备查找
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        try {
            bluetoothSocket.connect();
        } catch (IOException e) {
            //连接异常就关闭
            try {
                bluetoothSocket.close();
            } catch (IOException close) {
                close.printStackTrace();
            }
            return;
        }

        manageConnectedSocket(bluetoothSocket, mHandler);

    }

    /**
     * @param bluetoothSocket
     * @brief 数据传输线程
     * @date 2020-02-11 下午 2:05
     */
    private void manageConnectedSocket(BluetoothSocket bluetoothSocket, Handler handler) {
        //通知主线程更新UI

        mConnectedThread = new ConnectedThread(bluetoothSocket, mHandler);
        mConnectedThread.start();
    }

    /**
     * @param
     * @brief 取消连接
     * @date 2020-02-11 下午 2:05
     */
    public void cancle() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief 发送数据
     * @param
     * @date 2020-02-11 下午 2:05
     */
    public void sendData(byte[] data) {
        if (mConnectedThread != null) {
            mConnectedThread.write(data);
        }
    }

}
