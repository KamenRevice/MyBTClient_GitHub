package com.mybtclient;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class PortraitActivity extends AppCompatActivity implements View.OnClickListener {

    private Button go_btn, back_btn, left_btn, right_btn, stop_btn, send_btn, qingkong_btn;
    private CheckBox hex_show;
    private EditText edit_hex;
    private TextView textView;

    private ClientThread clientThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        init();
//        mBluetoothDevice mDevice = (mBluetoothDevice) getIntent().getSerializableExtra("device");
//        clientThread = new ClientThread(PortraitActivity.this,mDevice.getDevice());
        clientThread = ClientThread.getInstance(PortraitActivity.this, null);
        clientThread.setListener(new AListener() {
            @Override
            public void rendEnd(final String data) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        textView.setText(data);
                    }
                });
            }
        });
        clientThread.start();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.go:
                clientThread.sendData("01".getBytes());
                textView.setText("go");
                break;
            case R.id.back:
                textView.setText("back");
                break;
            case R.id.stop:
                textView.setText("stop");
                break;
            case R.id.turn_left:
                //textView.setText("left");
                textView.append("145211");
                break;
            case R.id.turn_right:
                textView.setText("right");
                break;
            case R.id.send:
                textView.setText("send");
                break;
            case R.id.qingkong:
                textView.setText(" ");
            default:
                LogUtils.d("onClick default");
                break;
        }
    }

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

    }
}
