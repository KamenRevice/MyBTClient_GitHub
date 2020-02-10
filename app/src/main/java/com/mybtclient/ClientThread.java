package com.mybtclient;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by QQ1778257558
 * on 2020-02-09
 */
public class ClientThread extends Thread {
    public static String UUID_STRING = "00001101-0000-1000-8000-00805F9B34FB";
    private static volatile ClientThread instance;
    private String TAG = "线程";
    private Context context;
    private BluetoothSocket bluetoothSocket;
    private BluetoothDevice bluetoothDevice;
    private ConnectedThread mConnectedThread;
    private AListener listener;

    private ClientThread(Context context, BluetoothDevice bluetoothDevice) {
        this.context = context;
        this.bluetoothDevice = bluetoothDevice;

        BluetoothSocket tmp = null;
        try {
            //创建客户端socket
            tmp = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString(UUID_STRING));
        } catch (IOException e) {
            e.printStackTrace();
        }
        bluetoothSocket = tmp;
    }

    public synchronized static ClientThread getInstance(Context context, BluetoothDevice bluetoothDevice) {
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
    }

    @Override
    public void run() {
        super.run();
        //关闭设备查找
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
        manageConnectedSocket(bluetoothSocket);

    }

    private void manageConnectedSocket(BluetoothSocket bluetoothSocket) {
        //通知主线程更新UI
        mConnectedThread = new ConnectedThread(bluetoothSocket, listener);
        mConnectedThread.start();
    }

    public void cancle() {
        try {
            bluetoothSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     *
     * @param data
     */
    public void sendData(byte[] data) {
        if (mConnectedThread != null) {
            mConnectedThread.write(data);
        }
    }

    //收
    public void setListener(AListener aListener) {
        listener = aListener;
    }
}
