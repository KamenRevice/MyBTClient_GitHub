package com.mybtclient;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.HashSet;
import java.util.Set;

public class PortraitActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * 命令 字节数组
     * 2020-02-11 下午 4:40
     */
    byte[] cmd_go = {0x01, 0x0d};
    private CheckBox hex_show;
    private EditText edit_hex;
    private TextView textView;
    byte[] cmd_stop = {0x05, 0x0d};
    byte[] cmd_back = {0x02, 0x0d};
    byte[] cmd_right = {0x04, 0x0d};
    private ClientThread clientThread;
    byte[] cmd_left = {0x03, 0x0d};
    //UI资源定义
    private Button go_btn, back_btn, left_btn, right_btn, stop_btn, send_btn, qingkong_btn;
    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> bondedDeviceSet = new HashSet<>();
    private mBluetoothDevice mBluetoothDevice;
    private Handler uiHandler;
    private String send_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //启动初始化
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        clientThread.cancle();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go:
                clientThread.sendData(cmd_go);
                textView.append("发送：01\n");
                break;
            case R.id.back:
                clientThread.sendData(cmd_back);
                textView.append("发送：02\n");
                break;
            case R.id.stop:
                clientThread.sendData(cmd_stop);
                textView.append("发送：05\n");
                break;
            case R.id.turn_left:
                clientThread.sendData(cmd_left);
                textView.append("发送：03\n");
                break;
            case R.id.turn_right:
                clientThread.sendData(cmd_right);
                textView.append("发送：04\n");
                break;
            case R.id.send:
                send_text = edit_hex.getText().toString().trim();
                clientThread.sendData(send_text.getBytes());
                textView.append("发送：" + send_text + "\n");
                break;
            case R.id.qingkong:
                textView.setText("");
            default:
                LogUtils.d("onClick default");
                break;
        }
    }

    /**
     * @param
     * @brief 启动初始化
     * @date 2020-02-11 下午 12:17
     */
    void init() {
        go_btn = findViewById(R.id.go);
        back_btn = findViewById(R.id.back);
        left_btn = findViewById(R.id.turn_left);
        right_btn = findViewById(R.id.turn_right);
        stop_btn = findViewById(R.id.stop);
        send_btn = findViewById(R.id.send);
        qingkong_btn = findViewById(R.id.qingkong);

        hex_show = findViewById(R.id.hex_checkBox);
        edit_hex = findViewById(R.id.edit_hex);
        textView = findViewById(R.id.show_view);

        go_btn.setOnClickListener(this);
        back_btn.setOnClickListener(this);
        left_btn.setOnClickListener(this);
        right_btn.setOnClickListener(this);
        stop_btn.setOnClickListener(this);
        send_btn.setOnClickListener(this);
        qingkong_btn.setOnClickListener(this);

        mBluetoothDevice = new mBluetoothDevice();
        Intent intent = getIntent();
        mBluetoothDevice = (mBluetoothDevice) intent.getSerializableExtra("device");
        LogUtils.d(" Intent数据传送校验--" + mBluetoothDevice.getName() + "--//////--" + mBluetoothDevice.getAddress());

        //接受数据更新界面UI
        uiHandler = new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        textView.append("接受：" + msg.obj + "\n");
                        break;
                    default:
                        break;
                }
            }
        };

        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bondedDeviceSet = bluetoothAdapter.getBondedDevices();

        if (bondedDeviceSet.size() > 0) {
            for (BluetoothDevice device : bondedDeviceSet) {
                if (device.getName().equals(mBluetoothDevice.getName())
                        || device.getAddress().equals(mBluetoothDevice.getAddress())) {

                    clientThread = new ClientThread(PortraitActivity.this, bluetoothAdapter, device, uiHandler);
                    clientThread.start();
                    LogUtils.d("蓝牙连接成功 ");
                    break;
                }
            }
        }
    }
}
