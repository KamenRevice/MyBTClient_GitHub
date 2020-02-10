package com.mybtclient;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Created by QQ1778257558
 * on 2020-02-09
 */
public class ConnectedThread extends Thread {

    /**
     * 当前连接的客户端BluetoothSocket
     */
    private final BluetoothSocket mmSokcet;
    /**
     * 读取数据流
     */
    private final InputStream mmInputStream;
    /**
     * 发送数据流
     */
    private final OutputStream mmOutputStream;
    /**
     * 与主线程通信Handler
     */
    private Handler mHandler;
    private String TAG = "ConnectedThread";
    private AListener listener;

    public ConnectedThread(BluetoothSocket socket, AListener listener) {
        this.listener = listener;
        mmSokcet = socket;
        // mHandler = handler;

        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        try {
            tmpIn = socket.getInputStream();
            tmpOut = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mmInputStream = tmpIn;
        mmOutputStream = tmpOut;
    }

    @Override
    public void run() {
        super.run();
        byte[] buffer = new byte[1024];
        StringBuffer dataBuffer = new StringBuffer();
        while (true) {
            try {
                // 读取数据
                int bytes = mmInputStream.read(buffer);

                if (bytes > 0) {
                    String data = new String(buffer, 0, bytes, StandardCharsets.UTF_8);
                    // 把数据发送到主线程, 此处还可以用广播
                    //Message message = mHandler.obtainMessage(Constant.MSG_GOT_DATA,data);
                    //mHandler.sendMessage(message);
                    dataBuffer.append(data);
                }

                Log.d(TAG, "messge size :" + bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (listener != null) {
                listener.rendEnd(dataBuffer.toString());
            }
        }
    }

    // 踢掉当前客户端
    public void cancle() {
        try {
            mmSokcet.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送数据
     *
     * @param data
     */
    public void write(byte[] data) {
        try {
            mmOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}