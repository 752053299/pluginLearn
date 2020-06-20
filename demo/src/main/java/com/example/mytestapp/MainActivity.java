package com.example.mytestapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int MSG = 0x10;
    private int oriTime = 1;
    private String testMem;

    //
    Handler msgHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            if (msg.what == MSG){
                Log.i("msg", "handle msg 16");
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btUp = findViewById(R.id.bt_update_force);
        btUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "onClick: ");
                Toast.makeText(MainActivity.this,"oooooooo",Toast.LENGTH_SHORT).show();
                testMem = "adbcdeb";
            }
        });

        Button btc = findViewById(R.id.bt_update_voluntary);
        btc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "oritimes: " + oriTime);
                Toast.makeText(MainActivity.this,"aaaaa",Toast.LENGTH_SHORT).show();
              //  sendRunnable.changeTimes(oriTime+=4);
                testMem = null;
            }
        });

       // ProxyClickListener proxyOnClickListener = new ProxyClickListener(onClickListenerInstance);

        //   startThread(msgHandler,oriTime);
        File fi = new File("./","mo.xml");

    }







}
