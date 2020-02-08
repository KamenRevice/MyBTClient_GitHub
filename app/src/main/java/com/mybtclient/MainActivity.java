package com.mybtclient;

import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkBlueToothEnable();
//        android:screenOrientation="portrait"  //强制竖屏
//        android:screenOrientation=“landscape”//强制横屏
        Button btn_portrait = findViewById(R.id.btn_portrait);
        Button btn_landscape = findViewById(R.id.btn_landscape);


        btn_landscape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LandscapeActivity.class);
                startActivity(intent);
                LogUtils.d("启动横屏");
            }
        });

        btn_portrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PortraitActivity.class);
                startActivity(intent);
                LogUtils.d("启动竖屏");
            }
        });
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



    /********* 启动检测蓝牙是否开启     *********/
    /********* 2019-11-24            *********/

    private void checkBlueToothEnable() {

        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

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
                            startActivity(new Intent(Settings.ACTION_BLUETOOTH_SETTINGS));
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
            return;
        }
    }
}
