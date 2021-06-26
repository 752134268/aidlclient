package com.wangbl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import com.wangbl.aidlclient.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
private Context mContext = MainActivity.this;
    private static final String TAG = "MainActivity";
    private IWangblAidlInterface iWangblAidlInterface;
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "onServiceConnected: ");
            iWangblAidlInterface = IWangblAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "onServiceDisconnected: ");
            iWangblAidlInterface = null;

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_bind_service).setOnClickListener( this );
        findViewById(R.id.btn_unbind_service).setOnClickListener( this );
        findViewById(R.id.btn_add).setOnClickListener( this );
        findViewById(R.id.btn_min).setOnClickListener( this );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_bind_service:
                Intent intent = new Intent();
                intent.setAction("com.wangbl");
                intent.setPackage("com.wangbl.aidlclient");
                bindService(intent,  serviceConnection, BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                unbindService( serviceConnection);
                break;
            case R.id.btn_add:
                if ( iWangblAidlInterface != null){
                    try {
                        showToast(iWangblAidlInterface.add(12,14) +"" );
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    showToast("服务已终止" );
                }
                break;
            case R.id.btn_min:
                Log.i(TAG, "onClick: " + getProcessName());
                Log.i(TAG, "onClick: " + android.os.Process.myPid());
                if ( iWangblAidlInterface != null){
                    try {
                        showToast(iWangblAidlInterface.min(12,14) +"" );

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }else {
                    showToast("服务已终止" );

                }
                break;

        }
    }
    private void showToast(String value){
        Toast.makeText(mContext,value,Toast.LENGTH_SHORT).show();
    }
    private String getProcessName(){
        File file = new File("/proc/" + android.os.Process.myPid() +"/cmdline");
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            String processName = bufferedReader.readLine().trim();
            bufferedReader.close();
            return  processName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}