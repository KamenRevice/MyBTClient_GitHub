package com.mybtclient;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    //获取蓝牙适配器
    public BluetoothAdapter bluetoothAdapter;
    Button openBtn, searchBtn, btn_portrait, btn_landscape;
    ListView show_lv;
    //保存搜索到的设备信息
    private List<String> bluetoothDevices = new ArrayList<>();
    //ListView的字符串数组适配器
    private ArrayAdapter<String> arrayAdapter;
    private Set<BluetoothDevice> bondedDeviceSet = new HashSet<>();
    private List<BluetoothDevice> devicesList = new ArrayList<>();
    private List<String> showList = new ArrayList<>();
    private IntentFilter intentFilter;
    private BluetoothReceiver receiver;
    private Intent nextIntent;
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
        checkBlueToothEnable();
        setListener();
//        android:screenOrientation="portrait"  //强制竖屏
//        android:screenOrientation=“landscape”//强制横屏

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {

            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));

            LogUtils.d("打开蓝牙debug");

            Log.d("MainActivity", "onOptionsItemSelected: 打开蓝牙设置");
//            Toast.makeText(this,"action_setting",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    /********* 启动检测蓝牙是否开启     *********/
    /********* 2019-11-24            *********/

    private void checkBlueToothEnable() {

        if (bluetoothAdapter == null) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("错误")
                    .setMessage("您的设备不支持蓝牙")
                    .create();
            dialog.show();
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage("蓝牙设备未开启，请打开蓝牙!")
                    .setPositiveButton("打开蓝牙", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
                            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(intent, 1);
                        }
                    })
                    .setNegativeButton("退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .create();
            dialog.show();
            //return;
        }
    }


    @Override
    public void onClick(View v) {
        isGrantedPermission(this);
        switch (v.getId()) {
            case R.id.open_bt://打开蓝牙
                if (bluetoothAdapter != null) {
                    if (bluetoothAdapter.isEnabled()) {
                        Toast.makeText(MainActivity.this, "蓝牙已打开", Toast.LENGTH_SHORT).show();
                    } else {
                        bluetoothAdapter.enable();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                    LogUtils.d("不支持蓝牙");
                }
                break;
            case R.id.search_bt://搜索蓝牙
                if (bluetoothAdapter != null) {
                    if (bluetoothAdapter.isEnabled()) {
                        if (bluetoothAdapter.startDiscovery()) {
                            Toast.makeText(MainActivity.this, "正在搜索蓝牙设备", Toast.LENGTH_SHORT).show();
                            LogUtils.d("正在搜索蓝牙设备");
                        }
                    } else {
                        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(intent, 1);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "设备不支持蓝牙", Toast.LENGTH_SHORT).show();
                    LogUtils.d("不支持蓝牙");
                }
                break;
            case R.id.btn_landscape:
                Intent intent1 = new Intent(MainActivity.this, LandscapeActivity.class);
                startActivity(intent1);
//                String ms = "01";
//                clientThread.sendData(ms.getBytes());
                LogUtils.d("启动横屏");
                break;
            case R.id.btn_portrait:
                if (clientThread != null) {
                    Intent intent2 = new Intent(MainActivity.this, PortraitActivity.class);
                    startActivity(intent2);
                }
                LogUtils.d("启动竖屏");
                break;
            default:
                break;
        }

    }

    private boolean isGrantedPermission(MainActivity mainActivity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mainActivity.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            mainActivity.requestPermissions(new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 1);
            return false;
        }
        return true;
    }

    private void setListener() {
        show_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //如果正在搜索设备，则取消搜索
                if (bluetoothAdapter.isDiscovering()) {
                    bluetoothAdapter.cancelDiscovery();
                }
                BluetoothDevice device = devicesList.get(position);
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {//如果设备未绑定
                    try {
                        Method method = BluetoothDevice.class.getMethod("createBond");
                        method.invoke(device);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {//客户端连接线程
                    clientThread = ClientThread.getInstance(MainActivity.this, device);

                    nextIntent = new Intent(MainActivity.this, PortraitActivity.class);
//                    mBluetoothDevice mDevice = new mBluetoothDevice(device);
//                    nextIntent.putExtra("device",mDevice);
                    //startActivity(intent);

//                    intent.putExtras();

                }
            }
        });
    }

    void init() {
        openBtn = findViewById(R.id.open_bt);
        searchBtn = findViewById(R.id.search_bt);
        btn_landscape = findViewById(R.id.btn_landscape); //横屏
        btn_portrait = findViewById(R.id.btn_portrait);//竖屏
        show_lv = findViewById(R.id.show_bt);

        openBtn.setOnClickListener(this);
        searchBtn.setOnClickListener(this);
        btn_portrait.setOnClickListener(this);
        btn_landscape.setOnClickListener(this);

        //获取蓝牙适配器
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //获取已配对的设备
        bondedDeviceSet = bluetoothAdapter.getBondedDevices();
        //设置arrayAdapter适配器 样式
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, showList);
        show_lv.setAdapter(arrayAdapter);

        if (bondedDeviceSet.size() > 0) {
            for (BluetoothDevice device : bondedDeviceSet) {
                showList.add(device.getName() + ":" + device.getAddress() + "已配对\n");
                devicesList.add(device);
            }
            //更新适配器
            arrayAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "没有已配对的设备", Toast.LENGTH_SHORT).show();
        }

        //蓝牙状态改变广播
        receiver = new BluetoothReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(receiver, intentFilter);
    }

    public class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String actionName = intent.getAction();
            int bondState = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.BOND_NONE);
            BluetoothDevice bluetoothDevice = null;
            switch (actionName) {
                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    Toast.makeText(MainActivity.this, "开始扫描", Toast.LENGTH_SHORT).show();
                    LogUtils.d("开始扫描");
                    break;
                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    Toast.makeText(MainActivity.this, "扫描结束", Toast.LENGTH_SHORT).show();
                    break;
                case BluetoothDevice.ACTION_FOUND:
                    LogUtils.d("扫描到设备");
                    bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (devicesList.indexOf(bluetoothDevice) == -1) {
                        showList.add(bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress() + "\t未配对");
                        devicesList.add(bluetoothDevice);
                        arrayAdapter.notifyDataSetChanged();
                    }
                    break;
                case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
                    if (bondState == BluetoothDevice.BOND_BONDED) {
                        Toast.makeText(MainActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                        showList.set(devicesList.indexOf(bluetoothDevice), bluetoothDevice.getName() + ":" + bluetoothDevice.getAddress() + "\t已配对");
                        arrayAdapter.notifyDataSetChanged();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
