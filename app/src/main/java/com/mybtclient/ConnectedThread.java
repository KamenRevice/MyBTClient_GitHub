package com.mybtclient;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 数据处理线程
 * Created by QQ1778257558
 * on 2020-02-09
 */
public class ConnectedThread extends Thread {

    //当前连接的客户端BluetoothSocket
    private final BluetoothSocket mmSokcet;
    //读取数据流
    private final InputStream mmInputStream;
    //发送数据流
    private final OutputStream mmOutputStream;
    //与主线程通信Handler
    private Handler mHandler;
    private String TAG = "ConnectedThread";

    /**
     * @param
     * @brief ConnectedThread构造方法
     * @date 2020-02-11 下午 2:32
     */
    public ConnectedThread(BluetoothSocket socket, Handler handler) {
        mmSokcet = socket;
        mHandler = handler;

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

    /**
     * @param
     * @brief 字节数据转换成16进制字符串
     * @date 2020-02-11 下午 2:56
     */
    public static String bytesToHexString(byte[] bytes) {
        String result = "";
        for (int i = 0; i < bytes.length; i++) {
            String hexString = Integer.toHexString(bytes[i] & 0xFF);
            if (hexString.length() == 1) {
                hexString = '0' + hexString;
            }
            result += hexString.toUpperCase();
        }
        return result;
    }

    /**
     * @param
     * @brief 读取数据
     * @date 2020-02-11 下午 2:57
     */
    @Override
    public void run() {
        super.run();
        //PC机测试用字符数组
        byte[] buffer = new byte[10];
        byte[] rec = new byte[5];
        String msg = "";
        while (true) {
            try {
                // 读取数据
                int numBytes = mmInputStream.read(buffer);
//                int n=0,i=0;
//                for (i =0;i<numBytes;i++){
//                    while(buffer[i]!=0x0a){
//                        rec[n] =buffer[i];
//                    }
//                }
                msg = bytesToHexString(buffer).trim();
                //把数据发送到主线程, 此处还可以用广播
                Message message = mHandler.obtainMessage();
                message.what = 1;
                message.arg1 = numBytes;
                message.arg2 = -1;
                message.obj = msg;
                mHandler.sendMessage(message);
                //Thread.sleep(10);
                LogUtils.d("messge size :" + numBytes + "|--message content--" + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
//            catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }

    /**
     * @param
     * @brief 关掉当前连接的客户端
     * @date 2020-02-11 下午 2:40
     */
    public void cancle() {
        try {
            mmSokcet.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @brief 发送字节数据
     * @param data
     */
    public void write(byte[] data) {
        try {
            mmOutputStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 0D 回车 \r
     * 0A 换行 \n
     */
}