package com.sonu.clientapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private TextView random_num;
    public static final int RANDOM_NUMBER = 0;
    private Messenger requestMsg, responseMsg;
    private ServiceConnection connection;
    private Intent intent;
    private static final String TAG = "###MainActivity";
    private boolean mBind = false;

     class ResponseHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            Log.d(TAG, "handleMessage: "+msg.toString());
            random_num.setText(String.valueOf(msg.arg1));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        random_num = findViewById(R.id.random_num);
        intent = new Intent();
        intent.setComponent(new ComponentName("com.sonu.learning", "com.sonu.learning.services.ServerSideService"));
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "onServiceConnected: ");
                mBind = true;
                requestMsg = new Messenger(service);
                responseMsg = new Messenger(new ResponseHandler());

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "onServiceDisconnected: ");
                mBind = false;
            }
        };

    }

    public void bindToService(View view) {
        bindService(intent, connection, BIND_AUTO_CREATE);
        Toast.makeText(this, "Service Binded", Toast.LENGTH_SHORT).show();

    }

    public void getRandomNum(View view) {
        if (mBind) {
            Message requestMessage = Message.obtain(null, RANDOM_NUMBER);
            requestMessage.replyTo = responseMsg;
            try {
                requestMsg.send(requestMessage);
            } catch (RemoteException e) {
                e.printStackTrace();
                Log.i(TAG, "getRandomNum: error  " + e.getMessage());
            }
        }
    }

    public void unBindService(View view) {
        if (mBind) {
            unbindService(connection);
            mBind = false;
            Toast.makeText(this, "Service Unbound", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}